# Proteins

This document describes the protein-layer types in `life.sim.biology.proteins`.

These types interpret `Polypeptide` sequences into runtime domain/capability values and bridge those capabilities into interaction-state changes.

---

## Overview

Protein-package types:

- `ProteinInterpreter` — maps `Polypeptide` motifs to interpreted `ProteinDomain` hits
- `ProteinDomain` — one interpreted motif hit with source span and capabilities
- `MolecularCapability` — sealed runtime capability model (`SequenceBinder`, `Cutter`, `Ligase`, `Blocker`)
- `ActiveProtein` — first-class runtime protein value with stable `MoleculeId`, source chain, interpreted domains, and flattened capabilities
- `ProteinBinding` — helper that turns `SequenceBinder` capabilities into concrete `Bond` associations

---

## `ProteinInterpreter`

`ProteinInterpreter.interpret(polypeptide)` scans a `Polypeptide` for known motifs and emits sorted `ProteinDomain` values.

Current behavior:

- each motif hit becomes a `ProteinDomain` with:
  - `name`
  - `startInclusive`
  - `endExclusive`
  - `motif`
  - `capabilities`
- domains are sorted by `(startInclusive, name)` for deterministic output
- an empty polypeptide yields no domains

The interpreter currently emits one capability per domain and derives binder target sequences via a deterministic pattern-generation step over local residue context.

---

## `ProteinDomain`

`ProteinDomain` is an immutable description of one interpreted region in a source `Polypeptide`.

It keeps both:

- structural metadata (`name`, motif span, motif text)
- behavior metadata (`capabilities`)

Because capabilities are carried on the domain itself, higher-level code can preserve interpretation output directly rather than re-deriving behavior later.

---

## `MolecularCapability`

`MolecularCapability` is a sealed interface for runtime behavior tags and parameters.

Current capability data classes:

- `SequenceBinder(bindingPattern, affinity, specificity)`
- `Cutter(catalyticStrength)`
- `Ligase(catalyticStrength)`
- `Blocker(potency)`

Each capability also exposes a string `kind` value for lightweight runtime classification.

---

## `ActiveProtein`

`ActiveProtein` is the runtime protein aggregate:

- `moleculeId: MoleculeId`
- `source: Polypeptide`
- `domains: List<ProteinDomain>`
- `capabilities: List<MolecularCapability>`

Construction helpers:

- `ActiveProtein.fromDomains(...)` for already interpreted domains
- `ActiveProtein.interpret(...)` to interpret a source chain and build the runtime object in one step

Use `ActiveProtein` when runtime systems need a first-class protein identity/value rather than passing around temporary tuples.

---

## `ProteinBinding`

`ProteinBinding.tryBind(...)` bridges interpreted binders into interactions by:

1. normalizing binder affinity into bond strength
2. scanning complementary match sites via `BindingMatcher`
3. resolving overlap conflicts through `BondRegistry`
4. creating/storing a `Bond` from protein (`WholeMoleculeEndpoint`) to target site (`SiteEndpoint`)

This keeps molecule/protein values immutable while runtime occupancy and conflicts stay in the interactions layer.

---

## Relationship to molecule docs

`docs/biology/molecules.md` covers molecule wrappers (`Dna`, `MRna`, `TRna`, `Polypeptide`) and references protein concepts only where needed for cross-layer flow.

For protein-package details, use this document as the primary reference.
