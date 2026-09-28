import java.util.*;

public class CPU {
    // Simplified 8051/MS51 state used by this educational simulator.
    private int A = 0;                 // Accumulator
    private final int[] R = new int[8];
    private int PC = 0;                // Program Counter
    private int SP = 7;                // 8051 reset-style starting SP
    private boolean carry = false;
    private boolean zero = false;
    private boolean halted = false;

    private List<Instruction> program = new ArrayList<>();
    private final Memory memory;

    private String lastTrace = "";

    public CPU(Memory memory) {
        this.memory = memory;
        reset();
    }

    public void loadProgram(List<Instruction> instructions) {
        program = new ArrayList<>(instructions);
        reset();
    }

    public void reset() {
        A = 0;
        Arrays.fill(R, 0);
        PC = 0;
        SP = 7;
        carry = false;
        zero = false;
        halted = false;
        lastTrace = "";
    }

    public boolean isHalted() {
        return halted;
    }

    public int getA() {
        return A;
    }

    public int getR(int index) {
        return R[index];
    }

    public int getPC() {
        return PC;
    }

    public int getSP() {
        return SP;
    }

    public boolean isCarry() {
        return carry;
    }

    public boolean isZero() {
        return zero;
    }

    public String getLastTrace() {
        return lastTrace;
    }

    public String step() {
        if (halted) {
            lastTrace = "Program already terminated.";
            return lastTrace;
        }

        if (PC < 0 || PC >= program.size()) {
            halted = true;
            lastTrace = "Program Counter outside program. Program terminated.";
            return lastTrace;
        }

        // FETCH
        Instruction instruction = fetch();

        // DECODE
        String decoded = decode(instruction);

        // EXECUTE
        String result = execute(instruction);

        lastTrace = "FETCH  ✓  PC=" + String.format("%02X", instruction.address)
                + "\nDECODE ✓  " + decoded
                + "\nEXECUTE ✓  " + result
                + "\nSTATE  A=" + hex(A)
                + "  R0=" + hex(R[0])
                + "  R1=" + hex(R[1])
                + "  PC=" + String.format("%02X", PC)
                + "  SP=" + String.format("%02X", SP)
                + "  C=" + (carry ? 1 : 0)
                + "  Z=" + (zero ? 1 : 0);

        return lastTrace;
    }

    public void run(int maxSteps) {
        int count = 0;
        while (!halted && count < maxSteps) {
            step();
            count++;
        }
        if (!halted && count >= maxSteps) {
            throw new IllegalStateException("Execution stopped after safety limit of " + maxSteps + " steps.");
        }
    }

    // FETCH: obtain instruction using PC.
    private Instruction fetch() {
        return program.get(PC);
    }

    // DECODE: identify opcode and operands.
    private String decode(Instruction instruction) {
        return instruction.operation + " " + String.join(", ", instruction.operands);
    }

    // EXECUTE: perform the selected operation.
    private String execute(Instruction i) {
        String op = i.operation;
        String[] x = i.operands;

        switch (op) {
            case "MOV":
                return executeMov(x);
            case "ADD":
                requireOperands(x, 2);
                requireAccumulator(x[0]);
                int oldAdd = A;
                int addValue = valueOf(x[1]);
                int sum = A + addValue;
                carry = sum > 0xFF;
                A = sum & 0xFF;
                updateZero();
                PC++;
                return "A: " + hex(oldAdd) + " → " + hex(A);
            case "SUBB":
                requireOperands(x, 2);
                requireAccumulator(x[0]);
                int oldSub = A;
                int subValue = valueOf(x[1]);
                int borrow = carry ? 1 : 0;
                int diff = A - subValue - borrow;
                carry = diff < 0;
                A = diff & 0xFF;
                updateZero();
                PC++;
                return "A: " + hex(oldSub) + " → " + hex(A);
            case "ANL":
                requireOperands(x, 2);
                requireAccumulator(x[0]);
                int oldAnd = A;
                A = A & valueOf(x[1]);
                updateZero();
                PC++;
                return "A: " + hex(oldAnd) + " → " + hex(A);
            case "INC":
                requireOperands(x, 1);
                if (x[0].equalsIgnoreCase("A")) {
                    int old = A;
                    A = (A + 1) & 0xFF;
                    updateZero();
                    PC++;
                    return "A: " + hex(old) + " → " + hex(A);
                }
                int r = registerIndex(x[0]);
                int oldR = R[r];
                R[r] = (R[r] + 1) & 0xFF;
                updateZero();
                PC++;
                return x[0].toUpperCase() + ": " + hex(oldR) + " → " + hex(R[r]);
            case "JZ":
                requireOperands(x, 1);
                int oldPc = PC;
                int offset = Integer.parseInt(x[0]);
                if (zero) {
                    PC = PC + offset;
                } else {
                    PC++;
                }
                return "PC: " + oldPc + " → " + PC;
            case "RET":
                // For the educational top-level simulator, RET terminates the demo program.
                halted = true;
                PC++;
                return "Program terminated by RET.";
            default:
                throw new IllegalArgumentException("Unsupported instruction: " + op);
        }
    }

    private String executeMov(String[] x) {
        requireOperands(x, 2);
        String dest = x[0].toUpperCase();
        int value;

        if (dest.equals("A")) {
            value = valueOf(x[1]);
            int old = A;
            A = value;
            updateZero();
            PC++;
            return "A: " + hex(old) + " → " + hex(A);
        }

        int r = registerIndex(dest);
        value = valueOf(x[1]);
        int old = R[r];
        R[r] = value;
        updateZero();
        PC++;
        return dest + ": " + hex(old) + " → " + hex(R[r]);
    }

    private int valueOf(String token) {
        token = token.trim();
        if (token.startsWith("#")) token = token.substring(1);

        if (token.startsWith("0x") || token.startsWith("0X")) {
            return Integer.parseInt(token.substring(2), 16) & 0xFF;
        }
        if (token.endsWith("H") || token.endsWith("h")) {
            return Integer.parseInt(token.substring(0, token.length() - 1), 16) & 0xFF;
        }
        if (token.matches("\\d+")) {
            return Integer.parseInt(token) & 0xFF;
        }

        if (token.equalsIgnoreCase("A")) return A;
        return R[registerIndex(token)];
    }

    private int registerIndex(String token) {
        if (!token.matches("(?i)R[0-7]")) {
            throw new IllegalArgumentException("Expected R0-R7 but got: " + token);
        }
        return Integer.parseInt(token.substring(1));
    }

    private void requireAccumulator(String token) {
        if (!token.equalsIgnoreCase("A")) {
            throw new IllegalArgumentException("This simulator currently requires A as destination.");
        }
    }

    private void requireOperands(String[] x, int n) {
        if (x.length != n) {
            throw new IllegalArgumentException("Expected " + n + " operands.");
        }
    }

    private void updateZero() {
        zero = A == 0;
    }

    private String hex(int n) {
        return String.format("%02X", n & 0xFF);
    }
}
