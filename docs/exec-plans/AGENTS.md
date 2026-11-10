# Execution Plan Rules

## Module Context

Execution plans preserve decisions and progress for changes that span modules, include schema migrations or external integrations, or continue across multiple work sessions. Small local edits do not need a plan.

- Put in-progress plans in `active/`.
- Move a plan to `completed/` only after implementation and verification finish and the plan has been condensed into the completed format.
- Use `YYYY-MM-DD-short-topic.ko.md` filenames.
- Follow the required sections in `README.md`.

## Tech Stack & Constraints

- Plans are Markdown and must be understandable without access to the originating conversation.
- Link to repository files with relative Markdown links that resolve from the plan's directory.
- Record verified facts separately from assumptions and pending decisions.
- Never include credentials, tokens, personal data, raw production payloads, or sensitive logs.

## Implementation Patterns

Each plan must define:

1. A user-visible goal and measurable completion criteria.
2. Included and excluded scope.
3. Current state separated into completed, in-progress, and remaining work.
4. The next file, command, and task that the next session must inspect first.
5. Verified evidence with links to relevant code, tests, schema, and documentation.
6. Small ordered tasks whose completion can be verified independently.
7. Dated decisions with rationale and rejected alternatives when relevant.
8. Exact automated commands and required manual scenarios.
9. A dated progress log containing only meaningful state changes, failures, discoveries, and the next starting point.

Update the plan during implementation, not only at the end. When evidence changes the design, revise the current state, task list, decisions, and next starting point. Record the state another engineer needs to resume, not the sequence of activities.

In completed plans, preserve only the final result, major change scope, long-term decisions, final verification, remaining risks, and follow-up work. Remove resolved intermediate states, session-resumption instructions, and repeated verification.

## Testing Strategy

- Every implementation step must name its focused verification.
- The final gate must include the root `verifyAll` command.
- Database changes must include empty-schema migration and upgrade-path checks.
- API changes must include contract, validation, and error cases.
- UI changes must include relevant route, state, accessibility, and responsive checks.
- If a check cannot run, record the exact command, failure, impact, and required follow-up.

## Local Golden Rules

### Do's

- Keep the plan current enough that another engineer can resume from the next unchecked item.
- Link claims to code or executable configuration rather than copying large excerpts.
- Link to the owning document instead of copying architecture or design guidance into the plan.
- Record migrations, configuration changes, rollout order, recovery, and reviewer focus explicitly.
- Split follow-up debt into a separate issue or plan before marking the current plan complete.

### Don'ts

- Do not use a plan as a speculative backlog or daily activity log.
- Do not record file opens, searches, attempts that did not affect the result, repeated successful commands, or full raw logs.
- Do not mark work complete before its stated verification passes.
- Do not erase failed approaches or decision history that future work needs.
- Do not move a plan to `completed/` while required work or known blocking risk remains.
