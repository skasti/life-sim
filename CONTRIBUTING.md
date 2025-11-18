# Contributing to Life-Sim

Thank you for your interest in contributing to **Life-Sim**!

This project is a personal research and exploration environment,
developed through a hybrid process involving both human creativity and
AI assistance. Contributions are welcome, but the workflow is
intentionally lightweight and flexible.

------------------------------------------------------------------------

## 🧭 Project Philosophy

Life-Sim is an open-ended experimental project focused on:

-   byte-encoded genomes\
-   emergent behavior\
-   evolvable neural networks\
-   biologically inspired mechanisms\
-   long-term tinkering and discovery

Contributions should aim to preserve this exploratory spirit.

------------------------------------------------------------------------

## 📝 How to Contribute

### 1. **Open an Issue**

Before writing code, please open an Issue if you plan to contribute:

-   a new feature proposal\
-   a significant change to genome architecture\
-   a new module or system\
-   documentation improvements\
-   bug reports

Discussion helps keep the project coherent.

------------------------------------------------------------------------

### 2. **Small Fixes and Improvements**

For minor changes (spelling corrections, small bug fixes, tight
refactors), feel free to submit a Pull Request without prior discussion.

------------------------------------------------------------------------

### 3. **Pull Requests**

When submitting PRs:

-   Keep them focused and reasonably small\
-   Write clear descriptions\
-   Include rationale when changing core behavior\
-   Add tests when adding logic to the genome engine\
-   Follow Kotlin idioms and keep `genome` free of simulator-specific
    dependencies

PRs may be revised for consistency with the project's long-term
direction.

------------------------------------------------------------------------

## 🧬 Code Style and Structure

### Genome (`genome`)

-   **Pure logic only**: No references to rendering, physics, or
    simulation types\
-   Clearly defined gene interfaces\
-   Deterministic behavior where possible\
-   Avoid excessive allocation in core update loops\
-   Keep mutation and genome parsing fully isolated

### Simulator (`simulator`)

-   Handles all world interactions\
-   Encodes environmental rules and physics\
-   Should call into genome logic, not the other way around\
-   Keeps organism lifecycle and rendering logic separate

------------------------------------------------------------------------

## 🤖 AI-Assisted Contributions

This project is partially developed with AI tools.\
If you contribute code generated or assisted by an AI model:

-   Please review all generated code manually\
-   Ensure correctness, clarity, and consistency\
-   Mention in your PR description that AI assistance was used (optional
    but appreciated)

Transparency helps maintain the integrity of the project.

------------------------------------------------------------------------

## 🛠 Development Setup

1.  Clone the repository:

```{=html}
<!-- -->
```
    git clone https://github.com/skasti/life-sim.git
    cd life-sim

2.  Ensure you have:
    -   JDK 21+\
    -   Gradle (wrapper included)\
    -   Kotlin (automatically handled by Gradle)
3.  Build the project:

```{=html}
<!-- -->
```
    ./gradlew build

4.  Run tests:

```{=html}
<!-- -->
```
    ./gradlew test

------------------------------------------------------------------------

## ❤️ Code of Conduct

Be respectful and constructive.\
This project thrives on curiosity, experimentation, and shared
knowledge.

------------------------------------------------------------------------

## 🙌 Thanks

Thanks for taking the time to contribute!\
Even small ideas and suggestions can help shape the evolutionary
direction of Life-Sim.
