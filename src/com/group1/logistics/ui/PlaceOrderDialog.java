package com.group1.logistics.ui;

import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

public class PlaceOrderDialog extends JDialog {
    private final FulfillmentService service;
    private final MainFrame mainFrame;

    private static final String[] DESTINATIONS = {
            "Manila Central Hub",
            "Quezon City Hub",
            "Makati Logistics Hub"
    };
    private static final String[] DELIVERY_SPEEDS = {
            "Express (fastest)",
            "Standard",
            "Economy (slowest)"
    };
    private static final int[] DELIVERY_SPEED_PRIORITIES = {1, 3, 4};

    private JTextField txtCustomer;
    private JComboBox<OrdersPanel.ProductComboItem> cmbProduct;
    private JTextField txtQuantity;
    private JComboBox<String> cmbDestination;
    private JComboBox<String> cmbPriority;
    private JLabel lblError;
    private JButton btnCancel;
    private JButton btnPlace;
    private boolean orderPlaced = false;
    private boolean submitting = false;
    private boolean refreshingProducts = false;

    public PlaceOrderDialog(Frame parent, FulfillmentService service, MainFrame mainFrame) {
        super(parent, "Place Order", true);
        this.service = service;
        this.mainFrame = mainFrame;

        setUndecorated(true);
        setSize(480, 500);
        setLocationRelativeTo(parent);

        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBackground(UiTheme.SURFACE);
        contentPane.setBorder(new LineBorder(new Color(0xD1, 0xD5, 0xDB), 1, true));

        contentPane.add(createHeader(), BorderLayout.NORTH);

        contentPane.add(createBodyForm(), BorderLayout.CENTER);

        contentPane.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(contentPane);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        loadProducts();
        SwingUtilities.invokeLater(() -> txtCustomer.requestFocusInWindow());
    }

