package com.group1.logistics.ui;

import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import java.awt.*;

/**
 * Tab 2: SKU Catalog Index (Custom Binary Search Tree - BST).
 * Supports recursive insertion, O(log n) target search, in-order catalog table, and 3-case deletion.
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

    // Search and Delete controls
    private JTextField txtSearchSku;
    private JLabel lblSearchResult;

    public CatalogPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.catalogTableModel = new CatalogTableModel(service.getInventoryBst());

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Registration & Search forms
        JPanel topContainer = new JPanel(new GridLayout(1, 2, 10, 10));
        topContainer.add(createRegistrationSubPanel());
        topContainer.add(createSearchDeleteSubPanel());
        add(topContainer, BorderLayout.NORTH);

        // Center Panel: Master Catalog Table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Master Inventory Catalog (Populated via BST In-Order Traversal)"));

        JTable tblCatalog = new JTable(catalogTableModel);
        tblCatalog.setFillsViewportHeight(true);
        tablePanel.add(new JScrollPane(tblCatalog), BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createRegistrationSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Register New SKU (Recursive BST Insert)"));

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        txtNewSku = new JTextField();
        txtNewName = new JTextField();
        txtNewPrice = new JTextField();
        txtNewStock = new JTextField();
        JButton btnAdd = new JButton("Add Product to Tree");

        form.add(new JLabel("SKU ID (Unique Int):"));
        form.add(txtNewSku);
        form.add(new JLabel("Product Name:"));
        form.add(txtNewName);
        form.add(new JLabel("Unit Price (PHP):"));
        form.add(txtNewPrice);
        form.add(new JLabel("Initial Stock:"));
        form.add(txtNewStock);
        form.add(new JLabel(""));
        form.add(btnAdd);

        panel.add(form, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> handleAddProduct());
        return panel;
    }

    private JPanel createSearchDeleteSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Product Lookup & Deletion (O(log n) Search / 3-Case Delete)"));

        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        txtSearchSku = new JTextField();
        JButton btnSearch = new JButton("Search SKU");
        searchBar.add(new JLabel("Enter SKU ID: "), BorderLayout.WEST);
        searchBar.add(txtSearchSku, BorderLayout.CENTER);
        searchBar.add(btnSearch, BorderLayout.EAST);

        panel.add(searchBar, BorderLayout.NORTH);

        // Result display area
        lblSearchResult = new JLabel("<html><i>Enter a SKU ID above and click Search...</i></html>");
        lblSearchResult.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(lblSearchResult, BorderLayout.CENTER);

        JButton btnDelete = new JButton("Discontinue / Delete SKU (3-Case Deletion)");
        btnDelete.setForeground(new Color(180, 0, 0));
        panel.add(btnDelete, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> handleSearchProduct());
        btnDelete.addActionListener(e -> handleDeleteProduct());

        return panel;
    }

    private void handleAddProduct() {
        String skuStr = txtNewSku.getText().trim();
        String name = txtNewName.getText().trim();
        String priceStr = txtNewPrice.getText().trim();
        String stockStr = txtNewStock.getText().trim();

        if (skuStr.isEmpty() || name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all product registration fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            OpResult result = service.addProduct(skuId, name, price, stock);
            if (result.isSuccess()) {
                txtNewSku.setText("");
                txtNewName.setText("");
                txtNewPrice.setText("");
                txtNewStock.setText("");
                mainFrame.refreshAll();
                JOptionPane.showMessageDialog(this, result.getMessage(), "Product Registered", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "SKU ID and Stock must be integers; Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearchProduct() {
        String skuStr = txtSearchSku.getText().trim();
        if (skuStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a SKU ID to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            Product product = service.findProduct(skuId);
            if (product != null) {
                lblSearchResult.setText("<html><b>Found:</b> " + product.getName() + "<br>" +
                        "<b>Price:</b> PHP " + String.format("%.2f", product.getPrice()) + "<br>" +
                        "<b>Current Stock:</b> " + product.getStockQty() + " units</html>");
            } else {
                lblSearchResult.setText("<html><font color='red'><b>Product not found:</b> SKU #" + skuId + " does not exist in catalog.</font></html>");
                JOptionPane.showMessageDialog(this, "SKU #" + skuId + " was not found in the inventory tree.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "SKU ID must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteProduct() {
        String skuStr = txtSearchSku.getText().trim();
        if (skuStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search or enter the SKU ID you wish to delete.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int skuId = Integer.parseInt(skuStr);
            Product product = service.findProduct(skuId);
            if (product == null) {
                JOptionPane.showMessageDialog(this, "SKU #" + skuId + " does not exist in the catalog.", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete SKU #" + skuId + " (" + product.getName() + ")?\n" +
                    "This will execute the 3-Case BST deletion algorithm.",
                    "Confirm Deletion (BR-07)", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                OpResult result = service.deleteProduct(skuId);
                if (result.isSuccess()) {
                    txtSearchSku.setText("");
                    lblSearchResult.setText("<html><i>Product deleted. Enter a SKU ID above...</i></html>");
                    mainFrame.refreshAll();
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Product Deleted", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "SKU ID must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTables() {
        catalogTableModel.refresh();
    }
}
