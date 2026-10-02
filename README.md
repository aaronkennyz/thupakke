# Thupakki — Week 3 Simulator

**Project:** Educational Microcontroller Simulator with Process Scheduling  
**Processor:** Nuvoton MS51FB9AE  
**Language:** Java

## Team
1. Aaron Kenneth Dsouza — Team Lead
2. Aman Dsilva
3. Alwisha Sweedal Tauro
4. Georgie Shibu

## Week 3 objective
Enhance the Week 2 simulator with Memory, Stack and FIFO Queue functionality and validate
the queue using a flowchart, processor-specific assembly program and documented tests.

## Structure
```text
src/
  CPU.java
  FIFOQueue.java
  Instruction.java
  Main.java
  Memory.java
  Simulator.java
  SimulatorUI.java
  StackMemory.java
tests/
  SimulatorTests.java
programs/
  queue_validation.asm
docs/
  week3_design.md
  week3_queue_flowchart.md
  week3_test_results.md
  week3_status.md
```

## Compile
```bash
javac -d out src/*.java
javac -cp out -d out tests/SimulatorTests.java
```

## Run tests
```bash
java -cp out SimulatorTests
```

## Run console demonstration
```bash
java -cp out simulator.Main
```

## Run GUI
```bash
java -cp out simulator.SimulatorUI
```

## Important
The assembly file is an 8051-family style validation program for the assigned Nuvoton processor.
Before submitting, verify the exact syntax against the assembler/toolchain used by the team.
