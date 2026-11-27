package com.group1.logistics.datastructures;

import com.group1.logistics.models.Product;

public class InventoryBST {
    private SKUNode root;
    private int size;

    public InventoryBST() {
        this.root = null;
        this.size = 0;
    }

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
        return current;
    }

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
            deleted[0] = true;

            if (current.left == null && current.right == null) {
                return null;
            }
            if (current.left == null) {
                return current.right;
            }
            if (current.right == null) {
                return current.left;
            }

            SKUNode successor = findMin(current.right);
            current.product = successor.product;
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
