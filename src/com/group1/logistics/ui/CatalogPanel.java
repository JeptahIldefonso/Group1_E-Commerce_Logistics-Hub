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
 * Tab 2: Product Catalog.
 * Clean product registration, search, and deletion.
 * Pure standard Java Swing; zero Java Collections.
 */
public class CatalogPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final CatalogTableModel catalogTableModel;

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

    public CatalogPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.catalogTableModel = new CatalogTableModel(service.getInventoryBst());

        setLayout(new BorderLayout(0, 10));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        // Top Forms: Add New Product & Search/Manage
        JPanel topForms = new JPanel(new GridLayout(1, 2, 10, 0));
        topForms.setBackground(UiTheme.BG_WINDOW);
        topForms.add(createRegistrationSubPanel());
        topForms.add(createSearchDeleteSubPanel());
        add(topForms, BorderLayout.NORTH);

        // Center: Master Catalog Table
        add(createCatalogTableSubPanel(), BorderLayout.CENTER);
    }

    private JPanel createRegistrationSubPanel() {
        JPanel card = UiTheme.createCardPanel("Add New Product");
        card.setLayout(new BorderLayout(0, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.SURFACE);
        form.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_CARD, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNewSku = new JTextField(8);
        txtNewName = new JTextField(15);
        txtNewPrice = new JTextField(8);
        txtNewStock = new JTextField(8);

        txtNewSku.setFont(UiTheme.FONT_REGULAR);
        txtNewName.setFont(UiTheme.FONT_REGULAR);
        txtNewPrice.setFont(UiTheme.FONT_REGULAR);
        txtNewStock.setFont(UiTheme.FONT_REGULAR);

        txtNewSku.addActionListener(e -> handleAddProduct());
        txtNewName.addActionListener(e -> handleAddProduct());
        txtNewPrice.addActionListener(e -> handleAddProduct());
        txtNewStock.addActionListener(e -> handleAddProduct());

        btnAdd = UiTheme.createSuccessButton("Add Product", "Adds new product to catalog");
        btnAdd.addActionListener(e -> handleAddProduct());

        // Row 0: SKU ID & Stock
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("SKU ID:"), g);
        g.gridx = 1; g.weightx = 1.0; form.add(txtNewSku, g);
        g.gridx = 2; g.weightx = 0.0; form.add(new JLabel("Stock:"), g);
        g.gridx = 3; g.weightx = 1.0; form.add(txtNewStock, g);

        // Row 1: Product Name
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Name:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1.0; form.add(txtNewName, g);
        g.gridwidth = 1;

        // Row 2: Price
        g.gridy = 2;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Price (PHP):"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1.0; form.add(txtNewPrice, g);
        g.gridwidth = 1;

        // Row 3: Button
        g.gridy = 3;
        g.gridx = 0; g.gridwidth = 4; g.weightx = 1.0;
        g.insets = new Insets(6, 4, 2, 4);
        form.add(btnAdd, g);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSearchDeleteSubPanel() {
        JPanel card = UiTheme.createCardPanel("Search & Manage");
        card.setLayout(new BorderLayout(0, 8));

        // Top Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(UiTheme.SURFACE);
        searchBar.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_CARD, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblPrompt = new JLabel("Search SKU:");
        lblPrompt.setFont(UiTheme.FONT_BOLD);
        searchBar.add(lblPrompt, BorderLayout.WEST);

        txtSearchSku = new JTextField();
        txtSearchSku.setFont(UiTheme.FONT_REGULAR);
        txtSearchSku.addActionListener(e -> handleSearchProduct());
        searchBar.add(txtSearchSku, BorderLayout.CENTER);

        btnSearch = UiTheme.createPrimaryButton("Search", "Finds product by SKU ID");
        btnSearch.addActionListener(e -> handleSearchProduct());
        searchBar.add(btnSearch, BorderLayout.EAST);

        card.add(searchBar, BorderLayout.NORTH);

        // Result display area
        lblSearchResult = new JLabel("Select a product row below or type a SKU ID above.");
        lblSearchResult.setFont(UiTheme.FONT_REGULAR);
        lblSearchResult.setForeground(UiTheme.TEXT_MUTED);
        lblSearchResult.setOpaque(true);
        lblSearchResult.setBackground(UiTheme.SURFACE_ALT);
        lblSearchResult.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        card.add(lblSearchResult, BorderLayout.CENTER);

        // Delete button
        btnDelete = UiTheme.createDangerButton("Delete Product", "Removes product from catalog");
        btnDelete.addActionListener(e -> handleDeleteProduct());
        card.add(btnDelete, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCatalogTableSubPanel() {
        JPanel card = UiTheme.createCardPanel("Product Catalog");
        card.setLayout(new BorderLayout(0, 6));

        tblCatalog = new JTable(catalogTableModel);
        UiTheme.configureTable(tblCatalog);
        tblCatalog.setDefaultRenderer(Object.class, UiTheme.createCatalogCellRenderer());
        tblCatalog.setFillsViewportHeight(true);

        TableColumnModel colModel = tblCatalog.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(70);  // SKU ID
        colModel.getColumn(1).setPreferredWidth(230); // Product Name
        colModel.getColumn(2).setPreferredWidth(110); // Price
        colModel.getColumn(3).setPreferredWidth(90);  // Stock Qty
        colModel.getColumn(4).setPreferredWidth(120); // Status

        // Clicking any row auto-populates the search panel
        tblCatalog.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblCatalog.getSelectedRow();
                if (row >= 0) {
                    Product p = catalogTableModel.getProductAt(row);
                    if (p != null) {
                        txtSearchSku.setText(String.valueOf(p.getSkuId()));
                        updateSearchResultDisplay(p);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblCatalog);
        scrollPane.setBorder(new LineBorder(UiTheme.BORDER, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void updateSearchResultDisplay(Product p) {
        String statusText;
        if (p.getStockQty() == 0) {
            statusText = "<font color='#991B1B'><b>Out of Stock</b></font>";
        } else if (p.getStockQty() <= 10) {
            statusText = "<font color='#B45309'><b>Low Stock</b></font>";
        } else {
            statusText = "<font color='#166534'><b>In Stock</b></font>";
        }

        lblSearchResult.setText(String.format(
                "<html><body style='font-family: Segoe UI, sans-serif; font-size: 11px;'>"
                + "<b style='color: #1D4ED8;'>SKU #%d</b>: <b>%s</b><br>"
                + "Price: PHP %.2f &nbsp;&nbsp;|&nbsp;&nbsp; Stock: %d units (%s)"
                + "</body></html>",
                p.getSkuId(), p.getName(), p.getPrice(), p.getStockQty(), statusText
        ));
    }

    private void handleAddProduct() {
        String skuStr = txtNewSku.getText().trim();
        String name = txtNewName.getText().trim();
        String priceStr = txtNewPrice.getText().trim();
        String stockStr = txtNewStock.getText().trim();

        if (skuStr.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            mainFrame.setFeedback(false, "Please fill in all product registration fields.");
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            OpResult result = service.addProduct(skuId, name, price, stock);
            mainFrame.setFeedback(result);
            if (result.isSuccess()) {
                txtNewSku.setText("");
                txtNewName.setText("");
                txtNewPrice.setText("");
                txtNewStock.setText("");
                mainFrame.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "SKU ID and Stock must be integers; Price must be numeric.");
        }
    }

    private void handleSearchProduct() {
        String skuStr = txtSearchSku.getText().trim();
        if (skuStr.isEmpty()) {
            mainFrame.setFeedback(false, "Please enter a SKU ID to search.");
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            Product product = service.findProduct(skuId);
            if (product != null) {
                updateSearchResultDisplay(product);
                mainFrame.setFeedback(true, "Found SKU #" + skuId + " (" + product.getName() + ").");

                for (int i = 0; i < tblCatalog.getRowCount(); i++) {
                    Product p = catalogTableModel.getProductAt(i);
                    if (p != null && p.getSkuId() == skuId) {
                        tblCatalog.setRowSelectionInterval(i, i);
                        tblCatalog.scrollRectToVisible(tblCatalog.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                lblSearchResult.setText("<html><body style='font-family: Segoe UI, sans-serif; font-size: 11px;'>"
                        + "<font color='#DC2626'><b>Not found:</b> SKU #" + skuId + " does not exist in catalog.</font>"
                        + "</body></html>");
                mainFrame.setFeedback(false, "SKU #" + skuId + " was not found in catalog.");
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "SKU ID must be a valid integer.");
        }
    }

    private void handleDeleteProduct() {
        String skuStr = txtSearchSku.getText().trim();
        if (skuStr.isEmpty()) {
            mainFrame.setFeedback(false, "Please select or enter the SKU ID you wish to delete.");
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            Product product = service.findProduct(skuId);
            if (product == null) {
                mainFrame.setFeedback(false, "SKU #" + skuId + " does not exist in the catalog.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete SKU #" + skuId + " (" + product.getName() + ")?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                OpResult result = service.deleteProduct(skuId);
                mainFrame.setFeedback(result);
                if (result.isSuccess()) {
                    txtSearchSku.setText("");
                    lblSearchResult.setText("Select a product row below or type a SKU ID above.");
                    mainFrame.refreshAll();
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "SKU ID must be a valid integer.");
        }
    }

    public void refreshTables() {
        catalogTableModel.refresh();
    }
}
