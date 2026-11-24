package com.group1.logistics.ui;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Modal dialog for registering a new product into the catalog.
 * Modern SaaS card popup matching Image 3 design.
 * Pure standard Java Swing; zero Java Collections.
 */
public class AddProductDialog extends JDialog {
    private final FulfillmentService service;
    private final MainFrame mainFrame;

    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtStock;
    private JLabel lblError;
    private boolean productAdded = false;

    public AddProductDialog(Frame parent, FulfillmentService service, MainFrame mainFrame) {
        super(parent, "Add Product", true);
        this.service = service;
        this.mainFrame = mainFrame;

        setUndecorated(true); // Custom clean SaaS header border
        setSize(480, 460);
        setLocationRelativeTo(parent);

        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBackground(UiTheme.SURFACE);
        contentPane.setBorder(new LineBorder(new Color(0xD1, 0xD5, 0xDB), 1, true));

        // 1. Header (Title + Close Button)
        contentPane.add(createHeader(), BorderLayout.NORTH);

        // 2. Body Form
        contentPane.add(createBodyForm(), BorderLayout.CENTER);

        // 3. Footer (Cancel + Add Product buttons)
        contentPane.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(contentPane);

        // Escape key to close
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        SwingUtilities.invokeLater(() -> txtCode.requestFocusInWindow());
    }

    public boolean isProductAdded() {
        return productAdded;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiTheme.SURFACE);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(0xF3, 0xF4, 0xF6)),
                new EmptyBorder(14, 18, 14, 18)
        ));

        // Title with Icon
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleBox.setOpaque(false);

        JLabel lblIcon = new JLabel(new UiTheme.VectorIcon(UiTheme.IconType.BOX, 16, UiTheme.TEXT_PRIMARY));
        JLabel lblTitle = new JLabel("Add Product");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);

        titleBox.add(lblIcon);
        titleBox.add(lblTitle);
        header.add(titleBox, BorderLayout.WEST);

        // Close X button
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
        body.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 4, 6, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtCode = new JTextField();
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();

        UiTheme.installFocusRing(txtCode);
        UiTheme.installFocusRing(txtName);
        UiTheme.installFocusRing(txtPrice);
        UiTheme.installFocusRing(txtStock);

        txtCode.setFont(UiTheme.FONT_REGULAR);
        txtName.setFont(UiTheme.FONT_REGULAR);
        txtPrice.setFont(UiTheme.FONT_REGULAR);
        txtStock.setFont(UiTheme.FONT_REGULAR);

        txtCode.addActionListener(e -> submit());
        txtName.addActionListener(e -> submit());
        txtPrice.addActionListener(e -> submit());
        txtStock.addActionListener(e -> submit());

        // Row 0: Code
        g.gridy = 0; g.gridx = 0; g.weightx = 0.0;
        body.add(createFieldLabel("Product Code *", "Unique numeric ID"), g);
        g.gridy = 1; g.gridx = 0; g.weightx = 1.0;
        body.add(txtCode, g);

        // Row 1: Name
        g.gridy = 2; g.gridx = 0; g.weightx = 0.0;
        body.add(createFieldLabel("Product Name / Description *", "Full item title"), g);
        g.gridy = 3; g.gridx = 0; g.weightx = 1.0;
        body.add(txtName, g);

        // Row 2: Price & Stock
        JPanel rowPriceStock = new JPanel(new GridLayout(1, 2, 12, 0));
        rowPriceStock.setOpaque(false);

        JPanel pnlPrice = new JPanel(new BorderLayout(0, 4));
        pnlPrice.setOpaque(false);
        pnlPrice.add(createFieldLabel("Price (₱) *", "e.g. 1499.00"), BorderLayout.NORTH);
        pnlPrice.add(txtPrice, BorderLayout.CENTER);

        JPanel pnlStock = new JPanel(new BorderLayout(0, 4));
        pnlStock.setOpaque(false);
        pnlStock.add(createFieldLabel("Quantity in Stock *", "Initial count"), BorderLayout.NORTH);
        pnlStock.add(txtStock, BorderLayout.CENTER);

        rowPriceStock.add(pnlPrice);
        rowPriceStock.add(pnlStock);

        g.gridy = 4; g.gridx = 0; g.weightx = 1.0;
        body.add(rowPriceStock, g);

        // Error message label
        lblError = new JLabel(" ");
        lblError.setFont(UiTheme.FONT_HINT);
        lblError.setForeground(UiTheme.DANGER_RED);
        g.gridy = 5; g.gridx = 0;
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

        JButton btnCancel = UiTheme.createSecondaryButton("Cancel", "Discard and close");
        btnCancel.addActionListener(e -> dispose());

        JButton btnAdd = UiTheme.createPrimaryButton("Add Product", new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 14), "Register this product into the catalog");
        btnAdd.addActionListener(e -> submit());

        footer.add(btnCancel);
        footer.add(btnAdd);
        return footer;
    }

    private void submit() {
        String codeStr = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String stockStr = txtStock.getText().trim();

        if (codeStr.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            lblError.setText("Please fill in all required fields.");
            return;
        }

        int skuId;
        try {
            skuId = Integer.parseInt(codeStr);
            if (skuId <= 0) {
                lblError.setText("Product Code must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblError.setText("Product Code must be a valid whole number.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                lblError.setText("Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblError.setText("Price must be a valid number (e.g. 1499.00).");
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                lblError.setText("Quantity in stock cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblError.setText("Quantity must be a valid whole number.");
            return;
        }

        OpResult result = service.addProduct(skuId, name, price, stock);
        if (result.isSuccess()) {
            this.productAdded = true;
            mainFrame.setFeedback(true, "Product '" + name + "' added successfully.");
            mainFrame.refreshAll();
            dispose();
        } else {
            lblError.setText(result.getMessage());
        }
    }
}
