package com.group1.logistics.datastructures;

import com.group1.logistics.models.Product;

/**
 * Node structure for InventoryBST.
 * Wraps Product record and contains explicit left and right child pointers.
 * Keeping tree pointers inside SKUNode prevents them from leaking into the UI layer.
 */
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
