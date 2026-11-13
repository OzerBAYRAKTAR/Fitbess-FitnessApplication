"""Exercise installation and update behavior against isolated consumer projects."""
import json
import contextlib
import io
import subprocess
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from sync_playbook import BEGIN, DIRECTORIES, MANIFEST, SyncError, main, sync


class SyncTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.source = Path(self.temp.name) / "source"
        self.project = Path(self.temp.name) / "consumer"
        self.project.mkdir()
        for directory in DIRECTORIES:
            (self.source / directory).mkdir(parents=True)
        for name in ("README.md", "VERSION", "SOURCES.md"):
            (self.source / name).write_text("0.4.0\n", encoding="utf-8")
        self.skill = self.source / "skills/example/SKILL.md"
        self.skill.parent.mkdir()
        self.skill.write_text("---\nname: example\ndescription: Example\n---\n", encoding="utf-8")
        (self.project / "AGENTS.md").write_text("# Local policy\nKeep existing data.\n", encoding="utf-8")
        (self.project / "project-context.md").write_text("Local authorization", encoding="utf-8")

    def install(self, **kwargs):
        return sync(self.source, self.project, **kwargs)

    def test_discovery_path_support_files_and_idempotence(self):
        (self.skill.parent / "references").mkdir()
        (self.skill.parent / "references/details.md").write_text("details", encoding="utf-8")
        self.install()
        self.assertEqual((self.project / ".agents/skills/example/SKILL.md").read_bytes(), self.skill.read_bytes())
        self.assertEqual((self.project / ".agents/skills/example/references/details.md").read_text(), "details")
        self.assertIn("Keep existing data.", (self.project / "AGENTS.md").read_text())
        self.assertEqual((self.project / "project-context.md").read_text(), "Local authorization")
        self.assertEqual(self.install()["writes"], [])
        self.assertEqual((self.project / "AGENTS.md").read_text().count(BEGIN), 1)

    def test_preview_leaves_project_unchanged(self):
        before = {p.relative_to(self.project): p.read_bytes() for p in self.project.rglob("*") if p.is_file()}
        self.assertTrue(self.install(check=True)["writes"])
        after = {p.relative_to(self.project): p.read_bytes() for p in self.project.rglob("*") if p.is_file()}
        self.assertEqual(before, after)

    def test_update_removes_only_unchanged_owned_files(self):
        self.install()
        custom = self.project / ".agents/skills/custom/SKILL.md"
        custom.parent.mkdir()
        custom.write_text("custom", encoding="utf-8")
        self.skill.unlink()
        replacement = self.source / "skills/new/SKILL.md"
        replacement.parent.mkdir()
        replacement.write_text("new", encoding="utf-8")
        self.install()
        self.assertFalse((self.project / ".agents/skills/example/SKILL.md").exists())
        self.assertEqual(custom.read_text(), "custom")

    def test_conflict_prevents_all_writes(self):
        self.install()
        local = self.project / ".agents/skills/example/SKILL.md"
        local.write_text("local edit", encoding="utf-8")
        (self.source / "VERSION").write_text("0.5.0\n", encoding="utf-8")
        with self.assertRaises(SyncError):
            self.install()
        self.assertEqual((self.project / ".ai-playbook/VERSION").read_text(), "0.4.0\n")
        self.assertEqual(local.read_text(), "local edit")

    def test_legacy_adoption_and_explicit_override(self):
        legacy = self.project / ".agents/skills/example/SKILL.md"
        legacy.parent.mkdir(parents=True)
        legacy.write_text("project variant", encoding="utf-8")
        with self.assertRaises(SyncError):
            self.install()
        relative = ".agents/skills/example/SKILL.md"
        self.install(preserve=[relative])
        self.install()
        self.assertEqual(legacy.read_text(), "project variant")
        state = json.loads((self.project / MANIFEST).read_text())
        self.assertIn(relative, state["preserved"])
        self.assertNotIn(relative, state["files"])

    def test_manifest_traversal_and_symlink_are_rejected(self):
        (self.project / MANIFEST).write_text(json.dumps({"schema": 1, "files": {".ai-playbook/../../outside": "x"}}))
        with self.assertRaises(SyncError):
            self.install()
        (self.project / MANIFEST).unlink()
        outside = Path(self.temp.name) / "outside"
        outside.mkdir()
        try:
            (self.project / ".agents").symlink_to(outside, target_is_directory=True)
        except OSError:
            self.skipTest("Creating symlinks is not available in this environment")
        with self.assertRaises(SyncError):
            self.install()
        self.assertEqual(list(outside.iterdir()), [])

    def test_concurrent_install_is_rejected(self):
        (self.project / ".ai-playbook-sync.lock").write_text("in progress")
        with self.assertRaises(SyncError):
            self.install()
        self.assertFalse((self.project / MANIFEST).exists())

    def test_baseline_adopts_only_matching_legacy_files(self):
        relative = ".agents/skills/example/SKILL.md"
        legacy = self.project / relative
        legacy.parent.mkdir(parents=True)
        legacy.write_bytes(b"old\r\n")
        self.install(baseline={relative: b"old\n"})
        self.assertEqual(legacy.read_bytes(), self.skill.read_bytes())
        with self.assertRaises(SyncError):
            self.install(baseline={relative: b"old\n"})

    def test_mismatching_baseline_does_not_authorize_overwrite(self):
        relative = ".agents/skills/example/SKILL.md"
        legacy = self.project / relative
        legacy.parent.mkdir(parents=True)
        legacy.write_text("local edits", encoding="utf-8")
        with self.assertRaises(SyncError):
            self.install(baseline={relative: b"old\n"})
        self.assertEqual(legacy.read_text(), "local edits")

    def test_git_line_ending_conversion_is_not_a_local_override(self):
        self.install()
        local = self.project / ".agents/skills/example/SKILL.md"
        local.write_bytes(local.read_bytes().replace(b"\r\n", b"\n").replace(b"\n", b"\r\n"))
        self.assertEqual(self.install()["writes"], [])
        self.skill.write_text("updated content\n", encoding="utf-8")
        self.install()
        self.assertEqual(local.read_bytes(), self.skill.read_bytes())

    def test_malformed_agents_markers_fail_before_writes(self):
        (self.project / "AGENTS.md").write_text(BEGIN)
        with self.assertRaises(SyncError):
            self.install()
        self.assertFalse((self.project / ".agents").exists())

    def test_update_fetches_exact_commit_without_executing_source(self):
        for directory in DIRECTORIES:
            (self.source / directory / ".keep").write_text("fixture", encoding="utf-8")
        (self.source / "scripts/never_run.py").write_text("raise RuntimeError('must not execute')", encoding="utf-8")
        def command(*arguments):
            return subprocess.run(["git", "-C", str(self.source), *arguments], check=True,
                                  capture_output=True, text=True).stdout.strip()
        command("init")
        command("add", ".")
        command("-c", "user.name=Fixture", "-c", "user.email=fixture@example.invalid", "commit", "-m", "Fixture")
        commit = command("rev-parse", "HEAD")
        arguments = ["sync_playbook.py", "update", "--project", str(self.project), "--ref", commit]
        with patch("sync_playbook.REPOSITORY", str(self.source)), patch("sys.argv", arguments), contextlib.redirect_stdout(io.StringIO()):
            main()
        state = json.loads((self.project / MANIFEST).read_text())
        self.assertEqual(state["source"]["commit"], commit)
        self.assertEqual(state["source"]["dirty"], False)
        self.assertTrue((self.project / ".agents/skills/example/SKILL.md").exists())


if __name__ == "__main__":
    unittest.main()
