package simulator;

import java.util.ArrayList;
import java.util.List;

public class StackMemory {
    private final int capacity;
    private final List<Integer> stack = new ArrayList<>();
    private int sp = -1;

    public StackMemory(int capacity) {
        this.capacity = capacity;
    }

    public void reset() {
        stack.clear();
        sp = -1;
    }

    public void push(int value) {
        if (isFull()) throw new IllegalStateException("Stack overflow");
        stack.add(value & 0xFF);
        sp++;
    }

    public int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack underflow");
        int value = stack.remove(stack.size() - 1);
        sp--;
        return value;
    }

    public int peek() {
        if (isEmpty()) throw new IllegalStateException("Stack underflow");
        return stack.get(stack.size() - 1);
    }

    public boolean isEmpty() { return stack.isEmpty(); }
    public boolean isFull() { return stack.size() >= capacity; }
    public int getSP() { return sp; }
    public List<Integer> getValues() { return new ArrayList<>(stack); }

    public String display() {
        if (stack.isEmpty()) return "[empty]";
        StringBuilder s = new StringBuilder();
        for (int i = stack.size() - 1; i >= 0; i--) {
            s.append(String.format("%02X", stack.get(i)));
            if (i != 0) s.append(" <- ");
        }
        return s.toString();
    }
}
