package com.group1.logistics.ui;

import com.group1.logistics.datastructures.InventoryBST;
import com.group1.logistics.models.Product;

import javax.swing.table.AbstractTableModel;

/**
 * Custom AbstractTableModel backed by InventoryBST.inOrder() traversal.
 * Populates rows strictly in ascending order by SKU ID.
 * Pure standard Java Swing; zero Java Collections.
 */
public class CatalogTableModel extends AbstractTableModel {
    private final InventoryBST bst;
    private Product[] cachedProducts;
    private final String[] columns = {"SKU ID", "Product Name", "Price (₱)", "Stock Qty", "Stock Status"};

    public CatalogTableModel(InventoryBST bst) {
        this.bst = bst;
        refresh();
    }

    public void refresh() {
        this.cachedProducts = bst != null ? bst.inOrder() : new Product[0];
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        if (cachedProducts != null && row >= 0 && row < cachedProducts.length) {
            return cachedProducts[row];
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return cachedProducts != null ? cachedProducts.length : 0;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (cachedProducts == null || row < 0 || row >= cachedProducts.length) {
            return "";
        }
        Product product = cachedProducts[row];
        switch (col) {
            case 0: return product.getSkuId();
            case 1: return product.getName();
            case 2: return "₱" + String.format("%.2f", product.getPrice());
            case 3: return product.getStockQty();
            case 4:
                if (product.getStockQty() == 0) return "● Out of Stock";
                if (product.getStockQty() <= 10) return "▲ Low Stock";
                return "✓ In Stock";
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
