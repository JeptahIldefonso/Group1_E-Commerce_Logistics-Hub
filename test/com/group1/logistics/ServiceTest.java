package com.group1.logistics;

import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.FulfillmentStorage;
import com.group1.logistics.service.OpResult;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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

        OpResult overOrder = service.enqueueOrder("Maria", 900, 10, "Quezon", 2);
        assert !overOrder.isSuccess() : "Order exceeding available stock must be rejected";

        service.enqueueOrder("User1", 800, 8, "Manila", 1);
        OpResult doubleOrder = service.enqueueOrder("User2", 800, 8, "Manila", 1);
        assert !doubleOrder.isSuccess() : "Double-order exceeding stock on SKU 800 must be blocked";
        assert service.getReservedQtyInQueue(800) == 8 : "Reserved quantity on SKU 800 must stay 8";
        service.rejectFrontOrder();

        service.restock(900, 8);
        OpResult validMaria = service.enqueueOrder("Maria", 900, 10, "Quezon", 2);
        assert validMaria.isSuccess() : "Order matching restocked available stock should succeed";
        service.undoRestock();
        OpResult oosRes = service.dispatchNext(2.0);
        assert !oosRes.isSuccess() : "Dispatch should fail due to insufficient stock";
        assert service.getOrderQueue().peek().getCustomerName().equals("Maria") : "Order must REMAIN in queue!";
        assert service.findProduct(900).getStockQty() == 2 : "Stock must remain unchanged";
        System.out.println("  -> BR-03/BR-04 (Over-order & double-order blocked; insufficient dispatch keeps queue safe): PASS");

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

        testPersistenceAndBackupRecovery();
        System.out.println("  -> Safe atomic local save, BST pre-order height preservation, and .bak recovery: PASS");

        System.out.println("\n>>> ALL BUSINESS SERVICE TESTS PASSED!\n");
    }

    private static void testPersistenceAndBackupRecovery() {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("lhub_persist_test");
            File saveFile = new File(tempDir.toFile(), "logistics-data.txt");

            FulfillmentService firstRun = new FulfillmentService(saveFile);
            assert saveFile.exists() : "Initial save file must be written on first launch";

            assert firstRun.addProduct(950, "UPS Backup \\ Unit", 3299.50, 12).isSuccess();
            assert firstRun.enqueueOrder("Maria Reyes", 800, 3, "Manila Central Hub", 1).isSuccess();
            assert firstRun.dispatchNext(4.25).isSuccess();
            assert firstRun.enqueueOrder("Alex Santos", 800, 2, "Quezon City Hub", 3).isSuccess();
            assert firstRun.restock(800, 5).isSuccess();
            int expectedHeight = firstRun.getInventoryBst().height();
            int expectedSize = firstRun.getInventoryBst().size();
            int expectedPkgs = firstRun.getPackageArray().size();
            int expectedNextOrder = firstRun.getNextOrderId();

            FulfillmentService reloaded = new FulfillmentService(saveFile);
            assert reloaded.getInventoryBst().size() == expectedSize : "Product count must persist across restart";
            assert reloaded.getInventoryBst().height() == expectedHeight : "Pre-order save must preserve BST height";
            assert reloaded.findProduct(950).getName().equals("UPS Backup \\ Unit") : "Escaped name must round-trip cleanly";
            assert reloaded.findProduct(800).getStockQty() == 10 : "8 - 3 + 5 = 10 shelf stock must persist";
            assert reloaded.getAvailableStock(800) == 8 : "10 - 2 queued = 8 available must persist";
            assert reloaded.getOrderQueue().size() == 1 && reloaded.getOrderQueue().peek().getOrderId() == 1002 : "Queued order #1002 must persist";
            assert reloaded.getNextOrderId() == expectedNextOrder : "Order ID counter must persist";
            assert reloaded.getRestockStack().peek().getSkuId() == 800 : "LIFO restock top must persist";
            assert reloaded.getPackageArray().size() == expectedPkgs : "Shipments must persist";

            File bakFile = FulfillmentStorage.backupFileFor(saveFile);
            assert bakFile.exists() : "Rolling .bak backup must exist after mutations";

            try (FileOutputStream fos = new FileOutputStream(saveFile, false)) {
                fos.write("LHUB|V1\nMETA|CORRUPTED\n".getBytes(StandardCharsets.UTF_8));
            }
            FulfillmentService recovered = new FulfillmentService(saveFile);
            assert recovered.getStartupNotice() != null : "Recovery notice must be exposed after backup fallback";
            assert recovered.findProduct(950) != null : "Backup recovery must restore valid prior saved state";

            File[] quarantine = tempDir.toFile().listFiles((d, name) -> name.contains(".corrupt."));
            assert quarantine != null && quarantine.length >= 1 : "Corrupted primary file must be quarantined aside";

            assert recovered.resetToDefaultSeedData().isSuccess();
            FulfillmentService afterReset = new FulfillmentService(saveFile);
            assert afterReset.getInventoryBst().size() == 14 : "Reset must persist clean 14-item catalog";
            assert afterReset.getOrderQueue().size() == 0 : "Reset must clear pending order queue";
            assert afterReset.findProduct(800).getStockQty() == 8 : "Reset must restore SKU #800 to 8";
        } catch (Exception ex) {
            throw new AssertionError("Persistence test failed: " + ex.getMessage(), ex);
        } finally {
            if (tempDir != null) {
                File[] files = tempDir.toFile().listFiles();
                if (files != null) {
                    for (File f : files) {
                        f.delete();
                    }
                }
                tempDir.toFile().delete();
            }
        }
    }
}
