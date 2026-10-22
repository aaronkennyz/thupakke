#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
IPC_DIR="/tmp/thupakki-ipc-test-$$"
LOG_FILE="$PWD/logs/integration-test.log"
mkdir -p "$IPC_DIR" logs
rm -f "$IPC_DIR"/*.fifo "$LOG_FILE"
mkfifo "$IPC_DIR/ui_to_core.fifo" "$IPC_DIR/core_to_ui.fifo" "$IPC_DIR/core_to_logger.fifo"
cleanup() {
  for pid in "${UI_PID:-}" "${CORE_PID:-}" "${LOGGER_PID:-}"; do
    if [[ -n "$pid" ]]; then kill "$pid" 2>/dev/null || true; fi
  done
  rm -rf "$IPC_DIR"
}
trap cleanup EXIT
java -Dthupakki.ipcDir="$IPC_DIR" -Dthupakki.logFile="$LOG_FILE" -cp out simulator.LoggerProcess > /tmp/thupakki-logger-test.out 2>&1 & LOGGER_PID=$!
sleep 0.2
java -Dthupakki.ipcDir="$IPC_DIR" -cp out simulator.CoreProcess > /tmp/thupakki-core-test.out 2>&1 & CORE_PID=$!
sleep 0.2
# Scripted UI sends commands and checks responses using a tiny temporary Java test client.
java -Dthupakki.ipcDir="$IPC_DIR" -cp out simulator.Week4TestClient
sleep 0.3
grep -q 'COMMAND STEP' "$LOG_FILE"
grep -q 'COMMAND STATUS' "$LOG_FILE"
grep -q 'CORE_STOP' "$LOG_FILE"
echo "PASS: IPC commands worked and log file contains recorded events: $LOG_FILE"
