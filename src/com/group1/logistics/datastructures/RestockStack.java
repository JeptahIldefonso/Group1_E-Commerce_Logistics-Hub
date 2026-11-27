package com.group1.logistics.datastructures;

import com.group1.logistics.models.RestockAction;

public class RestockStack {
    private RestockAction[] elements;
    private int top;
    private int capacity;

    public RestockStack(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        this.capacity = capacity;
        this.elements = new RestockAction[capacity];
        this.top = -1;
    }

    public boolean push(RestockAction action) {
        if (action == null || isFull()) {
            return false;
        }
        elements[++top] = action;
        return true;
    }

    public RestockAction pop() {
        if (isEmpty()) {
            return null;
        }
        RestockAction removed = elements[top];
        elements[top] = null;
        top--;
        return removed;
    }

    public RestockAction peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[top];
    }

    public RestockAction get(int indexFromTop) {
        if (indexFromTop < 0 || indexFromTop > top) {
            return null;
        }
        return elements[top - indexFromTop];
    }

    public int size() {
        return top + 1;
    }

    public int capacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == capacity - 1;
    }

    public RestockAction[] toArray() {
        int currentSize = size();
        RestockAction[] snapshot = new RestockAction[currentSize];
        for (int i = 0; i < currentSize; i++) {
            snapshot[i] = get(i);
        }
        return snapshot;
    }

    public void clear() {
        for (int i = 0; i <= top; i++) {
            elements[i] = null;
        }
        top = -1;
    }
}
