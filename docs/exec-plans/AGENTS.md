# Execution Plan Rules

## Module Context

Execution plans preserve decisions and progress for changes that span modules, include schema migrations or external integrations, or continue across multiple work sessions. Small local edits do not need a plan.

- Put in-progress plans in `active/`.
- Move completed plans to `completed/` only after implementation and verification finish.
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
3. Current state with links to relevant code, tests, schema, and documentation.
4. Small ordered tasks whose completion can be verified independently.
5. Dated decisions with rationale and rejected alternatives when relevant.
6. Exact automated commands and required manual scenarios.
7. A dated progress log containing completed work, failures, discoveries, and the next starting point.

Update the plan during implementation, not only at the end. When evidence changes the design, revise the task list and record the decision.

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
- Record migrations, configuration changes, rollout order, recovery, and reviewer focus explicitly.
- Split follow-up debt into a separate issue or plan before marking the current plan complete.

### Don'ts

- Do not use a plan as a speculative backlog or daily activity log.
- Do not mark work complete before its stated verification passes.
- Do not erase failed approaches or decision history that future work needs.
- Do not move a plan to `completed/` while required work or known blocking risk remains.
