package com.group1.logistics.sorting;

import com.group1.logistics.models.FreightPackage;

/**
 * Custom InsertionSort implementation.
 * Guarantees stability (TC-SO07 and TC-SO12) by shifting strictly on inequality.
 * Equal-priority packages strictly preserve their natural FIFO arrival order.
 * Operates strictly on active elements 0 .. count - 1.
 */
public class InsertionSort {

    /**
     * Sorts the freight package array in-place using stable insertion sort.
     * @param arr the array to sort.
     * @param count the number of active elements in the array.
     * @param c the comparison criteria.
     * @param asc true for ascending, false for descending.
     */
    public static void sort(FreightPackage[] arr, int count, SortCriteria c, boolean asc) {
        if (arr == null || count <= 1 || c == null) {
            return;
        }

        for (int i = 1; i < count; i++) {
            FreightPackage key = arr[i];
            int j = i - 1;

            if (asc) {
                // Strict inequality (> 0) ensures stability for ascending sort
                while (j >= 0 && c.compare(arr[j], key) > 0) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            } else {
                // Strict inequality (< 0) ensures stability for descending sort
                while (j >= 0 && c.compare(arr[j], key) < 0) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            }
            arr[j + 1] = key;
        }
    }
}
