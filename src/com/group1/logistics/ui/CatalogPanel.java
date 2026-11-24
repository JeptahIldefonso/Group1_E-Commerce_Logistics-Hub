package com.group1.logistics.ui;

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
 * Tab 2: Products.
 * Product lookup, single-column product creation, and safe deletion.
 * Pure standard Java Swing; zero Java Collections.
 */
public class CatalogPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final CatalogTableModel catalogTableModel;
    private boolean technicalDetails = false;

    // Register product form
    private JTextField txtNewSku;
    private JTextField txtNewName;
    private JTextField txtNewPrice;
    private JTextField txtNewStock;
    private JButton btnAdd;

    // Search and Delete controls
    private JTextField txtSearchSku;
    private JButton btnSearch;
    private JButton btnDelete;
    private JLabel lblSearchResult;
    private JTable tblCatalog;

    private Product selectedProduct = null;
    private JLabel lblTreeHeightBadge;
    private JLabel lblKpiTotal;
    private JLabel lblKpiHealthy;
    private JLabel lblKpiAttention;

    public CatalogPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.catalogTableModel = new CatalogTableModel(service.getInventoryBst());

        setLayout(new BorderLayout(0, 10));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Top Panel: Find Product Row & Forms
        JPanel topContainer = new JPanel(new BorderLayout(0, 8));
        topContainer.setBackground(UiTheme.BG_WINDOW);

        topContainer.add(createFindProductBar(), BorderLayout.NORTH);

        JPanel formsContainer = new JPanel(new GridLayout(1, 2, 12, 0));
        formsContainer.setBackground(UiTheme.BG_WINDOW);
        formsContainer.add(createAddProductCard());
        formsContainer.add(createProductDetailsCard());

        topContainer.add(formsContainer, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 2. Center: Product List Table
        add(createProductTableCard(), BorderLayout.CENTER);

        updateKpiCards();
        updateDeleteButtonState();
        SwingUtilities.invokeLater(() -> txtSearchSku.requestFocusInWindow());
    }

    public void setTechnicalDetails(boolean enabled) {
        this.technicalDetails = enabled;
        if (lblTreeHeightBadge != null) {
            lblTreeHeightBadge.setVisible(enabled);
            if (enabled) {
                lblTreeHeightBadge.setText(" BST Height: " + service.getInventoryBst().height() + " ");
            }
        }
    }

    private JPanel createFindProductBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(UiTheme.SURFACE);
        bar.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblPrompt = new JLabel("Find Product:");
        lblPrompt.setFont(UiTheme.FONT_BOLD);
        bar.add(lblPrompt, BorderLayout.WEST);

        txtSearchSku = new JTextField();
        txtSearchSku.setFont(UiTheme.FONT_REGULAR);
        txtSearchSku.setToolTipText("Type a product code or product name");
        txtSearchSku.addActionListener(e -> handleSearchProduct());
        txtSearchSku.registerKeyboardAction(
                e -> {
                    txtSearchSku.setText("");
                    selectedProduct = null;
                    updateDeleteButtonState();
                    lblSearchResult.setText("Click a product in the list, or type its code or name above.");
                    tblCatalog.clearSelection();
                    mainFrame.setFeedback(true, "Search cleared.");
                },
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED
        );
        UiTheme.installFocusRing(txtSearchSku);
        bar.add(txtSearchSku, BorderLayout.CENTER);

        btnSearch = UiTheme.createPrimaryButton("Find Product",
                new UiTheme.VectorIcon(UiTheme.IconType.SEARCH, 14),
                "Search for a product by its code or name");
        btnSearch.addActionListener(e -> handleSearchProduct());
        bar.add(btnSearch, BorderLayout.EAST);

        return bar;
    }

    private JPanel createAddProductCard() {
        JPanel card = UiTheme.createCardPanel("Add Product");
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNewSku = new JTextField(12);
        txtNewName = new JTextField(16);
        txtNewPrice = new JTextField(12);
        txtNewStock = new JTextField(12);

        txtNewSku.setFont(UiTheme.FONT_REGULAR);
        txtNewName.setFont(UiTheme.FONT_REGULAR);
        txtNewPrice.setFont(UiTheme.FONT_REGULAR);
        txtNewStock.setFont(UiTheme.FONT_REGULAR);

        txtNewSku.setToolTipText("Unique numeric product code (e.g. 150)");
        txtNewName.setToolTipText("Full name of the product");
        txtNewPrice.setToolTipText("Price in Philippine Pesos (e.g. 1499.00)");
        txtNewStock.setToolTipText("Initial quantity available in stock");

        txtNewSku.addActionListener(e -> handleAddProduct());
        txtNewName.addActionListener(e -> handleAddProduct());
        txtNewPrice.addActionListener(e -> handleAddProduct());
        txtNewStock.addActionListener(e -> handleAddProduct());

        UiTheme.installFocusRing(txtNewSku);
        UiTheme.installFocusRing(txtNewName);
        UiTheme.installFocusRing(txtNewPrice);
        UiTheme.installFocusRing(txtNewStock);

        btnAdd = UiTheme.createSuccessButton("Add Product",
                new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 14),
                "Register this new product into the catalog");
        btnAdd.addActionListener(e -> handleAddProduct());

        // Single Column Layout: Code, Name, Price, Quantity
        g.gridx = 0; g.gridy = 0; g.weightx = 0.0; card.add(new JLabel("Product Code:"), g);
        g.gridx = 1; g.weightx = 1.0; card.add(txtNewSku, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0.0; card.add(new JLabel("Product Name:"), g);
        g.gridx = 1; g.weightx = 1.0; card.add(txtNewName, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0.0; card.add(new JLabel("Price (₱):"), g);
        g.gridx = 1; g.weightx = 1.0; card.add(txtNewPrice, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0.0; card.add(new JLabel("Quantity in Stock:"), g);
        g.gridx = 1; g.weightx = 1.0; card.add(txtNewStock, g);

        g.gridx = 0; g.gridy = 4; g.gridwidth = 2; g.weightx = 1.0;
        g.insets = new Insets(8, 4, 2, 4);
        card.add(btnAdd, g);

        return card;
    }

    private JPanel createProductDetailsCard() {
        JPanel card = UiTheme.createCardPanel("Product Details");
        card.setLayout(new BorderLayout(0, 8));

        lblSearchResult = new JLabel("Click a product in the list, or type its code or name above.");
        lblSearchResult.setFont(UiTheme.FONT_REGULAR);
        lblSearchResult.setForeground(UiTheme.TEXT_MUTED);
        lblSearchResult.setOpaque(true);
        lblSearchResult.setBackground(UiTheme.SURFACE_ALT);
        lblSearchResult.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.add(lblSearchResult, BorderLayout.CENTER);

        // Delete button (outlined danger style, disabled by default!)
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnBar.setBackground(UiTheme.SURFACE);

        btnDelete = UiTheme.createOutlineDangerButton("Delete Product",
                new UiTheme.VectorIcon(UiTheme.IconType.DELETE, 14),
                "Remove this product from the inventory");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> handleDeleteProduct());
        btnBar.add(btnDelete);

        card.add(btnBar, BorderLayout.SOUTH);
        return card;
    }

    private JLabel createKpiPill(Color bg, Color border, Color fg) {
        JLabel lbl = new JLabel();
        lbl.setFont(UiTheme.FONT_BADGE);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setBorder(new CompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        return lbl;
    }

    private void updateKpiCards() {
        Product[] products = service.getInventoryBst().inOrder();
        int total = (products != null) ? products.length : 0;
        int healthy = 0;
        int attention = 0;

        if (products != null) {
            for (int i = 0; i < products.length; i++) {
                if (products[i].getStockQty() > 20) {
                    healthy++;
                } else {
                    attention++;
                }
            }
        }

        if (lblKpiTotal != null) {
            lblKpiTotal.setText("Total: " + total + " products");
        }
        if (lblKpiHealthy != null) {
            lblKpiHealthy.setText("Healthy: " + healthy + " in stock");
        }
        if (lblKpiAttention != null) {
            lblKpiAttention.setText("Attention: " + attention + " low/out");
        }
    }

    private JPanel createProductTableCard() {
        JPanel card = UiTheme.createCardPanel("Product List");
        card.setLayout(new BorderLayout(0, 6));

        // KPI stat pill badges (Total, Healthy Stock, Low/Out of Stock)
        JPanel pnlKpis = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlKpis.setBackground(UiTheme.SURFACE);

        lblKpiTotal = createKpiPill(UiTheme.SURFACE_ALT, UiTheme.BORDER, UiTheme.TEXT_PRIMARY);
        lblKpiHealthy = createKpiPill(new Color(0xDC, 0xFC, 0xE7), new Color(0xBB, 0xF7, 0xD0), new Color(0x16, 0x65, 0x34));
        lblKpiAttention = createKpiPill(new Color(0xFE, 0xF3, 0xC7), new Color(0xFD, 0xE6, 0x8A), new Color(0xB4, 0x53, 0x09));

        pnlKpis.add(lblKpiTotal);
        pnlKpis.add(lblKpiHealthy);
        pnlKpis.add(lblKpiAttention);

        // Technical details header badge
        lblTreeHeightBadge = new JLabel();
        lblTreeHeightBadge.setFont(UiTheme.FONT_BADGE);
        lblTreeHeightBadge.setOpaque(true);
        lblTreeHeightBadge.setBackground(UiTheme.SURFACE_ALT);
        lblTreeHeightBadge.setForeground(UiTheme.TEXT_MUTED);
        lblTreeHeightBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
        lblTreeHeightBadge.setVisible(false);

        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setBackground(UiTheme.SURFACE);
        headerPanel.add(pnlKpis, BorderLayout.WEST);
        headerPanel.add(lblTreeHeightBadge, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);

        tblCatalog = new JTable(catalogTableModel);
        UiTheme.configureTable(tblCatalog);
        tblCatalog.setDefaultRenderer(Object.class, UiTheme.createCatalogCellRenderer());
        tblCatalog.setFillsViewportHeight(true);

        TableColumnModel colModel = tblCatalog.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(95);  // Product Code
        colModel.getColumn(1).setPreferredWidth(260); // Product Name
        colModel.getColumn(2).setPreferredWidth(120); // Price
        colModel.getColumn(3).setPreferredWidth(100); // In Stock
        colModel.getColumn(4).setPreferredWidth(120); // Status

        // Clicking any row selects product and enables Delete Product button!
        tblCatalog.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblCatalog.getSelectedRow();
                if (row >= 0) {
                    Product p = catalogTableModel.getProductAt(row);
                    if (p != null) {
                        selectProduct(p);
                    }
                }
            }
        });

        JScrollPane scrollPane = UiTheme.createModernScrollPane(tblCatalog);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void selectProduct(Product p) {
        this.selectedProduct = p;
        txtSearchSku.setText(String.valueOf(p.getSkuId()));

        String statusHtml;
        if (p.getStockQty() == 0) {
            statusHtml = "<font color='#DC2626'><b>Out of Stock</b></font>";
        } else if (p.getStockQty() <= 20) {
            statusHtml = "<font color='#D97706'><b>Low Stock</b></font>";
        } else {
            statusHtml = "<font color='#166534'><b>In Stock</b></font>";
        }

        lblSearchResult.setText(String.format(
                "<html><body style='font-family: Segoe UI, sans-serif; font-size: 12px;'>"
                + "<b style='color: #1D4ED8;'>Code #%d</b>: <b>%s</b><br>"
                + "Price: ₱%,.2f &nbsp;&nbsp;|&nbsp;&nbsp; In Stock: %d units (%s)"
                + "</body></html>",
                p.getSkuId(), p.getName(), p.getPrice(), p.getStockQty(), statusHtml
        ));

        updateDeleteButtonState();
        SwingUtilities.invokeLater(() -> txtSearchSku.requestFocusInWindow());
    }

    private void updateDeleteButtonState() {
        btnDelete.setEnabled(selectedProduct != null);
    }

    private void handleAddProduct() {
        String skuStr = txtNewSku.getText().trim();
        String name = txtNewName.getText().trim();
        String priceStr = txtNewPrice.getText().trim();
        String stockStr = txtNewStock.getText().trim();

        if (skuStr.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            mainFrame.setFeedback(false, "Please fill in all product fields.");
            return;
        }

        int skuId;
        try {
            skuId = Integer.parseInt(skuStr);
            if (skuId <= 0) {
                mainFrame.setFeedback(false, "Product Code must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Product Code must be a valid whole number.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                mainFrame.setFeedback(false, "Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Price must be a valid number (e.g. 1499.00).");
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                mainFrame.setFeedback(false, "Quantity in stock cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Quantity must be a valid whole number.");
            return;
        }

        OpResult result = service.addProduct(skuId, name, price, stock);
        if (result.isSuccess()) {
            txtNewSku.setText("");
            txtNewName.setText("");
            txtNewPrice.setText("");
            txtNewStock.setText("");
            mainFrame.setFeedback(true, "Product '" + name + "' added successfully.");
            mainFrame.refreshAll();
        } else {
            mainFrame.setFeedback(false, result.getMessage());
            JOptionPane.showMessageDialog(this, result.getMessage(), "Registration Blocked", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearchProduct() {
        String query = txtSearchSku.getText().trim();
        if (query.isEmpty()) {
            mainFrame.setFeedback(false, "Please type a product code or product name to find.");
            return;
        }

        Product product = null;
        // 1. Try numeric lookup by Product Code first (fast BST lookup)
        try {
            int skuId = Integer.parseInt(query);
            product = service.findProduct(skuId);
        } catch (NumberFormatException ignored) {
            // Not a numeric code, search by product name
        }

        // 2. If not found by numeric code, search by product name (case-insensitive substring)
        if (product == null) {
            product = service.findProductByName(query);
        }

        if (product != null) {
            selectProduct(product);
            mainFrame.setFeedback(true, "Found " + product.getName() + " (Code #" + product.getSkuId() + ").");

            for (int i = 0; i < tblCatalog.getRowCount(); i++) {
                Product p = catalogTableModel.getProductAt(i);
                if (p != null && p.getSkuId() == product.getSkuId()) {
                    tblCatalog.setRowSelectionInterval(i, i);
                    tblCatalog.scrollRectToVisible(tblCatalog.getCellRect(i, 0, true));
                    break;
                }
            }
        } else {
            selectedProduct = null;
            updateDeleteButtonState();
            lblSearchResult.setText("<html><body style='font-family: Segoe UI, sans-serif; font-size: 12px;'>"
                    + "<font color='#DC2626'><b>Not found:</b> No product found matching '<b>" + query + "</b>'.</font><br>"
                    + "<span style='color: #6B7280;'>Check the product code or name and try again.</span>"
                    + "</body></html>");
            mainFrame.setFeedback(false, "No product found matching '" + query + "'.");
        }
    }

    private void handleDeleteProduct() {
        if (selectedProduct == null) {
            mainFrame.setFeedback(false, "Please click a product in the list first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selectedProduct.getName() + " (Code #" + selectedProduct.getSkuId() + ")?",
                "Confirm Delete Product", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int skuId = selectedProduct.getSkuId();
            String name = selectedProduct.getName();
            OpResult result = service.deleteProduct(skuId);
            if (result.isSuccess()) {
                selectedProduct = null;
                txtSearchSku.setText("");
                lblSearchResult.setText("Click a product in the list, or type its code or name above.");
                updateDeleteButtonState();
                mainFrame.setFeedback(true, "Product '" + name + "' was deleted.");
                mainFrame.refreshAll();
            } else {
                mainFrame.setFeedback(false, result.getMessage());
                JOptionPane.showMessageDialog(this, result.getMessage(), "Delete Blocked", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshTables() {
        catalogTableModel.refresh();
        updateKpiCards();
        if (lblTreeHeightBadge != null && technicalDetails) {
            lblTreeHeightBadge.setText(" BST Height: " + service.getInventoryBst().height() + " ");
        }
    }
}
