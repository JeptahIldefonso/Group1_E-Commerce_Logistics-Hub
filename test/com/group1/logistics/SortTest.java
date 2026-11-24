package com.group1.logistics;

import com.group1.logistics.models.FreightPackage;
import com.group1.logistics.sorting.InsertionSort;
import com.group1.logistics.sorting.PackageCriteria;
import com.group1.logistics.sorting.QuickSort;

public class SortTest {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  MODULE 3 UNIT TESTS: QuickSort & InsertionSort          ");
        System.out.println("==========================================================");

        testQuickSort();
        testQuickSortEqualWeightsStress();
        testInsertionSortStability();

        System.out.println("\n>>> ALL SORTING ENGINE TESTS PASSED!\n");
    }

    private static void testQuickSort() {
        System.out.println("\n[Testing QuickSort by Weight]");
        FreightPackage[] arr = new FreightPackage[5];
        arr[0] = new FreightPackage(1, 101, "Manila", 12.5, 3, 1);
        arr[1] = new FreightPackage(2, 102, "Quezon", 2.1, 1, 2);
        arr[2] = new FreightPackage(3, 103, "Makati", 45.0, 2, 3);
        arr[3] = new FreightPackage(4, 104, "Pasig", 5.0, 4, 4);
        arr[4] = new FreightPackage(5, 105, "Taguig", 1.2, 1, 5);

        // Ascending sort
        QuickSort.sort(arr, 5, PackageCriteria.BY_WEIGHT, true);
        assert arr[0].getWeightKg() == 1.2 : "Lightest package should be first";
        assert arr[4].getWeightKg() == 45.0 : "Heaviest package should be last";
        for (int i = 0; i < 4; i++) {
            assert arr[i].getWeightKg() <= arr[i + 1].getWeightKg() : "Array not sorted ascending";
        }

        // Descending sort
        QuickSort.sort(arr, 5, PackageCriteria.BY_WEIGHT, false);
        assert arr[0].getWeightKg() == 45.0 : "Heaviest should be first descending";
        assert arr[4].getWeightKg() == 1.2 : "Lightest should be last descending";
        for (int i = 0; i < 4; i++) {
            assert arr[i].getWeightKg() >= arr[i + 1].getWeightKg() : "Array not sorted descending";
        }
        System.out.println("  -> QuickSort ascending and descending: PASS");
    }

    private static void testQuickSortEqualWeightsStress() {
        System.out.println("\n[Testing QuickSort Stack-Overflow Guard on 20,000 Equal Weights (Trap 1)]");
        int n = 20000;
        FreightPackage[] massiveArr = new FreightPackage[n];
        for (int i = 0; i < n; i++) {
            massiveArr[i] = new FreightPackage(i + 1, 1000 + i, "Hub", 5.0, 1, i + 1);
        }

        // In naive Lomuto partitioning, this would crash the JVM with StackOverflowError!
        // With Hoare partitioning and tail-call elimination, this completes in depth < 16 frames!
        QuickSort.sort(massiveArr, n, PackageCriteria.BY_WEIGHT, true);
        assert massiveArr[0].getWeightKg() == 5.0;
        assert massiveArr[n - 1].getWeightKg() == 5.0;
        System.out.println("  -> 20,000 identical items sorted without StackOverflowError (Hoare fix): PASS");
    }

    private static void testInsertionSortStability() {
        System.out.println("\n[Testing InsertionSort Stability on Delivery Priority (Trap 2)]");
        // Four packages arriving in order:
        // PKG-1 (Priority 1, Seq 1)
        // PKG-2 (Priority 2, Seq 2)
        // PKG-3 (Priority 1, Seq 3)
        // PKG-4 (Priority 2, Seq 4)
        FreightPackage[] arr = new FreightPackage[4];
        arr[0] = new FreightPackage(1, 101, "A", 1.0, 1, 1);
        arr[1] = new FreightPackage(2, 102, "B", 1.0, 2, 2);
        arr[2] = new FreightPackage(3, 103, "C", 1.0, 1, 3);
        arr[3] = new FreightPackage(4, 104, "D", 1.0, 2, 4);

        // Sort ascending by priority
        InsertionSort.sort(arr, 4, PackageCriteria.BY_PRIORITY, true);
        // Expected:
        // Index 0: PKG-1 (Prio 1, Seq 1)
        // Index 1: PKG-3 (Prio 1, Seq 3)  <-- Must preserve arrival order!
        // Index 2: PKG-2 (Prio 2, Seq 2)
        // Index 3: PKG-4 (Prio 2, Seq 4)  <-- Must preserve arrival order!
        assert arr[0].getPackageId() == 1 && arr[0].getSequenceNo() == 1;
        assert arr[1].getPackageId() == 3 && arr[1].getSequenceNo() == 3 : "Stability broken for Priority 1!";
        assert arr[2].getPackageId() == 2 && arr[2].getSequenceNo() == 2;
        assert arr[3].getPackageId() == 4 && arr[3].getSequenceNo() == 4 : "Stability broken for Priority 2!";
        System.out.println("  -> InsertionSort stability check (FIFO arrival order preserved): PASS");
    }
}
