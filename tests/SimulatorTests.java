
import simulator.CPU;
import simulator.Instruction;
import java.util.Arrays;

public class SimulatorTests {

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void main(String[] args) {
        testArithmetic();
        testLogical();
        testTermination();

        System.out.println("ALL TESTS PASSED");
    }

    private static void testArithmetic() {
        CPU cpu = new CPU();

        cpu.loadProgram(Arrays.asList(
            new Instruction("ADD", "#05H", ""),
            new Instruction("ADD", "#03H", ""),
            new Instruction("HALT", "", "")
        ));

        cpu.run();

        check(cpu.getAccumulator() == 8,
              "Expected accumulator A=08H");

        System.out.println("Arithmetic test passed");
    }

    private static void testLogical() {
        CPU cpu = new CPU();

        cpu.loadProgram(Arrays.asList(
            new Instruction("ADD", "#0FH", ""),
            new Instruction("AND", "#03H", ""),
            new Instruction("HALT", "", "")
        ));

        cpu.run();

        check(cpu.getAccumulator() == 3,
              "Expected accumulator A=03H");

        System.out.println("Logical test passed");
    }

    private static void testTermination() {
        CPU cpu = new CPU();

        cpu.loadProgram(Arrays.asList(
            new Instruction("ADD", "#01H", ""),
            new Instruction("HALT", "", "")
        ));

        cpu.run();

        check(cpu.isHalted(),
              "CPU should halt after HALT instruction");

        System.out.println("Termination test passed");
    }
}
