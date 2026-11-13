"""Install project-local Codex skills and update a reviewed playbook snapshot."""
import argparse
import hashlib
import json
import os
import re
import subprocess
import tempfile
from pathlib import Path, PurePosixPath

REPOSITORY = "https://github.com/OzerBAYRAKTAR/ai-engineering-playbook.git"
MANIFEST = ".ai-playbook-manifest.json"
BEGIN = "<!-- ai-playbook:begin -->"
END = "<!-- ai-playbook:end -->"
DIRECTORIES = ("core", "platforms", "workflows", "research", "integrations", "skills", "scripts", "examples")
FILES = ("README.md", "VERSION", "SOURCES.md")
BLOCK = f"""{BEGIN}
## AI playbook (managed integration)

Read `project-context.md` when present and `.ai-playbook/core/engineering.md`,
`.ai-playbook/core/architecture.md`, and `.ai-playbook/workflows/development.md`
before implementation. Discover project skills in `.agents/skills/`; read a
relevant SKILL.md explicitly if automatic discovery is unavailable.
On an explicit owner/orchestrator designation, use the corresponding skill and
`.ai-playbook/workflows/owner-orchestrator.md` instead of the default PO-manager
topology for that outcome. Role names do not grant tool or Git permissions.
Preserve project-specific decisions and locally retained overrides recorded in
`.ai-playbook-manifest.json`. Use the sync command documented in
`.ai-playbook/integrations/codex/README.md` for updates.
{END}"""


class SyncError(ValueError):
    pass


def digest(data, relative=""):
    if Path(relative).suffix in (".md", ".py", ".toml", ".yaml", ".yml", ".json", ".txt") or relative.endswith("/VERSION"):
        data = data.replace(b"\r\n", b"\n")
    return hashlib.sha256(data).hexdigest()


def linked(path):
    return path.is_symlink() or bool(getattr(path.lstat(), "st_file_attributes", 0) & 0x400)


def checked_path(root, relative):
    parts = PurePosixPath(relative).parts
    if not parts or "\\" in relative or ":" in relative or PurePosixPath(relative).is_absolute() or any(p in ("", ".", "..") for p in relative.split("/")):
        raise SyncError(f"Unsafe path: {relative}")
    path = root
    for part in parts:
        path /= part
        if path.exists() or path.is_symlink():
            if linked(path):
                raise SyncError(f"Symlink/junction is not supported: {relative}")
    return path


def managed_path(relative):
    return relative.startswith((".ai-playbook/", ".agents/skills/"))


def git(root, *args):
    return subprocess.run(["git", "-c", f"safe.directory={root.as_posix()}", "-C", str(root), *args],
                          check=True, capture_output=True, text=True).stdout.strip()


def source_files(source):
    result = {}
    for directory in DIRECTORIES:
        base = checked_path(source, directory)
        if not base.is_dir():
            raise SyncError(f"Missing source directory: {directory}")
        for path in sorted(base.rglob("*")):
            relative = path.relative_to(source).as_posix()
            checked_path(source, relative)
            if path.is_file() and "__pycache__" not in path.parts and path.suffix != ".pyc":
                result[f".ai-playbook/{relative}"] = path.read_bytes()
                if directory == "skills":
                    result[f".agents/{relative}"] = path.read_bytes()
    for name in FILES:
        result[f".ai-playbook/{name}"] = checked_path(source, name).read_bytes()
    manifests = [p for p in result if p.startswith(".agents/skills/") and p.endswith("/SKILL.md")]
    if not manifests:
        raise SyncError("No skill manifests found")
    return result


def baseline_files(source, reference):
    commit = git(source, "rev-parse", "--verify", "--end-of-options", f"{reference}^{{commit}}")
    result = {}
    for relative in git(source, "ls-tree", "-r", "--name-only", commit).splitlines():
        if relative in FILES or relative.split("/", 1)[0] in DIRECTORIES:
            data = subprocess.run(["git", "-c", f"safe.directory={source.as_posix()}", "-C", str(source),
                                   "show", f"{commit}:{relative}"], check=True, capture_output=True).stdout
            result[f".ai-playbook/{relative}"] = data
            if relative.startswith("skills/"):
                result[f".agents/{relative}"] = data
    return result


