package com.group1.logistics;

import com.group1.logistics.datastructures.InventoryBST;
import com.group1.logistics.models.Product;

public class BSTTest {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   MODULE 2 UNIT TESTS: InventoryBST & 3-Case Deletion    ");
        System.out.println("==========================================================");

        InventoryBST bst = new InventoryBST();
        assert bst.isEmpty() || bst.size() == 0 : "Initial BST size should be 0";

        assert bst.insert(new Product(50, "Root Product", 100.0, 10));
        assert bst.insert(new Product(30, "P30", 50.0, 15));
        assert bst.insert(new Product(70, "P70", 150.0, 20));
        assert bst.insert(new Product(20, "P20", 25.0, 5));
        assert bst.insert(new Product(40, "P40", 45.0, 8));
        assert bst.insert(new Product(60, "P60", 65.0, 12));
        assert bst.insert(new Product(80, "P80", 85.0, 18));

        assert bst.size() == 7 : "Size should be 7";
        assert bst.height() == 3 : "Height of balanced 7-node tree should be 3";

        assert !bst.insert(new Product(50, "Duplicate", 99.0, 1)) : "Duplicate SKU should be rejected";
        assert bst.size() == 7 : "Size should remain 7 after rejected duplicate";

        Product found = bst.search(40);
        assert found != null && found.getSkuId() == 40 && found.getName().equals("P40") : "Search for 40 failed";
        assert bst.search(999) == null : "Search for non-existent SKU should return null";

        Product[] inOrderArr = bst.inOrder();
        assert inOrderArr.length == 7 : "inOrder array length should equal size (7)";
        int[] expectedKeys = {20, 30, 40, 50, 60, 70, 80};
        for (int i = 0; i < expectedKeys.length; i++) {
            assert inOrderArr[i].getSkuId() == expectedKeys[i] : "inOrder mismatch at index " + i;
        }
        System.out.println("  -> Recursive insert, search, and inOrder traversal: PASS");

        assert bst.delete(20) : "Delete leaf 20 should succeed";
        assert bst.size() == 6 : "Size should be 6 after deleting leaf";
        assert bst.search(20) == null : "20 should no longer exist";
        assert bst.inOrder().length == 6 : "inOrder length should match size 6";
        System.out.println("  -> Deletion Case 1 (Leaf Node): PASS");

        assert bst.delete(40) : "Delete 40 should succeed";
        assert bst.insert(new Product(25, "P25", 30.0, 4));
        assert bst.delete(30) : "Delete node with 1 child (30) should succeed";
        assert bst.size() == 5 : "Size should be 5";
        assert bst.search(30) == null;
        assert bst.search(25) != null : "Child 25 should still exist";
        System.out.println("  -> Deletion Case 2 (Node with 1 Child): PASS");

        int prevSize = bst.size();
        assert bst.delete(50) : "Delete root 50 (2 children) should succeed";
        assert bst.size() == prevSize - 1 : "CRITICAL: Size must decrement exactly once (from " + prevSize + " to " + (prevSize - 1) + ", but got " + bst.size() + ")";
        assert bst.search(50) == null : "50 should be deleted";

        Product[] remaining = bst.inOrder();
        assert remaining.length == bst.size() : "inOrder length must match bst.size() (Trap 3 guard)";
        for (int i = 0; i < remaining.length - 1; i++) {
            assert remaining[i].getSkuId() < remaining[i + 1].getSkuId() : "BST ordering corrupted";
        }
        System.out.println("  -> Deletion Case 3 (2 Children & Double-Decrement Guard): PASS");

        System.out.println("\n>>> ALL BST TESTS PASSED!\n");
    }
}
