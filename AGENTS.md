# AGENTS Instructions for Life-Sim

These instructions are for coding agents (for example Codex) operating in this repository.
Scope: this entire repository unless overridden by a more specific nested `AGENTS.md`.

## Project intent

Life-Sim explores emergent biology-like behavior from simple, composable building blocks.
Use the roadmap in GitHub Issue #19 and the root `README.md` as strategic context.

## Change guidelines

1. Avoid widespread refactors unless the task explicitly asks for it or maintainers approve it.
2. Keep changes focused and minimal; prefer small, reviewable commits.
3. When implementing or changing behavior, update relevant docs in the same PR.
4. Prefer composition over hardcoded specialized entities.
   - Build small reusable mechanisms that can self-assemble into higher-order behavior.
   - Avoid introducing privileged high-level types unless explicitly required.
5. Prefer future-proof abstractions over one-off specialization when it does not overcomplicate the current change.

## Architecture boundaries

- `biology` should remain pure domain logic and stay simulator-agnostic.
- `simulator` may orchestrate world/environment behavior and call into `biology`.
- Do not introduce `simulator` dependencies into `biology`.

## Code review checklist

- Test names should clearly describe the behavior under test and reference the correct type/method names.
- Code behavior must match documentation; if behavior changes, docs must be updated.
- Add comments where useful, preferring **why** over **what**.
- Keep naming and package organization consistent with existing Kotlin style.

## Testing expectations

- Run relevant tests for touched modules.
- Prefer adding or updating tests whenever core logic changes.
