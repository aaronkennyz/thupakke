#!/usr/bin/env bash
set -euo pipefail
IPC_DIR="${THUPAKKI_IPC_DIR:-/tmp/thupakki-ipc}"
mkdir -p "$IPC_DIR" logs
for name in ui_to_core.fifo core_to_ui.fifo core_to_logger.fifo; do
  if [[ -e "$IPC_DIR/$name" && ! -p "$IPC_DIR/$name" ]]; then
    echo "Refusing to replace non-FIFO path: $IPC_DIR/$name" >&2; exit 1
  fi
  [[ -p "$IPC_DIR/$name" ]] || mkfifo "$IPC_DIR/$name"
done
echo "Created/verified POSIX FIFOs in $IPC_DIR"
