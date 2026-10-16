package simulator;

import java.util.ArrayList;
import java.util.List;

public class FIFOQueue {
    private final int capacity;
    private final int[] data;
    private int front = 0;
    private int rear = -1;
    private int count = 0;

    public FIFOQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.data = new int[capacity];
    }

    public void reset() {
        front = 0;
        rear = -1;
        count = 0;
        for (int i = 0; i < capacity; i++) data[i] = 0;
    }

    public void enqueue(int value) {
        if (isFull()) throw new IllegalStateException("Queue is full");
        rear = (rear + 1) % capacity;
        data[rear] = value & 0xFF;
        count++;
    }

    public int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        int value = data[front];
        data[front] = 0;
        front = (front + 1) % capacity;
        count--;
        return value;
    }

    public boolean isEmpty() { return count == 0; }
    public boolean isFull() { return count == capacity; }
    public int size() { return count; }
    public int getFrontIndex() { return front; }
    public int getRearIndex() { return rear; }

    public List<Integer> values() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < count; i++)
            result.add(data[(front + i) % capacity]);
        return result;
    }

    public String display() {
        if (isEmpty()) return "[empty]";
        StringBuilder s = new StringBuilder("[");
        List<Integer> values = values();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) s.append(", ");
            s.append(String.format("%02X", values.get(i)));
        }
        return s.append("]").toString();
    }
}
