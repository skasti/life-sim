# Primitives

This document describes the biology-layer primitives in `life.sim.biology.primitives`.

These types form the low-level sequence model used by higher-level molecule and interaction APIs.

---

## Design goals

The primitive layer is intentionally small and predictable:

- represent nucleotides compactly and consistently
- keep sequence data immutable
- make directionality explicit
- use clear index/range semantics for slicing and binding

---

## `Nucleotide`

`Nucleotide` is an enum with four RNA-style symbols:

- `A`
- `C`
- `G`
- `U`

Each value stores:

- a compact 2-bit encoding (`bits`)
- a printable symbol (`symbol`)

Current bit mapping:

- `A = 0b00`
- `C = 0b01`
- `G = 0b10`
- `U = 0b11`

Complement rules are RNA-style:

- `A <-> U`
- `C <-> G`

Construction helpers:

- `Nucleotide.fromChar(symbol)`
- `Nucleotide.fromBits(bits: Byte)`
- `Nucleotide.fromBits(bits: Int)`

All constructors validate input and throw `IllegalArgumentException` on invalid values.

---

## `SequenceDirection`

`SequenceDirection` models orientation:

- `FORWARD` (marker `>`)
- `BACKWARD` (marker `<`)

It also provides `opposite()` to flip orientation.

Direction is part of sequence identity in this model.

---

## `NucleotideSequence`

`NucleotideSequence` is an immutable wrapper around a list of `Nucleotide` values plus a `SequenceDirection`.

### Core API

- random access: `get(index)`
- iteration: `Iterable<Nucleotide>`
- `size`, `isEmpty()`
- slicing: `slice(range)` or `slice(start, endExclusive)`
- transforms:
  - `complement()` (base-complement + opposite direction)
  - `reversed()` (reverse order + opposite direction)

### Parsing and text format

`NucleotideSequence.parse(text)` supports:

- bare text (defaults to forward): `AUGC`
- explicit forward: `>AUGC>`
- explicit backward: `<AUGC<`

Markers must be paired and matching. Invalid markers and invalid symbols both raise `IllegalArgumentException`; invalid nucleotide symbols use index-aware error messages, while marker-related errors are reported against the full input string.

`toString()` emits marker-wrapped text, e.g. `>AUGC>`.

### Construction helpers

- `empty(direction = FORWARD)`
- `of(vararg nucleotides, direction = FORWARD)`
- `of(text)` / `parse(text)`
- `from(list, direction = FORWARD)`
- extension: `List<Nucleotide>.toNucleotideSequence()`

---

## `SequenceRange`

`SequenceRange(start, endExclusive)` defines a half-open range over sequence indexes:

- `start` is inclusive
- `endExclusive` is exclusive

Examples:

- `SequenceRange(2, 5)` covers indexes `2`, `3`, `4`
- `length = endExclusive - start`

Validation and behavior:

- `start >= 0`
- `endExclusive >= start`
- empty ranges are allowed
- `contains(index)` checks membership
- `overlaps(other)` uses strict overlap semantics

Half-open ranges are used throughout interaction code to make slicing and overlap checks straightforward.

---

## Example workflow

1. Parse a sequence: `NucleotideSequence.parse(">AUGCUA>")`
2. Select a region: `slice(SequenceRange(1, 4))` → `>UGC>`
3. Compute complement for pairing logic
4. Use `SequenceRange` values as coordinates in binding models

This keeps sequence-level operations deterministic and independent of simulator/runtime state.
