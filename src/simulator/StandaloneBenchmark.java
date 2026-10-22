package simulator;

/** Baseline CPU-only benchmark; excludes IPC and file logging. */
public final class StandaloneBenchmark {
    private static final int WARMUP = 100;
    private static final int ITERATIONS = 1000;
    public static void main(String[] args) {
        Simulator s = new Simulator();
        s.loadQueueValidation();
        for (int i = 0; i < WARMUP; i++) executeCycle(s);
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) executeCycle(s);
        long elapsed = System.nanoTime() - start;
        System.out.printf("Standalone benchmark: %d cycles, total %.3f ms, average %.3f us/cycle%n",
                ITERATIONS, elapsed / 1_000_000.0, elapsed / 1000.0 / ITERATIONS);
    }
    private static void executeCycle(Simulator s) {
        s.getCPU().reset();
        for (int i = 0; i < 6; i++) s.getCPU().step();
    }
}
