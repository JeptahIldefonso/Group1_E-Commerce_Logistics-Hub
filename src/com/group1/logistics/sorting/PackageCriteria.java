package com.group1.logistics.sorting;

import com.group1.logistics.models.FreightPackage;

/**
 * Predefined sorting criteria for FreightPackage.
 */
public class PackageCriteria {

    /**
     * Orders packages by weight (kg).
     * Breaks ties using sequenceNo so order is deterministic.
     */
    public static final SortCriteria BY_WEIGHT = (a, b) -> {
        int diff = Double.compare(a.getWeightKg(), b.getWeightKg());
        if (diff != 0) {
            return diff;
        }
        return Integer.compare(a.getSequenceNo(), b.getSequenceNo());
    };

    /**
     * Orders packages strictly by delivery priority (1 = Express, 5 = Bulk).
     * Does NOT bake in sequenceNo so InsertionSort stability preserves FIFO arrival order naturally.
     */
    public static final SortCriteria BY_PRIORITY = (a, b) -> {
        return Integer.compare(a.getDeliveryPriority(), b.getDeliveryPriority());
    };

    /**
     * Orders packages by numeric packageId.
     */
    public static final SortCriteria BY_ID = (a, b) -> {
        return Integer.compare(a.getPackageId(), b.getPackageId());
    };

    /**
     * Orders packages alphabetically by destination hub.
     */
    public static final SortCriteria BY_DESTINATION = (a, b) -> {
        return a.getDestination().compareToIgnoreCase(b.getDestination());
    };
}
