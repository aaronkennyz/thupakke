public class Memory {
    private final int[] data;

    public Memory(int size) {
        data = new int[size];
    }

    public int read(int address) {
        check(address);
        return data[address] & 0xFF;
    }

    public void write(int address, int value) {
        check(address);
        data[address] = value & 0xFF;
    }

    public int size() {
        return data.length;
    }

    public void reset() {
        java.util.Arrays.fill(data, 0);
    }

    private void check(int address) {
        if (address < 0 || address >= data.length) {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }
    }
}
