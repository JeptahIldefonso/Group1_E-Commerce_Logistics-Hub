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

        OpResult dupRes = service.addProduct(500, "Duplicate Product", 999.0, 10);
        assert !dupRes.isSuccess() : "BR-01: Adding duplicate SKU 500 should fail";
        System.out.println("  -> BR-01 (Duplicate SKU rejected): PASS");

        OpResult badOrder = service.enqueueOrder("Test Customer", 9999, 1, "Manila", 1);
        assert !badOrder.isSuccess() : "BR-02: Order for non-existent SKU should fail";
        System.out.println("  -> BR-02 (Non-existent SKU order rejected): PASS");

        service.getOrderQueue().clear();
        service.addProduct(900, "Dispatch Item", 100.0, 5);
        service.enqueueOrder("Juan", 900, 3, "Manila", 1);

        OpResult dispOk = service.dispatchNext(1.50);
        assert dispOk.isSuccess() : "Dispatch should succeed";
        Product p900 = service.findProduct(900);
        assert p900.getStockQty() == 2 : "Stock after dispatching 3 units should be 2";
        System.out.println("  -> BR-03 (Peek -> Validate -> Commit dispatch): PASS");

        service.enqueueOrder("Maria", 900, 10, "Quezon", 2);
        OpResult oosRes = service.dispatchNext(2.0);
        assert !oosRes.isSuccess() : "Dispatch should fail due to insufficient stock";
        assert service.getOrderQueue().peek().getCustomerName().equals("Maria") : "Order must REMAIN in queue!";
        assert service.findProduct(900).getStockQty() == 2 : "Stock must remain unchanged";
        System.out.println("  -> BR-03/BR-04 (Insufficient stock keeps order queued without stock loss): PASS");

        OpResult restockRes = service.restock(900, 50);
        assert restockRes.isSuccess();
        assert service.findProduct(900).getStockQty() == 52;

        service.getOrderQueue().clear();
        service.enqueueOrder("Buyer", 900, 5, "Makati", 1);
        service.dispatchNext(1.0);
        assert service.findProduct(900).getStockQty() == 47;

        OpResult undoBlocked = service.undoRestock();
        assert !undoBlocked.isSuccess() : "Undo must be REFUSED because units have already been shipped!";
        assert service.findProduct(900).getStockQty() == 47 : "Stock must remain 47";
        System.out.println("  -> BR-06 (Anti-Lost-Update: Undo blocked when shipped): PASS");

        service.findProduct(900).addStock(10);
        OpResult undoOk = service.undoRestock();
        assert undoOk.isSuccess() : "Undo should succeed now that stock >= 50";
        assert service.findProduct(900).getStockQty() == 7 : "57 - 50 = 7 (Correct subtraction rollback!)";
        System.out.println("  -> BR-06 (Safe Subtraction Undo): PASS");

        int bstSizeBefore = service.getInventoryBst().size();
        service.sortPackages(1, 1, true);
        service.sortPackages(2, 2, true);
        assert service.getInventoryBst().size() == bstSizeBefore : "Sorting packages must not affect BST!";
        System.out.println("  -> BR-09 (Sorting packages does not mutate BST): PASS");

        System.out.println("\n>>> ALL BUSINESS SERVICE TESTS PASSED!\n");
    }
}
