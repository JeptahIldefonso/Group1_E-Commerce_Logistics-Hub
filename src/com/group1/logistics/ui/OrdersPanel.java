package com.group1.logistics.ui;

import com.group1.logistics.models.Order;
import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * Tab 1: Orders.
 * Top "Place an Order" intake form; full-width "Waiting Orders" table below.
 * Shows product names, NEXT badge, and confirmation dialogs.
 * Pure standard Java Swing; zero Java Collections.
 */
public class OrdersPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final QueueTableModel queueTableModel;

    // Form inputs
    private JTextField txtCustomer;
    private JComboBox<ProductComboItem> cmbProduct;
    private JTextField txtQuantity;
    private JComboBox<String> cmbDestination;
    private JComboBox<String> cmbPriority;
    private JButton btnPlaceOrder;

    // Action buttons & cues
    private JButton btnShipNext;
    private JButton btnCancelNext;
    private JLabel lblNextOrderSummary;
    private JTable tblOrders;

    public static class ProductComboItem {
        private final int skuId;
        private final String name;
        private final int stock;

        public ProductComboItem(int skuId, String name, int stock) {
            this.skuId = skuId;
            this.name = name;
            this.stock = stock;
        }

        public int getSkuId() {
            return skuId;
        }

        public String getName() {
            return name;
        }

        public int getStock() {
            return stock;
        }

        @Override
        public String toString() {
            return name + " - " + stock + " available";
        }
    }

    public OrdersPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.queueTableModel = new QueueTableModel(service.getOrderQueue(), service);

        setLayout(new BorderLayout(0, 10));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Top Form: "Place an Order" (Full Width)
        add(createOrderFormCard(), BorderLayout.NORTH);

        // 2. Center Panel: "Waiting Orders" (Full Width)
        add(createWaitingOrdersCard(), BorderLayout.CENTER);

        refreshProductDropdown();
        updateButtonStatesAndSummary();

        // Focus cursor in first field
        SwingUtilities.invokeLater(() -> txtCustomer.requestFocusInWindow());
    }

    private JPanel createOrderFormCard() {
        JPanel card = UiTheme.createCardPanel("Place an Order");
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtCustomer = new JTextField(16);
        txtCustomer.setFont(UiTheme.FONT_REGULAR);
        txtCustomer.setToolTipText("Enter the customer's full name");
        txtCustomer.addActionListener(e -> handlePlaceOrder());

        cmbProduct = new JComboBox<>();
        cmbProduct.setFont(UiTheme.FONT_REGULAR);
        cmbProduct.setToolTipText("Select the product to order");

        txtQuantity = new JTextField("1", 4);
        txtQuantity.setFont(UiTheme.FONT_REGULAR);
        txtQuantity.setToolTipText("Quantity of items to order");
        txtQuantity.addActionListener(e -> handlePlaceOrder());

        String[] destinations = {
                "Manila Central Hub", "Quezon City Hub", "Makati Logistics Hub",
                "Pasig Cargo Depot", "Taguig Freight Terminal"
        };
        cmbDestination = new JComboBox<>(destinations);
        cmbDestination.setFont(UiTheme.FONT_REGULAR);
        cmbDestination.setToolTipText("Select destination warehouse or hub");

        String[] priorities = {
                "Express (fastest)", "Priority", "Standard", "Economy (slowest)", "Bulk Saver"
        };
        cmbPriority = new JComboBox<>(priorities);
        cmbPriority.setFont(UiTheme.FONT_REGULAR);
        cmbPriority.setToolTipText("Select delivery speed priority");

        btnPlaceOrder = UiTheme.createPrimaryButton("Place Order", "Submit and add this order to the waiting list");
        btnPlaceOrder.addActionListener(e -> handlePlaceOrder());

        // Row 0: Customer Name & Quantity
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.0; card.add(new JLabel("Customer Name:"), g);
        g.gridx = 1; g.weightx = 1.0; card.add(txtCustomer, g);
        g.gridx = 2; g.weightx = 0.0; card.add(new JLabel("Quantity:"), g);
        g.gridx = 3; g.weightx = 0.25; card.add(txtQuantity, g);

        // Row 1: Product Selection
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.0; card.add(new JLabel("Product:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1.0; card.add(cmbProduct, g);
        g.gridwidth = 1;

        // Row 2: Destination & Priority
        g.gridy = 2;
        g.gridx = 0; g.weightx = 0.0; card.add(new JLabel("Deliver To:"), g);
        g.gridx = 1; g.weightx = 1.0; card.add(cmbDestination, g);
        g.gridx = 2; g.weightx = 0.0; card.add(new JLabel("Delivery Speed:"), g);
        g.gridx = 3; g.weightx = 0.25; card.add(cmbPriority, g);

        // Row 3: Place Order Button
        g.gridy = 3;
        g.gridx = 0; g.gridwidth = 4; g.weightx = 1.0;
        g.insets = new Insets(8, 6, 4, 6);
        card.add(btnPlaceOrder, g);

        return card;
    }

    private JPanel createWaitingOrdersCard() {
        JPanel card = UiTheme.createCardPanel("Waiting Orders");
        card.setLayout(new BorderLayout(0, 8));

        // Table
        tblOrders = new JTable(queueTableModel);
        UiTheme.configureTable(tblOrders);
        tblOrders.setDefaultRenderer(Object.class, UiTheme.createQueueCellRenderer());
        tblOrders.setFillsViewportHeight(true);

        TableColumnModel colModel = tblOrders.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(50);  // # (NEXT)
        colModel.getColumn(1).setPreferredWidth(75);  // Order No.
        colModel.getColumn(2).setPreferredWidth(160); // Customer Name
        colModel.getColumn(3).setPreferredWidth(210); // Product
        colModel.getColumn(4).setPreferredWidth(55);  // Qty
        colModel.getColumn(5).setPreferredWidth(160); // Deliver To
        colModel.getColumn(6).setPreferredWidth(140); // Priority

        JScrollPane scrollPane = new JScrollPane(tblOrders);
        scrollPane.setBorder(new LineBorder(UiTheme.BORDER, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        // Bottom Controls: Next Order summary + Ship & Cancel buttons
        JPanel bottomArea = new JPanel(new BorderLayout(0, 8));
        bottomArea.setBackground(UiTheme.SURFACE);

        lblNextOrderSummary = new JLabel(" Next order to ship: Loading...");
        lblNextOrderSummary.setFont(UiTheme.FONT_SUBTITLE);
        lblNextOrderSummary.setOpaque(true);
        lblNextOrderSummary.setBackground(UiTheme.HIGHLIGHT_QUEUE_ROW0);
        lblNextOrderSummary.setForeground(UiTheme.TEXT_PRIMARY);
        lblNextOrderSummary.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.HIGHLIGHT_QUEUE_ROW0_BORDER, 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        bottomArea.add(lblNextOrderSummary, BorderLayout.NORTH);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setBackground(UiTheme.SURFACE);

        btnShipNext = UiTheme.createPrimaryButton("Ship Next Order", "Ship the next order in line and prompt for weight");
        btnCancelNext = UiTheme.createOutlineDangerButton("Cancel Next Order", "Cancel the next order in line without deducting stock");

        btnShipNext.addActionListener(e -> handleShipNext());
        btnCancelNext.addActionListener(e -> handleCancelNext());

        btnBar.add(btnCancelNext);
        btnBar.add(btnShipNext);
        bottomArea.add(btnBar, BorderLayout.SOUTH);

        card.add(bottomArea, BorderLayout.SOUTH);
        return card;
    }

    public void refreshProductDropdown() {
        ProductComboItem prevSelected = (ProductComboItem) cmbProduct.getSelectedItem();
        cmbProduct.removeAllItems();

        Product[] products = service.getInventoryBst().inOrder();
        if (products != null) {
            for (int i = 0; i < products.length; i++) {
                Product p = products[i];
                ProductComboItem item = new ProductComboItem(p.getSkuId(), p.getName(), p.getStockQty());
                cmbProduct.addItem(item);

                if (prevSelected != null && prevSelected.getSkuId() == p.getSkuId()) {
                    cmbProduct.setSelectedItem(item);
                }
            }
        }
    }

    public void updateButtonStatesAndSummary() {
        boolean queueEmpty = service.getOrderQueue().isEmpty();
        btnShipNext.setEnabled(!queueEmpty);
        btnCancelNext.setEnabled(!queueEmpty);

        if (queueEmpty) {
            lblNextOrderSummary.setText(" No orders waiting. Place an order above.");
            lblNextOrderSummary.setForeground(UiTheme.TEXT_MUTED);
        } else {
            Order front = service.getOrderQueue().peek();
            Product p = service.findProduct(front.getSkuId());
            String productName = p != null ? p.getName() : "Item #" + front.getSkuId();

            lblNextOrderSummary.setText(" Next order to ship: #" + front.getOrderId() + ", "
                    + front.getCustomerName() + ", " + front.getQuantity() + " × " + productName
                    + ", to " + front.getDestination());
            lblNextOrderSummary.setForeground(UiTheme.TEXT_PRIMARY);
        }
    }

    private void handlePlaceOrder() {
        String customer = txtCustomer.getText().trim();
        ProductComboItem selectedProduct = (ProductComboItem) cmbProduct.getSelectedItem();
        String qtyStr = txtQuantity.getText().trim();
        String destination = (String) cmbDestination.getSelectedItem();
        int priority = cmbPriority.getSelectedIndex() + 1;

        // Validation 1: Customer name cannot be empty or only numbers (e.g. "300")
        if (customer.isEmpty() || !customer.matches(".*[a-zA-Z].*")) {
            mainFrame.setFeedback(false, "Please enter a valid customer name (cannot be blank or only numbers).");
            txtCustomer.requestFocus();
            return;
        }

        // Validation 2: Quantity must be a valid positive integer
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                mainFrame.setFeedback(false, "Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Quantity must be a valid whole number.");
            return;
        }

        // Validation 3: Quantity cannot exceed available stock
        if (selectedProduct == null) {
            mainFrame.setFeedback(false, "Please select a product from the list.");
            return;
        }

        if (qty > selectedProduct.getStock()) {
            mainFrame.setFeedback(false, "Only " + selectedProduct.getStock() + " units available in stock.");
            return;
        }

        int skuId = selectedProduct.getSkuId();
        OpResult result = service.enqueueOrder(customer, skuId, qty, destination, priority);
        if (result.isSuccess()) {
            txtCustomer.setText("");
            txtQuantity.setText("1");
            mainFrame.setFeedback(true, "Order #" + (service.getNextOrderId() - 1) + " placed for " + customer + ".");
            mainFrame.refreshAll();
        } else {
            mainFrame.setFeedback(false, result.getMessage());
            JOptionPane.showMessageDialog(this, result.getMessage(), "Order Blocked", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleShipNext() {
        if (service.getOrderQueue().isEmpty()) {
            mainFrame.setFeedback(false, "No orders waiting to ship.");
            return;
        }

        Order front = service.getOrderQueue().peek();
        Product p = service.findProduct(front.getSkuId());
        String productName = p != null ? p.getName() : "Item #" + front.getSkuId();

        String input = JOptionPane.showInputDialog(this,
                "Shipping Order #" + front.getOrderId() + " (" + front.getCustomerName() + ")\n" +
                "Item: " + front.getQuantity() + " × " + productName + "\n" +
                "Deliver To: " + front.getDestination() + "\n\n" +
                "Enter parcel weight in kg (0.01 - 50.00 kg):",
                "Ship Order", JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            return; // Cancelled
        }

        try {
            double weight = Double.parseDouble(input.trim());
            if (weight < 0.01 || weight > 50.00) {
                mainFrame.setFeedback(false, "Parcel weight must be between 0.01 and 50.00 kg.");
                JOptionPane.showMessageDialog(this, "Parcel weight must be between 0.01 and 50.00 kg.", "Invalid Weight", JOptionPane.WARNING_MESSAGE);
                return;
            }
            OpResult result = service.dispatchNext(weight);
            if (result.isSuccess()) {
                mainFrame.setFeedback(true, "Order #" + front.getOrderId() + " shipped to " + front.getDestination() + ".");
                mainFrame.refreshAll();
            } else {
                mainFrame.setFeedback(false, result.getMessage());
                JOptionPane.showMessageDialog(this, result.getMessage(), "Shipping Blocked", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Weight must be a valid number (e.g. 2.45).");
        }
    }

    private void handleCancelNext() {
        if (service.getOrderQueue().isEmpty()) {
            mainFrame.setFeedback(false, "No orders waiting to cancel.");
            return;
        }

        Order front = service.getOrderQueue().peek();
        Product p = service.findProduct(front.getSkuId());
        String productName = p != null ? p.getName() : "Item #" + front.getSkuId();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel the next order for " + front.getCustomerName() + " (" + productName + ")?",
                "Confirm Cancel Order", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            OpResult result = service.rejectFrontOrder();
            if (result.isSuccess()) {
                mainFrame.setFeedback(true, "Order #" + front.getOrderId() + " for " + front.getCustomerName() + " was cancelled.");
                mainFrame.refreshAll();
            } else {
                mainFrame.setFeedback(false, result.getMessage());
            }
        }
    }

    public void refreshTables() {
        queueTableModel.fireTableDataChanged();
        refreshProductDropdown();
        updateButtonStatesAndSummary();

        // Focus cursor in first field
        SwingUtilities.invokeLater(() -> txtCustomer.requestFocusInWindow());
    }
}
