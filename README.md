# Life-Sim

**Life-Sim** is an experimental artificial-life project focused on emergent
biology-like behavior from simple, composable rules.

The core research question is:

> *How far can evolution go when both inherited data and the machinery that
> interprets it are built from low-level building blocks?*

---

## Vision and Core Ideas

While the implementation is still early, the long-term motivation remains
ambitious. Life-Sim is intended to explore whether complex behavior can emerge
from compact heredity plus simple, composable execution chemistry.

Core ideas:

* **Byte-encoded heredity**
  Organisms store inheritable information in compact encoded forms where
  mutation, inheritance, and expression all work at low level.
* **Genes as patterns, not only direct instructions**
  Encoded sequences can be interpreted, copied, assembled, or repurposed by
  internal machinery instead of mapping one-to-one to fixed commands.
* **Emergent machinery over hardcoded roles**
  Systems analogous to polymerases, ribosomes, and nucleases should ideally
  arise from smaller parts and interactions, not from privileged built-ins.
* **Binding/catalysis as the execution substrate**
  Molecule-to-molecule associations, temporary complexes, and catalytic effects
  are the intended foundation for higher-order behavior.
* **Environment-driven adaptation**
  The simulator aims to pressure organisms through resources, hazards, and
  spatial constraints so survival depends on viable internal organization.

This is the "north star" for the project and remains relevant even when current
milestones focus on nearer-term building blocks.

---

## Current State (April 2026)

The repository currently has a strong **biology foundation** and an early
**simulator scaffold**:

* **Biology primitives are implemented**
  (`Nucleotide`, `NucleotideSequence`, ranges, directionality).
* **Molecule models are implemented**
  (DNA/RNA-like polymers, amino acids, polypeptides).
* **Interaction mechanics are implemented**
  (binding sites/surfaces/strands, bonds, matcher, bond registry).
* **Protein interpretation has started**
  with motif/domain-capability mapping in `ProteinInterpreter`.
* **Simulator module exists**
  but still contains placeholder/demo-level behavior compared with biology.

The project is still intentionally exploratory and architecture may continue
to evolve quickly.

---

## Project Structure

```
life-sim/
├─ biology/      # Sequence primitives, molecules, interactions, proteins
├─ simulator/    # World/runtime module (early-stage scaffold)
└─ docs/         # Design notes and evolving documentation
```

Useful entry points:

* `README.md` (this file) for high-level context.
* `biology/README.md` for biology-module goals.
* `simulator/README.md` for simulator-module goals.
* `docs/README.md` and `docs/biology/*` for design notes.

---

## Roadmap

Roadmap planning is tracked in **Issue #19**:

* <https://github.com/skasti/life-sim/issues/19>

That issue is the canonical source for near-term priorities and sequencing.
At a high level, upcoming work centers on:

* expanding reaction/execution chemistry,
* connecting biology dynamics into richer simulation ticks, and
* improving architecture/docs as modules mature.

---

## Design Philosophy

Life-Sim does **not** aim to be a biologically accurate simulator. Instead, it
aims to discover minimal rule sets that can produce lifelike complexity.

Principles:

* Prefer composable primitives over hardcoded high-level entities.
* Let structure emerge through interactions and constraints.
* Keep every layer evolvable (representation, execution, behavior).

---

## Contributing

The project is currently personal and research-oriented, but feedback and PRs
are welcome.

See `CONTRIBUTING.md` for process details.

---

## License

This project is licensed under **Creative Commons BY-NC 4.0**.
