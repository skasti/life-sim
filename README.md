# Life-Sim

**Life-Sim** is an experimental artificial-life simulation where
organisms evolve from compact, byte-encoded genomes.

The project explores how complex behaviors, structures, and learning
can emerge when *everything*—from chemistry to cognition—is built from
simple, composable units.

> *How far can evolution go when both the code **and the machinery that executes it** are encoded in raw bytes?*

---

## ✨ Core Ideas

* **Byte-encoded genomes**
  Organisms store their genetic information as raw bytes. Mutation,
  inheritance, and expression all operate directly on this compact
  representation.

* **Genes as patterns, not instructions**
  Genes are not necessarily executed directly. Instead, they define
  patterns that can be interpreted, copied, or assembled by internal
  biological machinery.

* **Biology-inspired execution layer**
  Organisms may include systems analogous to:

    * Polymerases (copying / reading sequences)
    * Ribosomes (assembling functional units)
    * RNases / Nucleases (cutting and recycling)

  These systems are *not hardcoded types*, but emerge from smaller
  base-units and interactions.

* **Base-units and emergent machinery**
  The simulation aims to push toward a lower-level model where:

    * Small building blocks ("base-units") define behavior
    * Binding, matching, and catalysis emerge from interactions
    * Complex machinery (like ribosomes) can evolve rather than be predefined

* **Evolvable neural and behavioral systems**
  Organisms can form internal signaling and neural-like structures,
  encoded in the genome and shaped by evolution.

* **Cells with physical structure**
  Organisms consist of one or more connected cells with properties like:

    * Membrane permeability
    * Channels and pumps
    * Spatial orientation
    * Optional child cells forming multicellular structures

* **Environment-driven adaptation**
  The world provides nutrients, hazards, and gradients. Survival depends on:

    * Energy balance
    * Resource acquisition
    * Structural integrity
    * Coordinated behavior

---

## 🧬 Conceptual Layers

Life-Sim separates *what is encoded* from *how it is executed*:

* **Genome layer**
  Defines inheritable byte sequences and mutation rules.

* **Biology layer**
  Defines how sequences become active processes through:

    * Polymer formation (DNA/RNA/protein analogs)
    * Binding and matching rules
    * Catalytic behavior (copying, cutting, assembling)
    * Decay and recycling

* **Simulation layer**
  Defines the physical world in which organisms exist:

    * Space, movement, diffusion
    * Nutrients and hazards
    * Interactions between organisms and environment

This separation allows experimentation with different "execution chemistries"
without changing the underlying genome format.

---

## 🧱 Project Structure

```
life-sim/
├─ genome/           # Genome encoding, parsing, mutation, gene patterns
├─ biology/          # Base-units, polymers, ribosome/polymerase-like systems, decay/recycling
├─ simulator/        # World model, physics, rendering, organism lifecycle
└─ docs/             # Architecture notes, experiments, dev logs
```

### genome/

* Byte-level genome representation
* Mutation and recombination
* Gene pattern definitions
* Expression hooks (entry points into biology layer)

### biology/

* Base-units (minimal functional building blocks)
* Polymer representations (sequence chains)
* Binding and matching rules
* Machinery (emergent or constructed):

    * Polymerases
    * Ribosome-like assemblers
    * RNase-like cutters
* Reaction systems (copy, cut, bind, release)
* Resource pools and recycling

### simulator/

* Spatial world and physics
* Diffusion of signals and resources
* Cell and organism lifecycle
* Integration of biology processes into time steps

---

## 🧠 Design Philosophy

Life-Sim is not trying to simulate biology accurately.
Instead, it explores a question:

> *What is the **minimum set of rules** required for something resembling life to emerge?*

Key principles:

* Avoid hardcoding high-level concepts ("ribosome", "neuron")
* Prefer small, composable primitives
* Let structure and behavior emerge from interactions
* Keep the system evolvable at every layer

---

## 🤖 AI-Assisted Development

Life-Sim is developed collaboratively—partly by a human author, partly
with the assistance of AI tools such as ChatGPT.

AI contributes ideas, drafts, and refactor suggestions, while the human
developer makes final architectural decisions.

---

## 🚧 Status

This project is in **early development**.

The biological execution layer is actively evolving and may change
significantly as new models are explored.

---

## 📜 License

This project is released under the **Creative Commons BY-NC 4.0** license.

You may use and modify it for non-commercial purposes with proper
attribution.

---

## 🤝 Contributing

This is currently a personal research project, but ideas, suggestions,
and discussions are welcome via Issues or PRs.

---

## 🧠 Motivation

Life-Sim is inspired by:

* Biological evolution
* Minimalist virtual machines
* Emergent behavior in complex systems
* Curiosity about how "intelligence" forms from simple parts

If those ideas excite you, feel free to follow along.
