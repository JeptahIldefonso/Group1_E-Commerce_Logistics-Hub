package com.group1.logistics.models;

public class FreightPackage {
    private int packageId;
    private int orderId;
    private String destination;
    private double weightKg;
    private int deliveryPriority;
    private int sequenceNo;

    public FreightPackage(int packageId, int orderId, String destination, double weightKg, int deliveryPriority, int sequenceNo) {
        if (packageId <= 0) {
            throw new IllegalArgumentException("Package ID must be positive.");
        }
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be positive.");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be empty.");
        }
        if (Double.isNaN(weightKg) || Double.isInfinite(weightKg) || weightKg < 0.01 || weightKg > 1000.0) {
            throw new IllegalArgumentException("Weight must be a valid finite number between 0.01 and 1000.00 kg.");
        }
        if (deliveryPriority < 1 || deliveryPriority > 5) {
            throw new IllegalArgumentException("Delivery priority must be between 1 and 5.");
        }
        this.packageId = packageId;
        this.orderId = orderId;
        this.destination = destination.trim();
        this.weightKg = weightKg;
        this.deliveryPriority = deliveryPriority;
        this.sequenceNo = sequenceNo;
    }

    public int getPackageId() {
        return packageId;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getDestination() {
        return destination;
    }

    public double getWeightKg() {
        return weightKg;
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
        return "PKG-" + packageId + " (Order #" + orderId + ") -> " + destination +
               " | " + String.format("%.2f", weightKg) + " kg | " + getPriorityLabel() + " (Seq #" + sequenceNo + ")";
    }
}
