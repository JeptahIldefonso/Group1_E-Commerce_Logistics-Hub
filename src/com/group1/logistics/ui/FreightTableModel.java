package com.group1.logistics.ui;

import com.group1.logistics.datastructures.FreightPackageArray;
import com.group1.logistics.models.FreightPackage;

import javax.swing.table.AbstractTableModel;

public class FreightTableModel extends AbstractTableModel {
    private final FreightPackageArray packages;
    private boolean technicalDetails = false;
    private final String[] columns = {"Package No.", "Order No.", "Deliver To", "Weight", "Delivery Speed", "Shipped #"};

    private FreightPackage[] cachedRefs = new FreightPackage[0];
    private String[] cachedWeights = new String[0];

    public FreightTableModel(FreightPackageArray packages) {
        this.packages = packages;
    }

    public void setTechnicalDetails(boolean enabled) {
        this.technicalDetails = enabled;
        fireTableDataChanged();
    }

    private String getFormattedWeight(int row, FreightPackage pkg) {
        int size = packages.size();
        if (cachedRefs.length < size) {
            cachedRefs = new FreightPackage[size];
            cachedWeights = new String[size];
        }
        if (cachedRefs[row] != pkg) {
            cachedRefs[row] = pkg;
            cachedWeights[row] = String.format("%.2f kg", pkg.getWeightKg());
        }
        return cachedWeights[row];
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
                return getFormattedWeight(row, pkg);
            case 4:
                return UiTheme.formatDeliverySpeed(pkg.getDeliveryPriority());
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
