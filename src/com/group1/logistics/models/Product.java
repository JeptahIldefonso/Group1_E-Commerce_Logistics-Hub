package com.group1.logistics.models;

/**
 * Domain model representing a product in the warehouse catalog.
 * Stored in InventoryBST and keyed by unique numeric skuId.
 * Enforces BR-04 (stockQty is never negative).
 */
public class Product {
    private int skuId;
    private String name;
    private double price;
    private int stockQty;

    public Product(int skuId, String name, double price, int stockQty) {
        if (skuId <= 0) {
            throw new IllegalArgumentException("SKU ID must be a positive integer.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (stockQty < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        this.skuId = skuId;
        this.name = name.trim();
        this.price = price;
        this.stockQty = stockQty;
    }

    public int getSkuId() {
        return skuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price >= 0.0) {
            this.price = price;
        }
    }

    public int getStockQty() {
        return stockQty;
    }

    /**
     * Safely increases stock quantity.
     * @param amount units to add (must be > 0).
     */
    public void addStock(int amount) {
        if (amount > 0) {
            this.stockQty += amount;
        }
    }

    /**
     * Safely decreases stock quantity, preventing negative stock (BR-04).
     * @param amount units to deduct.
     * @return true if deduction succeeded, false if insufficient stock.
     */
    public boolean removeStock(int amount) {
        if (amount > 0 && this.stockQty >= amount) {
            this.stockQty -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SKU #" + skuId + " - " + name + " (PHP " + String.format("%.2f", price) + " | Stock: " + stockQty + ")";
    }
}
