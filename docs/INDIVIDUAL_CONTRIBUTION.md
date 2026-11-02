# Individual Contribution – Logging Module

## Assigned responsibility

The assigned area is the Logging Module of the Week 4 multiprocess
microcontroller simulator.

## Module objective

Receive event messages from CoreProcess through the POSIX named pipe
`core_to_logger.fifo`, add timestamps, and append the events to
`logs/simulator.log`.

## Work items to complete and demonstrate

- [ ] Review the LoggerProcess implementation and explain each section.
- [ ] Verify the FIFO path matches the CoreProcess producer.
- [ ] Compile the project with the existing project source files.
- [ ] Run LoggerProcess and the other processes in the required order.
- [ ] Execute simulator commands and inspect the resulting log file.
- [ ] Run the standalone logger smoke test.
- [ ] Run the project's full integration test, if available.
- [ ] Record actual test results and capture screenshots.
- [ ] Commit only the files personally reviewed, tested, or authored.

## Contribution statement template

Use this only after completing the listed work:

"I worked on understanding and validating the logging module of the
multiprocess simulator. I examined the LoggerProcess implementation,
verified the Core-to-Logger FIFO communication, tested timestamped event
recording and append behavior, and documented the module's execution and
test procedure. I also recorded the results of the tests I performed."

If another team member authored the original LoggerProcess.java, acknowledge
that authorship accurately. Describe your own testing, documentation,
integration, debugging, or additional implementation separately.

## Suggested Git commit

```text
Add logging module documentation and verification test
```
