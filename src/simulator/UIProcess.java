package simulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

/** Separate terminal UI process; communicates with CoreProcess only through named pipes. */
public final class UIProcess {
    private static final Path IPC_DIR = Path.of(System.getProperty("thupakki.ipcDir", "/tmp/thupakki-ipc"));
    private static final Path COMMAND_FIFO = IPC_DIR.resolve("ui_to_core.fifo");
    private static final Path RESPONSE_FIFO = IPC_DIR.resolve("core_to_ui.fifo");

    public static void main(String[] args) throws Exception {
        System.out.println("Opening IPC channels. If this hangs, start LoggerProcess and CoreProcess first.");
        try (PrintWriter commands = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(COMMAND_FIFO), StandardCharsets.UTF_8), true);
             BufferedReader responses = new BufferedReader(new InputStreamReader(Files.newInputStream(RESPONSE_FIFO), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {
            String ready = responses.readLine();
            System.out.println("CORE: " + ready);
            System.out.println("Commands: STEP | RUN | RESET | LOAD_DEMO | LOAD_QUEUE | STATUS | QUIT");
            while (true) {
                System.out.print("thupakki> ");
                if (!scanner.hasNextLine()) break;
                String command = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
                if (command.isEmpty()) continue;
                commands.println(command);
                String response = responses.readLine();
                if (response == null) {
                    System.out.println("Core disconnected.");
                    break;
                }
                System.out.println("CORE: " + response);
                if (command.equals("QUIT")) break;
            }
        }
        System.out.println("UI stopped.");
    }
}
