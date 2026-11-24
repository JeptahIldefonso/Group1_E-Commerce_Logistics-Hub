package com.group1.logistics.models;

/**
 * Domain model representing an inventory restock transaction.
 * Stored in RestockStack (LIFO) to support atomic rollback via Safe Undo (BR-06).
 */
public class RestockAction {
    private int actionId;
    private int skuId;
    private int quantityAdded;
    private int sequenceNo;

    public RestockAction(int actionId, int skuId, int quantityAdded, int sequenceNo) {
        if (actionId <= 0) {
            throw new IllegalArgumentException("Action ID must be positive.");
        }
        if (skuId <= 0) {
            throw new IllegalArgumentException("SKU ID must be positive.");
        }
        if (quantityAdded <= 0) {
            throw new IllegalArgumentException("Quantity added must be at least 1.");
        }
        this.actionId = actionId;
        this.skuId = skuId;
        this.quantityAdded = quantityAdded;
        this.sequenceNo = sequenceNo;
    }

    public int getActionId() {
        return actionId;
    }

    public int getSkuId() {
        return skuId;
    }

    public int getQuantityAdded() {
        return quantityAdded;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    @Override
    public String toString() {
        return "Action #" + actionId + " | SKU: " + skuId + " | Added: +" + quantityAdded + " (Seq #" + sequenceNo + ")";
    }
}
