package com.group1.logistics.datastructures;

import com.group1.logistics.models.Order;

public class OrderQueue {
    private Order[] elements;
    private int front;
    private int rear;
    private int count;
    private int capacity;

    public OrderQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        this.capacity = capacity;
        this.elements = new Order[capacity];
        this.front = 0;
        this.rear = -1;
        this.count = 0;
    }

    public boolean enqueue(Order order) {
        if (order == null || isFull()) {
            return false;
        }
        rear = (rear + 1) % capacity;
        elements[rear] = order;
        count++;
        return true;
    }

    public Order dequeue() {
        if (isEmpty()) {
            return null;
        }
        Order removed = elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        count--;
        return removed;
    }

    public Order peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[front];
    }

    public Order get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= count) {
            return null;
        }
        int actualIndex = (front + logicalIndex) % capacity;
        return elements[actualIndex];
    }

    public int size() {
        return count;
    }

    public int capacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == capacity;
    }

    public Order[] toArray() {
        Order[] snapshot = new Order[count];
        for (int i = 0; i < count; i++) {
            snapshot[i] = get(i);
        }
        return snapshot;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            elements[i] = null;
        }
        front = 0;
        rear = -1;
        count = 0;
    }
}
