package simulator;

public class Memory {
    private final int[] data;

    public Memory(int size) {
        data = new int[size];
    }

    public void reset() {
        for (int i = 0; i < data.length; i++) data[i] = 0;
    }

    public void write(int address, int value) {
        checkAddress(address);
        data[address] = value & 0xFF;
    }

    public int read(int address) {
        checkAddress(address);
        return data[address];
    }

    public int size() {
        return data.length;
    }

    private void checkAddress(int address) {
        if (address < 0 || address >= data.length)
            throw new IllegalArgumentException("Invalid memory address: " + address);
    }

    public String dump(int start, int end) {
        checkAddress(start);
        checkAddress(end);
        if (start > end) throw new IllegalArgumentException("Start > end");
        StringBuilder s = new StringBuilder();
        for (int i = start; i <= end; i++) {
            s.append(String.format("[%02X]=%02X  ", i, data[i]));
            if ((i - start + 1) % 8 == 0) s.append('\n');
        }
        return s.toString();
    }
}
