# Thupakki — Week 4 multiprocess simulator (starter implementation)

**Processor target:** Nuvoton MS51FB9AE (educational model, not a hardware-accurate emulator)  
**Language:** Java 21+ recommended  
**IPC:** POSIX named pipes (FIFOs), so run on Linux or WSL2 Ubuntu—not directly in native Windows PowerShell.

## What is included
- `UIProcess`: terminal UI in its own OS process.
- `CoreProcess`: owns the Week 3 simulator and handles commands in its own OS process.
- `LoggerProcess`: separate OS process that receives Core events over a FIFO and appends timestamped entries to `logs/simulator.log`.
- Two-way UI/Core IPC using `ui_to_core.fifo` and `core_to_ui.fifo`; Core-to-Logger IPC using `core_to_logger.fifo`.
- Standalone and IPC benchmark programs, plus an integration test script.

This is a Week 4 starter implementation using the Week 3 CPU model. The UI is terminal-based to keep IPC transparent and testable. It does not yet retrofit the Week 3 Swing window to display the remote Core's full memory/stack state.

## Windows users: run in WSL2 Ubuntu
1. In PowerShell (Admin), run `wsl --install -d Ubuntu` if WSL2 is not installed. Restart if prompted, open Ubuntu and create your Linux username.
2. Install Java and basic tools inside Ubuntu:
   ```bash
   sudo apt update
   sudo apt install -y openjdk-21-jdk
   java -version
   javac -version
   ```
3. Put this project in your WSL home folder, e.g. `~/thupakki-week4`. If the ZIP is in Windows Downloads, access it under `/mnt/c/Users/YOUR_WINDOWS_USERNAME/Downloads/` and unzip it into your Linux home directory.
4. Run every process and command below inside the same Ubuntu/WSL environment. Do not run Java processes in Windows while using Linux FIFOs in WSL.

## Compile
From the project root:
```bash
rm -rf out
mkdir -p out
javac -d out src/*.java tests/Week4TestClient.java
```

## Manual run (three separate Ubuntu terminals)
First create the FIFO endpoints (only needed once, or after deleting `/tmp/thupakki-ipc`):
```bash
./scripts/setup-ipc.sh
```
Terminal 1 — Logger:
```bash
java -cp out simulator.LoggerProcess
```
Terminal 2 — Core:
```bash
java -cp out simulator.CoreProcess
```
Terminal 3 — UI:
```bash
java -cp out simulator.UIProcess
```
Type these commands in the UI terminal:
```text
STATUS
STEP
STEP
RUN
RESET
LOAD_DEMO
STATUS
QUIT
```
`QUIT` shuts down Core and tells Logger to stop. Stop Logger with Ctrl+C if it does not exit. Start processes in the order Logger → Core → UI because FIFO opens synchronize the processes.

## Confirm logging to a file
After sending commands, open a fourth terminal or run:
```bash
tail -n 30 logs/simulator.log
```
You should see timestamped `CORE_START`, `COMMAND STEP`, `RESULT ...`, and shutdown entries. The log is appended, not overwritten.

## Automated integration test
This script creates isolated FIFOs and a test log, launches Logger/Core, sends commands, checks responses and verifies the log contents:
```bash
javac -d out src/*.java tests/Week4TestClient.java
bash tests/Week4IntegrationTest.sh
```
Expected final line resembles:
`PASS: IPC commands worked and log file contains recorded events: .../logs/integration-test.log`

## Benchmarks
Run the standalone CPU baseline (no IPC, no file logging):
```bash
java -cp out simulator.StandaloneBenchmark
```
For IPC benchmark, keep Logger and Core running in the manual setup, then in another terminal run:
```bash
java -cp out simulator.IPCBenchmark
```
The IPC benchmark performs 100 cycles after warm-up. Each cycle sends RESET plus six STEP commands. It includes FIFO round trips and synchronous logging, so it is **not pure IPC overhead**. Record machine/OS/JDK, repeat each benchmark at least five times, and report median/average; don't compare one run as definitive.

## Resource measurements (Linux/WSL)
Find PIDs:
```bash
pgrep -af 'simulator.(UIProcess|CoreProcess|LoggerProcess)'
```
Measure each PID (replace PID):
```bash
ps -p PID -o pid,comm,%cpu,%mem,rss,etime
```
`rss` is resident memory in KiB. Capture idle and during benchmark measurements, and document the method/limitations.

## IPC protocol
Each command/response is one newline-terminated UTF-8 line. Commands: `STEP`, `RUN`, `RESET`, `LOAD_DEMO`, `LOAD_QUEUE`, `STATUS`, `QUIT`. Unknown commands return an `ERROR` response. This prototype uses three POSIX FIFOs and assumes one UI client at a time.
y/stack/queue remain the Week 3 educational abstraction, not a complete MS51FB9AE hardware implementation.
