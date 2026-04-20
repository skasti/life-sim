# GitHub Copilot instructions for Life-Sim

## Goal
Help contributors produce focused, biologically grounded changes that align with Life-Sim's long-term direction.

## Repository context

- Core modules:
  - `biology`: sequence primitives, molecules, interactions, proteins.
  - `simulator`: world/runtime orchestration.
- Strategic references:
  - Root `README.md`
  - `biology/README.md`
  - `simulator/README.md`
  - `docs/README.md`
  - Roadmap: [GitHub Issue #19](https://github.com/<OWNER>/<REPO>/issues/19)

## Implementation guidelines

1. Avoid broad refactors unless requested by task requirements or maintainers.
2. Keep PRs focused and cohesive.
3. If code behavior changes, update the relevant docs in the same change.
4. Prefer simple composable building blocks over hardcoded high-level mechanisms.
5. When reasonable, choose designs that support future roadmap evolution over narrow one-off specialization.

## Module boundaries

- Keep `biology` independent from simulator/world/rendering concerns.
- Keep simulator-specific orchestration in `simulator`.
- Preserve deterministic behavior where practical in biology-domain logic.

## Testing and review guidance

- Add/adjust tests when changing logic.
- Ensure test names clearly state what is tested and match actual APIs.
- Verify documentation remains consistent with behavior.
- Prefer comments explaining intent/rationale (`why`) rather than restating implementation (`what`).

## PR quality

- Summarize user-visible or behavior changes clearly.
- Call out assumptions and follow-up work when a design is intentionally partial.
