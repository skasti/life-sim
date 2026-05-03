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

## Scene/state baseline

The simulator now uses a simple `Scene` abstraction backed by simulator-side scene objects:

- `SimulatorApplication` owns the libGDX lifecycle and delegates update/render work
- each scene exposes an `ObjectManager` that owns scene-object lifecycle
- the default `Scene.render(...)` path finalizes the `RenderContext` each frame (`finish()`)
- scenes use an explicit `init()` hook for setup that depends on systems created alongside the scene
- `SimObject` is the common base concept for objects that exist in a scene
- objects may optionally implement `Updateable`, `Renderable`, or both
- `ObjectManager` keeps deterministic insertion order for update/render iteration

This keeps the simulator shell thin and leaves scene-specific behavior in scene objects.

### `SimWrapper` bridge object

`SimWrapper(position: Vector2, content: Any)` is the initial scene-object bridge between simulator infrastructure and biology-domain values.

- it stores a center position plus wrapped domain content
- it resolves the matching renderer once during construction
- render frames reuse the resolved renderer instead of re-running lookup each frame

This keeps biology/domain objects independent of simulator rendering lifecycle concerns while still allowing them to be managed as scene objects.
Renderers interpret that position as the visual center of the object they draw, so rotation behavior stays consistent across nucleotide, sequence, and DNA renderers without sharing anchor internals.

## Demo scene contents

On launch, the simulator renders all of the following together in a single static scene:

- one nucleotide example
- one nucleotide sequence example
- one DNA duplex example (forward and reverse strands)

Rendering now uses type-specific renderers and includes both text labels and a minimal graphical treatment:

- `NucleotideRenderer` for schematic nucleotide silhouettes with centered symbols and upright labels
- `geometry` package for reusable simulator-side primitives (`Geometry`, `Arc`, `Polygon`, etc.)
- `NucleotideSequenceRenderer` for sequence layout, backbones, and direction indicators that rotate as one rigid model around the backbone midpoint
- `DnaRenderer` for duplex layout and pair connectors built on the sequence renderer so the full duplex rotates coherently

Nucleotide visualization is intentionally schematic (not biologically realistic):

- each nucleotide still has a type-specific color and centered base letter (`A`, `C`, `G`, `U`)
- each nucleotide uses one of two shared connector families to suggest complement compatibility
  - `A` / `U`: angled family
  - `C` / `G`: rounded family
- complementary bases use opposite polarity inside the same family (protrusion vs indentation)
- sequence and duplex layout rotate around computed visual pivots derived from their rendered spans, rather than rotating each tile in place
- connector meaning is carried by the silhouette itself (no separate socket-hint overlays)
- all silhouette geometry stays within a consistent bounding box based on the baseSize of the nucleotide.
- empty nucleotide sequences are treated as non-renderable and are skipped by the higher-level sequence renderer.

Text labels use a FreeType-generated font at its target size (no post-load bitmap upscaling),
which keeps label glyphs sharper than scaling the default libGDX bitmap font.

This remains intentionally a rendering/demo scene only; it is **not** yet a dynamic simulation.

## Entrypoint

The desktop launcher lives at:

- `simulator/src/main/kotlin/life/sim/simulator/DesktopLauncher.kt`

The render-loop application class lives at:

- `simulator/src/main/kotlin/life/sim/simulator/SimulatorApplication.kt`

The hand-authored scene implementation/data lives at:

- `simulator/src/main/kotlin/life/sim/simulator/DemoScene.kt`

The scene abstraction lives at:

- `simulator/src/main/kotlin/life/sim/simulator/Scene.kt`

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
- The app renders a static scene showing nucleotide/sequence/DNA text plus simple graphics.
- A small FPS counter is pinned to the lower-left corner as a lightweight debug overlay.
- The scene can be used as a baseline for manual rendering verification during future simulator changes.

## Next steps

Planned follow-up work includes:

- camera controls
- scene/world object management
- dynamic simulation/render integration
- richer molecule/organism rendering and debug overlays


### Geometry and polygon rendering

Simulator rendering geometry is centralized in `rendering/geometry` so shapes, bounds rules, and construction helpers evolve together.
`Geometry` is now an ordered list of `GeometryElement`s, and `Geometry.render(context)` draws those elements in list order (front-to-back layering is caller-controlled).
Each element owns its style (for example `color` and arc `lineWidth`) and calls the corresponding `RenderContext` draw method.

Use shape helpers for common cases:
- `Polygon.rect(...)` / `Polygon.triangle(...)` for filled polygons with explicit color.
- `Arc(..., lineWidth = 0f)` for filled arcs; positive `lineWidth` renders stroked arcs.
- `Line(...)` for explicit line segments.

For complex connectors, use `Polygon.of(color = ...).add(...).close()` and `arc(start, center, end, segments, sweepDirection)` to approximate curved edges as vertices.
Filled polygons are triangulated from that outline data before rendering, so curved and concave silhouettes do not need to be authored as triangle fans by hand.
Set `sweepDirection` explicitly when `start` and `end` sit opposite each other on a diameter, because those points alone do not determine which side of the circle should be traced.
This keeps awkward silhouettes composable and prepares the pipeline for later startup-time sprite generation.

### Sprite cache baseline

Simulator rendering now includes a small sprite cache keyed by `SpriteKey` (`String`-backed) so renderers can reuse generated textures across instances.
`Renderer<T>` exposes optional sprite hooks (`spriteKey`, `renderToSprite`) with safe defaults, and `NucleotideRenderer` uses this as the first cached path.
Nucleotide sprite keys encode only canonical base identity (`Nucleotide_A`, `Nucleotide_U`, `Nucleotide_C`, `Nucleotide_G`). The cached sprite is generated from the normal nucleotide geometry rendered once to a padded off-screen target in canonical RIGHT orientation, and the cache stores anchor metadata so drawing stays tile-aligned even with transparent padding. `PairingSide` is handled at draw time through rotation.

