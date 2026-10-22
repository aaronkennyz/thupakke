package simulator;

import java.util.*;

public class Simulator {
    private final CPU cpu = new CPU();

    public CPU getCPU() { return cpu; }

    public void loadDemo() {
        cpu.loadProgram(Arrays.asList(
            new Instruction("MOV", "R0", "#05"),
            new Instruction("MOV", "R1", "#03"),
            new Instruction("ADD", "R0", ""),
            new Instruction("PUSH", "R1", ""),
            new Instruction("POP", "R2", ""),
            new Instruction("ENQ", "#10H", ""),
            new Instruction("ENQ", "#20H", ""),
            new Instruction("ENQ", "#30H", ""),
            new Instruction("DEQ", "", ""),
            new Instruction("HALT", "", "")
        ));
    }

    public void loadQueueValidation() {
        cpu.loadProgram(Arrays.asList(
            new Instruction("ENQ", "#10H", ""),
            new Instruction("ENQ", "#20H", ""),
            new Instruction("ENQ", "#30H", ""),
            new Instruction("DEQ", "", ""),
            new Instruction("DEQ", "", ""),
            new Instruction("HALT", "", "")
        ));
    }
}
