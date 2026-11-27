package com.group1.logistics.datastructures;

import com.group1.logistics.models.FreightPackage;

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
        System.arraycopy(data, 0, newData, 0, count);
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

    public FreightPackage[] rawArray() {
        return data;
    }

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
