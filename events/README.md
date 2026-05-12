# Events module

The `events` module provides a generic, simulator-agnostic event model and an
in-memory event stream implementation.

## Scope

This module intentionally contains no `biology` or `simulator` dependencies. It
is designed to be reused by both modules and by future transports/recorders.

## Core abstractions

- `Event`: immutable event envelope metadata.
- `EventListener`: callback interface for consumers.
- `EventStream`: publish/subscribe API.
- `Subscription`: unsubscribe handle.

## In-memory stream

`InMemoryEventStream` is a synchronous in-memory implementation that supports:

- subscribing to all events,
- filtering by exact topic,
- filtering by routing tag prefix,
- combined topic + routing tag filtering,
- unsubscribing.
