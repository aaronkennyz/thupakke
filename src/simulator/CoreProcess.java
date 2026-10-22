package simulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Separate CPU/core process. Receives one command per line and emits one response per command. */
public final class CoreProcess {
    private static final Path IPC_DIR = Path.of(System.getProperty("thupakki.ipcDir", "/tmp/thupakki-ipc"));
    private static final Path COMMAND_FIFO = IPC_DIR.resolve("ui_to_core.fifo");
    private static final Path RESPONSE_FIFO = IPC_DIR.resolve("core_to_ui.fifo");
    private static final Path LOG_FIFO = IPC_DIR.resolve("core_to_logger.fifo");

    public static void main(String[] args) throws Exception {
        Simulator simulator = new Simulator();
        simulator.loadQueueValidation();
        System.out.println("Core starting. Open UIProcess after this process starts.");
        // Open order matters for FIFOs: UI opens command writer then response reader.
        try (BufferedReader commands = new BufferedReader(new InputStreamReader(Files.newInputStream(COMMAND_FIFO), StandardCharsets.UTF_8));
             PrintWriter responses = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(RESPONSE_FIFO), StandardCharsets.UTF_8), true);
             PrintWriter logs = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(LOG_FIFO), StandardCharsets.UTF_8), true)) {
            logs.println("CORE_START program=queue_validation");
            responses.println("READY | queue_validation loaded | " + state(simulator));
            String line;
            while ((line = commands.readLine()) != null) {
                String command = line.trim().toUpperCase();
                if (command.isEmpty()) continue;
                logs.println("COMMAND " + command);
                try {
                    String message;
                    switch (command) {
                        case "STEP":
                            simulator.getCPU().step();
                            message = "STEP_OK | " + oneLine(simulator.getCPU().getLastTrace()) + " | " + state(simulator);
                            break;
                        case "RUN":
                            simulator.getCPU().run();
                            message = "RUN_OK | " + oneLine(simulator.getCPU().getLastTrace()) + " | " + state(simulator);
                            break;
                        case "RESET":
                            simulator.getCPU().reset();
                            message = "RESET_OK | " + state(simulator);
                            break;
                        case "LOAD_DEMO":
                            simulator.loadDemo();
                            message = "LOAD_DEMO_OK | " + state(simulator);
                            break;
                        case "LOAD_QUEUE":
                            simulator.loadQueueValidation();
                            message = "LOAD_QUEUE_OK | " + state(simulator);
                            break;
                        case "STATUS":
                            message = "STATUS | " + state(simulator);
                            break;
                        case "QUIT":
                            logs.println("CORE_STOP requested_by_ui");
                            responses.println("BYE | Core shutting down.");
                            logs.println("STOP");
                            return;
                        default:
                            message = "ERROR | Unknown command: " + command + " (use STEP, RUN, RESET, LOAD_DEMO, LOAD_QUEUE, STATUS, QUIT)";
                    }
                    responses.println(message);
                    logs.println("RESULT " + oneLine(message));
                } catch (Exception ex) {
                    String error = "ERROR | " + ex.getClass().getSimpleName() + ": " + ex.getMessage();
                    responses.println(error);
                    logs.println(error);
                }
            }
            logs.println("CORE_STOP command_pipe_closed");
            logs.println("STOP");
        }
    }

    private static String state(Simulator simulator) {
        CPU c = simulator.getCPU();
        return String.format("PC=%02X A=%02X R0=%02X R1=%02X R2=%02X Z=%s C=%s HALTED=%s QUEUE=%s",
                c.getPC(), c.getAccumulator(), c.getRegisters()[0], c.getRegisters()[1], c.getRegisters()[2],
                c.isZeroFlag(), c.isCarryFlag(), c.isHalted(), c.getQueue().display());
    }
    private static String oneLine(String s) { return s.replace('\n', ' ').replace('\r', ' ').replace('|', '/'); }
}
