package com.group1.logistics;

import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

public class ServiceTest {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  BUSINESS SERVICE TESTS: FulfillmentService (BR-01..BR-09)");
        System.out.println("==========================================================");

        FulfillmentService service = new FulfillmentService();

        // BR-01: Duplicate SKU rejected
        OpResult dupRes = service.addProduct(500, "Duplicate Product", 999.0, 10);
        assert !dupRes.isSuccess() : "BR-01: Adding duplicate SKU 500 should fail";
        System.out.println("  -> BR-01 (Duplicate SKU rejected): PASS");

        // BR-02: Non-existent SKU order rejected
        OpResult badOrder = service.enqueueOrder("Test Customer", 9999, 1, "Manila", 1);
        assert !badOrder.isSuccess() : "BR-02: Order for non-existent SKU should fail";
        System.out.println("  -> BR-02 (Non-existent SKU order rejected): PASS");

        // BR-03: Dispatch peek -> validate -> commit
        // Clear order queue and test explicit dispatch flow
        service.getOrderQueue().clear();
        service.addProduct(900, "Dispatch Item", 100.0, 5); // Stock = 5
        service.enqueueOrder("Juan", 900, 3, "Manila", 1);

        // Dispatch order of 3 (Stock becomes 2)
        OpResult dispOk = service.dispatchNext(1.50);
        assert dispOk.isSuccess() : "Dispatch should succeed";
        Product p900 = service.findProduct(900);
        assert p900.getStockQty() == 2 : "Stock after dispatching 3 units should be 2";
        System.out.println("  -> BR-03 (Peek -> Validate -> Commit dispatch): PASS");

        // BR-03 & BR-04: Insufficient stock blocks dispatch without dropping order from queue!
        service.enqueueOrder("Maria", 900, 10, "Quezon", 2); // Requests 10, but stock is 2
        OpResult oosRes = service.dispatchNext(2.0);
        assert !oosRes.isSuccess() : "Dispatch should fail due to insufficient stock";
        assert service.getOrderQueue().peek().getCustomerName().equals("Maria") : "Order must REMAIN in queue!";
        assert service.findProduct(900).getStockQty() == 2 : "Stock must remain unchanged";
        System.out.println("  -> BR-03/BR-04 (Insufficient stock keeps order queued without stock loss): PASS");

        // BR-05 & BR-06: Atomic Restock and Safe Undo (Anti-Lost-Update Bug)
        // SKU 900 stock is currently 2.
        // Restock +50 (Stock becomes 52).
        OpResult restockRes = service.restock(900, 50);
        assert restockRes.isSuccess();
        assert service.findProduct(900).getStockQty() == 52;

        // Now dispatch 5 units (Stock becomes 47).
        service.getOrderQueue().clear();
        service.enqueueOrder("Buyer", 900, 5, "Makati", 1);
        service.dispatchNext(1.0);
        assert service.findProduct(900).getStockQty() == 47;

        // CRITICAL TEST: Undo Restock!
        // In naive systems, restoring "previous stock" (2) would erase the 5 units just sold!
        // In our safe system, Undo subtracts 50 from 47. But wait: 47 < 50! (Units already shipped!)
        // So Undo MUST be refused!
        OpResult undoBlocked = service.undoRestock();
        assert !undoBlocked.isSuccess() : "Undo must be REFUSED because units have already been shipped!";
        assert service.findProduct(900).getStockQty() == 47 : "Stock must remain 47";
        System.out.println("  -> BR-06 (Anti-Lost-Update: Undo blocked when shipped): PASS");

        // Now add +10 stock so total is 57 (which is >= 50). Now Undo should succeed!
        service.findProduct(900).addStock(10); // Stock = 57
        OpResult undoOk = service.undoRestock();
        assert undoOk.isSuccess() : "Undo should succeed now that stock >= 50";
        assert service.findProduct(900).getStockQty() == 7 : "57 - 50 = 7 (Correct subtraction rollback!)";
        System.out.println("  -> BR-06 (Safe Subtraction Undo): PASS");

        // BR-09: Sorting packages never mutates BST
        int bstSizeBefore = service.getInventoryBst().size();
        service.sortPackages(1, 1, true); // QuickSort by weight
        service.sortPackages(2, 2, true); // InsertionSort by priority
        assert service.getInventoryBst().size() == bstSizeBefore : "Sorting packages must not affect BST!";
        System.out.println("  -> BR-09 (Sorting packages does not mutate BST): PASS");

        System.out.println("\n>>> ALL BUSINESS SERVICE TESTS PASSED!\n");
    }
}
