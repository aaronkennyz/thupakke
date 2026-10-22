package simulator;

public final class Week4TestClient {
    public static void main(String[] args) throws Exception {
        try (IPCClient c = new IPCClient()) {
            check(c.send("STATUS"), "STATUS");
            check(c.send("STEP"), "STEP_OK");
            check(c.send("STEP"), "STEP_OK");
            check(c.send("RESET"), "RESET_OK");
            check(c.send("STATUS"), "STATUS");
            check(c.send("QUIT"), "BYE");
        }
        System.out.println("PASS: Core responded to STATUS, STEP, RESET and QUIT.");
    }
    private static void check(String response, String prefix) {
        if (response == null || !response.startsWith(prefix)) throw new AssertionError("Expected " + prefix + ", got " + response);
    }
}
