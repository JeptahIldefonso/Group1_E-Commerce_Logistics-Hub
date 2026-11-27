package com.group1.logistics.sorting;

import com.group1.logistics.models.FreightPackage;

public class InsertionSort {
    public static void sort(FreightPackage[] arr, int count, SortCriteria c, boolean asc) {
        if (arr == null || count <= 1 || c == null) {
            return;
        }

        for (int i = 1; i < count; i++) {
            FreightPackage key = arr[i];
            int j = i - 1;

            if (asc) {
                while (j >= 0 && c.compare(arr[j], key) > 0) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            } else {
                while (j >= 0 && c.compare(arr[j], key) < 0) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            }
            arr[j + 1] = key;
        }
    }
}
