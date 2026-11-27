package com.group1.logistics.service;

import com.group1.logistics.datastructures.FreightPackageArray;
import com.group1.logistics.datastructures.InventoryBST;
import com.group1.logistics.datastructures.OrderQueue;
import com.group1.logistics.datastructures.RestockStack;
import com.group1.logistics.models.FreightPackage;
import com.group1.logistics.models.Order;
import com.group1.logistics.models.Product;
import com.group1.logistics.models.RestockAction;
import com.group1.logistics.sorting.InsertionSort;
import com.group1.logistics.sorting.PackageCriteria;
import com.group1.logistics.sorting.QuickSort;
import com.group1.logistics.sorting.SortCriteria;

/**
 * Core business logic coordinator for the E-Commerce Fulfillment & Logistics System.
 * Owns OrderQueue, RestockStack, InventoryBST, and FreightPackageArray.
 * Enforces business rules BR-01 through BR-09.
 */
public class FulfillmentService {
    private final OrderQueue orderQueue;
    private final RestockStack restockStack;
    private final InventoryBST inventoryBst;
    private final FreightPackageArray packageArray;

    private int nextOrderId;
    private int nextActionId;
    private int nextPackageId;
    private int sequenceCounter;

    public FulfillmentService() {
        this.orderQueue = new OrderQueue(20);
        this.restockStack = new RestockStack(20);
        this.inventoryBst = new InventoryBST();
        this.packageArray = new FreightPackageArray(16);

        this.nextOrderId = 1001;
        this.nextActionId = 1;
        this.nextPackageId = 501;
        this.sequenceCounter = 1;

        loadDefaultSeedData();
    }

    // =========================================================================
    // PRODUCT / SKU CATALOG OPERATIONS (BR-01, BR-04, BR-07)
    // =========================================================================

    public OpResult addProduct(int skuId, String name, double price, int stockQty) {
        if (skuId <= 0) {
            return OpResult.err("SKU ID must be a positive integer.");
        }
        if (name == null || name.trim().isEmpty()) {
            return OpResult.err("Product name cannot be empty.");
        }
        if (name.contains("|")) {
            return OpResult.err("Product name cannot contain '|' character.");
        }
        if (Double.isNaN(price) || Double.isInfinite(price) || price < 0.0) {
            return OpResult.err("Price must be a non-negative finite number.");
        }
        if (stockQty < 0) {
            return OpResult.err("Initial stock cannot be negative (BR-04).");
        }

        Product product = new Product(skuId, name, price, stockQty);
        boolean inserted = inventoryBst.insert(product);
        if (!inserted) {
            return OpResult.err("SKU ID #" + skuId + " already exists in the catalog (BR-01).");
        }
        return OpResult.ok("Product '" + product.getName() + "' (SKU #" + skuId + ") added successfully.");
    }

    public Product findProduct(int skuId) {
        return inventoryBst.search(skuId);
    }

    /**
     * Counts units of the given SKU currently reserved in pending queued orders.
     */
    public int getReservedQtyInQueue(int skuId) {
        if (skuId <= 0) {
            return 0;
        }
        int reserved = 0;
        int size = orderQueue.size();
        for (int i = 0; i < size; i++) {
            Order o = orderQueue.get(i);
            if (o != null && o.getSkuId() == skuId) {
                reserved += o.getQuantity();
            }
        }
        return reserved;
    }

    /**
     * Returns net unreserved stock available for new orders (physical stock minus queued orders, floored at 0).
     */
    public int getAvailableStock(int skuId) {
        Product p = findProduct(skuId);
        if (p == null) {
            return 0;
        }
        int available = p.getStockQty() - getReservedQtyInQueue(skuId);
        return available > 0 ? available : 0;
    }

