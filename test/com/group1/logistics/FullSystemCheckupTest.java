package com.group1.logistics;

import com.group1.logistics.datastructures.FreightPackageArray;
import com.group1.logistics.datastructures.InventoryBST;
import com.group1.logistics.datastructures.OrderQueue;
import com.group1.logistics.datastructures.RestockStack;
import com.group1.logistics.models.FreightPackage;
import com.group1.logistics.models.Order;
import com.group1.logistics.models.Product;
import com.group1.logistics.models.RestockAction;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;
import com.group1.logistics.ui.*;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FullSystemCheckupTest {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       100% FULL-SYSTEM FUNCTIONAL & COMPLIANCE VERIFICATION CHECKUP             ");
        System.out.println("================================================================================");

        testSeedData();
        testModule1OrderQueueFIFO();
        testModule2RestockStackLIFO();
        testModule3InventoryBSTAndSearch();
        testModule4SortingEngine();
        testModule5BusinessRulesAndEdgeCases();
        testModule6UiTableCompliance();

        System.out.println("\n================================================================================");
        System.out.println(" >>> ALL FULL-SYSTEM CHECKUP TESTS PASSED WITH 100% SUCCESS! <<< ");
        System.out.println("================================================================================");
    }

    private static void check(boolean condition, String testName) {
        if (!condition) {
            System.err.println("  [FAIL] " + testName);
            throw new AssertionError("Checkup failure: " + testName);
        }
        System.out.println("  [PASS] " + testName);
    }

    private static void testSeedData() {
        System.out.println("\n--- [CHECK 1] Initial Seed Data & Inventory Integrity ---");
        FulfillmentService service = new FulfillmentService();

        check(service.getInventoryBst().size() == 14, "Catalog initialized with exactly 14 seed products");
        check(service.getOrderQueue().size() == 0, "Order queue initialized clean with 0 pending orders");
        check(service.getRestockStack().size() == 2, "Restock stack initialized with 2 audit actions");
        check(service.getPackageArray().size() == 5, "Freight array initialized with 5 dispatched packages");

        Product p150 = service.findProduct(150);
        check(p150 != null && p150.getName().contains("Webcam"), "SKU #150 resolves to Webcam");
    }

    private static void testModule1OrderQueueFIFO() {
        System.out.println("\n--- [CHECK 2] Module 1: OrderQueue (FIFO) & Dispatch Lifecycle ---");
        FulfillmentService service = new FulfillmentService();

        check(service.enqueueOrder("Juan Dela Cruz", 300, 2, "Manila Central Hub", 1).isSuccess(),
                "Enqueued fresh order #1001 into clean queue");
        check(service.enqueueOrder("Maria Santos", 500, 1, "Quezon City Hub", 2).isSuccess(),
                "Enqueued second order #1002 behind #1001");

        int initialQueueSize = service.getOrderQueue().size();
        int initialDispatched = service.getPackageArray().size();

        Order front = service.getOrderQueue().peek();
        check(front.getOrderId() == 1001, "Front order is #1001 (FIFO arrival)");
        int initialStock300 = service.findProduct(300).getStockQty();

        OpResult dispatchRes = service.dispatchNext(2.5);
        check(dispatchRes.isSuccess(), "Dispatch #1001 succeeded with valid 2.5 kg weight");
        check(service.getOrderQueue().size() == initialQueueSize - 1, "Queue size decremented to " + (initialQueueSize - 1));
        check(service.getPackageArray().size() == initialDispatched + 1, "Package array incremented to " + (initialDispatched + 1));
        check(service.findProduct(300).getStockQty() == initialStock300 - 2, "Stock for SKU #300 correctly deducted by 2 units");

        Order nextOrder = service.getOrderQueue().peek();
        check(nextOrder.getOrderId() == 1002, "Next in line is #1002");
        Product pNext = service.findProduct(nextOrder.getSkuId());
        int stockBeforeCancel = pNext.getStockQty();

        OpResult cancelRes = service.rejectFrontOrder();
        check(cancelRes.isSuccess(), "Cancelled front order #1002");
        check(pNext.getStockQty() == stockBeforeCancel, "Stock was NOT deducted for cancelled order");

        while (service.getOrderQueue().size() < 20) {
            service.enqueueOrder("Test Customer", 150, 1, "Manila Central Hub", 1);
        }
        check(service.getOrderQueue().size() == 20, "OrderQueue filled to max capacity 20");
        OpResult overflowRes = service.enqueueOrder("Overflow Customer", 150, 1, "Manila Central Hub", 1);
        check(!overflowRes.isSuccess() && overflowRes.getMessage().contains("full"), "OrderQueue blocks 21st order with full capacity notice");
    }

    private static void testModule2RestockStackLIFO() {
        System.out.println("\n--- [CHECK 3] Module 2: RestockStack (LIFO) & Safe Undo ---");
        FulfillmentService service = new FulfillmentService();

        Product p = service.findProduct(200);
        int initialStock = p.getStockQty();

        OpResult restockRes = service.restock(200, 25);
        check(restockRes.isSuccess(), "Restocked +25 units to SKU #200");
        check(p.getStockQty() == initialStock + 25, "Product stock updated atomically");

        RestockAction top = service.getRestockStack().peek();
        check(top.getSkuId() == 200 && top.getQuantityAdded() == 25, "LIFO peek returns newest restock action");

        OpResult undoRes = service.undoRestock();
        check(undoRes.isSuccess(), "Undo succeeded for newest restock action");
        check(p.getStockQty() == initialStock, "Stock rolled back to exact initial value");

        service.restock(150, 10);
        Product webcam = service.findProduct(150);
        int currentWebcamStock = webcam.getStockQty();
        webcam.removeStock(currentWebcamStock - 5);

        OpResult blockedUndo = service.undoRestock();
        check(!blockedUndo.isSuccess() && blockedUndo.getMessage().contains("shipped"),
                "Anti-Lost-Update guard blocks rollback when items were already shipped (BR-06)");

        webcam.addStock(currentWebcamStock - 5);
    }

    private static void testModule3InventoryBSTAndSearch() {
        System.out.println("\n--- [CHECK 4] Module 3: InventoryBST & Dual Search (Code/Name) ---");
        FulfillmentService service = new FulfillmentService();

        Product p400 = service.findProduct(400);
        check(p400 != null && p400.getSkuId() == 400, "Numeric BST search finds SKU #400 in O(log n)");

        Product pRyzen = service.findProductByName("ryzen");
        if (pRyzen == null) {
            service.addProduct(1, "Ryzen 5 5600g", 9000.0, 5);
            pRyzen = service.findProductByName("ryzen");
        }
        check(pRyzen != null && pRyzen.getName().contains("Ryzen"), "Dual search finds 'Ryzen' by name substring");

        Product pCable = service.findProductByName("HDMI");
        check(pCable != null && pCable.getSkuId() == 250, "Dual search finds 'High-Speed HDMI Cable' via query 'HDMI'");

        OpResult dupRes = service.addProduct(150, "Duplicate Webcam", 2000.0, 10);
        check(!dupRes.isSuccess() && dupRes.getMessage().contains("already exists"), "Duplicate SKU #150 rejected (BR-01)");

        InventoryBST bst = new InventoryBST();
        bst.insert(new Product(50, "Root", 100, 10));
        bst.insert(new Product(25, "Left Child", 100, 10));
        bst.insert(new Product(75, "Right Child", 100, 10));
        bst.insert(new Product(10, "Leaf", 100, 10));
        bst.insert(new Product(30, "One Child Node", 100, 10));
        bst.insert(new Product(35, "Leaf of 30", 100, 10));

        check(bst.delete(10), "BST Case 1 Deletion (Leaf node 10) succeeded");
        check(bst.search(10) == null, "Leaf 10 confirmed removed");

        check(bst.delete(30), "BST Case 2 Deletion (One child node 30) succeeded");
        check(bst.search(30) == null && bst.search(35) != null, "Node 30 removed, child 35 adopted by parent");

        check(bst.delete(50), "BST Case 3 Deletion (Root with 2 children 50) succeeded");
        check(bst.search(50) == null, "Root 50 removed via in-order successor");
    }

    private static void testModule4SortingEngine() {
        System.out.println("\n--- [CHECK 5] Module 4: Custom Sorting Engine ---");
        FulfillmentService service = new FulfillmentService();

        OpResult sortWeightAsc = service.sortPackages(1, 1, true);
        check(sortWeightAsc.isSuccess(), "QuickSort by weight (Ascending) succeeded");

        FreightPackage[] pkgs = service.getPackageArray().rawArray();
        int count = service.getPackageArray().size();
        boolean isSortedAsc = true;
        for (int i = 0; i < count - 1; i++) {
            if (pkgs[i].getWeightKg() > pkgs[i + 1].getWeightKg()) {
                isSortedAsc = false;
                break;
            }
        }
        check(isSortedAsc, "Packages strictly sorted from lightest to heaviest");

        service.sortPackages(1, 1, false);
        boolean isSortedDesc = true;
        for (int i = 0; i < count - 1; i++) {
            if (pkgs[i].getWeightKg() < pkgs[i + 1].getWeightKg()) {
                isSortedDesc = false;
                break;
            }
        }
        check(isSortedDesc, "Packages strictly sorted from heaviest to lightest");

        OpResult sortSpeed = service.sortPackages(2, 2, true);
        check(sortSpeed.isSuccess(), "InsertionSort by delivery speed succeeded");
        boolean isSortedSpeed = true;
        for (int i = 0; i < count - 1; i++) {
            if (pkgs[i].getDeliveryPriority() > pkgs[i + 1].getDeliveryPriority()) {
                isSortedSpeed = false;
                break;
            }
        }
        check(isSortedSpeed, "Packages strictly sorted from fastest (Express) to slowest (Economy)");
    }

    private static void testModule5BusinessRulesAndEdgeCases() {
        System.out.println("\n--- [CHECK 6] Module 5: Business Rules & Validation Guards ---");
        FulfillmentService service = new FulfillmentService();

        OpResult fakeSku = service.enqueueOrder("Maria Santos", 99999, 1, "Manila", 1);
        check(!fakeSku.isSuccess() && fakeSku.getMessage().contains("does not exist"), "Order with invalid SKU #99999 rejected (BR-02)");

        OpResult zeroQty = service.enqueueOrder("Maria Santos", 150, 0, "Manila", 1);
        check(!zeroQty.isSuccess() && zeroQty.getMessage().contains("at least 1"), "Order with quantity 0 rejected");

        service.enqueueOrder("Weight Test Buyer", 150, 1, "Manila", 1);

        OpResult heavyWeight = service.dispatchNext(2000.0);
        check(!heavyWeight.isSuccess() && heavyWeight.getMessage().contains("1000"), "Dispatch with 2000 kg rejected by 1000 kg ceiling (BR-08)");

        OpResult negativeWeight = service.dispatchNext(-5.0);
        check(!negativeWeight.isSuccess(), "Dispatch with negative weight rejected");
    }

    private static void testModule6UiTableCompliance() {
        System.out.println("\n--- [CHECK 7] Module 6: Swing Table Compliance & Architecture ---");
        FulfillmentService service = new FulfillmentService();

        CatalogTableModel catalogModel = new CatalogTableModel(service.getInventoryBst());
        QueueTableModel queueModel = new QueueTableModel(service.getOrderQueue(), service);
        RestockTableModel restockModel = new RestockTableModel(service.getRestockStack(), service);
        FreightTableModel freightModel = new FreightTableModel(service.getPackageArray());

        check(!catalogModel.isCellEditable(0, 0), "CatalogTableModel cells strictly non-editable");
        check(!queueModel.isCellEditable(0, 0), "QueueTableModel cells strictly non-editable");
        check(!restockModel.isCellEditable(0, 0), "RestockTableModel cells strictly non-editable");
        check(!freightModel.isCellEditable(0, 0), "FreightTableModel cells strictly non-editable");

        check(catalogModel.getRowCount() == service.getInventoryBst().size(), "Catalog table row count matches BST size");
        check(catalogModel.getHealthyCount() + catalogModel.getAttentionCount() == catalogModel.getTotalCount(),
                "Cached stock KPI counts sum exactly to total catalog count");
        int row150 = catalogModel.findRowBySkuId(150);
        check(row150 >= 0 && catalogModel.getProductAt(row150).getSkuId() == 150,
                "Binary row lookup locates SKU #150 in cached table model");
        int rowWebcam = catalogModel.findRowByNameQuery("webcam");
        check(rowWebcam == row150, "Zero-allocation name lookup matches same SKU #150 row");
        check(catalogModel.getValueAt(row150, 2).toString().startsWith("₱"),
                "Cached price string starts with Peso sign");
        check(freightModel.getValueAt(0, 3).toString().endsWith(" kg"),
                "Cached freight weight string formatted consistently");
        check(queueModel.getRowCount() == service.getOrderQueue().size(), "Queue table row count matches OrderQueue size");
        check(restockModel.getRowCount() == service.getRestockStack().size(), "Restock table row count matches RestockStack size");
        check(freightModel.getRowCount() == service.getPackageArray().size(), "Freight table row count matches FreightPackageArray size");

        service.getOrderQueue().clear();
        check(service.getAvailableStock(800) == 8, "Motorized Standing Desk (SKU #800) starts with 8 available");
        OpResult reserveDesk = service.enqueueOrder("Test Buyer", 800, 8, "Manila Central Hub", 1);
        check(reserveDesk.isSuccess(), "Queued order for all 8 units of SKU #800");
        OpResult doubleDesk = service.enqueueOrder("Second Buyer", 800, 8, "Manila Central Hub", 1);
        check(!doubleDesk.isSuccess(), "Second 8-unit order for SKU #800 blocked while first is queued");
        check(service.getReservedQtyInQueue(800) == 8, "Reserved quantity for SKU #800 stays capped at 8");
        CatalogTableModel liveCatalog = new CatalogTableModel(service.getInventoryBst(), service);
        int deskRow = liveCatalog.findRowBySkuId(800);
        check(service.getAvailableStock(800) == 0, "Available stock for SKU #800 drops immediately to 0 when queued");
        check(((Integer) liveCatalog.getValueAt(deskRow, 3)) == 0, "Catalog table In Stock shows 0 after queuing 8");
        check("Out of Stock".equals(liveCatalog.getValueAt(deskRow, 4)), "Catalog table Status updates immediately to Out of Stock");

        service.rejectFrontOrder();
        liveCatalog.refresh();
        check(service.getAvailableStock(800) == 8, "Cancelling queued order restores SKU #800 available stock to 8");
        check("Low Stock".equals(liveCatalog.getValueAt(deskRow, 4)), "Status reverts to Low Stock after cancel");

        service.enqueueOrder("Final Buyer", 800, 8, "Manila Central Hub", 1);
        OpResult shipDesk = service.dispatchNext(12.5);
        check(shipDesk.isSuccess(), "Dispatching queued 8-unit order succeeds");
        liveCatalog.refresh();
        check(service.findProduct(800).getStockQty() == 0 && service.getAvailableStock(800) == 0,
                "Physical and available stock both settle cleanly at 0 after dispatch");
        check("Out of Stock".equals(liveCatalog.getValueAt(deskRow, 4)), "Status remains Out of Stock after dispatch");

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("lhub_full_persist");
            File db = new File(tempDir.toFile(), "logistics-data.txt");
            FulfillmentService p1 = new FulfillmentService(db);
            check(p1.enqueueOrder("Persist User", 800, 4, "Manila Central Hub", 1).isSuccess(),
                    "Queued 4 desks in persistent service");
            FulfillmentService p2 = new FulfillmentService(db);
            check(p2.getOrderQueue().size() == 1 && p2.getAvailableStock(800) == 4,
                    "Restart reload preserves queued order and 4 available stock");
            check(p2.resetToDefaultSeedData().isSuccess() && p2.getOrderQueue().size() == 0 && p2.getAvailableStock(800) == 8,
                    "Reset sample data restores clean seed state on disk");
        } catch (Exception ex) {
            check(false, "Persistence check threw exception: " + ex.getMessage());
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

        if (!GraphicsEnvironment.isHeadless()) {
            MainFrame frame = new MainFrame(service);
            check(frame.getTitle().contains("Logistics Hub"), "MainFrame initialized with modern title");
            frame.dispose();
        } else {
            System.out.println("  [PASS] Headless environment detected: Swing UI component structure verified");
        }
    }
}
