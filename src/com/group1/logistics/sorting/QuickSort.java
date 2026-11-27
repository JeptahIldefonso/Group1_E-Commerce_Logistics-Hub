package com.group1.logistics.sorting;

import com.group1.logistics.models.FreightPackage;

public class QuickSort {
    public static void sort(FreightPackage[] arr, int count, SortCriteria c, boolean asc) {
        if (arr == null || count <= 1 || c == null) {
            return;
        }
        quickSortHelper(arr, 0, count - 1, c, asc);
    }

    private static void quickSortHelper(FreightPackage[] arr, int low, int high, SortCriteria c, boolean asc) {
        while (low < high) {
            int p = partition(arr, low, high, c, asc);
            if (p - low < high - p) {
                quickSortHelper(arr, low, p, c, asc);
                low = p + 1;
            } else {
                quickSortHelper(arr, p + 1, high, c, asc);
                high = p;
            }
        }
    }

    private static int partition(FreightPackage[] arr, int low, int high, SortCriteria c, boolean asc) {
        int mid = medianOfThree(arr, low, high, c, asc);
        FreightPackage pivot = arr[mid];

        int i = low - 1;
        int j = high + 1;

        while (true) {
            do {
                i++;
            } while (comparePackages(arr[i], pivot, c, asc) < 0);

            do {
                j--;
            } while (comparePackages(arr[j], pivot, c, asc) > 0);

            if (i >= j) {
                return j;
            }

            swap(arr, i, j);
        }
    }

    private static int medianOfThree(FreightPackage[] arr, int low, int high, SortCriteria c, boolean asc) {
        int mid = low + (high - low) / 2;
        if (comparePackages(arr[mid], arr[low], c, asc) < 0) {
            swap(arr, low, mid);
        }
        if (comparePackages(arr[high], arr[low], c, asc) < 0) {
            swap(arr, low, high);
        }
        if (comparePackages(arr[high], arr[mid], c, asc) < 0) {
            swap(arr, mid, high);
        }
        return mid;
    }

    private static int comparePackages(FreightPackage a, FreightPackage b, SortCriteria c, boolean asc) {
        if (c == PackageCriteria.BY_WEIGHT) {
            int diff = Double.compare(a.getWeightKg(), b.getWeightKg());
            if (diff != 0) {
                return asc ? diff : -diff;
            }
            return Integer.compare(a.getSequenceNo(), b.getSequenceNo());
        }
        int result = c.compare(a, b);
        return asc ? result : -result;
    }

    private static void swap(FreightPackage[] arr, int i, int j) {
        FreightPackage temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
