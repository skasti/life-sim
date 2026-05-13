# Biology

This module now houses the low-level biology model: sequence primitives, molecule representations, emerging genetics concepts, and reaction systems. It is intended to stay modular so different execution chemistries can be explored without coupling them to the simulator runtime.

Planned components:
- Base-unit definitions and composition rules
- Sequence primitives and helpers
- Molecule models such as DNA- and RNA-like polymers
- Genetics-oriented representations (genes, regulatory regions, mutations)
- Polymer and sequence interaction models
- Reaction primitives (bind, copy, cut, release)
- Resource pools, decay, and recycling helpers
- Unit tests for interaction and reaction behavior



## Binding availability events

The biology module now emits domain event types for binding availability:

- `BindingEndpointAvailable` for concrete endpoint availability
- `BindingSurfaceAvailable` for searchable surface availability
- `BindingCapabilities` for compatibility metadata (bond types, sequence pattern, affinity)

These events implement the generic `events` module `Event` contract while keeping biology logic simulator-agnostic.
