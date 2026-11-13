"""Exercise validator failure paths using isolated copies."""
import shutil
import tempfile
import unittest
from pathlib import Path

from validate import ROOT, validate


class ValidatorTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name) / "playbook"
        shutil.copytree(ROOT, self.root, ignore=shutil.ignore_patterns(".git", "__pycache__"))

    def test_real_repository_passes(self):
        self.assertEqual(validate(self.root), [])

    def test_missing_skill_is_detected(self):
        (self.root / "skills/code-review/SKILL.md").unlink()
        self.assertTrue(any("skill set" in error for error in validate(self.root)))

    def test_broken_local_link_is_detected(self):
        with (self.root / "README.md").open("a", encoding="utf-8") as out:
            out.write("\n[missing](missing.md)\n")
        self.assertTrue(any("Broken link" in error for error in validate(self.root)))

    def test_invalid_agent_toml_is_detected(self):
        (self.root / "integrations/codex/technical-manager.toml").write_text('name = "unterminated', encoding="utf-8")
        self.assertTrue(any("Invalid TOML" in error for error in validate(self.root)))

    def test_agent_missing_instructions_is_detected(self):
        (self.root / "integrations/codex/technical-manager.toml").write_text('name = "manager"\ndescription = "Task owner"\n', encoding="utf-8")
        self.assertTrue(any("Missing agent field: developer_instructions" in error for error in validate(self.root)))

    def test_agent_permission_override_is_detected(self):
        with (self.root / "integrations/codex/technical-manager.toml").open("a", encoding="utf-8") as out:
            out.write('\nsandbox_mode = "danger-full-access"\n')
        self.assertTrue(any("override parent permissions" in error for error in validate(self.root)))

    def test_escaping_link_is_detected(self):
        with (self.root / "README.md").open("a", encoding="utf-8") as out:
            out.write("\n[outside](../outside.md)\n")
        self.assertTrue(any("escapes repository" in error for error in validate(self.root)))

    def test_mismatched_skill_name_is_detected(self):
        path = self.root / "skills/code-review/SKILL.md"
        path.write_text(path.read_text(encoding="utf-8").replace(
            "name: code-review", "name: other-review"), encoding="utf-8")
        self.assertTrue(any("Invalid skill metadata" in error for error in validate(self.root)))


if __name__ == "__main__":
    unittest.main()