def remote_commit(source, reference):
    if re.fullmatch(r"[0-9a-fA-F]{40}", reference) or reference.startswith("refs/"):
        return git(source, "rev-parse", "--verify", "--end-of-options", f"{reference}^{{commit}}")
    matches = set()
    for candidate in (f"refs/tags/{reference}", f"refs/remotes/origin/{reference}"):
        try:
            matches.add(git(source, "rev-parse", "--verify", "--end-of-options", f"{candidate}^{{commit}}"))
        except subprocess.CalledProcessError:
            pass
    if len(matches) != 1:
        raise SyncError("Ref is absent or ambiguous; select a full commit or explicit refs/tags/... / refs/remotes/origin/...")
    return matches.pop()


def agents_document(existing):
    text = existing.decode("utf-8")
    if text.count(BEGIN) != text.count(END) or text.count(BEGIN) > 1:
        raise SyncError("Invalid managed AGENTS.md markers")
    if BEGIN in text:
        start, end = text.index(BEGIN), text.index(END)
        if start > end:
            raise SyncError("Invalid managed AGENTS.md marker order")
        text = text[:start] + BLOCK + text[end + len(END):]
    else:
        text = text.rstrip() + ("\n\n" if text.strip() else "") + BLOCK + "\n"
    return text.encode("utf-8")


def atomic_write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    descriptor, temporary = tempfile.mkstemp(prefix=".playbook-", dir=path.parent)
    try:
        with os.fdopen(descriptor, "wb") as stream:
            stream.write(data)
        os.replace(temporary, path)
    finally:
        Path(temporary).unlink(missing_ok=True)


def _sync(source, project, *, check=False, preserve=(), provenance=None, baseline=None):
    source, project = source.resolve(), project.resolve()
    if not project.is_dir():
        raise SyncError("Project directory must already exist")
    if source == project:
        raise SyncError("Select a consumer project, not the source repository")
    incoming = source_files(source)  # Read the full snapshot before any mutation.
    manifest_path = checked_path(project, MANIFEST)
    previous = json.loads(manifest_path.read_text(encoding="utf-8")) if manifest_path.exists() else {}
    if previous and previous.get("schema") != 1:
        raise SyncError("Unsupported manifest schema")
    owned = previous.get("files", {})
    if baseline and previous:
        raise SyncError("Baseline adoption is only for installations without a manifest")
    if baseline:
        for relative, data in baseline.items():
            path = checked_path(project, relative)
            if path.is_file():
                actual = path.read_bytes()
                # Git checkout may normalize text line endings on Windows.
                text_file = Path(relative).suffix in (".md", ".py", ".toml", ".yaml", ".yml") or relative.endswith("/VERSION")
                if actual == data or (text_file and actual.replace(b"\r\n", b"\n") == data.replace(b"\r\n", b"\n")):
                    owned[relative] = digest(actual, relative)
    retained = set(previous.get("preserved", [])) | set(preserve)
    for relative in set(owned) | retained:
        if not managed_path(relative):
            raise SyncError(f"Manifest/preserve path outside managed content: {relative}")
        checked_path(project, relative)
    changes, removals, conflicts = {}, [], []
    for relative in sorted(set(incoming) | set(owned)):
        path = checked_path(project, relative)
        if relative in retained:
            if not path.is_file():
                raise SyncError(f"Preserved file is missing: {relative}")
            continue
        current = path.read_bytes() if path.is_file() else None
        new = incoming.get(relative)
        if current == new or (current is not None and new is not None and digest(current, relative) == digest(new, relative)):
            continue
        if current is not None and digest(current, relative) != owned.get(relative):
            conflicts.append(relative)
        elif path.exists() and not path.is_file():
            conflicts.append(relative)
        elif new is None:
            if current is not None:
                removals.append(relative)
        else:
            changes[relative] = new
    if conflicts:
        raise SyncError("Local/unmanaged files differ; inspect and explicitly --preserve if intentional:\n" + "\n".join(conflicts))
    agents = checked_path(project, "AGENTS.md")
    existing = agents.read_bytes() if agents.exists() else b""
    updated = agents_document(existing)
    if updated != existing:
        changes["AGENTS.md"] = updated
    state = {"schema": 1, "source": provenance or {"repository": REPOSITORY, "commit": None},
             "version": incoming[".ai-playbook/VERSION"].decode().strip(),
             "preserved": sorted(retained),
             "files": {p: digest(b, p) for p, b in sorted(incoming.items()) if p not in retained}}
    encoded = (json.dumps(state, indent=2, ensure_ascii=False) + "\n").encode("utf-8")
    if not manifest_path.exists() or manifest_path.read_bytes() != encoded:
        changes[MANIFEST] = encoded
    if check:
        return {"writes": sorted(changes), "removals": removals, "preserved": sorted(retained)}
    # Per-file replacement is atomic; a filesystem failure can interrupt a batch.
    for relative, data in changes.items():
        if relative != MANIFEST:
            atomic_write(checked_path(project, relative), data)
    for relative in removals:
        checked_path(project, relative).unlink()
    if MANIFEST in changes:
        atomic_write(manifest_path, encoded)  # Commit ownership after file writes.
    return {"writes": sorted(changes), "removals": removals, "preserved": sorted(retained)}


