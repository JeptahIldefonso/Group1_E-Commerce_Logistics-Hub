package com.group1.logistics.ui;

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

/**
 * Tab 3: Restock.
 * Dedicated inventory replenishment tab with LIFO rollback.
 * Pure standard Java Swing; zero Java Collections.
 */
public class RestockPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final RestockTableModel restockTableModel;

    // Form inputs
    private JComboBox<OrdersPanel.ProductComboItem> cmbRestockProduct;
    private JTextField txtRestockQty;
    private JButton btnAddStock;

    // Action button & cues
    private JButton btnUndo;
    private JLabel lblLastRestockSummary;
    private JTable tblRestock;

    public RestockPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.restockTableModel = new RestockTableModel(service.getRestockStack(), service);

        setLayout(new BorderLayout(0, 10));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Top Form: "Add Stock to a Product"
        add(createRestockFormCard(), BorderLayout.NORTH);

        // 2. Center Panel: "Restock History"
        add(createRestockHistoryCard(), BorderLayout.CENTER);

        refreshProductDropdown();
        updateButtonStatesAndSummary();
        SwingUtilities.invokeLater(() -> txtRestockQty.requestFocusInWindow());
    }

    public void setTechnicalDetails(boolean enabled) {
        restockTableModel.setTechnicalDetails(enabled);
    }

    private JPanel createRestockFormCard() {
        JPanel card = UiTheme.createCardPanel("Add Stock to a Product");
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        cmbRestockProduct = new JComboBox<>();
        cmbRestockProduct.setFont(UiTheme.FONT_REGULAR);
        cmbRestockProduct.setToolTipText("Select the product to add stock to");

        txtRestockQty = new JTextField("10", 6);
        txtRestockQty.setFont(UiTheme.FONT_REGULAR);
        txtRestockQty.setToolTipText("Number of units added to inventory");
        txtRestockQty.addActionListener(e -> handleAddStock());

        btnAddStock = UiTheme.createSuccessButton("Add Stock", "Increase stock for this product and log entry");
        btnAddStock.addActionListener(e -> handleAddStock());

        // Row 0: Select Product & Quantity
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.0; card.add(new JLabel("Product:"), g);
        g.gridx = 1; g.weightx = 1.0; card.add(cmbRestockProduct, g);
        g.gridx = 2; g.weightx = 0.0; card.add(new JLabel("Quantity to Add:"), g);
        g.gridx = 3; g.weightx = 0.25; card.add(txtRestockQty, g);

        // Row 1: Add Stock Button
        g.gridy = 1;
        g.gridx = 0; g.gridwidth = 4; g.weightx = 1.0;
        g.insets = new Insets(8, 6, 4, 6);
        card.add(btnAddStock, g);

        return card;
    }

    private JPanel createRestockHistoryCard() {
        JPanel card = UiTheme.createCardPanel("Restock History");
        card.setLayout(new BorderLayout(0, 8));

        // Table
        tblRestock = new JTable(restockTableModel);
        UiTheme.configureTable(tblRestock);
        tblRestock.setDefaultRenderer(Object.class, UiTheme.createStackCellRenderer());
        tblRestock.setFillsViewportHeight(true);

        TableColumnModel colModel = tblRestock.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100); // Restock #
        colModel.getColumn(1).setPreferredWidth(260); // Product
        colModel.getColumn(2).setPreferredWidth(120); // Quantity Added
        colModel.getColumn(3).setPreferredWidth(110); // Order of Entry

        JScrollPane scrollPane = new JScrollPane(tblRestock);
        scrollPane.setBorder(new LineBorder(UiTheme.BORDER, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        // Bottom Controls: Summary + Undo button
        JPanel bottomArea = new JPanel(new BorderLayout(0, 8));
        bottomArea.setBackground(UiTheme.SURFACE);

        lblLastRestockSummary = new JLabel(" Last Restock: Loading...");
        lblLastRestockSummary.setFont(UiTheme.FONT_SUBTITLE);
        lblLastRestockSummary.setOpaque(true);
        lblLastRestockSummary.setBackground(UiTheme.HIGHLIGHT_STACK_ROW0);
        lblLastRestockSummary.setForeground(UiTheme.TEXT_PRIMARY);
        lblLastRestockSummary.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.HIGHLIGHT_STACK_ROW0_BORDER, 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        bottomArea.add(lblLastRestockSummary, BorderLayout.NORTH);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setBackground(UiTheme.SURFACE);

        btnUndo = UiTheme.createWarningButton("Undo Last Restock", "Roll back the most recent restock and subtract stock");
        btnUndo.addActionListener(e -> handleUndo());
        btnBar.add(btnUndo);

        bottomArea.add(btnBar, BorderLayout.SOUTH);
        card.add(bottomArea, BorderLayout.SOUTH);

        return card;
    }

    public void refreshProductDropdown() {
        OrdersPanel.ProductComboItem prevSelected = (OrdersPanel.ProductComboItem) cmbRestockProduct.getSelectedItem();
        cmbRestockProduct.removeAllItems();

        Product[] products = service.getInventoryBst().inOrder();
        if (products != null) {
            for (int i = 0; i < products.length; i++) {
                Product p = products[i];
                OrdersPanel.ProductComboItem item = new OrdersPanel.ProductComboItem(p.getSkuId(), p.getName(), p.getStockQty());
                cmbRestockProduct.addItem(item);

                if (prevSelected != null && prevSelected.getSkuId() == p.getSkuId()) {
                    cmbRestockProduct.setSelectedItem(item);
                }
            }
        }
    }

    public void updateButtonStatesAndSummary() {
        boolean stackEmpty = service.getRestockStack().isEmpty();
        btnUndo.setEnabled(!stackEmpty);

        if (stackEmpty) {
            lblLastRestockSummary.setText(" No restock actions to undo.");
            lblLastRestockSummary.setForeground(UiTheme.TEXT_MUTED);
        } else {
            RestockAction top = service.getRestockStack().peek();
            Product p = service.findProduct(top.getSkuId());
            String productName = p != null ? p.getName() : "Item #" + top.getSkuId();

            lblLastRestockSummary.setText(" Last Restock: Restock #" + top.getActionId()
                    + " · Added +" + top.getQuantityAdded() + " units to " + productName);
            lblLastRestockSummary.setForeground(UiTheme.TEXT_PRIMARY);
        }
    }

    private void handleAddStock() {
        OrdersPanel.ProductComboItem selectedProduct = (OrdersPanel.ProductComboItem) cmbRestockProduct.getSelectedItem();
        String qtyStr = txtRestockQty.getText().trim();

        if (selectedProduct == null) {
            mainFrame.setFeedback(false, "Please select a product to restock.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                mainFrame.setFeedback(false, "Quantity to add must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Quantity must be a valid whole number.");
            return;
        }

        OpResult result = service.restock(selectedProduct.getSkuId(), qty);
        if (result.isSuccess()) {
            txtRestockQty.setText("10");
            mainFrame.setFeedback(true, "Added +" + qty + " units to " + selectedProduct.getName() + ".");
            mainFrame.refreshAll();
        } else {
            mainFrame.setFeedback(false, result.getMessage());
            JOptionPane.showMessageDialog(this, result.getMessage(), "Restock Blocked", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUndo() {
        if (service.getRestockStack().isEmpty()) {
            mainFrame.setFeedback(false, "No restock actions to undo.");
            return;
        }

        RestockAction top = service.getRestockStack().peek();
        Product p = service.findProduct(top.getSkuId());
        String productName = p != null ? p.getName() : "Item #" + top.getSkuId();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to undo the last restock of " + productName + " (+" + top.getQuantityAdded() + " units)?",
                "Confirm Undo Restock", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            OpResult result = service.undoRestock();
            if (result.isSuccess()) {
                mainFrame.setFeedback(true, "Undid restock for " + productName + " (-" + top.getQuantityAdded() + " units).");
                mainFrame.refreshAll();
            } else {
                mainFrame.setFeedback(false, result.getMessage());
                JOptionPane.showMessageDialog(this, result.getMessage(), "Undo Blocked", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public void refreshTables() {
        restockTableModel.fireTableDataChanged();
        refreshProductDropdown();
        updateButtonStatesAndSummary();
        SwingUtilities.invokeLater(() -> txtRestockQty.requestFocusInWindow());
    }
}
