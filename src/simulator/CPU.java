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

    public Memory getMemory() {
        return memory;
    }

    public StackMemory getStack() {
        return stack;
    }

    public FIFOQueue getQueue() {
        return queue;
    }

    public int[] getRegisters() {
        return registers.clone();
    }

    public int getPC() {
        return pc;
    }

    public int getAccumulator() {
        return accumulator;
    }

    public boolean isZeroFlag() {
        return zeroFlag;
    }

    public boolean isCarryFlag() {
        return carryFlag;
    }

    public boolean isHalted() {
        return halted;
    }

    public String getLastTrace() {
        return lastTrace;
    }

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
        if (halted) {
            return;
        }

        if (pc < 0 || pc >= program.size()) {
            throw new IllegalStateException("PC outside program.");
        }

        current = program.get(pc);
    }

    public void decode() {
        if (current == null) {
            throw new IllegalStateException("Nothing fetched.");
        }
    }

    public void execute() {
        if (current == null) {
            throw new IllegalStateException("Nothing decoded.");
        }

        int oldPC = pc;
        String op = current.getOpcode();

        switch (op) {

            case "MOV":
                writeOperand(
                    current.getOperand1(),
                    readOperand(current.getOperand2())
                );
                pc++;
                break;

            case "ADD":
                accumulator =
                    (accumulator + readOperand(current.getOperand1())) & 0xFF;

                updateFlags(accumulator);
                pc++;
                break;

            case "SUB":
                int subValue = readOperand(current.getOperand1());

                carryFlag = accumulator < subValue;

                accumulator =
                    (accumulator - subValue) & 0xFF;

                updateFlags(accumulator);
                pc++;
                break;

            case "INC":
                int incRegister = reg(current.getOperand1());

                registers[incRegister] =
                    (registers[incRegister] + 1) & 0xFF;

                updateFlags(registers[incRegister]);
                pc++;
                break;

            case "AND":
                accumulator &= readOperand(current.getOperand1());
                accumulator &= 0xFF;

                updateFlags(accumulator);
                pc++;
                break;

            case "XOR":
                accumulator ^= readOperand(current.getOperand1());
                accumulator &= 0xFF;

                updateFlags(accumulator);
                pc++;
                break;

            case "SHL":
                carryFlag = (accumulator & 0x80) != 0;

                accumulator =
                    (accumulator << 1) & 0xFF;

                updateFlags(accumulator);
                pc++;
                break;

            case "LOAD":
                accumulator =
                    memory.read(parseNumber(current.getOperand1()));

                updateFlags(accumulator);
                pc++;
                break;

            case "STORE":
                memory.write(
                    parseNumber(current.getOperand1()),
                    accumulator
                );

                pc++;
                break;

            case "PUSH":
                stack.push(readOperand(current.getOperand1()));
                pc++;
                break;

            case "POP":
                registers[reg(current.getOperand1())] = stack.pop();
                pc++;
                break;

            case "ENQ":
                queue.enqueue(readOperand(current.getOperand1()));
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

            case "JNC":
                if (!carryFlag) {
                    pc = parseNumber(current.getOperand1());
                } else {
                    pc++;
                }
                break;

            case "DJNZ":
                int registerIndex = reg(current.getOperand1());

                registers[registerIndex] =
                    (registers[registerIndex] - 1) & 0xFF;

                if (registers[registerIndex] != 0) {
                    pc = parseNumber(current.getOperand2());
                } else {
                    pc++;
                }
                break;

            /*
             * Simulator-specific CRC6 instruction.
             *
             * Operand 1 = start memory address
             * Operand 2 = byte count
             *
             * Polynomial:
             * g(D) = D^6 + D^5 + 1
             *
             * CRC length = 6 bits
             * Initial CRC = 00H
             * Processing = MSB-first
             * Final XOR = 00H
             *
             * Result is stored in memory location 30H.
             */
            case "CRC6":
                int startAddress =
                    parseNumber(current.getOperand1());

                int byteCount =
                    parseNumber(current.getOperand2());

                int crc = 0x00;

                for (int i = 0; i < byteCount; i++) {

                    int dataByte =
                        memory.read(startAddress + i);

                    for (int bit = 7; bit >= 0; bit--) {

                        int inputBit =
                            (dataByte >> bit) & 0x01;

                        int crcTopBit =
                            (crc >> 5) & 0x01;

                        int feedback =
                            crcTopBit ^ inputBit;

                        // Keep only 6 bits
                        crc = (crc << 1) & 0x3F;

                        // Polynomial: D^6 + D^5 + 1
                        // Lower 6 coefficients = 100001B = 21H
                        if (feedback != 0) {
                            crc ^= 0x21;
                        }
                    }
                }

                memory.write(0x30, crc);

                accumulator = crc;
                updateFlags(accumulator);

                pc++;
                break;

            case "HALT":
                halted = true;
                break;

            default:
                throw new IllegalArgumentException(
                    "Unsupported opcode: " + op
                );
        }

        lastTrace = String.format(
            "Instruction: %s%nFETCH ✓%nDECODE ✓%nEXECUTE ✓%nPC: %02X -> %02X%nCPU: A=%02X R0=%02X R1=%02X R2=%02X Z=%s C=%s",
            current,
            oldPC,
            pc,
            accumulator,
            registers[0],
            registers[1],
            registers[2],
            zeroFlag,
            carryFlag
        );

        current = null;
    }

    public void step() {
        if (halted) {
            return;
        }

        fetch();
        decode();
        execute();
    }

    public void run() {
        int guard = 10000;

        while (!halted && guard-- > 0) {
            step();
        }

        if (!halted) {
            throw new IllegalStateException(
                "Execution guard reached."
            );
        }
    }

    private void updateFlags(int value) {
        zeroFlag = (value & 0xFF) == 0;
    }

    private int reg(String s) {
        if (!s.matches("R[0-7]")) {
            throw new IllegalArgumentException(
                "Register required: " + s
            );
        }

        return Integer.parseInt(s.substring(1));
    }

    private int readOperand(String operand) {
        operand = operand.trim().toUpperCase();

        if (operand.isEmpty()) {
            return 0;
        }

        if (operand.startsWith("#")) {
            return parseNumber(operand.substring(1));
        }

        if (operand.equals("A")) {
            return accumulator;
        }

        if (operand.matches("R[0-7]")) {
            return registers[reg(operand)];
        }

        if (operand.matches("@R[01]")) {
            int registerIndex =
                reg(operand.substring(1));

            int address =
                registers[registerIndex];

            return memory.read(address);
        }

        return memory.read(parseNumber(operand));
    }

    private void writeOperand(String operand, int value) {
        operand = operand.trim().toUpperCase();
        value &= 0xFF;

        if (operand.equals("A")) {
            accumulator = value;
            updateFlags(accumulator);
            return;
        }

        if (operand.matches("R[0-7]")) {
            registers[reg(operand)] = value;
            updateFlags(value);
            return;
        }

        if (operand.matches("@R[01]")) {
            int registerIndex =
                reg(operand.substring(1));

            int address =
                registers[registerIndex];

            memory.write(address, value);
            return;
        }

        memory.write(parseNumber(operand), value);
    }

    public static int parseNumber(String s) {
        s = s.trim().toUpperCase();

        if (s.startsWith("#")) {
            s = s.substring(1);
        }

        if (s.endsWith("H")) {
            return Integer.parseInt(
                s.substring(0, s.length() - 1),
                16
            );
        }

        return Integer.parseInt(s);
    }
}