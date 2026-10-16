public class SimulatorTests {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        testArithmetic();
        testLogical();
        testTermination();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testArithmetic() {
        Simulator s = new Simulator();
        s.loadProgramText(
            "MOV A, #05H\n" +
            "MOV R0, #03H\n" +
            "ADD A, R0\n" +
            "INC A\n" +
            "RET\n"
        );

        s.run();
        check(s.getCPU().getA() == 9, "Expected A=09H");
    }

    private static void testLogical() {
        Simulator s = new Simulator();
        s.loadProgramText(
            "MOV A, #0FH\n" +
            "ANL A, #03H\n" +
            "RET\n"
        );

        s.run();
        check(s.getCPU().getA() == 3, "Expected A=03H");
    }

    private static void testTermination() {
        Simulator s = new Simulator();
        s.loadProgramText("MOV A, #01H\nRET\n");
        s.run();
        check(s.getCPU().isHalted(), "Program should terminate.");
    }
}
