package com.group1.logistics.ui;

import com.group1.logistics.datastructures.FreightPackageArray;
import com.group1.logistics.models.FreightPackage;

import javax.swing.table.AbstractTableModel;

/**
 * Custom AbstractTableModel backed directly by FreightPackageArray.
 * Reflects dynamic sorting changes when QuickSort or InsertionSort is run.
 */
public class FreightTableModel extends AbstractTableModel {
    private final FreightPackageArray packages;
    private final String[] columns = {"Package ID", "Order ID", "Destination Hub", "Weight (kg)", "Delivery Priority", "Dispatch Seq"};

    public FreightTableModel(FreightPackageArray packages) {
        this.packages = packages;
    }

    @Override
    public int getRowCount() {
        return packages != null ? packages.size() : 0;
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
        FreightPackage pkg = packages.get(row);
        if (pkg == null) {
            return "";
        }
        switch (col) {
            case 0: return "PKG-" + pkg.getPackageId();
            case 1: return "#" + pkg.getOrderId();
            case 2: return pkg.getDestination();
            case 3: return String.format("%.2f kg", pkg.getWeightKg());
            case 4: return pkg.getPriorityLabel();
            case 5: return "Seq #" + pkg.getSequenceNo();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
