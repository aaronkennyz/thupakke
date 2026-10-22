package simulator;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.loadQueueValidation();

        CPU cpu = simulator.getCPU();

        System.out.println("=== WEEK 3 QUEUE VALIDATION ===");
        while (!cpu.isHalted()) {
            cpu.step();
            System.out.println(cpu.getLastTrace());
            System.out.println("QUEUE: " + cpu.getQueue().display());
            System.out.println();
        }

        System.out.println("Final queue: " + cpu.getQueue().display());
        System.out.printf("Final accumulator (last dequeued value): %02X%n", cpu.getAccumulator());
    }
}
