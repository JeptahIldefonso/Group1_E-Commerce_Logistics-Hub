package com.group1.logistics.sorting;

import com.group1.logistics.models.FreightPackage;

/**
 * Custom comparison functional interface.
 * Replaces java.util.Comparator to comply with strict zero-collections rule.
 */
@FunctionalInterface
public interface SortCriteria {
    /**
     * Compares two FreightPackage objects.
     * @param a the first package.
     * @param b the second package.
     * @return negative if a < b, zero if a == b, positive if a > b.
     */
    int compare(FreightPackage a, FreightPackage b);
}
