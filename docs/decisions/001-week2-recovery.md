# Decision 001 — Week 2 Recovery Architecture

Java Swing is used for the initial UI because it is part of the standard Java platform and requires no external dependency.

The simulator is intentionally modular:
- CPU owns execution state.
- Memory owns byte-addressable storage.
- Simulator loads/parses programs and coordinates CPU + memory.
- SimulatorUI displays the state and controls execution.

This is a Week-2 prototype, not the final Week-4 multi-process architecture.
