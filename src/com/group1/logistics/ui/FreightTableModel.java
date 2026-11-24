package com.group1.logistics.ui;

import com.group1.logistics.datastructures.FreightPackageArray;
import com.group1.logistics.models.FreightPackage;

import javax.swing.table.AbstractTableModel;

/**
 * Custom AbstractTableModel backed directly by FreightPackageArray.
 * Displays plain-English shipping headers and consistent weight formatting.
 * Pure standard Java Swing; zero Java Collections.
 */
public class FreightTableModel extends AbstractTableModel {
    private final FreightPackageArray packages;
    private boolean technicalDetails = false;
    private final String[] columns = {"Package No.", "Order No.", "Deliver To", "Weight", "Delivery Speed", "Shipped #"};

    public FreightTableModel(FreightPackageArray packages) {
        this.packages = packages;
    }

    public void setTechnicalDetails(boolean enabled) {
        this.technicalDetails = enabled;
        fireTableDataChanged();
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
            case 0:
                return "PKG-" + pkg.getPackageId();
            case 1:
                return "#" + pkg.getOrderId();
            case 2:
                return pkg.getDestination();
            case 3:
                return String.format("%.2f kg", pkg.getWeightKg());
            case 4:
                switch (pkg.getDeliveryPriority()) {
                    case 1: return "Express (fastest)";
                    case 2: return "Priority";
                    case 3: return "Standard";
                    case 4: return "Economy (slowest)";
                    default: return "Bulk Saver";
                }
            case 5:
                return technicalDetails ? "Seq #" + pkg.getSequenceNo() : String.valueOf(row + 1);
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
