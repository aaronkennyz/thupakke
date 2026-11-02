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

    public void loadCRC8() {
    cpu.loadProgram(Arrays.asList(
        // Input ASCII "123456789" at memory 40H-48H
        new Instruction("MOV", "40H", "#31H"),
        new Instruction("MOV", "41H", "#32H"),
        new Instruction("MOV", "42H", "#33H"),
        new Instruction("MOV", "43H", "#34H"),
        new Instruction("MOV", "44H", "#35H"),
        new Instruction("MOV", "45H", "#36H"),
        new Instruction("MOV", "46H", "#37H"),
        new Instruction("MOV", "47H", "#38H"),
        new Instruction("MOV", "48H", "#39H"),

        // Initialize CRC = 00H
        new Instruction("MOV", "30H", "#00H"),

        // R0 = input pointer 40H
        new Instruction("MOV", "R0", "#40H"),

        // R6 = number of input bytes
        new Instruction("MOV", "R6", "#09H"),

        // ----- BYTE LOOP: instruction 12 -----
        // CRC = CRC XOR input byte
        new Instruction("MOV", "A", "30H"),
        new Instruction("XOR", "@R0", ""),
        new Instruction("MOV", "30H", "A"),

        // Process 8 bits
        new Instruction("MOV", "R7", "#08H"),

        // ----- BIT LOOP: instruction 16 -----
        new Instruction("MOV", "A", "30H"),
        new Instruction("SHL", "", ""),
        new Instruction("MOV", "30H", "A"),

        // If carry clear, skip polynomial XOR
        new Instruction("JNC", "23", ""),

        // CRC = CRC XOR 07H
        new Instruction("MOV", "A", "30H"),
        new Instruction("XOR", "#07H", ""),
        new Instruction("MOV", "30H", "A"),

        // Repeat 8 bits
        new Instruction("DJNZ", "R7", "16"),

        // Next input byte
        new Instruction("INC", "R0", ""),

        // Repeat for all 9 input bytes
        new Instruction("DJNZ", "R6", "12"),

        // Finished
        new Instruction("HALT", "", "")
    ));
}
}