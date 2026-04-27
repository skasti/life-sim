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

The module currently contains a minimal **libGDX desktop application shell**.
It opens a window and runs a render loop that clears the frame each tick.
This is the current entrypoint and runtime scaffold for upcoming world/object rendering
and simulation orchestration work.

## Entrypoint

The desktop launcher lives at:

- `simulator/src/main/kotlin/life/sim/simulator/DesktopLauncher.kt`

The render-loop application class lives at:

- `simulator/src/main/kotlin/life/sim/simulator/SimulatorApplication.kt`

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
- The app stays responsive with a running render loop.
- The frame is cleared every render tick.

## Next steps

Planned follow-up work includes:

- camera controls
- scene/world object management
- rendering molecules/organisms and debug overlays
