package com.group1.logistics.datastructures;

import com.group1.logistics.models.FreightPackage;

/**
 * Custom primitive resizable dynamic array for FreightPackage.
 * Replaces java.util.ArrayList to comply with strict zero-collections rule.
 * Manually doubles capacity whenever full (initial capacity 16).
 */
public class FreightPackageArray {
    private FreightPackage[] data;
    private int count;

    public FreightPackageArray() {
        this(16);
    }

    public FreightPackageArray(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = 16;
        }
        this.data = new FreightPackage[initialCapacity];
        this.count = 0;
    }

    /**
     * Appends a FreightPackage to the dynamic array, manually doubling capacity if full.
     * @param pkg the package to add.
     */
    public void add(FreightPackage pkg) {
        if (pkg == null) {
            return;
        }
        if (count == data.length) {
            ensureCapacity();
        }
        data[count++] = pkg;
    }

    private void ensureCapacity() {
        int newCapacity = data.length * 2;
        FreightPackage[] newData = new FreightPackage[newCapacity];
        for (int i = 0; i < count; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    public FreightPackage get(int index) {
        if (index < 0 || index >= count) {
            return null;
        }
        return data[index];
    }

    public int size() {
        return count;
    }

    /**
     * Returns the internal backing array for in-place sorting over active range 0 .. count - 1.
     */
    public FreightPackage[] rawArray() {
        return data;
    }

    /**
     * Returns a compacted primitive array containing only active elements (length == count).
     */
    public FreightPackage[] toArray() {
        FreightPackage[] result = new FreightPackage[count];
        for (int i = 0; i < count; i++) {
            result[i] = data[i];
        }
        return result;
    }

    public void clear() {
        for (int i = 0; i < count; i++) {
            data[i] = null;
        }
        count = 0;
    }
}