    /**
     * Case-insensitive product search by name or substring.
     * Searches all products in the catalog using in-order traversal array.
     */
    public Product findProductByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        String q = query.trim().toLowerCase();
        Product[] products = inventoryBst.inOrder();
        if (products != null) {
            for (int i = 0; i < products.length; i++) {
                if (products[i] != null && products[i].getName() != null) {
                    if (products[i].getName().toLowerCase().contains(q)) {
                        return products[i];
                    }
                }
            }
        }
        return null;
    }

    public OpResult deleteProduct(int skuId) {
        if (skuId <= 0) {
            return OpResult.err("Invalid SKU ID.");
        }
        boolean deleted = inventoryBst.delete(skuId);
        if (deleted) {
            return OpResult.ok("SKU #" + skuId + " has been successfully removed from catalog (BR-07).");
        }
        return OpResult.err("SKU #" + skuId + " not found in catalog.");
    }

    // =========================================================================
    // ORDER DISPATCH OPERATIONS (BR-02, BR-03, BR-08)
    // =========================================================================

    public OpResult enqueueOrder(String customerName, int skuId, int quantity, String destination, int priority) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return OpResult.err("Customer name cannot be empty.");
        }
        if (customerName.contains("|") || (destination != null && destination.contains("|"))) {
            return OpResult.err("Inputs cannot contain '|' character.");
        }
        if (skuId <= 0) {
            return OpResult.err("Invalid SKU ID.");
        }
        if (findProduct(skuId) == null) {
            return OpResult.err("SKU #" + skuId + " does not exist in catalog (BR-02).");
        }
        if (quantity <= 0) {
            return OpResult.err("Order quantity must be at least 1.");
        }
        if (destination == null || destination.trim().isEmpty()) {
            return OpResult.err("Destination cannot be empty.");
        }
        if (priority < 1 || priority > 5) {
            return OpResult.err("Delivery priority must be between 1 and 5.");
        }
        if (orderQueue.isFull()) {
            return OpResult.err("The waiting list is full (" + orderQueue.capacity() + " orders). Please ship or cancel an order first.");
        }

        Order order = new Order(nextOrderId++, customerName, skuId, quantity, destination, priority, sequenceCounter++);
        orderQueue.enqueue(order);
        return OpResult.ok("Order #" + order.getOrderId() + " (" + order.getCustomerName() + ") queued successfully.");
    }

    /**
     * Dispatch workflow: Peek -> Validate -> Commit (BR-03, BR-04, BR-08).
     */
    public OpResult dispatchNext(double weightKg) {
        if (orderQueue.isEmpty()) {
            return OpResult.err("Order Queue is empty. No orders to dispatch.");
        }

        Order frontOrder = orderQueue.peek();
        Product product = findProduct(frontOrder.getSkuId());

        if (product == null) {
            return OpResult.err("Dispatch blocked: SKU #" + frontOrder.getSkuId() + " was discontinued! (Click 'Cancel Next' to remove).");
        }

        if (product.getStockQty() < frontOrder.getQuantity()) {
            return OpResult.err("Insufficient stock for SKU #" + frontOrder.getSkuId() +
                    " (Available: " + product.getStockQty() + ", Requested: " + frontOrder.getQuantity() + "). Order remains in queue.");
        }

        if (Double.isNaN(weightKg) || Double.isInfinite(weightKg) || weightKg < 0.01 || weightKg > 1000.0) {
            return OpResult.err("Parcel weight must be a valid number between 0.01 and 1000.00 kg (BR-08).");
        }

        // Commit: deduct stock and dequeue order
        product.removeStock(frontOrder.getQuantity());
        Order dispatchedOrder = orderQueue.dequeue();

        // Generate FreightPackage
        FreightPackage pkg = new FreightPackage(
                nextPackageId++,
                dispatchedOrder.getOrderId(),
                dispatchedOrder.getDestination(),
                weightKg,
                dispatchedOrder.getDeliveryPriority(),
                sequenceCounter++
        );
        packageArray.add(pkg);

        return OpResult.ok("Dispatched Order #" + dispatchedOrder.getOrderId() + " -> Created PKG-" + pkg.getPackageId() +
                " (" + String.format("%.2f", weightKg) + " kg). Stock remaining: " + product.getStockQty());
    }

    public OpResult rejectFrontOrder() {
        if (orderQueue.isEmpty()) {
            return OpResult.err("Order Queue is empty.");
        }
        Order rejected = orderQueue.dequeue();
        return OpResult.ok("Order #" + rejected.getOrderId() + " (SKU #" + rejected.getSkuId() + ") was rejected and removed from queue.");
    }

    // =========================================================================
    // RESTOCK & SAFE UNDO OPERATIONS (BR-05, BR-06)
    // =========================================================================

    public OpResult restock(int skuId, int quantityAdded) {
        if (skuId <= 0) {
            return OpResult.err("Invalid SKU ID.");
        }
        if (quantityAdded <= 0) {
            return OpResult.err("Quantity to add must be at least 1.");
        }
        if (restockStack.isFull()) {
            return OpResult.err("The restock history is full (" + restockStack.capacity() + " actions). Please undo or restart to add more.");
        }

        Product product = findProduct(skuId);
        if (product == null) {
            return OpResult.err("Cannot restock: SKU #" + skuId + " does not exist in catalog (BR-05).");
        }

        // Atomic commit: add stock and push log
        product.addStock(quantityAdded);
        RestockAction action = new RestockAction(nextActionId++, skuId, quantityAdded, sequenceCounter++);
        restockStack.push(action);

        return OpResult.ok("Restocked +" + quantityAdded + " units to SKU #" + skuId + ". New stock: " + product.getStockQty());
    }

    /**
     * Safe Undo (Anti-Lost-Update - BR-06).
     * Subtracts quantityAdded from current stock. Never restores snapshots.
     */
    public OpResult undoRestock() {
        if (restockStack.isEmpty()) {
            return OpResult.err("No restock actions available to undo.");
        }

        RestockAction topAction = restockStack.peek();
        Product product = findProduct(topAction.getSkuId());

        if (product == null) {
            // SKU was deleted after restock: discard entry with warning
            restockStack.pop();
            return OpResult.ok("Restocked SKU #" + topAction.getSkuId() + " was deleted from catalog. Discarded audit entry.");
        }

        if (product.getStockQty() < topAction.getQuantityAdded()) {
            return OpResult.err("Cannot undo: units have already been shipped/dispatched! (Current stock: " +
                    product.getStockQty() + ", Needed to rollback: " + topAction.getQuantityAdded() + "). Entry remains on stack.");
        }

        // Safe subtraction rollback
        product.removeStock(topAction.getQuantityAdded());
        RestockAction popped = restockStack.pop();

        return OpResult.ok("Undone Action #" + popped.getActionId() + ": Subtracted " + popped.getQuantityAdded() +
                " units from SKU #" + popped.getSkuId() + ". Restored stock: " + product.getStockQty());
    }

    // =========================================================================
    // SORTING OPERATIONS (BR-09)
    // =========================================================================

    public OpResult sortPackages(int algorithm, int criteriaType, boolean asc) {
        if (packageArray.size() <= 1) {
            return OpResult.ok("Package list has " + packageArray.size() + " items; already sorted.");
        }

        SortCriteria criteria;
        String criteriaName;
        switch (criteriaType) {
            case 1:
                criteria = PackageCriteria.BY_WEIGHT;
                criteriaName = "Weight (kg)";
                break;
            case 2:
                criteria = PackageCriteria.BY_PRIORITY;
                criteriaName = "Delivery Priority";
                break;
            case 3:
                criteria = PackageCriteria.BY_ID;
                criteriaName = "Package ID";
                break;
            case 4:
                criteria = PackageCriteria.BY_DESTINATION;
                criteriaName = "Destination";
                break;
            default:
                criteria = PackageCriteria.BY_WEIGHT;
                criteriaName = "Weight (kg)";
                break;
        }

        String algoName;
        if (algorithm == 1) {
            algoName = "QuickSort (Hoare)";
            QuickSort.sort(packageArray.rawArray(), packageArray.size(), criteria, asc);
        } else {
            algoName = "InsertionSort (Stable)";
            InsertionSort.sort(packageArray.rawArray(), packageArray.size(), criteria, asc);
        }

        return OpResult.ok("Sorted " + packageArray.size() + " packages by " + criteriaName +
                " using " + algoName + " (" + (asc ? "Ascending" : "Descending") + ").");
    }

    // =========================================================================
    // SEED DATA LOADER
    // =========================================================================

    public void loadDefaultSeedData() {
        // Balanced SKU insertion order (tree height stays at 4 instead of degenerate chain)
        int[] skuIds = {500, 300, 700, 200, 400, 600, 800, 150, 250, 350, 450, 550, 650, 750};
        String[] names = {
                "Pro Mechanical Keyboard", "Wireless Ergonomic Mouse", "4K Ultra-HD Monitor 27\"",
                "USB-C Multiport Hub", "Ergonomic Desk Mat XL", "Noise-Cancelling Headset",
                "Motorized Standing Desk", "1080p Streaming Webcam", "High-Speed HDMI Cable",
                "Vertical Laptop Stand", "RGB Gaming Mousepad", "Screen Cleaning Kit",
                "Laptop Cooling Pad", "Dual Monitor Arm Mount"
        };
        double[] prices = {
                3499.00, 1250.00, 14999.00,
                1199.00, 650.00, 4299.00,
                18500.00, 2199.00, 450.00,
                899.00, 799.00, 250.00,
                1450.00, 2899.00
        };
        int[] stocks = {
                25, 40, 10,
                35, 60, 18,
                8, 30, 80,
                45, 50, 100,
                22, 15
        };

        for (int i = 0; i < skuIds.length; i++) {
            inventoryBst.insert(new Product(skuIds[i], names[i], prices[i], stocks[i]));
        }

        // Start with a clean order queue so newly placed orders immediately become #1001 (NEXT)
        // and displayed product counts match unreserved physical shelf quantities.

        // Pre-load a restock history
        restock(300, 20);
        restock(500, 15);

        // Pre-load several dispatched packages
        packageArray.add(new FreightPackage(nextPackageId++, 991, "Manila Central Hub", 1.45, 1, sequenceCounter++));
        packageArray.add(new FreightPackage(nextPackageId++, 992, "Quezon City Hub", 0.65, 3, sequenceCounter++));
        packageArray.add(new FreightPackage(nextPackageId++, 993, "Makati Logistics Hub", 8.20, 1, sequenceCounter++));
        packageArray.add(new FreightPackage(nextPackageId++, 994, "Manila Central Hub", 15.40, 3, sequenceCounter++));
        packageArray.add(new FreightPackage(nextPackageId++, 995, "Quezon City Hub", 2.10, 4, sequenceCounter++));
    }

    // =========================================================================
    // ACCESSORS FOR UI TABLE MODELS & STATUS BAR
    // =========================================================================

    public OrderQueue getOrderQueue() {
        return orderQueue;
    }

    public RestockStack getRestockStack() {
        return restockStack;
    }

    public InventoryBST getInventoryBst() {
        return inventoryBst;
    }

    public FreightPackageArray getPackageArray() {
        return packageArray;
    }

    public int getNextOrderId() {
        return nextOrderId;
    }

    public int getNextPackageId() {
        return nextPackageId;
    }
}
