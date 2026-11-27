package com.group1.logistics.datastructures;

import com.group1.logistics.models.Product;

/**
 * Hierarchical indexing engine using a custom Binary Search Tree (BST).
 * Complies with strict project constraints: Zero Java Collections Framework.
 * Key: numeric skuId. Invariant: left.skuId < root.skuId < right.skuId (BR-01).
 * Features recursive insert, search, inOrder traversal, and 3-case node deletion
 * with the Double-Decrement Guard (Trap 3).
 */
public class InventoryBST {
    private SKUNode root;
    private int size;

    public InventoryBST() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Recursively inserts a product into the BST in a single traversal.
     * Enforces unique SKU keys (BR-01) and maintains left < root < right invariant.
     * @param product the Product to insert.
     * @return true if inserted, false if product is null, skuId <= 0, or skuId already exists.
     */
    public boolean insert(Product product) {
        if (product == null || product.getSkuId() <= 0) {
            return false;
        }
        boolean[] inserted = { false };
        root = insertRecursive(root, product, inserted);
        if (inserted[0]) {
            size++;
        }
        return inserted[0];
    }

    private SKUNode insertRecursive(SKUNode current, Product product, boolean[] inserted) {
        if (current == null) {
            inserted[0] = true;
            return new SKUNode(product);
        }
        int targetId = product.getSkuId();
        int currentId = current.product.getSkuId();
        if (targetId < currentId) {
            current.left = insertRecursive(current.left, product, inserted);
        } else if (targetId > currentId) {
            current.right = insertRecursive(current.right, product, inserted);
        }
        // If targetId == currentId, duplicate SKU rejected (inserted[0] stays false)
        return current;
    }

    /**
     * Iteratively searches for a product by its numeric SKU ID in O(log n) average time
     * and O(1) stack space.
     * @param skuId the numeric ID to find.
     * @return the Product record, or null if not found.
     */
    public Product search(int skuId) {
        if (skuId <= 0) {
            return null;
        }
        SKUNode current = root;
        while (current != null) {
            int currentId = current.product.getSkuId();
            if (skuId == currentId) {
                return current.product;
            }
            current = (skuId < currentId) ? current.left : current.right;
        }
        return null;
    }

    /**
     * Deletes a product from the BST by SKU ID handling all 3 deletion cases:
     * Case 1: Leaf node (0 children).
     * Case 2: Node with 1 child.
     * Case 3: Node with 2 children (In-Order Successor substitution).
     * Guaranteed to decrement size exactly once per deleted record (Double-Decrement Guard).
     * @param skuId the SKU ID to delete.
     * @return true if deleted, false if not found or invalid.
     */
    public boolean delete(int skuId) {
        if (skuId <= 0 || root == null) {
            return false;
        }
        boolean[] deleted = { false };
        root = deleteRecursive(root, skuId, deleted);
        if (deleted[0]) {
            size--;
        }
        return deleted[0];
    }

    private SKUNode deleteRecursive(SKUNode current, int skuId, boolean[] deleted) {
        if (current == null) {
            return null;
        }
        if (skuId < current.product.getSkuId()) {
            current.left = deleteRecursive(current.left, skuId, deleted);
            return current;
        } else if (skuId > current.product.getSkuId()) {
            current.right = deleteRecursive(current.right, skuId, deleted);
            return current;
        } else {
            // Found target node
            deleted[0] = true;

            // Case 1: Leaf node
            if (current.left == null && current.right == null) {
                return null;
            }
            // Case 2: One child (left is null)
            if (current.left == null) {
                return current.right;
            }
            // Case 2: One child (right is null)
            if (current.right == null) {
                return current.left;
            }

            // Case 3: Two children
            // Find In-Order Successor (smallest node in the right subtree)
            SKUNode successor = findMin(current.right);
            // Copy successor's product data into current node
            current.product = successor.product;
            // Delete successor from right subtree using a dummy flag to prevent double-decrement
            boolean[] dummy = { false };
            current.right = deleteRecursive(current.right, successor.product.getSkuId(), dummy);
            return current;
        }
    }

    private SKUNode findMin(SKUNode node) {
        SKUNode curr = node;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr;
    }

    /**
     * In-Order Traversal (Left -> Root -> Right).
     * Extracts all products into a primitive Product[] strictly ascending by skuId.
     * @return Product[size] in sorted key order.
     */
    public Product[] inOrder() {
        Product[] result = new Product[size];
        int[] index = { 0 };
        inOrderRecursive(root, result, index);
        return result;
    }

    private void inOrderRecursive(SKUNode node, Product[] result, int[] index) {
        if (node != null) {
            inOrderRecursive(node.left, result, index);
            if (index[0] < result.length) {
                result[index[0]++] = node.product;
            }
            inOrderRecursive(node.right, result, index);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int height() {
        return calculateHeight(root);
    }

    private int calculateHeight(SKUNode node) {
        if (node == null) {
            return 0;
        }
        int leftH = calculateHeight(node.left);
        int rightH = calculateHeight(node.right);
        return 1 + (leftH > rightH ? leftH : rightH);
    }

    public void clear() {
        root = null;
        size = 0;
    }
}