    public boolean isOrderPlaced() {
        return orderPlaced;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiTheme.SURFACE);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(0xF3, 0xF4, 0xF6)),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleBox.setOpaque(false);

        JLabel lblIcon = new JLabel(new UiTheme.VectorIcon(UiTheme.IconType.SHIP, 16, UiTheme.TEXT_PRIMARY));
        JLabel lblTitle = new JLabel("Place New Order");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);

        titleBox.add(lblIcon);
        titleBox.add(lblTitle);
        header.add(titleBox, BorderLayout.WEST);

        JButton btnClose = new JButton(new UiTheme.VectorIcon(UiTheme.IconType.CLOSE, 12, new Color(0x9C, 0xA3, 0xAF)));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setToolTipText("Close");
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);

        return header;
    }

    private JPanel createBodyForm() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UiTheme.SURFACE);
        body.setBorder(new EmptyBorder(14, 20, 14, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtCustomer = new JTextField();
        cmbProduct = new JComboBox<>();
        txtQuantity = new JTextField("1");

        cmbDestination = new JComboBox<>(DESTINATIONS);
        cmbPriority = new JComboBox<>(DELIVERY_SPEEDS);
        cmbPriority.setSelectedIndex(1);

        UiTheme.installFocusRing(txtCustomer);
        UiTheme.installFocusRing(txtQuantity);

        txtCustomer.setFont(UiTheme.FONT_REGULAR);
        cmbProduct.setFont(UiTheme.FONT_REGULAR);
        txtQuantity.setFont(UiTheme.FONT_REGULAR);
        cmbDestination.setFont(UiTheme.FONT_REGULAR);
        cmbPriority.setFont(UiTheme.FONT_REGULAR);

        txtCustomer.addActionListener(e -> submit());
        txtQuantity.addActionListener(e -> submit());
        cmbProduct.addActionListener(e -> {
            if (!refreshingProducts && lblError != null) {
                lblError.setText(" ");
            }
        });

        g.gridy = 0; g.gridx = 0; g.weightx = 1.0;
        body.add(createFieldLabel("Customer Name *", "Recipient full name"), g);
        g.gridy = 1; g.gridx = 0;
        body.add(txtCustomer, g);

        g.gridy = 2; g.gridx = 0;
        body.add(createFieldLabel("Product *", "Select item to order"), g);
        g.gridy = 3; g.gridx = 0;
        body.add(cmbProduct, g);

        JPanel rowQtyPriority = new JPanel(new GridLayout(1, 2, 12, 0));
        rowQtyPriority.setOpaque(false);

        JPanel pnlQty = new JPanel(new BorderLayout(0, 4));
        pnlQty.setOpaque(false);
        pnlQty.add(createFieldLabel("Quantity *", "Items to order"), BorderLayout.NORTH);
        pnlQty.add(txtQuantity, BorderLayout.CENTER);

        JPanel pnlPri = new JPanel(new BorderLayout(0, 4));
        pnlPri.setOpaque(false);
        pnlPri.add(createFieldLabel("Delivery Speed *", "Urgency tier"), BorderLayout.NORTH);
        pnlPri.add(cmbPriority, BorderLayout.CENTER);

        rowQtyPriority.add(pnlQty);
        rowQtyPriority.add(pnlPri);

        g.gridy = 4; g.gridx = 0;
        body.add(rowQtyPriority, g);

        g.gridy = 5; g.gridx = 0;
        body.add(createFieldLabel("Deliver To *", "Target warehouse hub"), g);
        g.gridy = 6; g.gridx = 0;
        body.add(cmbDestination, g);

        lblError = new JLabel(" ");
        lblError.setFont(UiTheme.FONT_HINT);
        lblError.setForeground(UiTheme.DANGER_RED);
        g.gridy = 7; g.gridx = 0;
        body.add(lblError, g);

        return body;
    }

    private JLabel createFieldLabel(String text, String hint) {
        JLabel lbl = new JLabel("<html><b>" + text + "</b> <span style='color: #9CA3AF; font-size: 11px;'>(" + hint + ")</span></html>");
        lbl.setFont(UiTheme.FONT_REGULAR);
        lbl.setForeground(UiTheme.TEXT_PRIMARY);
        return lbl;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(UiTheme.SURFACE_ALT);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, new Color(0xF3, 0xF4, 0xF6)));

        btnCancel = UiTheme.createSecondaryButton("Cancel", "Discard and close");
        btnCancel.addActionListener(e -> dispose());

        btnPlace = UiTheme.createPrimaryButton("Place Order", new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 14), "Submit and enqueue this order");
        btnPlace.addActionListener(e -> submit());

        footer.add(btnCancel);
        footer.add(btnPlace);
        return footer;
    }

    private void loadProducts() {
        OrdersPanel.ProductComboItem prev = (OrdersPanel.ProductComboItem) cmbProduct.getSelectedItem();
        int selectedSkuId = prev != null ? prev.getSkuId() : -1;
        refreshingProducts = true;
        try {
            cmbProduct.removeAllItems();
            Product[] products = service.getInventoryBst().inOrder();
            int restoreIndex = 0;
            if (products != null) {
                for (int i = 0; i < products.length; i++) {
                    Product p = products[i];
                    int available = service.getAvailableStock(p.getSkuId());
                    cmbProduct.addItem(new OrdersPanel.ProductComboItem(p.getSkuId(), p.getName(), available));
                    if (p.getSkuId() == selectedSkuId) {
                        restoreIndex = i;
                    }
                }
            }
            if (cmbProduct.getItemCount() > 0) {
                cmbProduct.setSelectedIndex(restoreIndex);
            }
        } finally {
            refreshingProducts = false;
        }
    }

    private void submit() {
        if (submitting || orderPlaced || !isDisplayable()) {
            return;
        }
        String customer = txtCustomer.getText().trim();
        OrdersPanel.ProductComboItem selectedProduct = (OrdersPanel.ProductComboItem) cmbProduct.getSelectedItem();
        String qtyStr = txtQuantity.getText().trim();
        String destination = (String) cmbDestination.getSelectedItem();
        int speedIdx = cmbPriority.getSelectedIndex();
        int priority = (speedIdx >= 0 && speedIdx < DELIVERY_SPEED_PRIORITIES.length)
                ? DELIVERY_SPEED_PRIORITIES[speedIdx]
                : 3;

        if (customer.isEmpty()) {
            lblError.setText("Please enter the customer's name.");
            return;
        }

        boolean hasLetter = false;
        for (int i = 0; i < customer.length(); i++) {
            if (Character.isLetter(customer.charAt(i))) {
                hasLetter = true;
                break;
            }
        }
        if (!hasLetter) {
            lblError.setText("Customer name must contain letters, not only numbers.");
            return;
        }

        if (selectedProduct == null) {
            lblError.setText("Please select a product from the list.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                lblError.setText("Order quantity must be at least 1.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblError.setText("Quantity must be a valid whole number.");
            return;
        }

        int liveAvailable = service.getAvailableStock(selectedProduct.getSkuId());
        if (liveAvailable <= 0) {
            if (selectedProduct.getStock() != liveAvailable) {
                loadProducts();
            }
            lblError.setText(selectedProduct.getName() + " is out of stock (all units are reserved or depleted).");
            return;
        }
        if (quantity > liveAvailable) {
            if (selectedProduct.getStock() != liveAvailable) {
                loadProducts();
            }
            lblError.setText("Only " + liveAvailable + " unreserved units available in stock.");
            return;
        }

        submitting = true;
        btnPlace.setEnabled(false);
        btnCancel.setEnabled(false);
        OpResult result = service.enqueueOrder(customer, selectedProduct.getSkuId(), quantity, destination, priority);
        if (result.isSuccess()) {
            this.orderPlaced = true;
            mainFrame.setFeedback(true, "Order placed for " + customer + " (" + quantity + "x " + selectedProduct.getName() + ").");
            mainFrame.refreshAll();
            setVisible(false);
            dispose();
        } else {
            submitting = false;
            btnPlace.setEnabled(true);
            btnCancel.setEnabled(true);
            loadProducts();
            lblError.setText(result.getMessage());
        }
    }
}
