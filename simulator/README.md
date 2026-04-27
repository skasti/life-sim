# Simulator

The simulator module is intended to host the runtime orchestration for Life-Sim.
Over time, this includes the world state, organism placement and lifecycle coordination,
and the simulation tick loop that advances the model over time.

## Simulator goals

Planned simulator responsibilities include:

- maintaining world/scene state for a running simulation
- coordinating organisms and other simulated objects within that world
- driving the main simulation tick/update loop
- providing runtime hooks for visualization, debugging, and interaction

The module currently contains a **libGDX desktop demo scene**.
It opens a window and renders a small, static, hand-authored baseline scene for manual
visual verification of core biology rendering output.

## Demo scene contents

On launch, the simulator renders all of the following together in a single static scene:

- one nucleotide example
- one nucleotide sequence example
- one DNA duplex example (forward and reverse strands)

This is intentionally a rendering/demo scene only; it is **not** yet a dynamic simulation.

## Entrypoint

The desktop launcher lives at:

- `simulator/src/main/kotlin/life/sim/simulator/DesktopLauncher.kt`

The render-loop application class lives at:

- `simulator/src/main/kotlin/life/sim/simulator/SimulatorApplication.kt`

The hand-authored demo data lives at:

- `simulator/src/main/kotlin/life/sim/simulator/DemoScene.kt`

## Run locally

From the repository root:

```bash
./gradlew :simulator:run
```

> **Note**
> `:simulator:run` launches a libGDX desktop application and requires a graphical desktop/session.
> It may fail in headless environments such as CI, containers, or some WSL setups with errors like `GLFW_PLATFORM_UNAVAILABLE`.
> If that happens, run it from a local desktop session with display access instead.

Expected result:

- A desktop window titled **Life-Sim Simulator** opens.
- The app renders a static scene showing nucleotide, sequence, and DNA text output.
- The scene can be used as a baseline for manual rendering verification during future simulator changes.

## Next steps

Planned follow-up work includes:

- camera controls
- scene/world object management
- dynamic simulation/render integration
- richer molecule/organism rendering and debug overlays
