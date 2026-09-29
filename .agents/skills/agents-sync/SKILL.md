---
name: agents-sync
description: Translate changed AGENTS.ko.md sections into paired English AGENTS.md files and record verified synchronization. Use only when explicitly invoked after Korean agent rules change.
---

# Agents Sync

Synchronize repository agent rules without an external translation API. `AGENTS.ko.md` is the only authoring source; `AGENTS.md` is a committed generated artifact.

## Required Inputs

- Run from the repository root.
- Read every stale `AGENTS.ko.md` and its paired `AGENTS.md`.
- Read [references/glossary.md](references/glossary.md) before translating.
- Run `node scripts/agent-sync.mjs check` to identify stale or invalid pairs. A stale result is expected before synchronization.

## Translation Workflow

1. Inspect the Git diff for each stale Korean source and compare its Markdown sections with the current English target.
2. Translate only changed sections when their heading structure still maps reliably.
3. Regenerate the full English target when headings were added, removed, renamed, reordered, or cannot be mapped safely.
4. Preserve heading levels, list structure, fenced code blocks, inline code, Markdown link destinations, commands, file paths, identifiers, versions, and placeholders exactly.
5. Translate prose into concise imperative English. Preserve requirement strength such as must, never, and may.
6. Edit only paired `AGENTS.md` targets. Never rewrite Korean sources to simplify translation.
7. Run `node scripts/agent-sync.mjs record` only after every pair is structurally aligned and meaning has been reviewed.
8. Run `node scripts/agent-sync.mjs check` and the repository documentation-link test.

## Boundaries

- Do not call an external translation API or add an API dependency.
- Do not stage, commit, push, or install Git hooks.
- Do not change product rules while translating. Report suspected contradictions instead.
- Do not accept edits made only to generated English files; restore them from the Korean source.
- Keep both language files below 500 lines and free of emojis.
- If a translation is materially ambiguous, stop before recording hashes and ask the user for the intended meaning.

## Completion Report

Report the synchronized pairs, validation commands, and any wording that required judgment. Leave both language files and `.agents/agent-sync-manifest.json` ready for review and commit.
