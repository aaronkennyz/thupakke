import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Simulator {
    private final Memory memory = new Memory(256);
    private final CPU cpu = new CPU(memory);
    private List<Instruction> program = new ArrayList<>();

    public void loadProgram(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        program = parseProgram(lines);
        cpu.loadProgram(program);
    }

    public void loadProgramText(String text) {
        program = parseProgram(Arrays.asList(text.split("\\R")));
        cpu.loadProgram(program);
    }

    private List<Instruction> parseProgram(List<String> lines) {
        List<Instruction> result = new ArrayList<>();
        int address = 0;

        for (String raw : lines) {
            String line = raw.split(";", 2)[0].trim();
            if (line.isEmpty()) continue;

            String[] pieces = line.split("\\s+", 2);
            String op = pieces[0].toUpperCase();
            String operandPart = pieces.length > 1 ? pieces[1].trim() : "";

            if (op.equals("END")) {
                op = "RET"; // Friendly simulator alias.
            }

            String[] operands = operandPart.isEmpty()
                    ? new String[0]
                    : Arrays.stream(operandPart.split(","))
                            .map(String::trim)
                            .toArray(String[]::new);

            result.add(new Instruction(op, operands, raw.trim(), address));
            address++;
        }
        return result;
    }

    public CPU getCPU() {
        return cpu;
    }

    public Memory getMemory() {
        return memory;
    }

    public List<Instruction> getProgram() {
        return Collections.unmodifiableList(program);
    }

    public void reset() {
        cpu.reset();
    }

    public String step() {
        return cpu.step();
    }

    public void run() {
        cpu.run(1000);
    }
}
