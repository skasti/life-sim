# Molecules

This document describes the molecule-level types in `life.sim.biology.molecules`.

These types wrap primitive nucleotide sequences with biological meaning, while keeping data structures compact and immutable.

---

## Overview

Current molecule types:

- `Dna` — double-stranded DNA-like duplex
- `MRna` — messenger RNA wrapper
- `TRna` — transfer RNA wrapper with complementary scanning support

The current biology model uses the shared RNA nucleotide alphabet (`A`, `C`, `G`, `U`) across all molecule types.

---

## `Dna`

`Dna` represents a duplex with two strands:

- `forward: NucleotideSequence`
- `reverse: NucleotideSequence`

### Invariants

`Dna.of(...)` normalizes and validates both strands:

- forward strand direction is normalized to `FORWARD`
- reverse strand direction is normalized to `BACKWARD`
- both strands must have equal length
- each index must be complementary (`A<->U`, `C<->G`)

If any invariant fails, construction throws `IllegalArgumentException`.

### Constructors and parsing

- `empty()` creates a zero-length duplex
- `of(forward, reverse = forward.complement())`
- `of(forwardText, reverseText)`
- `of(text)` / `parse(text)`

Text parsing supports one or two lines:

- one line: parses forward and auto-generates the complementary reverse strand
- two lines: parses both and validates complementarity

`toString()` serializes DNA as two lines (`forward` then `reverse`).

---

## `MRna`

`MRna` is a lightweight inline wrapper around `NucleotideSequence`.

Purpose:

- preserve type-level distinction from other RNA species
- keep APIs explicit at call sites

Core operations:

- `size`, `isEmpty()`
- `toNucleotideSequence()`
- `complement()`
- `toString()`
- factories: `empty()`, `of(sequence)`, `of(text)`, `parse(text)`

---

## `TRna`

`TRna` is also a lightweight inline wrapper around `NucleotideSequence`, with one extra behavior for binding-style matching.

Core operations:

- `size`, `isEmpty()`
- `toNucleotideSequence()`
- `toString()`
- factories: `empty()`, `of(sequence)`, `of(text)`, `parse(text)`

Binding-specific helper:

- `scan(target: NucleotideSequence): Int`

`scan(...)` delegates to `BindingMatcher.complementaryMatchStart(...)`, so matching is based on **complementary pairing rules** rather than direct nucleotide equality.

---

## Relationship to interactions

Molecule values describe structural identity.

Runtime occupancy and binding state are modeled separately by the interactions layer (`BindingSurface`, `BindingSite`, `Bond`, `BondRegistry`).

That separation allows molecule values to remain immutable while runtime systems track transient binding dynamics.

---

## Example usage pattern

1. Build or parse molecules (`Dna`, `MRna`, `TRna`)
2. Extract `NucleotideSequence` when low-level operations are needed
3. Use interaction APIs to represent where and what is bound at runtime

This keeps the molecule layer focused on type semantics and sequence integrity, with binding state handled elsewhere.
