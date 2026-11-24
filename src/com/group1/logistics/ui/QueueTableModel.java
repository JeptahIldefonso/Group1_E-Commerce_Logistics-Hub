package com.group1.logistics.ui;

import com.group1.logistics.datastructures.OrderQueue;
import com.group1.logistics.models.Order;
import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;

import javax.swing.table.AbstractTableModel;

/**
 * Custom AbstractTableModel backed directly by OrderQueue.
 * Shows friendly Product Names, NEXT indicator, and plain-English headers.
 * Pure standard Java Swing; zero Java Collections.
 */
public class QueueTableModel extends AbstractTableModel {
    private final OrderQueue queue;
    private final FulfillmentService service;
    private final String[] columns = {"#", "Order No.", "Customer Name", "Product", "Qty", "Deliver To", "Priority"};

    public QueueTableModel(OrderQueue queue, FulfillmentService service) {
        this.queue = queue;
        this.service = service;
    }

    public QueueTableModel(OrderQueue queue) {
        this(queue, null);
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
            case 0:
                return row == 0 ? "NEXT" : String.valueOf(row + 1);
            case 1:
                return "#" + order.getOrderId();
            case 2:
                return order.getCustomerName();
            case 3:
                if (service != null) {
                    Product p = service.findProduct(order.getSkuId());
                    if (p != null) {
                        return p.getName();
                    }
                }
                return "SKU #" + order.getSkuId();
            case 4:
                return order.getQuantity();
            case 5:
                return order.getDestination();
            case 6:
                switch (order.getDeliveryPriority()) {
                    case 1: return "Express (fastest)";
                    case 2: return "Priority";
                    case 3: return "Standard";
                    case 4: return "Economy (slowest)";
                    default: return "Bulk Saver";
                }
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
