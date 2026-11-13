"""Validate repository structure, local Markdown links and skill metadata."""
import re
import sys
import tomllib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
EXPECTED_SKILLS = {"android-verification", "code-review", "git-checkpoint", "product-owner", "technical-manager", "owner", "orchestrator"}


def validate(root):
    errors = []
    for required in (
        "README.md", "VERSION", "AGENTS.md", "core/engineering.md",
        "core/architecture.md", "platforms/android-kotlin.md",
        "workflows/development.md", "integrations/codex/AGENTS.template.md",
        "integrations/codex/project-context.template.md",
        "workflows/po-manager.md", "integrations/codex/po-manager-research.md",
        "integrations/codex/technical-manager.toml", "integrations/codex/po-manager.config.toml",
        "workflows/owner-orchestrator.md",
    ):
        if not (root / required).is_file():
            errors.append(f"Missing required file: {required}")

    version_file = root / "VERSION"
    if version_file.exists() and not re.fullmatch(r"\d+\.\d+\.\d+\n?", version_file.read_text()):
        errors.append("VERSION must contain a semantic version")

    skills = {p.parent.name for p in (root / "skills").glob("*/SKILL.md")}
    if skills != EXPECTED_SKILLS:
        errors.append(f"Unexpected skill set: {sorted(skills)}")
    for name in sorted(skills):
        path = root / "skills" / name / "SKILL.md"
        content = path.read_text(encoding="utf-8")
        frontmatter = re.match(r"\A---\nname: ([a-z0-9-]+)\ndescription: ([^\n]+)\n---\n", content)
        if not frontmatter or frontmatter[1] != name:
            errors.append(f"Invalid skill metadata: {path.relative_to(root)}")
        elif len(frontmatter[2]) > 1024:
            errors.append(f"Skill description too long: {name}")

    for path in (root / "integrations/codex").glob("*.toml"):
        try:
            data = tomllib.loads(path.read_text(encoding="utf-8"))
        except tomllib.TOMLDecodeError as error:
            errors.append(f"Invalid TOML: {path.relative_to(root)}: {error}")
            continue
        if path.name == "technical-manager.toml":
            for key in ("name", "description", "developer_instructions"):
                if not isinstance(data.get(key), str) or not data[key].strip():
                    errors.append(f"Missing agent field: {key}")
            # These settings do not override parent permissions in a custom agent.
            if any(key in data for key in ("sandbox_mode", "approval_policy", "approvals_reviewer")):
                errors.append("Custom agent must not claim to override parent permissions")

    for path in root.rglob("*.md"):
        if ".git" in path.relative_to(root).parts:
            continue
        content = path.read_text(encoding="utf-8")
        for target in re.findall(r"\[[^\]]+\]\(([^\s)]+)\)", content):
            if "://" in target or target.startswith(("#", "mailto:")):
                continue
            local = target.split("#", 1)[0]
            resolved = (path.parent / local).resolve()
            if not resolved.is_relative_to(root.resolve()):
                errors.append(f"Link escapes repository: {path.relative_to(root)} -> {target}")
            elif not resolved.exists():
                errors.append(f"Broken link: {path.relative_to(root)} -> {target}")
        for number, line in enumerate(content.splitlines(), 1):
            if line.rstrip() != line:
                errors.append(f"Trailing whitespace: {path.relative_to(root)}:{number}")
    return errors


if __name__ == "__main__":
    problems = validate(ROOT)
    if problems:
        print("\n".join(problems))
        sys.exit(1)
    print("Structure, local links, seven skill manifests and Codex TOML validated.")
    print("Agent behavior and Android runtime checks are not evaluated by this script.")
