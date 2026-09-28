public class Instruction {
    public final String operation;
    public final String[] operands;
    public final String originalLine;
    public final int address;

    public Instruction(String operation, String[] operands, String originalLine, int address) {
        this.operation = operation.toUpperCase();
        this.operands = operands;
        this.originalLine = originalLine;
        this.address = address;
    }

    @Override
    public String toString() {
        return originalLine;
    }
}
