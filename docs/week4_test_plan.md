# Week 4 test plan and result recording

## Integration checks
| Test | Action | Expected result |
|---|---|---|
| IPC startup | Start Logger → Core → UI | UI receives `READY` from Core |
| Status | Send `STATUS` | Core state is returned without changing CPU state |
| Single step | Send `STEP` | PC/state response is returned and command/result are logged |
| Run | Send `RUN` | CPU runs until HALT or execution guard error; response is returned |
| Reset | Send `RESET`, then `STATUS` | PC and registers reset; queue empty |
| Invalid command | Send `HELLO` | Core returns an `ERROR` response and remains alive |
| File logger | Send several commands, inspect `logs/simulator.log` | Timestamped commands and results appear |
| Shutdown | Send `QUIT` | UI gets `BYE`; Core exits; Logger receives STOP and exits |
| Repeat | Run integration test twice | Isolated test FIFO paths avoid stale FIFO interference |

## Automated command
```bash
javac -d out src/*.java tests/Week4TestClient.java
bash tests/Week4IntegrationTest.sh
```

## Record real results here
Do not claim measurements until run on the team's test machine.

| Metric | Standalone | Multi-process | Method / notes |
|---|---:|---:|---|
| Mean execution time | To measure | To measure | At least 5 runs; report average and median |
| CPU usage | To measure | To measure | `ps` snapshot during workload; specify duration |
| RSS memory | To measure | To measure | `ps ... rss`; report each process and total |
| IPC/logging overhead | N/A | To estimate | Difference is approximate; benchmark includes file logging and scheduling |

## Known limitations
The included IPC benchmark is a practical end-to-end comparison, not a controlled measurement of pure IPC overhead. OS scheduling, JIT warm-up and synchronous disk writes affect results. The terminal UI is used to make process boundaries and IPC behavior easy to verify; the Swing GUI is not yet connected to the remote Core.
