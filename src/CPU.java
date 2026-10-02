package simulator;

import java.util.*;

public class CPU {
    private final Memory memory = new Memory(256);
    private final StackMemory stack = new StackMemory(16);
    private final FIFOQueue queue = new FIFOQueue(8);
    private final int[] registers = new int[8];

    private List<Instruction> program = new ArrayList<>();
    private int pc = 0;
    private int accumulator = 0;
    private boolean zeroFlag = false;
    private boolean carryFlag = false;
    private boolean halted = false;

    private Instruction current;
    private String lastTrace = "No instruction executed.";

    public Memory getMemory() { return memory; }
    public StackMemory getStack() { return stack; }
    public FIFOQueue getQueue() { return queue; }
    public int[] getRegisters() { return registers.clone(); }
    public int getPC() { return pc; }
    public int getAccumulator() { return accumulator; }
    public boolean isZeroFlag() { return zeroFlag; }
    public boolean isCarryFlag() { return carryFlag; }
    public boolean isHalted() { return halted; }
    public String getLastTrace() { return lastTrace; }

    public void loadProgram(List<Instruction> instructions) {
        program = new ArrayList<>(instructions);
        reset();
    }

    public void reset() {
        pc = 0;
        accumulator = 0;
        zeroFlag = false;
        carryFlag = false;
        halted = false;
        current = null;
        lastTrace = "CPU reset.";
        Arrays.fill(registers, 0);
        memory.reset();
        stack.reset();
        queue.reset();
    }

    public void fetch() {
        if (halted) return;
        if (pc < 0 || pc >= program.size())
            throw new IllegalStateException("PC outside program.");
        current = program.get(pc);
    }

    public void decode() {
        if (current == null) throw new IllegalStateException("Nothing fetched.");
    }

    public void execute() {
        if (current == null) throw new IllegalStateException("Nothing decoded.");

        int oldPC = pc;
        String op = current.getOpcode();

        switch (op) {
            case "MOV":
                registers[reg(current.getOperand1())] = value(current.getOperand2());
                pc++;
                break;
            case "ADD":
                accumulator = (accumulator + value(current.getOperand1())) & 0xFF;
                updateFlags(accumulator);
                pc++;
                break;
            case "SUB":
                int v = value(current.getOperand1());
                carryFlag = accumulator < v;
                accumulator = (accumulator - v) & 0xFF;
                updateFlags(accumulator);
                pc++;
                break;
            case "INC":
                int r = reg(current.getOperand1());
                registers[r] = (registers[r] + 1) & 0xFF;
                updateFlags(registers[r]);
                pc++;
                break;
            case "AND":
                accumulator &= value(current.getOperand1());
                updateFlags(accumulator);
                pc++;
                break;
            case "LOAD":
                accumulator = memory.read(parseNumber(current.getOperand1()));
                updateFlags(accumulator);
                pc++;
                break;
            case "STORE":
                memory.write(parseNumber(current.getOperand1()), accumulator);
                pc++;
                break;
            case "PUSH":
                stack.push(value(current.getOperand1()));
                pc++;
                break;
            case "POP":
                registers[reg(current.getOperand1())] = stack.pop();
                pc++;
                break;
            case "ENQ":
                queue.enqueue(value(current.getOperand1()));
                pc++;
                break;
            case "DEQ":
                accumulator = queue.dequeue();
                updateFlags(accumulator);
                pc++;
                break;
            case "JMP":
                pc = parseNumber(current.getOperand1());
                break;
            case "HALT":
                halted = true;
                break;
            default:
                throw new IllegalArgumentException("Unsupported opcode: " + op);
        }

        lastTrace = String.format(
            "Instruction: %s%nFETCH ✓%nDECODE ✓%nEXECUTE ✓%nPC: %02X -> %02X%nCPU: A=%02X R0=%02X R1=%02X R2=%02X Z=%s C=%s",
            current, oldPC, pc, accumulator, registers[0], registers[1], registers[2],
            zeroFlag, carryFlag);
        current = null;
    }

    public void step() {
        if (halted) return;
        fetch();
        decode();
        execute();
    }

    public void run() {
        int guard = 10000;
        while (!halted && guard-- > 0) step();
        if (!halted) throw new IllegalStateException("Execution guard reached.");
    }

    private void updateFlags(int x) { zeroFlag = (x & 0xFF) == 0; }
    private int reg(String s) {
        if (!s.matches("R[0-7]")) throw new IllegalArgumentException("Register required: " + s);
        return Integer.parseInt(s.substring(1));
    }

    private int value(String s) {
        if (s.startsWith("#")) return parseNumber(s.substring(1));
        if (s.matches("R[0-7]")) return registers[reg(s)];
        return parseNumber(s);
    }

    public static int parseNumber(String s) {
        s = s.trim().toUpperCase();
        if (s.endsWith("H")) return Integer.parseInt(s.substring(0, s.length()-1), 16);
        return Integer.parseInt(s);
    }
}
