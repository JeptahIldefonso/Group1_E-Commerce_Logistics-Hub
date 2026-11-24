package com.group1.logistics.ui;

import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * Tab 2: Product Management.
 * SaaS ERP layout matching Image 1:
 * Page header, filter pills, search input, "+ Add Product" modal trigger,
 * and borderless modern table with rounded status pills.
 * Pure standard Java Swing; zero Java Collections.
 */
public class CatalogPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final CatalogTableModel catalogTableModel;
    private boolean technicalDetails = false;

    // Search and Action controls
    private JTextField txtSearch;
    private JButton btnAddProduct;
    private JButton btnDeleteProduct;
    private JLabel lblTreeHeightBadge;

    // Filter pills
    private JLabel lblPillAll;
    private JLabel lblPillHealthy;
    private JLabel lblPillAttention;
    private JLabel lblTableSummary;

    private JTable tblCatalog;
    private Product selectedProduct = null;

    public CatalogPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.catalogTableModel = new CatalogTableModel(service.getInventoryBst());

        setLayout(new BorderLayout(0, 14));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Page Header (Title, Subtitle & Tech Badge)
        add(createPageHeader(), BorderLayout.NORTH);

        // 2. Center Content (Toolbar + Table Card)
        JPanel centerCard = new JPanel(new BorderLayout(0, 10));
        centerCard.setBackground(UiTheme.SURFACE);
        centerCard.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_CARD, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        centerCard.add(createToolbar(), BorderLayout.NORTH);
        centerCard.add(createTable(), BorderLayout.CENTER);
        centerCard.add(createTableFooter(), BorderLayout.SOUTH);

        add(centerCard, BorderLayout.CENTER);

        updateFilterPills();
        updateDeleteButtonState();
        SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
    }

    public void setTechnicalDetails(boolean enabled) {
        this.technicalDetails = enabled;
        if (lblTreeHeightBadge != null) {
            lblTreeHeightBadge.setVisible(enabled);
            if (enabled) {
                lblTreeHeightBadge.setText(" BST Height: " + service.getInventoryBst().height() + " | Size: " + service.getInventoryBst().size() + " ");
            }
        }
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftBox = new JPanel();
        leftBox.setOpaque(false);
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Product Management");
        lblTitle.setFont(UiTheme.FONT_PAGE_TITLE);
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Canonical inventory catalog entities, unit prices, and BST hierarchical indexing.");
        lblSubtitle.setFont(UiTheme.FONT_PAGE_SUBTITLE);
        lblSubtitle.setForeground(UiTheme.TEXT_MUTED);

        leftBox.add(lblTitle);
        leftBox.add(Box.createVerticalStrut(2));
        leftBox.add(lblSubtitle);
        header.add(leftBox, BorderLayout.WEST);

        // Technical details badge (Right)
        lblTreeHeightBadge = new JLabel();
        lblTreeHeightBadge.setFont(UiTheme.FONT_BADGE);
        lblTreeHeightBadge.setOpaque(true);
        lblTreeHeightBadge.setBackground(new Color(0xEB, 0xF5, 0xFF));
        lblTreeHeightBadge.setForeground(new Color(0x1D, 0x4E, 0xD8));
        lblTreeHeightBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xBF, 0xDB, 0xFE), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        lblTreeHeightBadge.setVisible(false);
        header.add(lblTreeHeightBadge, BorderLayout.EAST);

        return header;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 6, 0));

        // Left: Filter Pills
        JPanel pnlPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlPills.setOpaque(false);

        lblPillAll = createPill(UiTheme.SURFACE_ALT, UiTheme.BORDER, UiTheme.TEXT_PRIMARY);
        lblPillHealthy = createPill(new Color(0xDC, 0xFC, 0xE7), new Color(0xBB, 0xF7, 0xD0), new Color(0x16, 0x65, 0x34));
        lblPillAttention = createPill(new Color(0xFE, 0xF3, 0xC7), new Color(0xFD, 0xE6, 0x8A), new Color(0xB4, 0x53, 0x09));

        pnlPills.add(lblPillAll);
        pnlPills.add(lblPillHealthy);
        pnlPills.add(lblPillAttention);
        toolbar.add(pnlPills, BorderLayout.WEST);

        // Right: Search Input + "+ Add Product" + "Delete"
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBox.setOpaque(false);

        txtSearch = new JTextField(18);
        txtSearch.setFont(UiTheme.FONT_REGULAR);
        txtSearch.setToolTipText("Search products by code or name (press Enter)");
        UiTheme.installFocusRing(txtSearch);
        txtSearch.addActionListener(e -> handleSearch());
        txtSearch.registerKeyboardAction(
                e -> clearSearch(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED
        );

        JButton btnSearch = UiTheme.createSecondaryButton("Search", new UiTheme.VectorIcon(UiTheme.IconType.SEARCH, 13), "Search products");
        btnSearch.addActionListener(e -> handleSearch());

        btnAddProduct = UiTheme.createPrimaryButton("+ Add Product", new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 13), "Open modal to add a new product");
        btnAddProduct.addActionListener(e -> handleOpenAddDialog());

        btnDeleteProduct = UiTheme.createOutlineDangerButton("Delete", new UiTheme.VectorIcon(UiTheme.IconType.DELETE, 13), "Delete the selected product");
        btnDeleteProduct.setEnabled(false);
        btnDeleteProduct.addActionListener(e -> handleDeleteProduct());

        rightBox.add(txtSearch);
        rightBox.add(btnSearch);
        rightBox.add(btnAddProduct);
        rightBox.add(btnDeleteProduct);

        toolbar.add(rightBox, BorderLayout.EAST);
        return toolbar;
    }

    private JLabel createPill(Color bg, Color border, Color fg) {
        JLabel lbl = new JLabel();
        lbl.setFont(UiTheme.FONT_BADGE);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setBorder(new CompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return lbl;
    }

    private JScrollPane createTable() {
        tblCatalog = new JTable(catalogTableModel);
        UiTheme.configureTable(tblCatalog);

        TableColumnModel colModel = tblCatalog.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(95);  // PROD CODE
        colModel.getColumn(1).setPreferredWidth(280); // PRODUCT NAME
        colModel.getColumn(2).setPreferredWidth(120); // PRICE
        colModel.getColumn(3).setPreferredWidth(100); // IN STOCK
        colModel.getColumn(4).setPreferredWidth(120); // STATUS

        // Rounded pill status renderer on column 4
        colModel.getColumn(4).setCellRenderer(new UiTheme.StatusPillRenderer());

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

        JScrollPane sp = UiTheme.createModernScrollPane(tblCatalog);
        sp.setBorder(new LineBorder(new Color(0xE5, 0xE7, 0xEB), 1));
        return sp;
    }

    private JPanel createTableFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 4, 2, 4));

        lblTableSummary = new JLabel("Showing all products in catalog");
        lblTableSummary.setFont(UiTheme.FONT_HINT);
        lblTableSummary.setForeground(UiTheme.TEXT_MUTED);

        footer.add(lblTableSummary, BorderLayout.WEST);
        return footer;
    }

    private void handleOpenAddDialog() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        AddProductDialog dialog = new AddProductDialog(topFrame, service, mainFrame);
        dialog.setVisible(true);

        if (dialog.isProductAdded()) {
            refreshTables();
        }
    }

    private void selectProduct(Product p) {
        this.selectedProduct = p;
        updateDeleteButtonState();
        lblTableSummary.setText("Selected: Code #" + p.getSkuId() + " (" + p.getName() + ") • ₱"
                + String.format("%,.2f", p.getPrice()) + " • " + p.getStockQty() + " units in stock.");
    }

    private void updateDeleteButtonState() {
        btnDeleteProduct.setEnabled(selectedProduct != null);
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            mainFrame.setFeedback(false, "Please type a product code or product name to search.");
            return;
        }

        Product product = null;
        try {
            int skuId = Integer.parseInt(query);
            product = service.findProduct(skuId);
        } catch (NumberFormatException ignored) {
            // Search by name
        }

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
            mainFrame.setFeedback(false, "No product found matching '" + query + "'.");
            lblTableSummary.setText("No product found matching '" + query + "'.");
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        selectedProduct = null;
        updateDeleteButtonState();
        tblCatalog.clearSelection();
        mainFrame.setFeedback(true, "Search cleared.");
        updateFilterPills();
    }

    private void handleDeleteProduct() {
        if (selectedProduct == null) {
            mainFrame.setFeedback(false, "Please select a product row first.");
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
                updateDeleteButtonState();
                mainFrame.setFeedback(true, "Product '" + name + "' was deleted.");
                mainFrame.refreshAll();
            } else {
                mainFrame.setFeedback(false, result.getMessage());
                JOptionPane.showMessageDialog(this, result.getMessage(), "Delete Blocked", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateFilterPills() {
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

        if (lblPillAll != null) {
            lblPillAll.setText("All (" + total + ")");
        }
        if (lblPillHealthy != null) {
            lblPillHealthy.setText("In Stock (" + healthy + ")");
        }
        if (lblPillAttention != null) {
            lblPillAttention.setText("Low / Out (" + attention + ")");
        }
        if (lblTableSummary != null && selectedProduct == null) {
            lblTableSummary.setText("Showing all " + total + " products in catalog");
        }
    }

    public void refreshTables() {
        catalogTableModel.refresh();
        updateFilterPills();
        if (lblTreeHeightBadge != null && technicalDetails) {
            lblTreeHeightBadge.setText(" BST Height: " + service.getInventoryBst().height() + " | Size: " + service.getInventoryBst().size() + " ");
        }
    }
}
