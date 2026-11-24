package com.group1.logistics.ui;

import com.group1.logistics.models.Order;
import com.group1.logistics.models.Product;
import com.group1.logistics.models.RestockAction;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Tab 1: Orders & Restock Desk (Circular Queue FIFO & Array Stack LIFO).
 * Modern 62/38 split layout, dynamic SKU dropdowns, visual FIFO/LIFO cues, and responsive button states.
 * 100% pure standard Swing; zero Java Collections.
 */
public class OrdersRestockPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;

    private final QueueTableModel queueTableModel;
    private final RestockTableModel restockTableModel;

    // Order form inputs
    private JTextField txtCustomer;
    private JComboBox<ProductComboItem> cmbOrderSku;
    private JTextField txtQuantity;
    private JComboBox<String> cmbDestination;
    private JComboBox<String> cmbPriority;
    private JButton btnQueue;
    private JButton btnDispatch;
    private JButton btnReject;
    private JLabel lblQueueFrontSummary;
    private JTable tblQueue;

    // Restock form inputs
    private JComboBox<ProductComboItem> cmbRestockSku;
    private JTextField txtRestockQty;
    private JButton btnRestock;
    private JButton btnUndo;
    private JLabel lblStackTopSummary;
    private JTable tblRestock;

    /**
     * Helper representation for JComboBox items without importing java.util.Vector or Collections.
     */
    public static class ProductComboItem {
        private final int skuId;
        private final String displayText;

        public ProductComboItem(int skuId, String displayText) {
            this.skuId = skuId;
            this.displayText = displayText;
        }

        public int getSkuId() {
            return skuId;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    public OrdersRestockPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.queueTableModel = new QueueTableModel(service.getOrderQueue());
        this.restockTableModel = new RestockTableModel(service.getRestockStack());

        setLayout(new BorderLayout(0, 10));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        // Top 1-Line Plain-English Workflow Banner
        add(UiTheme.createWorkflowBanner(
                "Manage customer intake queue (FIFO) and audit inventory restocks with safe rollback (LIFO).",
                "Linear ADTs (Circular Queue & Array Stack)"
        ), BorderLayout.NORTH);

        // Center Proportional Split (~62% Left Order Queue / ~38% Right Restock Stack)
        JPanel centerSplit = new JPanel(new GridBagLayout());
        centerSplit.setBackground(UiTheme.BG_WINDOW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Subpanel: Order Queue (weightx = 0.62)
        gbc.gridx = 0;
        gbc.weightx = 0.62;
        gbc.insets = new Insets(0, 0, 0, 10);
        centerSplit.add(createOrderQueueSubPanel(), gbc);

        // Right Subpanel: Restock Stack (weightx = 0.38)
        gbc.gridx = 1;
        gbc.weightx = 0.38;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerSplit.add(createRestockStackSubPanel(), gbc);

        add(centerSplit, BorderLayout.CENTER);

        // Populate dynamic SKU combos from InventoryBST
        refreshSkuDropdowns();
        updateCuesAndButtonStates();
    }

    private JPanel createOrderQueueSubPanel() {
        JPanel card = UiTheme.createCardPanel("Customer Order Intake & Dispatch Line (Circular Queue — FIFO)");
        card.setLayout(new BorderLayout(0, 8));

        // Form Container
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.SURFACE);
        form.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_CARD, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtCustomer = new JTextField(15);
        txtCustomer.setFont(UiTheme.FONT_REGULAR);
        txtCustomer.addActionListener(e -> handleEnqueue());

        cmbOrderSku = new JComboBox<>();
        cmbOrderSku.setFont(UiTheme.FONT_REGULAR);

        txtQuantity = new JTextField("1", 5);
        txtQuantity.setFont(UiTheme.FONT_REGULAR);
        txtQuantity.addActionListener(e -> handleEnqueue());

        String[] destinations = {
                "Manila Central Hub", "Quezon City Hub", "Makati Logistics Hub",
                "Pasig Cargo Depot", "Taguig Freight Terminal"
        };
        cmbDestination = new JComboBox<>(destinations);
        cmbDestination.setFont(UiTheme.FONT_REGULAR);

        String[] priorities = {
                "1 - Express", "2 - Priority", "3 - Standard", "4 - Economy", "5 - Bulk Saver"
        };
        cmbPriority = new JComboBox<>(priorities);
        cmbPriority.setFont(UiTheme.FONT_REGULAR);

        btnQueue = UiTheme.createStyledButton(
                "Queue Order (Enqueue)",
                UiTheme.PRIMARY_BLUE,
                Color.WHITE,
                "Adds customer order to the circular queue rear in O(1) time"
        );
        btnQueue.addActionListener(e -> handleEnqueue());

        // Row 0: Customer Name & Quantity
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Customer Name:"), g);
        g.gridx = 1; g.weightx = 1.0; form.add(txtCustomer, g);
        g.gridx = 2; g.weightx = 0.0; form.add(new JLabel("Quantity:"), g);
        g.gridx = 3; g.weightx = 0.3; form.add(txtQuantity, g);

        // Row 1: Select SKU from dynamic tree
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Select SKU:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1.0; form.add(cmbOrderSku, g);
        g.gridwidth = 1; // reset

        // Row 2: Destination & Priority
        g.gridy = 2;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Destination Hub:"), g);
        g.gridx = 1; g.weightx = 1.0; form.add(cmbDestination, g);
        g.gridx = 2; g.weightx = 0.0; form.add(new JLabel("Priority:"), g);
        g.gridx = 3; g.weightx = 0.3; form.add(cmbPriority, g);

        // Row 3: Enqueue Button
        g.gridy = 3;
        g.gridx = 0; g.gridwidth = 4; g.weightx = 1.0;
        g.insets = new Insets(6, 4, 2, 4);
        form.add(btnQueue, g);

        card.add(form, BorderLayout.NORTH);

        // Table
        tblQueue = new JTable(queueTableModel);
        UiTheme.configureTable(tblQueue);
        tblQueue.setDefaultRenderer(Object.class, UiTheme.createQueueCellRenderer());
        tblQueue.setFillsViewportHeight(true);

        // Explicit column widths to completely eliminate "..." truncation
        TableColumnModel colModel = tblQueue.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(35);  // #
        colModel.getColumn(1).setPreferredWidth(70);  // Order ID
        colModel.getColumn(2).setPreferredWidth(135); // Customer Name
        colModel.getColumn(3).setPreferredWidth(55);  // SKU
        colModel.getColumn(4).setPreferredWidth(45);  // Qty
        colModel.getColumn(5).setPreferredWidth(145); // Destination
        colModel.getColumn(6).setPreferredWidth(95);  // Priority

        JScrollPane scrollPane = new JScrollPane(tblQueue);
        scrollPane.setBorder(new LineBorder(UiTheme.BORDER, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        // Bottom Dispatch & Reject Area
        JPanel southArea = new JPanel(new BorderLayout(0, 6));
        southArea.setBackground(UiTheme.SURFACE);

        // Front-of-Queue Visual Summary Card
        lblQueueFrontSummary = new JLabel(" Front of Queue: Loading...");
        lblQueueFrontSummary.setFont(UiTheme.FONT_SUBTITLE);
        lblQueueFrontSummary.setOpaque(true);
        lblQueueFrontSummary.setBackground(UiTheme.HIGHLIGHT_QUEUE_ROW0);
        lblQueueFrontSummary.setForeground(UiTheme.TEXT_PRIMARY);
        lblQueueFrontSummary.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.HIGHLIGHT_QUEUE_ROW0_BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        southArea.add(lblQueueFrontSummary, BorderLayout.NORTH);

        // Action Buttons Bar
        JPanel btnBar = new JPanel(new GridLayout(1, 2, 8, 0));
        btnBar.setBackground(UiTheme.SURFACE);

        btnDispatch = UiTheme.createStyledButton(
                "Dispatch Next Order (FIFO)",
                UiTheme.PRIMARY_BLUE,
                Color.WHITE,
                "Dispatches the front order in the circular queue (FIFO) and prompts cargo weight (BR-08)"
        );
        btnReject = UiTheme.createStyledButton(
                "Reject Front Order",
                UiTheme.DANGER_RED,
                Color.WHITE,
                "Removes the front order from the circular queue without deducting stock"
        );

        btnDispatch.addActionListener(e -> handleDispatch());
        btnReject.addActionListener(e -> handleReject());

        btnBar.add(btnDispatch);
        btnBar.add(btnReject);
        southArea.add(btnBar, BorderLayout.SOUTH);

        card.add(southArea, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createRestockStackSubPanel() {
        JPanel card = UiTheme.createCardPanel("Inventory Restock Audit & Undo (Stack — LIFO)");
        card.setLayout(new BorderLayout(0, 8));

        // Form Container
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.SURFACE);
        form.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_CARD, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        cmbRestockSku = new JComboBox<>();
        cmbRestockSku.setFont(UiTheme.FONT_REGULAR);

        txtRestockQty = new JTextField("10", 6);
        txtRestockQty.setFont(UiTheme.FONT_REGULAR);
        txtRestockQty.addActionListener(e -> handleRestock());

        btnRestock = UiTheme.createStyledButton(
                "Apply Restock (Push)",
                UiTheme.POSITIVE_GREEN,
                Color.WHITE,
                "Increments inventory stock and pushes an audit entry onto the LIFO stack"
        );
        btnRestock.addActionListener(e -> handleRestock());

        // Row 0: Select SKU to restock
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("SKU:"), g);
        g.gridx = 1; g.weightx = 1.0; form.add(cmbRestockSku, g);

        // Row 1: Quantity to Add
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Qty to Add:"), g);
        g.gridx = 1; g.weightx = 1.0; form.add(txtRestockQty, g);

        // Row 2: Push Button
        g.gridy = 2;
        g.gridx = 0; g.gridwidth = 2; g.weightx = 1.0;
        g.insets = new Insets(6, 4, 2, 4);
        form.add(btnRestock, g);

        card.add(form, BorderLayout.NORTH);

        // Table
        tblRestock = new JTable(restockTableModel);
        UiTheme.configureTable(tblRestock);
        tblRestock.setDefaultRenderer(Object.class, UiTheme.createStackCellRenderer());
        tblRestock.setFillsViewportHeight(true);

        TableColumnModel colModel = tblRestock.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(85); // Action ID
        colModel.getColumn(1).setPreferredWidth(80); // SKU ID
        colModel.getColumn(2).setPreferredWidth(95); // Units Added
        colModel.getColumn(3).setPreferredWidth(80); // Sequence No

        JScrollPane scrollPane = new JScrollPane(tblRestock);
        scrollPane.setBorder(new LineBorder(UiTheme.BORDER, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        // Bottom Undo Area
        JPanel southArea = new JPanel(new BorderLayout(0, 6));
        southArea.setBackground(UiTheme.SURFACE);

        // Top-of-Stack Visual Summary Card
        lblStackTopSummary = new JLabel(" Top of Stack: Loading...");
        lblStackTopSummary.setFont(UiTheme.FONT_SUBTITLE);
        lblStackTopSummary.setOpaque(true);
        lblStackTopSummary.setBackground(UiTheme.HIGHLIGHT_STACK_ROW0);
        lblStackTopSummary.setForeground(UiTheme.TEXT_PRIMARY);
        lblStackTopSummary.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.HIGHLIGHT_STACK_ROW0_BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        southArea.add(lblStackTopSummary, BorderLayout.NORTH);

        btnUndo = UiTheme.createStyledButton(
                "Undo Last Restock (LIFO Rollback)",
                UiTheme.WARNING_AMBER,
                Color.WHITE,
                "Pops top restock action from the LIFO stack and safely reverts inventory stock"
        );
        btnUndo.addActionListener(e -> handleUndo());
        southArea.add(btnUndo, BorderLayout.SOUTH);

        card.add(southArea, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Refreshes the dynamic SKU dropdowns directly from the InventoryBST in-order traversal.
     * Complies with zero collections rule.
     */
    public void refreshSkuDropdowns() {
        ProductComboItem prevOrderSelected = (ProductComboItem) cmbOrderSku.getSelectedItem();
        ProductComboItem prevRestockSelected = (ProductComboItem) cmbRestockSku.getSelectedItem();

        cmbOrderSku.removeAllItems();
        cmbRestockSku.removeAllItems();

        Product[] products = service.getInventoryBst().inOrder();
        if (products != null) {
            for (int i = 0; i < products.length; i++) {
                Product p = products[i];
                ProductComboItem item = new ProductComboItem(
                        p.getSkuId(),
                        "SKU #" + p.getSkuId() + " — " + p.getName() + " (Stock: " + p.getStockQty() + ")"
                );
                cmbOrderSku.addItem(item);
                cmbRestockSku.addItem(item);

                // Restore previous selection if possible
                if (prevOrderSelected != null && prevOrderSelected.getSkuId() == p.getSkuId()) {
                    cmbOrderSku.setSelectedItem(item);
                }
                if (prevRestockSelected != null && prevRestockSelected.getSkuId() == p.getSkuId()) {
                    cmbRestockSku.setSelectedItem(item);
                }
            }
        }
    }

    /**
     * Updates the summary cues (Front of Queue & Top of Stack) and enables/disables action buttons.
     */
    public void updateCuesAndButtonStates() {
        // Order Queue Cues
        boolean queueEmpty = service.getOrderQueue().isEmpty();
        btnDispatch.setEnabled(!queueEmpty);
        btnReject.setEnabled(!queueEmpty);

        if (queueEmpty) {
            lblQueueFrontSummary.setText(" (i) Order Queue is empty — waiting for incoming customer orders.");
            lblQueueFrontSummary.setForeground(UiTheme.TEXT_MUTED);
        } else {
            Order front = service.getOrderQueue().peek();
            lblQueueFrontSummary.setText(" Front of Queue: #" + front.getOrderId() + " · " + front.getCustomerName() +
                    " · SKU #" + front.getSkuId() + " (Qty: " + front.getQuantity() + ") → " + front.getDestination());
            lblQueueFrontSummary.setForeground(UiTheme.TEXT_PRIMARY);
        }

        // Restock Stack Cues
        boolean stackEmpty = service.getRestockStack().isEmpty();
        btnUndo.setEnabled(!stackEmpty);

        if (stackEmpty) {
            lblStackTopSummary.setText(" (i) Restock stack is empty — no actions to undo.");
            lblStackTopSummary.setForeground(UiTheme.TEXT_MUTED);
        } else {
            RestockAction top = service.getRestockStack().peek();
            lblStackTopSummary.setText(" Top of Stack (Will Undo): Action #" + top.getActionId() +
                    " · SKU #" + top.getSkuId() + " (+" + top.getQuantityAdded() + " units) · Seq #" + top.getSequenceNo());
            lblStackTopSummary.setForeground(UiTheme.TEXT_PRIMARY);
        }
    }

    private void handleEnqueue() {
        String customer = txtCustomer.getText().trim();
        ProductComboItem selectedProduct = (ProductComboItem) cmbOrderSku.getSelectedItem();
        String qtyStr = txtQuantity.getText().trim();
        String destination = (String) cmbDestination.getSelectedItem();
        int priority = cmbPriority.getSelectedIndex() + 1;

        if (customer.isEmpty() || selectedProduct == null || qtyStr.isEmpty()) {
            mainFrame.setFeedback(false, "Validation Error: Please enter customer name and valid quantity.");
            JOptionPane.showMessageDialog(this, "Please enter customer name and quantity.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = selectedProduct.getSkuId();
            int qty = Integer.parseInt(qtyStr);

            OpResult result = service.enqueueOrder(customer, skuId, qty, destination, priority);
            mainFrame.setFeedback(result);
            if (result.isSuccess()) {
                txtCustomer.setText("");
                txtQuantity.setText("1");
                mainFrame.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Order Rejected", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Input Error: Quantity must be a valid positive integer.");
            JOptionPane.showMessageDialog(this, "Quantity must be a valid positive integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDispatch() {
        if (service.getOrderQueue().isEmpty()) {
            mainFrame.setFeedback(false, "Dispatch Notice: Order Queue is empty. No orders waiting.");
            return;
        }

        Order front = service.getOrderQueue().peek();

        // Prompt weight dialog (BR-08)
        String input = JOptionPane.showInputDialog(this,
                "Dispatching Order #" + front.getOrderId() + " (" + front.getCustomerName() + ")\n" +
                "Item: SKU #" + front.getSkuId() + " (Qty: " + front.getQuantity() + ")\n" +
                "Destination: " + front.getDestination() + " (Priority " + front.getDeliveryPriority() + ")\n\n" +
                "Enter parcel cargo packing weight in kg (0.01 - 1000.00):",
                "Parcel Packing Weight (BR-08)", JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            return; // Operator cancelled
        }

        try {
            double weight = Double.parseDouble(input.trim());
            OpResult result = service.dispatchNext(weight);
            mainFrame.setFeedback(result);
            if (result.isSuccess()) {
                mainFrame.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Dispatch Blocked", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Invalid Weight: Cargo weight must be a valid number (e.g. 2.45).");
            JOptionPane.showMessageDialog(this, "Weight must be a valid number (e.g. 2.45).", "Invalid Weight", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReject() {
        if (service.getOrderQueue().isEmpty()) {
            mainFrame.setFeedback(false, "Order Queue is already empty.");
            return;
        }
        OpResult result = service.rejectFrontOrder();
        mainFrame.setFeedback(result);
        mainFrame.refreshAll();
    }

    private void handleRestock() {
        ProductComboItem selectedProduct = (ProductComboItem) cmbRestockSku.getSelectedItem();
        String qtyStr = txtRestockQty.getText().trim();

        if (selectedProduct == null || qtyStr.isEmpty()) {
            mainFrame.setFeedback(false, "Validation Error: Please select SKU and enter quantity to add.");
            return;
        }

        try {
            int skuId = selectedProduct.getSkuId();
            int qty = Integer.parseInt(qtyStr);

            OpResult result = service.restock(skuId, qty);
            mainFrame.setFeedback(result);
            if (result.isSuccess()) {
                txtRestockQty.setText("10");
                mainFrame.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Restock Blocked", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Input Error: Quantity to add must be a positive integer.");
            JOptionPane.showMessageDialog(this, "Quantity to add must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUndo() {
        if (service.getRestockStack().isEmpty()) {
            mainFrame.setFeedback(false, "Notice: Restock stack is empty.");
            return;
        }
        OpResult result = service.undoRestock();
        mainFrame.setFeedback(result);
        if (result.isSuccess()) {
            mainFrame.refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Undo Blocked", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshTables() {
        queueTableModel.fireTableDataChanged();
        restockTableModel.fireTableDataChanged();
        refreshSkuDropdowns();
        updateCuesAndButtonStates();
    }
}
