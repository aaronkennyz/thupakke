package simulator;

public class Instruction {
    private final String opcode;
    private final String operand1;
    private final String operand2;

    public Instruction(String opcode, String operand1, String operand2) {
        this.opcode = opcode.toUpperCase();
        this.operand1 = operand1 == null ? "" : operand1.trim().toUpperCase();
        this.operand2 = operand2 == null ? "" : operand2.trim().toUpperCase();
    }

    public String getOpcode() { return opcode; }
    public String getOperand1() { return operand1; }
    public String getOperand2() { return operand2; }

    @Override
    public String toString() {
        String s = opcode;
        if (!operand1.isEmpty()) s += " " + operand1;
        if (!operand2.isEmpty()) s += ", " + operand2;
        return s;
    }
}
