# Thupakki Week 4 — Presentation & Demo Runbook

This guide assumes the project is in `C:\Users\aaron\Desktop\projects\thupakke` on Windows and is run using Ubuntu/WSL because the IPC implementation uses POSIX named pipes (FIFOs).

> **Important distinction:** `UIProcess` is the terminal UI used for the multiprocess IPC demonstration. `SimulatorUI` is the Swing graphical UI from Week 3 and currently runs the simulator directly in the same JVM; it is **not yet connected to the Core process through IPC**. Present these as two separate demonstrations unless you first integrate the Swing UI with `IPCClient`.

## A. Before the presentation

1. Open Ubuntu (WSL).
2. Go to the project root:

   ```bash
   cd /mnt/c/Users/aaron/Desktop/projects/thupakke
   ```

3. Confirm Java is available:

   ```bash
   java -version
   javac -version
   ```

4. Confirm the package layout. Java sources should be under `src/simulator/` because they start with `package simulator;`.
5. Compile the project:

   ```bash
   mkdir -p out
   javac -d out src/simulator/*.java
   javac -cp out -d out tests/Week4TestClient.java
   ```

6. Check the required files and script:

   ```bash
   ls src/simulator
   ls tests
   ls scripts
   ```

7. Prepare the FIFO endpoints using the project script (run once before starting the processes):

   ```bash
   bash scripts/setup-ipc.sh
   ```

   If the script reports an endpoint already exists, inspect the script and existing processes before deleting anything. Do not remove FIFOs while the processes are running.

## B. Demo 1 — show the Week 3 Swing GUI

This is the graphical interface in `SimulatorUI.java`. It directly owns a `Simulator` instance; it is not the IPC UI process.

1. In Ubuntu, from the project root, compile if needed:

   ```bash
   javac -d out src/simulator/*.java
   ```

2. Launch the GUI:

   ```bash
   java -cp out simulator.SimulatorUI
   ```

   A Swing window should open. If GUI windows do not display in your WSL environment, run this command from Windows PowerShell in the project root instead:

   ```powershell
   javac -d out src\simulator\*.java
   java -cp out simulator.SimulatorUI
   ```

3. Demonstrate buttons in this order:
   - **Load Queue Validation** — loads the queue demonstration program.
   - **Step** — executes one instruction and displays the trace and current state.
   - **Step** again — show the PC and queue changing.
   - **Reset** — clears CPU state, memory, stack, and queue.
   - **Load Demo** — loads the other built-in demo.
   - **Run** — executes until `HALT` (or the CPU execution guard is reached).

4. Explain what is on screen:
   - Trace area: most recent action/trace plus memory dump, stack pointer/content, and queue state.
   - Bottom status: PC, accumulator A, R0–R2, zero/carry flags, and halted state.

**Honest limitation to mention:** this Swing window is the Week 3 single-process simulator GUI. It does not send commands to `CoreProcess` yet. The multiprocess IPC demonstration below uses `UIProcess` in a terminal.

## C. Demo 2 — prove the three independent Week 4 processes

Use **three separate Ubuntu terminals**. Start in this order and keep each terminal open.

### Terminal 1 — Logger

```bash
cd /mnt/c/Users/aaron/Desktop/projects/thupakke
java -cp out simulator.LoggerProcess
```

Expected: a message saying the logger is waiting on `core_to_logger.fifo` and naming `logs/simulator.log` as the log file.

### Terminal 2 — Core

```bash
cd /mnt/c/Users/aaron/Desktop/projects/thupakke
java -cp out simulator.CoreProcess
```

Expected: `Core starting...` and a note to open `UIProcess`.

### Terminal 3 — terminal UI

```bash
cd /mnt/c/Users/aaron/Desktop/projects/thupakke
java -cp out simulator.UIProcess
```

Type commands when prompted. Use this sequence:

```text
STATUS
STEP
STATUS
STEP
RESET
STATUS
```

Explain that UI sends a command through a FIFO, Core executes it against the CPU simulator, Core sends a response to the UI, and Core forwards events to the Logger through a separate FIFO.

### Verify all processes are alive (Terminal 4)

While the first three are still running, execute:

```bash
pgrep -af 'simulator\.(UIProcess|CoreProcess|LoggerProcess)'
```

You should see three Java process lines. If it prints nothing, check whether the processes have already exited and inspect each process terminal.

### Verify file logging (Terminal 4)

```bash
tail -n 30 logs/simulator.log
```

Point out timestamped `COMMAND`, `RESULT`, startup and shutdown entries. The Logger terminal should also print `logged: ...` as events arrive.

### Clean shutdown

Type `QUIT` in the UI terminal only after demonstrating STATUS/STEP/RESET. Check that the Core logs a stop event and the Logger prints `Logger stopped.`. After shutdown, `pgrep` should no longer list those processes.

