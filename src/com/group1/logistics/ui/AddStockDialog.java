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

/**
 * Modal dialog for restocking inventory items into the LIFO stack.
 * Pure standard Java Swing; zero Java Collections.
 */
public class AddStockDialog extends JDialog {
    private final FulfillmentService service;
    private final MainFrame mainFrame;

    private JComboBox<OrdersPanel.ProductComboItem> cmbProduct;
    private JTextField txtQuantity;
    private JLabel lblError;
    private boolean stockAdded = false;

    public AddStockDialog(Frame parent, FulfillmentService service, MainFrame mainFrame) {
        super(parent, "Add Stock", true);
        this.service = service;
        this.mainFrame = mainFrame;

        setUndecorated(true);
        setSize(440, 360);
        setLocationRelativeTo(parent);

        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBackground(UiTheme.SURFACE);
        contentPane.setBorder(new LineBorder(new Color(0xD1, 0xD5, 0xDB), 1, true));

        // 1. Header
        contentPane.add(createHeader(), BorderLayout.NORTH);

        // 2. Body Form
        contentPane.add(createBodyForm(), BorderLayout.CENTER);

        // 3. Footer
        contentPane.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(contentPane);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        loadProducts();
        SwingUtilities.invokeLater(() -> txtQuantity.requestFocusInWindow());
    }

    public boolean isStockAdded() {
        return stockAdded;
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

        JLabel lblIcon = new JLabel(new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 16, UiTheme.TEXT_PRIMARY));
        JLabel lblTitle = new JLabel("Add Stock to Product");
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
        body.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 4, 6, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        cmbProduct = new JComboBox<>();
        txtQuantity = new JTextField("10");

        UiTheme.installFocusRing(txtQuantity);

        cmbProduct.setFont(UiTheme.FONT_REGULAR);
        txtQuantity.setFont(UiTheme.FONT_REGULAR);
        txtQuantity.addActionListener(e -> submit());

        // Row 0: Product
        g.gridy = 0; g.gridx = 0; g.weightx = 1.0;
        body.add(createFieldLabel("Product *", "Item to restock"), g);
        g.gridy = 1; g.gridx = 0;
        body.add(cmbProduct, g);

        // Row 1: Quantity to Add
        g.gridy = 2; g.gridx = 0;
        body.add(createFieldLabel("Quantity to Add *", "Number of units"), g);
        g.gridy = 3; g.gridx = 0;
        body.add(txtQuantity, g);

        // Error message label
        lblError = new JLabel(" ");
        lblError.setFont(UiTheme.FONT_HINT);
        lblError.setForeground(UiTheme.DANGER_RED);
        g.gridy = 4; g.gridx = 0;
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

        JButton btnAdd = UiTheme.createSuccessButton("Add Stock", new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 14), "Commit stock and push to audit stack");
        btnAdd.addActionListener(e -> submit());

        footer.add(btnCancel);
        footer.add(btnAdd);
        return footer;
    }

    private void loadProducts() {
        cmbProduct.removeAllItems();
        Product[] products = service.getInventoryBst().inOrder();
        if (products != null) {
            for (int i = 0; i < products.length; i++) {
                Product p = products[i];
                cmbProduct.addItem(new OrdersPanel.ProductComboItem(p.getSkuId(), p.getName(), p.getStockQty()));
            }
        }
    }

    private void submit() {
        OrdersPanel.ProductComboItem selected = (OrdersPanel.ProductComboItem) cmbProduct.getSelectedItem();
        String qtyStr = txtQuantity.getText().trim();

        if (selected == null) {
            lblError.setText("Please select a product to restock.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                lblError.setText("Restock quantity must be at least 1.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblError.setText("Quantity must be a valid whole number.");
            return;
        }

        OpResult result = service.restock(selected.getSkuId(), quantity);
        if (result.isSuccess()) {
            this.stockAdded = true;
            mainFrame.setFeedback(true, "Restocked +" + quantity + " units to " + selected.getName() + ".");
            mainFrame.refreshAll();
            dispose();
        } else {
            lblError.setText(result.getMessage());
        }
    }
}
