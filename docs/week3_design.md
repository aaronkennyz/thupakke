# Week 3 Design — Memory, Stack and FIFO Queue

## Processor
Nuvoton MS51FB9AE (8051-family).

## Memory
`Memory.java` provides byte-addressable read/write memory using 256 locations.
The simulator UI displays a memory dump.

## Stack
`StackMemory.java` provides:
- Stack Pointer (SP)
- PUSH
- POP
- PE​EK
- empty/full checks

The implementation is a bounded LIFO stack for the simulator.

## FIFO Queue
`FIFOQueue.java` provides:
- Enqueue
- Dequeue
- FIFO ordering
- Empty/full checks
- front/rear indexes
- queue status
- circular reuse of storage

## CPU integration
`CPU.java` integrates memory, stack and queue into the existing execution model.
The logical FETCH -> DECODE -> EXECUTE flow from Week 2 is retained.

## UI
The UI displays:
- PC and CPU state
- memory contents
- stack and SP
- queue contents, front, rear and size
- execution trace

## Scope note
The Week 3 queue validation assembly is documented using 8051-family instructions and validates
FIFO ordering through RAM locations. The Java simulator demonstrates the same queue ordering
through its integrated ENQ/DEQ operations.
