# Molecules

This document describes the molecule-level types in `life.sim.biology.molecules`.

These types wrap primitive biological sequences (nucleotide strands and amino-acid chains) with biological meaning, while keeping data structures compact and immutable.

---

## Overview


Current molecule types:

- `Dna` — double-stranded DNA-like duplex
- `MRna` — messenger RNA wrapper
- `TRna` — transfer RNA wrapper with complementary scanning support
- `Polypeptide` — translated amino-acid chain used as input to protein-domain interpretation
- `ActiveProtein` — runtime protein molecule with stable `MoleculeId`, source chain, and interpreted protein outputs

Nucleotide-based molecule types (`Dna`, `MRna`, and `TRna`) use the shared RNA alphabet (`A`, `C`, `G`, `U`), while `Polypeptide` uses the amino-acid alphabet.

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


## `Polypeptide`

`Polypeptide` is an immutable amino-acid chain wrapper.

Core operations:

- `size`, `isEmpty()`
- index access (`operator get`)
- `subsequence(start, end)` for motif-window extraction
- `toList()` and `toString()`
- factories: `empty()`, `from(residues)`, `of(text)`, `parse(text)`

Parsing accepts one-letter amino-acid symbols using the standard 20-residue alphabet.

---

## Relationship to protein interpretation

A `Polypeptide` can be passed into `ProteinInterpreter` (in `life.sim.biology.proteins`).
The interpreter scans motif patterns and emits one or more `ProteinDomain`s, each of which exposes
`MolecularCapability` values such as `SequenceBinder`, `Cutter`, `Ligase`, or `Blocker`.

`SequenceBinder` capabilities include a derived nucleotide `bindingPattern`, so interpreted binders
carry concrete sequence targets that can be used during runtime matching.

When a protein needs to exist as an explicit runtime molecule, `ActiveProtein` can be created with:

- `moleculeId: MoleculeId` (stable runtime identity)
- `source: Polypeptide` (the source amino-acid chain)
- `domains: List<ProteinDomain>` (preserved interpreted domains)
- `capabilities: List<MolecularCapability>` (flattened capabilities derived from `domains`)

This keeps sequence storage separate from interpreted function while still allowing runtime code
to pass a first-class protein molecule value instead of carrying temporary tuples.

---

## Relationship to interactions

Molecule values describe structural identity.

Runtime occupancy and binding state are modeled separately by the interactions layer (`BindingSurface`, `BindingSite`, `Bond`, `BondRegistry`).

The `ProteinBinding.tryBind(...)` helper bridges interpretation to runtime associations by using a
`SequenceBinder` pattern plus `BindingMatcher` to create and register concrete `Bond` values.
`ActiveProtein` complements this by preserving interpreted domains/capabilities alongside the
protein's stable runtime `MoleculeId`.

That separation allows molecule values to remain immutable while runtime systems track transient binding dynamics.

---

## Example usage pattern

1. Build or parse molecules (`Dna`, `MRna`, `TRna`)
2. Extract `NucleotideSequence` when low-level operations are needed
3. Use interaction APIs to represent where and what is bound at runtime

This keeps the molecule layer focused on type semantics and sequence integrity, with binding state handled elsewhere.
