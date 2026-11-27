package com.group1.logistics.datastructures;

import com.group1.logistics.models.Product;

public class SKUNode {
    public Product product;
    public SKUNode left;
    public SKUNode right;

    public SKUNode(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        this.product = product;
        this.left = null;
        this.right = null;
    }
}
