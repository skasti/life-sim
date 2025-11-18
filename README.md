# Life-Sim

**Life-Sim** is an experimental artificial-life simulation where
organisms evolve from compact, byte-encoded genomes.\
The goal is to explore how complex behaviors, body structures, and
neural dynamics can emerge from simple genetic rules.

This project is a hobby research environment---part evolution sandbox,
part biology-inspired programming language---built to answer a simple
question:

> *How far can evolution go when everything about an organism---its
> shape, behavior, and even its learning ability---is encoded in raw
> bytes?*

------------------------------------------------------------------------

## ✨ Core Ideas

-   **Byte-encoded genomes**\
    Organisms store their entire genetic information as a sequence of
    bytes. Gene boundaries, expression, and mutations all operate
    directly on this low-level representation.

-   **Interpreted "genes"**\
    Each gene is a functional unit defined by a byte signature and an
    `update()` method. Genes can:

    -   Sense the environment
    -   Modify organism memory
    -   Interact with child cells
    -   Emit signals to neuron genes
    -   Control channels, pumps, and membrane properties

-   **Evolvable neural networks**\
    Neural-gene segments create small neurons that read/write memory,
    weight their inputs, and forward signals to others. The network
    topology itself is encoded in the genome and subject to mutation.

-   **Cells with physical structure**\
    Organisms are modeled as one or more connected cells, each defined
    by genome-expressed properties:

    -   Membrane permeability / stiffness\
    -   Channels and pumps\
    -   Position/orientation\
    -   Optional "child cells" forming simple multi-cell organisms

-   **Environment-driven adaptation**\
    The simulation world provides nutrients, hazards, and resource
    gradients. Organisms survive by:

    -   Maintaining energy\
    -   Avoiding leakage via membrane channels\
    -   Acquiring nutrients\
    -   Coordinating cell-level behaviors\
    -   Learning short-term strategies via neural feedback

------------------------------------------------------------------------

## 🧬 Project Structure

    life-sim/
    ├─ genome/           # Genome representation, parsing, gene interfaces, mutations
    ├─ simulator/        # World model, physics, rendering, organism lifecycle
    └─ docs/             # Architecture notes, gene specs, dev logs (optional)

Keeping these separate makes it possible to reuse `genome` for
future experiments. Each directory includes a short README with planned
components to guide initial contributions.

------------------------------------------------------------------------

## 🤖 AI-Assisted Development

Life-Sim is developed collaboratively---partly by a human author, partly
with the assistance of AI tools such as ChatGPT.\
All major decisions, architectural direction, and final approvals are
made by the human developer, but many ideas, drafts, and refactor
suggestions emerge from AI--human dialogue.\
This project aims to be transparent about this hybrid creative process.

------------------------------------------------------------------------

## 🚧 Status

This project is in **early development**.\
Features may change frequently as ideas evolve and experiments suggest
better models.

------------------------------------------------------------------------

## 📜 License

This project is released under the **Creative Commons BY-NC 4.0**
license.\
You may use and modify it for non-commercial purposes with proper
attribution.

------------------------------------------------------------------------

## 🤝 Contributing

This is currently a personal research project, but ideas, suggestions,
and discussions are welcome via Issues or PRs.

------------------------------------------------------------------------

## 🧠 Motivation

Life-Sim is a playground inspired by:

-   real-world biological evolution\
-   minimalist virtual machines\
-   emergent behavior in complex systems\
-   curiosity about how "intelligence" might form from non-intelligent
    parts

If those ideas excite you, feel free to follow along.
