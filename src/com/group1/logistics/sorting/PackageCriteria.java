package com.group1.logistics.sorting;

import com.group1.logistics.models.FreightPackage;

public class PackageCriteria {
    public static final SortCriteria BY_WEIGHT = (a, b) -> {
        int diff = Double.compare(a.getWeightKg(), b.getWeightKg());
        if (diff != 0) {
            return diff;
        }
        return Integer.compare(a.getSequenceNo(), b.getSequenceNo());
    };

    public static final SortCriteria BY_PRIORITY = (a, b) -> {
        return Integer.compare(a.getDeliveryPriority(), b.getDeliveryPriority());
    };

    public static final SortCriteria BY_ID = (a, b) -> {
        return Integer.compare(a.getPackageId(), b.getPackageId());
    };

    public static final SortCriteria BY_DESTINATION = (a, b) -> {
        return a.getDestination().compareToIgnoreCase(b.getDestination());
    };
}
