package com.group1.logistics.ui;

import com.group1.logistics.datastructures.RestockStack;
import com.group1.logistics.models.Product;
import com.group1.logistics.models.RestockAction;
import com.group1.logistics.service.FulfillmentService;

import javax.swing.table.AbstractTableModel;

/**
 * Custom AbstractTableModel backed directly by RestockStack.
 * Displays audit log with newest entries on top (index 0).
 * Displays friendly product names and supports technical details toggling.
 * Pure standard Java Swing; zero Java Collections.
 */
public class RestockTableModel extends AbstractTableModel {
    private final RestockStack stack;
    private final FulfillmentService service;
    private boolean technicalDetails = false;

    private final String[] columns = {"Restock #", "Product", "Quantity Added", "Order of Entry"};

    public RestockTableModel(RestockStack stack, FulfillmentService service) {
        this.stack = stack;
        this.service = service;
    }

    public RestockTableModel(RestockStack stack) {
        this(stack, null);
    }

    public void setTechnicalDetails(boolean enabled) {
        this.technicalDetails = enabled;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return stack != null ? stack.size() : 0;
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
        RestockAction action = stack.get(row); // 0 is newest
        if (action == null) {
            return "";
        }
        switch (col) {
            case 0:
                return "Restock #" + action.getActionId();
            case 1:
                if (service != null) {
                    Product p = service.findProduct(action.getSkuId());
                    if (p != null) {
                        return p.getName();
                    }
                }
                return "SKU #" + action.getSkuId();
            case 2:
                return "+" + action.getQuantityAdded() + " units";
            case 3:
                return technicalDetails ? "Seq #" + action.getSequenceNo() : String.valueOf(row + 1);
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
