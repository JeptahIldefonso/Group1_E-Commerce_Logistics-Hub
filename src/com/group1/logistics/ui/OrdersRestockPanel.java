package com.group1.logistics.ui;

import com.group1.logistics.models.Order;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import java.awt.*;

/**
 * Tab 1: Orders Desk (Circular Queue FIFO) & Restock Audit (Array Stack LIFO with Undo).
 */
public class OrdersRestockPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;

    private final QueueTableModel queueTableModel;
    private final RestockTableModel restockTableModel;

    // Order form inputs
    private JTextField txtCustomer;
    private JTextField txtSkuId;
    private JTextField txtQuantity;
    private JComboBox<String> cmbDestination;
    private JComboBox<String> cmbPriority;

    // Restock form inputs
    private JTextField txtRestockSku;
    private JTextField txtRestockQty;

    public OrdersRestockPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.queueTableModel = new QueueTableModel(service.getOrderQueue());
        this.restockTableModel = new RestockTableModel(service.getRestockStack());

        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createOrderQueueSubPanel());
        add(createRestockStackSubPanel());
    }

    private JPanel createOrderQueueSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Customer Order Line (Circular Queue - FIFO)"));

        // Order entry form
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        txtCustomer = new JTextField();
        txtSkuId = new JTextField();
        txtQuantity = new JTextField();

        String[] destinations = {
                "Manila Central Hub", "Quezon City Hub", "Makati Logistics Hub",
                "Pasig Cargo Depot", "Taguig Freight Terminal"
        };
        cmbDestination = new JComboBox<>(destinations);

        String[] priorities = {
                "1 - Express", "2 - Priority", "3 - Standard", "4 - Economy", "5 - Bulk Saver"
        };
        cmbPriority = new JComboBox<>(priorities);

        JButton btnQueue = new JButton("Queue Order (Enqueue)");

        form.add(new JLabel("Customer Name:"));
        form.add(txtCustomer);
        form.add(new JLabel("SKU ID:"));
        form.add(txtSkuId);
        form.add(new JLabel("Quantity:"));
        form.add(txtQuantity);
        form.add(new JLabel("Destination Hub:"));
        form.add(cmbDestination);
        form.add(new JLabel("Delivery Priority:"));
        form.add(cmbPriority);
        form.add(new JLabel(""));
        form.add(btnQueue);

        panel.add(form, BorderLayout.NORTH);

        // Table
        JTable tblQueue = new JTable(queueTableModel);
        tblQueue.setFillsViewportHeight(true);
        panel.add(new JScrollPane(tblQueue), BorderLayout.CENTER);

        // Action buttons
        JPanel btnBar = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton btnDispatch = new JButton("Dispatch Next Order (FIFO)");
        JButton btnReject = new JButton("Reject Front Order");

        btnDispatch.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBar.add(btnDispatch);
        btnBar.add(btnReject);
        panel.add(btnBar, BorderLayout.SOUTH);

        // Event listeners
        btnQueue.addActionListener(e -> handleEnqueue());
        btnDispatch.addActionListener(e -> handleDispatch());
        btnReject.addActionListener(e -> handleReject());

        return panel;
    }

    private JPanel createRestockStackSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Inventory Restock Audit & Undo (Stack - LIFO)"));

        // Restock entry form
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        txtRestockSku = new JTextField();
        txtRestockQty = new JTextField();
        JButton btnRestock = new JButton("Apply Restock (Push)");

        form.add(new JLabel("SKU ID to Restock:"));
        form.add(txtRestockSku);
        form.add(new JLabel("Quantity to Add:"));
        form.add(txtRestockQty);
        form.add(new JLabel(""));
        form.add(btnRestock);

        panel.add(form, BorderLayout.NORTH);

        // Table
        JTable tblRestock = new JTable(restockTableModel);
        tblRestock.setFillsViewportHeight(true);
        panel.add(new JScrollPane(tblRestock), BorderLayout.CENTER);

        // Undo button
        JButton btnUndo = new JButton("Undo Last Restock (LIFO Rollback)");
        btnUndo.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnUndo.setBackground(new Color(255, 235, 235));
        panel.add(btnUndo, BorderLayout.SOUTH);

        // Event listeners
        btnRestock.addActionListener(e -> handleRestock());
        btnUndo.addActionListener(e -> handleUndo());

        return panel;
    }

    private void handleEnqueue() {
        String customer = txtCustomer.getText().trim();
        String skuStr = txtSkuId.getText().trim();
        String qtyStr = txtQuantity.getText().trim();
        String destination = (String) cmbDestination.getSelectedItem();
        int priority = cmbPriority.getSelectedIndex() + 1;

        if (customer.isEmpty() || skuStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all order fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            int qty = Integer.parseInt(qtyStr);

            OpResult result = service.enqueueOrder(customer, skuId, qty, destination, priority);
            if (result.isSuccess()) {
                txtCustomer.setText("");
                txtSkuId.setText("");
                txtQuantity.setText("");
                mainFrame.refreshAll();
                JOptionPane.showMessageDialog(this, result.getMessage(), "Order Queued", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Order Rejected", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "SKU ID and Quantity must be valid positive integers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDispatch() {
        if (service.getOrderQueue().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order Queue is empty. No orders waiting.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Order front = service.getOrderQueue().peek();

        // Prompt weight dialog (BR-08)
        String input = JOptionPane.showInputDialog(this,
                "Dispatching Order #" + front.getOrderId() + " (" + front.getCustomerName() + ")\n" +
                "Item: SKU #" + front.getSkuId() + " (Qty: " + front.getQuantity() + ")\n\n" +
                "Enter parcel cargo weight in kg (0.01 - 1000.00):",
                "Parcel Packing Weight", JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            return; // Operator cancelled
        }

        try {
            double weight = Double.parseDouble(input.trim());
            OpResult result = service.dispatchNext(weight);
            if (result.isSuccess()) {
                mainFrame.refreshAll();
                JOptionPane.showMessageDialog(this, result.getMessage(), "Dispatch Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Dispatch Blocked", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Weight must be a valid number (e.g. 2.45).", "Invalid Weight", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReject() {
        if (service.getOrderQueue().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Queue is empty.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        OpResult result = service.rejectFrontOrder();
        mainFrame.refreshAll();
        JOptionPane.showMessageDialog(this, result.getMessage(), "Order Rejected", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleRestock() {
        String skuStr = txtRestockSku.getText().trim();
        String qtyStr = txtRestockQty.getText().trim();

        if (skuStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter SKU and quantity to restock.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            int qty = Integer.parseInt(qtyStr);

            OpResult result = service.restock(skuId, qty);
            if (result.isSuccess()) {
                txtRestockSku.setText("");
                txtRestockQty.setText("");
                mainFrame.refreshAll();
                JOptionPane.showMessageDialog(this, result.getMessage(), "Restock Applied", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Restock Blocked", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "SKU and Quantity must be valid integers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUndo() {
        OpResult result = service.undoRestock();
        if (result.isSuccess()) {
            mainFrame.refreshAll();
            JOptionPane.showMessageDialog(this, result.getMessage(), "Undo Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Undo Blocked", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshTables() {
        queueTableModel.fireTableDataChanged();
        restockTableModel.fireTableDataChanged();
    }
}
