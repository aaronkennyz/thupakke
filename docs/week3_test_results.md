# Week 3 Test Results

| Test | Expected | Result | Status |
|---|---|---|---|
| Memory write/read | Written byte can be read back | 0xAB read from 0x20 | PASS |
| Stack PUSH | SP increments | SP = 1 after two PUSH | PASS |
| Stack POP | LIFO order | 0x22 returned first | PASS |
| Queue full | Capacity reached | `isFull()` true | PASS |
| Queue dequeue 1 | First item leaves first | 0x10 | PASS |
| Queue dequeue 2 | Second item leaves second | 0x20 | PASS |
| Queue wrap-around | Reuse freed slot | 0x30 then 0x40 | PASS |
| Queue empty | No elements remain | `isEmpty()` true | PASS |
| CPU queue integration | ENQ/DEQ changes CPU state | A=0x10, 0x20 remains | PASS |

Run:
```text
javac -d out src/*.java
javac -cp out -d out tests/SimulatorTests.java
java -cp out SimulatorTests
```