def sync(source, project, *, check=False, preserve=(), provenance=None, baseline=None):
    project = project.resolve()
    if not project.is_dir():
        raise SyncError("Project directory must already exist")
    if check:
        return _sync(source, project, check=True, preserve=preserve, provenance=provenance, baseline=baseline)
    lock = checked_path(project, ".ai-playbook-sync.lock")
    try:
        descriptor = os.open(lock, os.O_CREAT | os.O_EXCL | os.O_WRONLY)
    except FileExistsError as error:
        raise SyncError("Another sync may be running; inspect .ai-playbook-sync.lock") from error
    os.close(descriptor)
    try:
        return _sync(source, project, preserve=preserve, provenance=provenance, baseline=baseline)
    finally:
        lock.unlink()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mode", choices=("install", "update"))
    parser.add_argument("--project", type=Path, required=True)
    parser.add_argument("--source", type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument("--ref", help="Required for update: reviewed upstream branch, tag or full commit")
    parser.add_argument("--baseline-ref", help="install only: previous source commit for verified legacy adoption")
    parser.add_argument("--check", action="store_true", help="Report planned changes without writing project files")
    parser.add_argument("--preserve", action="append", default=[], help="Retain a reviewed local override (relative file path)")
    args = parser.parse_args()
    try:
        if args.mode == "update":
            if args.baseline_ref:
                raise SyncError("Use install with --baseline-ref for legacy migration")
            if not args.ref or args.ref.startswith("-") or not re.fullmatch(r"[A-Za-z0-9_./-]+", args.ref):
                raise SyncError("update requires a valid explicit --ref")
            with tempfile.TemporaryDirectory(prefix="playbook-update-") as temporary:
                source = Path(temporary) / "source"
                subprocess.run(["git", "clone", "--no-checkout", "--", REPOSITORY, str(source)], check=True)
                commit = remote_commit(source, args.ref)
                hooks = Path(temporary) / "empty-hooks"
                hooks.mkdir()
                git(source, "-c", f"core.hooksPath={hooks.as_posix()}", "checkout", "--detach", commit)
                result = sync(source, args.project, check=args.check, preserve=args.preserve,
                              provenance={"repository": REPOSITORY, "commit": commit, "dirty": False})
        else:
            source = args.source.resolve()
            try:
                if Path(git(source, "rev-parse", "--show-toplevel")).resolve() != source:
                    raise SyncError("Source is a snapshot, not an upstream Git checkout; use update")
                provenance = {"repository": REPOSITORY, "commit": git(source, "rev-parse", "HEAD"),
                              "dirty": bool(git(source, "status", "--porcelain"))}
            except (subprocess.CalledProcessError, FileNotFoundError, SyncError):
                provenance = {"repository": REPOSITORY, "commit": None, "dirty": None}
            baseline = baseline_files(source, args.baseline_ref) if args.baseline_ref else None
            result = sync(source, args.project, check=args.check, preserve=args.preserve, provenance=provenance, baseline=baseline)
        print(json.dumps(result, indent=2))
    except (SyncError, OSError, subprocess.CalledProcessError, json.JSONDecodeError) as error:
        parser.exit(1, f"Playbook sync failed: {error}\n")


if __name__ == "__main__":
    main()
