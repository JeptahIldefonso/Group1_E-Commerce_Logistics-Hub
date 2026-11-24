package com.group1.logistics.ui;

import com.group1.logistics.datastructures.OrderQueue;
import com.group1.logistics.models.Order;

import javax.swing.table.AbstractTableModel;

/**
 * Custom AbstractTableModel backed directly by OrderQueue.
 * Avoids DefaultTableModel and java.util.Vector to stay 100% compliant with zero-collections rule.
 */
public class QueueTableModel extends AbstractTableModel {
    private final OrderQueue queue;
    private final String[] columns = {"#", "Order ID", "Customer Name", "SKU", "Qty", "Destination", "Priority"};

    public QueueTableModel(OrderQueue queue) {
        this.queue = queue;
    }

    @Override
    public int getRowCount() {
        return queue != null ? queue.size() : 0;
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
        Order order = queue.get(row);
        if (order == null) {
            return "";
        }
        switch (col) {
            case 0: return row + 1;
            case 1: return "#" + order.getOrderId();
            case 2: return order.getCustomerName();
            case 3: return order.getSkuId();
            case 4: return order.getQuantity();
            case 5: return order.getDestination();
            case 6: return order.getPriorityLabel();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false; // All table cells non-editable
    }
}
