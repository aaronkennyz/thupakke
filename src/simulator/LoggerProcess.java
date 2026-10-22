package simulator;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Separate process: consumes log events from a POSIX named pipe and appends them to a file. */
public final class LoggerProcess {
    static final Path IPC_DIR = Path.of(System.getProperty("thupakki.ipcDir", "/tmp/thupakki-ipc"));
    static final Path LOG_FILE = Path.of(System.getProperty("thupakki.logFile", "logs/simulator.log"));
    static final Path LOG_FIFO = IPC_DIR.resolve("core_to_logger.fifo");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(IPC_DIR);
        Files.createDirectories(LOG_FILE.toAbsolutePath().getParent());
        System.out.println("Logger waiting on " + LOG_FIFO + " ; writing to " + LOG_FILE.toAbsolutePath());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(LOG_FIFO), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE.toFile(), StandardCharsets.UTF_8, true))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("STOP")) {
                    out.println(stamp() + " [LOGGER] received STOP; shutting down");
                    out.flush();
                    break;
                }
                out.println(stamp() + " " + line);
                out.flush();
                System.out.println("logged: " + line);
            }
        }
        System.out.println("Logger stopped.");
    }

    private static String stamp() { return "[" + LocalDateTime.now().format(TIME) + "]"; }
}
