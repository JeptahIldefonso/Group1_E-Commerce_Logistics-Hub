package com.group1.logistics.models;

/**
 * Domain model representing a customer order waiting in the FIFO dispatch queue.
 * Managed by OrderQueue.
 */
public class Order {
    private int orderId;
    private String customerName;
    private int skuId;
    private int quantity;
    private String destination;
    private int deliveryPriority; // 1 = Express, 2 = Priority, 3 = Standard, 4 = Economy, 5 = Bulk
    private int sequenceNo;

    public Order(int orderId, String customerName, int skuId, int quantity, String destination, int deliveryPriority, int sequenceNo) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be positive.");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (skuId <= 0) {
            throw new IllegalArgumentException("SKU ID must be positive.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be empty.");
        }
        if (deliveryPriority < 1 || deliveryPriority > 5) {
            throw new IllegalArgumentException("Delivery priority must be between 1 and 5.");
        }
        this.orderId = orderId;
        this.customerName = customerName.trim();
        this.skuId = skuId;
        this.quantity = quantity;
        this.destination = destination.trim();
        this.deliveryPriority = deliveryPriority;
        this.sequenceNo = sequenceNo;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getSkuId() {
        return skuId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDestination() {
        return destination;
    }

    public int getDeliveryPriority() {
        return deliveryPriority;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getPriorityLabel() {
        switch (deliveryPriority) {
            case 1: return "1 - Express";
            case 2: return "2 - Priority";
            case 3: return "3 - Standard";
            case 4: return "4 - Economy";
            case 5: return "5 - Bulk";
            default: return String.valueOf(deliveryPriority);
        }
    }

    @Override
    public String toString() {
        return "Order #" + orderId + " | Customer: " + customerName + " | SKU: " + skuId +
               " | Qty: " + quantity + " | Destination: " + destination +
               " | Prio: " + getPriorityLabel() + " (Seq #" + sequenceNo + ")";
    }
}
