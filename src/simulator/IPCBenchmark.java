package simulator;

/** Measures round trips through UI->Core FIFO IPC, including Core response and file logging. */
public final class IPCBenchmark {
    private static final int WARMUP = 10;
    private static final int ITERATIONS = 100;
    public static void main(String[] args) throws Exception {
        try (IPCClient client = new IPCClient()) {
            for (int i = 0; i < WARMUP; i++) cycle(client);
            long start = System.nanoTime();
            for (int i = 0; i < ITERATIONS; i++) cycle(client);
            long elapsed = System.nanoTime() - start;
            System.out.printf("IPC benchmark: %d cycles, total %.3f ms, average %.3f ms/cycle%n",
                    ITERATIONS, elapsed / 1_000_000.0, elapsed / 1_000_000.0 / ITERATIONS);
            System.out.println("Note: includes command/response FIFO IPC and synchronous log writes; not pure IPC overhead.");
        }
    }
    private static void cycle(IPCClient client) throws Exception {
        expect(client.send("RESET"), "RESET_OK");
        for (int i = 0; i < 6; i++) expect(client.send("STEP"), "STEP_OK");
    }
    private static void expect(String response, String prefix) {
        if (response == null || !response.startsWith(prefix)) throw new IllegalStateException("Unexpected response: " + response);
    }
}
