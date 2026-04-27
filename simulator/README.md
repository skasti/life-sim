# Simulator

The simulator module now contains a minimal **libGDX desktop application shell**.
It opens a window and runs a render loop that clears the frame each tick.
This provides the baseline runtime structure for upcoming world/object rendering work.

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

Expected result:

- A desktop window titled **Life-Sim Simulator** opens.
- The app stays responsive with a running render loop.
- The frame is cleared every render tick.

## Next steps

Planned follow-up work includes:

- camera controls
- scene/world object management
- rendering molecules/organisms and debug overlays
