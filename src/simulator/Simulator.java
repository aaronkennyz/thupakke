package simulator;

import java.util.*;

public class Simulator {

    private final CPU cpu = new CPU();

    public CPU getCPU() {
        return cpu;
    }

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

    public void loadCRC6() {

        cpu.loadProgram(Arrays.asList(

            // Input data: ASCII "123456789"
            // Stored at memory locations 40H-48H

            new Instruction("MOV", "40H", "#31H"),
            new Instruction("MOV", "41H", "#32H"),
            new Instruction("MOV", "42H", "#33H"),
            new Instruction("MOV", "43H", "#34H"),
            new Instruction("MOV", "44H", "#35H"),
            new Instruction("MOV", "45H", "#36H"),
            new Instruction("MOV", "46H", "#37H"),
            new Instruction("MOV", "47H", "#38H"),
            new Instruction("MOV", "48H", "#39H"),

            // Execute CRC-6 over the 9 input bytes
            new Instruction("CRC6", "40H", "09H"),

            // Stop CPU
            new Instruction("HALT", "", "")
        ));
    }
}