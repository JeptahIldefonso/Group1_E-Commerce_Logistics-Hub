package com.group1.logistics.sorting;

import com.group1.logistics.models.FreightPackage;

@FunctionalInterface
public interface SortCriteria {
    int compare(FreightPackage a, FreightPackage b);
}
