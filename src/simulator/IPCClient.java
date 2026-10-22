package simulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Small synchronous IPC client for future GUI integration and the IPC benchmark. */
public final class IPCClient implements AutoCloseable {
    private final PrintWriter commands;
    private final BufferedReader responses;
    public IPCClient() throws Exception {
        Path dir = Path.of(System.getProperty("thupakki.ipcDir", "/tmp/thupakki-ipc"));
        commands = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(dir.resolve("ui_to_core.fifo")), StandardCharsets.UTF_8), true);
        responses = new BufferedReader(new InputStreamReader(Files.newInputStream(dir.resolve("core_to_ui.fifo")), StandardCharsets.UTF_8));
        String ready = responses.readLine();
        if (ready == null || !ready.startsWith("READY")) throw new IllegalStateException("Core did not send READY: " + ready);
    }
    public synchronized String send(String command) throws Exception {
        commands.println(command);
        String response = responses.readLine();
        if (response == null) throw new IllegalStateException("Core disconnected");
        return response;
    }
    @Override public void close() throws Exception { commands.close(); responses.close(); }
}
