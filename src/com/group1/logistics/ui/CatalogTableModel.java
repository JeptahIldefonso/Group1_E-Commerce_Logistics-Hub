package com.group1.logistics.ui;

import com.group1.logistics.datastructures.InventoryBST;
import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;

import javax.swing.table.AbstractTableModel;

public class CatalogTableModel extends AbstractTableModel {
    public static final int SORT_BY_SKU_ASC = 0;
    public static final int SORT_BY_STOCK_ASC = 1;
    public static final int SORT_BY_STOCK_DESC = 2;

    private final InventoryBST bst;
    private final FulfillmentService service;
    private Product[] cachedProducts;
    private String[] cachedPrices;
    private String[] cachedLowerNames;
    private int[] cachedAvailableStock;
    private int[] cachedReservedStock;
    private int healthyCount;
    private int attentionCount;
    private int sortMode = SORT_BY_SKU_ASC;
    private final String[] columns = {"Product Code", "Product Name", "Price", "In Stock", "Status"};

    public CatalogTableModel(InventoryBST bst, FulfillmentService service) {
        this.bst = bst;
        this.service = service;
        refresh();
    }

    public CatalogTableModel(InventoryBST bst) {
        this(bst, null);
    }

    public void refresh() {
        this.cachedProducts = bst != null ? bst.inOrder() : new Product[0];
        int len = cachedProducts.length;
        this.cachedPrices = new String[len];
        this.cachedLowerNames = new String[len];
        this.cachedAvailableStock = new int[len];
        this.cachedReservedStock = new int[len];
        int healthy = 0;
        int attention = 0;

        for (int i = 0; i < len; i++) {
            Product p = cachedProducts[i];
            cachedPrices[i] = "₱" + String.format("%,.2f", p.getPrice());
            cachedLowerNames[i] = p.getName() != null ? p.getName().toLowerCase() : "";

            int reserved = (service != null) ? service.getReservedQtyInQueue(p.getSkuId()) : 0;
            int available = p.getStockQty() - reserved;
            if (available < 0) {
                available = 0;
            }
            cachedReservedStock[i] = reserved;
            cachedAvailableStock[i] = available;

            if (available > 20) {
                healthy++;
            } else {
                attention++;
            }
        }
        this.healthyCount = healthy;
        this.attentionCount = attention;
        if (len > 1 && sortMode != SORT_BY_SKU_ASC) {
            sortCachedRowsByStock(sortMode == SORT_BY_STOCK_ASC);
        }
        fireTableDataChanged();
    }

    public int getSortMode() {
        return sortMode;
    }

    public void setSortMode(int mode) {
        if (mode != SORT_BY_STOCK_ASC && mode != SORT_BY_STOCK_DESC) {
            mode = SORT_BY_SKU_ASC;
        }
        this.sortMode = mode;
        refresh();
    }

    private void sortCachedRowsByStock(boolean ascending) {
        int len = cachedProducts.length;
        for (int i = 1; i < len; i++) {
            Product keyProd = cachedProducts[i];
            String keyPrice = cachedPrices[i];
            String keyLower = cachedLowerNames[i];
            int keyAvail = cachedAvailableStock[i];
            int keyReserved = cachedReservedStock[i];

            int j = i - 1;
            while (j >= 0 && (ascending ? cachedAvailableStock[j] > keyAvail : cachedAvailableStock[j] < keyAvail)) {
                cachedProducts[j + 1] = cachedProducts[j];
                cachedPrices[j + 1] = cachedPrices[j];
                cachedLowerNames[j + 1] = cachedLowerNames[j];
                cachedAvailableStock[j + 1] = cachedAvailableStock[j];
                cachedReservedStock[j + 1] = cachedReservedStock[j];
                j--;
            }
            cachedProducts[j + 1] = keyProd;
            cachedPrices[j + 1] = keyPrice;
            cachedLowerNames[j + 1] = keyLower;
            cachedAvailableStock[j + 1] = keyAvail;
            cachedReservedStock[j + 1] = keyReserved;
        }
    }

    public int getTotalCount() {
        return cachedProducts != null ? cachedProducts.length : 0;
    }

    public int getHealthyCount() {
        return healthyCount;
    }

    public int getAttentionCount() {
        return attentionCount;
    }

    public int getAvailableStockAt(int row) {
        if (cachedAvailableStock != null && row >= 0 && row < cachedAvailableStock.length) {
            return cachedAvailableStock[row];
        }
        return 0;
    }

    public int getReservedStockAt(int row) {
        if (cachedReservedStock != null && row >= 0 && row < cachedReservedStock.length) {
            return cachedReservedStock[row];
        }
        return 0;
    }

    public int findRowByNameQuery(String lowerQuery) {
        if (cachedLowerNames == null || lowerQuery == null || lowerQuery.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < cachedLowerNames.length; i++) {
            if (cachedLowerNames[i].contains(lowerQuery)) {
                return i;
            }
        }
        return -1;
    }

    public int findRowBySkuId(int skuId) {
        if (cachedProducts == null) {
            return -1;
        }
        if (sortMode == SORT_BY_SKU_ASC) {
            int lo = 0;
            int hi = cachedProducts.length - 1;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                int midId = cachedProducts[mid].getSkuId();
                if (midId == skuId) {
                    return mid;
                }
                if (midId < skuId) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            return -1;
        }
        for (int i = 0; i < cachedProducts.length; i++) {
            if (cachedProducts[i].getSkuId() == skuId) {
                return i;
            }
        }
        return -1;
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
            case 0:
                return product.getSkuId();
            case 1:
                return product.getName();
            case 2:
                return cachedPrices[row];
            case 3:
                return cachedAvailableStock[row];
            case 4:
                int avail = cachedAvailableStock[row];
                if (avail == 0) {
                    return "Out of Stock";
                }
                if (avail <= 20) {
                    return "Low Stock";
                }
                return "In Stock";
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
