package com.group1.logistics.ui;

import com.group1.logistics.datastructures.RestockStack;
import com.group1.logistics.models.RestockAction;

import javax.swing.table.AbstractTableModel;

/**
 * Custom AbstractTableModel backed directly by RestockStack.
 * Displays audit log with newest entries on top (index 0).
 */
public class RestockTableModel extends AbstractTableModel {
    private final RestockStack stack;
    private final String[] columns = {"Action ID", "SKU ID", "Units Added", "Sequence No"};

    public RestockTableModel(RestockStack stack) {
        this.stack = stack;
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
            case 0: return "Action #" + action.getActionId();
            case 1: return "SKU #" + action.getSkuId();
            case 2: return "+" + action.getQuantityAdded() + " units";
            case 3: return "Seq #" + action.getSequenceNo();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
