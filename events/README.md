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

`InMemoryEventStream` is an asynchronous in-memory implementation that enqueues published events and uses a single background worker thread to dispatch to subscribers sequentially in subscription order. It supports:

- non-blocking publish via queueing,
- sequential, predictable dispatch order,
- subscriber exception isolation (one failing listener does not stop dispatch),
- explicit lifecycle management via `AutoCloseable` (`close()` shuts down the worker),
- subscribing to all events,
- filtering by exact topic,
- filtering by routing tag prefix,
- combined topic + routing tag filtering,
- unsubscribing.
