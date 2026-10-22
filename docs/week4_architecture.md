# Week 4 architecture and IPC design

## Process separation
1. **UIProcess** — reads operator commands and prints Core responses. It does not instantiate or execute the CPU.
2. **CoreProcess** — owns `Simulator` and is the only process that changes CPU state.
3. **LoggerProcess** — receives event lines and appends them to `logs/simulator.log` with timestamps.

## IPC topology
- `ui_to_core.fifo`: UI → Core commands.
- `core_to_ui.fifo`: Core → UI responses.
- `core_to_logger.fifo`: Core → Logger events.

Named pipes are a POSIX IPC mechanism available on Linux and WSL2. A FIFO is a byte stream, so this implementation uses newline-delimited text messages and one response for each command. The FIFO paths are local to the same machine. The implementation is designed for one UI client.

## Why not threads?
A thread shares one process's address space. The requirement asks for independent processes, so UI, Core, and Logger are separate JVM processes. Each process may internally use threads, but this minimal prototype does not require additional worker threads.

## Reliability notes
- Start Logger, then Core, then UI: opening a FIFO blocks until the opposite endpoint opens.
- The Core catches command execution exceptions and returns an error line instead of terminating for an invalid operation.
- Logging is synchronous and flushed per event to make tests straightforward. This adds measurable latency and should be discussed in the performance analysis.
- `QUIT` asks Core to emit `STOP` to Logger. If a process is force-killed, graceful shutdown cannot be guaranteed.
