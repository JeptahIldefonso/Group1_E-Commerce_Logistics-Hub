package com.group1.logistics.datastructures;

import com.group1.logistics.models.RestockAction;

/**
 * Concrete primitive array stack (LIFO).
 * Complies with strict project constraints: Zero Java Collections Framework.
 * Used for tracking restock history and executing the Safe Undo feature (BR-06).
 */
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
        this.top = -1; // -1 represents empty stack
    }

    /**
     * Pushes a restock action onto the stack (LIFO).
     * @param action the RestockAction to push.
     * @return true if pushed, false if null or stack is full.
     */
    public boolean push(RestockAction action) {
        if (action == null || isFull()) {
            return false;
        }
        elements[++top] = action;
        return true;
    }

    /**
     * Pops and returns the most recent restock action from the top of the stack.
     * Sets vacated array slot to null.
     * @return the popped action, or null if empty.
     */
    public RestockAction pop() {
        if (isEmpty()) {
            return null;
        }
        RestockAction removed = elements[top];
        elements[top] = null; // Clean up reference
        top--;
        return removed;
    }

    /**
     * Returns the top restock action without removing it.
     */
    public RestockAction peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[top];
    }

    /**
     * Returns the restock action at indexFromTop (0 = newest/top, size-1 = oldest).
     * Used so audit tables show most recent restock at the top.
     */
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

    /**
     * Returns a snapshot array ordered from newest (top) to oldest.
     */
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
