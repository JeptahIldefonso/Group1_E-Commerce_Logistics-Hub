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
 * Tab 2: SKU Catalog Index (Custom Binary Search Tree — BST).
 * Supports recursive insertion, O(log n) target search, in-order catalog table, and 3-case deletion.
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

        // Top 1-Line Plain-English Workflow Banner
        add(UiTheme.createWorkflowBanner(
                "Hierarchical product catalog index with logarithmic search and full 3-case tree deletion.",
                "Hierarchical Indexing (Custom InventoryBST)"
        ), BorderLayout.NORTH);

        // Center Container: Split top forms and bottom master table
        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setBackground(UiTheme.BG_WINDOW);

        // Top Forms Container: 50/50 split between Registration and Search/Delete
        JPanel topForms = new JPanel(new GridLayout(1, 2, 10, 0));
        topForms.setBackground(UiTheme.BG_WINDOW);
        topForms.add(createRegistrationSubPanel());
        topForms.add(createSearchDeleteSubPanel());
        centerContainer.add(topForms, BorderLayout.NORTH);

        // Center: Master Catalog Table Panel
        centerContainer.add(createCatalogTableSubPanel(), BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createRegistrationSubPanel() {
        JPanel card = UiTheme.createCardPanel("Register New SKU (Recursive BST Insert)");
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

        btnAdd = UiTheme.createStyledButton(
                "Add Product to Tree (Insert)",
                UiTheme.POSITIVE_GREEN,
                Color.WHITE,
                "Inserts new product into the BST in O(log n) time; rejects duplicate SKUs (BR-01)"
        );
        btnAdd.addActionListener(e -> handleAddProduct());

        // Row 0: SKU ID & Stock
        g.gridy = 0;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("SKU ID:"), g);
        g.gridx = 1; g.weightx = 1.0; form.add(txtNewSku, g);
        g.gridx = 2; g.weightx = 0.0; form.add(new JLabel("Initial Stock:"), g);
        g.gridx = 3; g.weightx = 1.0; form.add(txtNewStock, g);

        // Row 1: Product Name
        g.gridy = 1;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Name:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1.0; form.add(txtNewName, g);
        g.gridwidth = 1;

        // Row 2: Price
        g.gridy = 2;
        g.gridx = 0; g.weightx = 0.0; form.add(new JLabel("Price (₱):"), g);
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
        JPanel card = UiTheme.createCardPanel("Product Lookup & Deletion (O(log n) Search / 3-Case Delete)");
        card.setLayout(new BorderLayout(0, 8));

        // Top Search Bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(UiTheme.SURFACE);
        searchBar.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_CARD, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblPrompt = new JLabel("SKU ID to Query: ");
        lblPrompt.setFont(UiTheme.FONT_BOLD);
        searchBar.add(lblPrompt, BorderLayout.WEST);

        txtSearchSku = new JTextField();
        txtSearchSku.setFont(UiTheme.FONT_REGULAR);
        txtSearchSku.addActionListener(e -> handleSearchProduct());
        searchBar.add(txtSearchSku, BorderLayout.CENTER);

        btnSearch = UiTheme.createStyledButton(
                "Search SKU",
                UiTheme.PRIMARY_BLUE,
                Color.WHITE,
                "Performs O(log n) binary search traversal on the BST"
        );
        btnSearch.addActionListener(e -> handleSearchProduct());
        searchBar.add(btnSearch, BorderLayout.EAST);

        card.add(searchBar, BorderLayout.NORTH);

        // Result display area
        lblSearchResult = new JLabel("<html><body style='padding:4px; font-family: Segoe UI, sans-serif; font-size: 11px;'>"
                + "<b>Tip:</b> Click any row in the table below or type a SKU ID above to inspect details and test 3-case tree deletion."
                + "</body></html>");
        lblSearchResult.setOpaque(true);
        lblSearchResult.setBackground(UiTheme.SURFACE_ALT);
        lblSearchResult.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        card.add(lblSearchResult, BorderLayout.CENTER);

        // Delete button
        btnDelete = UiTheme.createStyledButton(
                "Discontinue / Delete SKU (3-Case Deletion)",
                UiTheme.DANGER_RED,
                Color.WHITE,
                "Executes 3-case BST deletion (Leaf, 1 Child, or 2 Children via in-order successor) — BR-07"
        );
        btnDelete.addActionListener(e -> handleDeleteProduct());
        card.add(btnDelete, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCatalogTableSubPanel() {
        JPanel card = UiTheme.createCardPanel("Master Inventory Catalog (Sorted strictly via BST In-Order Traversal)");
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
        colModel.getColumn(4).setPreferredWidth(130); // Stock Status

        // Clicking any row auto-populates the search & delete panel!
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
        String statusHtml;
        if (p.getStockQty() == 0) {
            statusHtml = "<font color='#D93025'><b>Out of Stock</b></font>";
        } else if (p.getStockQty() <= 10) {
            statusHtml = "<font color='#E37400'><b>Low Stock (<= 10)</b></font>";
        } else {
            statusHtml = "<font color='#1E8E3E'><b>In Stock</b></font>";
        }

        lblSearchResult.setText(String.format(
                "<html><body style='font-family: Segoe UI, sans-serif; font-size: 11px;'>"
                + "<b style='color: #1A73E8;'>SKU #%d</b>: <b>%s</b><br>"
                + "<b>Unit Price:</b> ₱%.2f &nbsp;&nbsp;|&nbsp;&nbsp; <b>Current Stock:</b> %d units (%s)<br>"
                + "<span style='color: #6B7280;'>BST Node is ready for inspection or 3-case deletion.</span>"
                + "</body></html>",
                p.getSkuId(), p.getName(), p.getPrice(), p.getStockQty(), statusHtml
        ));
    }

    private void handleAddProduct() {
        String skuStr = txtNewSku.getText().trim();
        String name = txtNewName.getText().trim();
        String priceStr = txtNewPrice.getText().trim();
        String stockStr = txtNewStock.getText().trim();

        if (skuStr.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            mainFrame.setFeedback(false, "Validation Error: Please fill in all product registration fields.");
            JOptionPane.showMessageDialog(this, "Please fill in all product registration fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
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
            mainFrame.setFeedback(false, "Input Error: SKU ID and Stock must be integers; Price must be numeric.");
            JOptionPane.showMessageDialog(this, "SKU ID and Stock must be integers; Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearchProduct() {
        String skuStr = txtSearchSku.getText().trim();
        if (skuStr.isEmpty()) {
            mainFrame.setFeedback(false, "Input Required: Please enter a SKU ID to search.");
            JOptionPane.showMessageDialog(this, "Please enter a SKU ID to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            Product product = service.findProduct(skuId);
            if (product != null) {
                updateSearchResultDisplay(product);
                mainFrame.setFeedback(true, "Found SKU #" + skuId + " (" + product.getName() + ") in O(log n) time.");

                // Select corresponding row in table if present
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
                        + "<font color='#D93025'><b>Product not found:</b> SKU #" + skuId + " does not exist in BST.</font>"
                        + "</body></html>");
                mainFrame.setFeedback(false, "Search Result: SKU #" + skuId + " was not found in the inventory tree.");
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Input Error: SKU ID must be a valid integer.");
            JOptionPane.showMessageDialog(this, "SKU ID must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteProduct() {
        String skuStr = txtSearchSku.getText().trim();
        if (skuStr.isEmpty()) {
            mainFrame.setFeedback(false, "Input Required: Please select or enter the SKU ID you wish to delete.");
            JOptionPane.showMessageDialog(this, "Please search or enter the SKU ID you wish to delete.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            Product product = service.findProduct(skuId);
            if (product == null) {
                mainFrame.setFeedback(false, "Not Found: SKU #" + skuId + " does not exist in the catalog.");
                JOptionPane.showMessageDialog(this, "SKU #" + skuId + " does not exist in the catalog.", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete SKU #" + skuId + " (" + product.getName() + ")?\n" +
                    "This will execute the 3-Case BST deletion algorithm (with double-decrement protection).",
                    "Confirm Deletion (BR-07)", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                OpResult result = service.deleteProduct(skuId);
                mainFrame.setFeedback(result);
                if (result.isSuccess()) {
                    txtSearchSku.setText("");
                    lblSearchResult.setText("<html><body style='font-family: Segoe UI, sans-serif; font-size: 11px;'>"
                            + "<i>Product deleted. Enter a SKU ID above or click a table row...</i>"
                            + "</body></html>");
                    mainFrame.refreshAll();
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            mainFrame.setFeedback(false, "Input Error: SKU ID must be a valid integer.");
            JOptionPane.showMessageDialog(this, "SKU ID must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTables() {
        catalogTableModel.refresh();
    }
}