## D. Automated integration test

Do not run this concurrently with the manual demo because it may start and stop its own Core/Logger processes and use the same FIFOs.

From the project root:

```bash
bash tests/Week4IntegrationTest.sh
```

Expected success text includes a PASS message about STATUS, STEP, RESET and QUIT. If it fails, save the complete terminal output and inspect the script before rerunning.

## E. What to say to the evaluator

- **CPU simulator:** models a small instruction set and CPU state (PC, accumulator, registers, flags, memory, stack, queue).
- **Fetch–Decode–Execute:** `step()` fetches the instruction at PC, validates that an instruction was fetched, executes its opcode, updates state, and records a trace.
- **UI process:** collects commands and displays Core responses.
- **Core process:** owns the simulator state and processes one command at a time.
- **IPC:** POSIX named pipes/FIFOs carry UI commands to Core, Core responses back to UI, and Core log events to Logger.
- **Logger process:** independently consumes log events and appends timestamped entries to `logs/simulator.log`.
- **Why processes:** process separation gives each component its own OS process and failure/resource boundary; FIFOs provide simple stream-based local IPC. The trade-off is startup ordering, blocking I/O, and serialization/IPC overhead.
- **Model limitation:** this is an educational simplified simulator, not a cycle-accurate implementation of the complete Nuvoton MS51FB9AE hardware or its full memory map.

## F. Common problems

- **`package simulator` errors:** source files should be under `src/simulator/`, and compile from the project root.
- **`javac` cannot find files:** check `pwd` and `ls src/simulator`.
- **UI/Core appears to hang on startup:** FIFO open calls can block until the corresponding reader/writer opens. Start Logger, then Core, then UI.
- **No `pgrep` output:** processes may already have exited. Run `pgrep` while the three terminals are still active, before sending QUIT.
- **No log file:** check Logger's startup output for the target path and verify Core is running and sending events.
- **Swing window not visible from WSL:** launch the Swing GUI with native Windows Java from PowerShell, or use a WSL GUI setup supported by your Windows installation.
- **Don't mix runtimes for FIFOs:** run all three FIFO-based processes inside WSL. Do not launch the Linux FIFO processes with native Windows Java.

## G. GUI customization — safe starting points

Open `src/simulator/SimulatorUI.java`.

- Window title and dimensions: `setTitle(...)`, `setSize(850, 600)`.
- Button labels/order: the `new JButton(...)` declarations and `buttons.add(...)` lines.
- Overall layout: `setLayout(new BorderLayout(...))` and `add(..., BorderLayout.NORTH/CENTER/SOUTH)`.
- Trace appearance: `trace.setFont(...)`, `trace.setEditable(false)`.
- Spacing: change `new BorderLayout(8, 8)` and/or add empty borders with `BorderFactory.createEmptyBorder(...)`.
- Colours: call `setBackground(...)` on panels and text area, and `setForeground(...)` for text. Swing uses `Color`, e.g. `new Color(24, 28, 38)`.
- Make the state label more readable: set its font and add padding with a border.
- After edits, compile and launch again:

  ```bash
  javac -d out src/simulator/*.java
  java -cp out simulator.SimulatorUI
  ```

Change one or two things at a time so you can identify what each change does. Keep the functional button handlers intact until the visual changes work.

## H. Java-to-JavaScript migration: feasibility

It is possible, but it is a rewrite rather than a simple conversion. A Node.js implementation could model the CPU, memory, stack, queue and instruction execution in JavaScript; a web UI could use HTML/CSS/JavaScript or React. For the three-process requirement, Node child processes and IPC could be used, but POSIX FIFOs would still require a Linux/WSL runtime, and replacing Java FIFOs with sockets would need faculty approval if the rubric specifically requires POSIX IPC.

Before migrating, confirm with the instructor whether changing language and GUI technology is allowed. If the rubric expects Java, a migration adds risk and work without improving the core requirement. A safer option is to improve the Swing UI while keeping the working Java Core/IPC design.

## I. Suggested learning order

1. `Instruction.java` — how an instruction is represented.
2. `CPU.java` — CPU state, fetch/decode/execute, opcode switch, PC and flags.
3. `Memory.java`, `StackMemory.java`, `FIFOQueue.java` — data structures.
4. `Simulator.java` — builds predefined programs.
5. `CoreProcess.java` — command handling and response/log pipes.
6. `IPCClient.java` and `UIProcess.java` — command sender and terminal interface.
7. `LoggerProcess.java` — timestamped file logging.
8. `SimulatorUI.java` — Swing layout and event handlers.

Learn the purpose of each field/method and be able to trace one `ENQ`/`STEP` command from UI to CPU to logger. You do not need to memorize every syntax detail, but you should be able to explain the flow and why each component exists.
