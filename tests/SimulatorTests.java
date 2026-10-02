import simulator.*;

public class SimulatorTests {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name); failed++; }
    }

    public static void main(String[] args) {
        Memory memory = new Memory(256);
        memory.write(0x20, 0xAB);
        check("Memory write/read", memory.read(0x20) == 0xAB);

        StackMemory stack = new StackMemory(4);
        stack.push(0x11);
        stack.push(0x22);
        check("Stack SP after two PUSH", stack.getSP() == 1);
        check("Stack POP is LIFO", stack.pop() == 0x22);

        FIFOQueue q = new FIFOQueue(3);
        q.enqueue(0x10); q.enqueue(0x20); q.enqueue(0x30);
        check("Queue full condition", q.isFull());
        check("FIFO first dequeue", q.dequeue() == 0x10);
        check("FIFO second dequeue", q.dequeue() == 0x20);
        q.enqueue(0x40);
        check("FIFO wrap-around", q.dequeue() == 0x30);
        check("FIFO wrap-around second value", q.dequeue() == 0x40);
        check("Queue empty condition", q.isEmpty());

        CPU cpu = new CPU();
        cpu.loadProgram(java.util.Arrays.asList(
            new Instruction("ENQ", "#10H", ""),
            new Instruction("ENQ", "#20H", ""),
            new Instruction("DEQ", "", ""),
            new Instruction("HALT", "", "")
        ));
        cpu.step(); cpu.step(); cpu.step();
        check("CPU queue integration", cpu.getAccumulator() == 0x10);
        check("Queue retains FIFO order", cpu.getQueue().values().size() == 1
                && cpu.getQueue().values().get(0) == 0x20);

        System.out.println("\nPassed: " + passed + "  Failed: " + failed);
        if (failed > 0) System.exit(1);
    }
}
