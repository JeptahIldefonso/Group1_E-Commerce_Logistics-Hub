package com.group1.logistics.datastructures;

import com.group1.logistics.models.Order;

/**
 * Concrete primitive circular array queue (FIFO).
 * Complies with strict project constraints: Zero Java Collections Framework.
 * Reuses freed slots with modulo arithmetic: (index + 1) % capacity.
 */
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

    /**
     * Adds an order to the rear of the queue (FIFO).
     * @param order the order to enqueue.
     * @return true if enqueued successfully, false if null or queue is full.
     */
    public boolean enqueue(Order order) {
        if (order == null || isFull()) {
            return false;
        }
        rear = (rear + 1) % capacity;
        elements[rear] = order;
        count++;
        return true;
    }

    /**
     * Removes and returns the order at the front of the queue.
     * Sets vacated array slot to null for memory hygiene.
     * @return the front order, or null if empty.
     */
    public Order dequeue() {
        if (isEmpty()) {
            return null;
        }
        Order removed = elements[front];
        elements[front] = null; // Clean up reference
        front = (front + 1) % capacity;
        count--;
        return removed;
    }

    /**
     * Returns the front order without removing it.
     */
    public Order peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[front];
    }

    /**
     * Returns the order at the given logical FIFO index (0 = front, count-1 = newest).
     * Preserves logical ordering across wrap-arounds (TC-Q06).
     */
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

    /**
     * Returns a snapshot array in logical FIFO order.
     */
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
