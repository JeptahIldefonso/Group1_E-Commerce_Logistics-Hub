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
 * Tab 3: Restock (Inventory Restock Audit).
 * Modern SaaS ERP layout:
 * Page header, top action bar with "Undo Last Restock" and "+ Add Stock" modal trigger,
 * and clean borderless restock audit table with soft amber highlight for Row 0.
 * Pure standard Java Swing; zero Java Collections.
 */
public class RestockPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final RestockTableModel restockTableModel;

    // Action button & cues
    private JButton btnAddStock;
    private JButton btnUndo;
    private JLabel lblLastRestockSummary;
    private JLabel lblStackCapacity;
    private JTable tblRestock;

    public RestockPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.restockTableModel = new RestockTableModel(service.getRestockStack(), service);

        setLayout(new BorderLayout(0, 14));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Page Header (Title + Subtitle)
        add(createPageHeader(), BorderLayout.NORTH);

        // 2. Center Card (Toolbar + Table)
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

        updateButtonStatesAndSummary();
    }

    public void setTechnicalDetails(boolean enabled) {
        restockTableModel.setTechnicalDetails(enabled);
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftBox = new JPanel();
        leftBox.setOpaque(false);
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Inventory Restock Audit");
        lblTitle.setFont(UiTheme.FONT_PAGE_TITLE);
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("LIFO replenishment stack with atomic rollback protection and safe stock deduction.");
        lblSubtitle.setFont(UiTheme.FONT_PAGE_SUBTITLE);
        lblSubtitle.setForeground(UiTheme.TEXT_MUTED);

        leftBox.add(lblTitle);
        leftBox.add(Box.createVerticalStrut(2));
        leftBox.add(lblSubtitle);
        header.add(leftBox, BorderLayout.WEST);

        return header;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 6, 0));

        // Left: Last restock summary pill
        lblLastRestockSummary = new JLabel(" Last Restock: Loading...");
        lblLastRestockSummary.setFont(UiTheme.FONT_SUBTITLE);
        lblLastRestockSummary.setOpaque(true);
        lblLastRestockSummary.setBackground(UiTheme.HIGHLIGHT_STACK_ROW0);
        lblLastRestockSummary.setForeground(UiTheme.TEXT_PRIMARY);
        lblLastRestockSummary.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.HIGHLIGHT_STACK_ROW0_BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        toolbar.add(lblLastRestockSummary, BorderLayout.CENTER);

        // Right: Undo button + "+ Add Stock" button
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        btnUndo = UiTheme.createWarningButton("Undo Last Restock",
                new UiTheme.VectorIcon(UiTheme.IconType.UNDO, 14),
                "Roll back the most recent restock and subtract stock");
        btnUndo.addActionListener(e -> handleUndo());

        btnAddStock = UiTheme.createSuccessButton("Add Stock",
                new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 14),
                "Open modal to replenish stock for a product");
        btnAddStock.addActionListener(e -> handleOpenAddStockDialog());

        btnBar.add(btnUndo);
        btnBar.add(btnAddStock);

        toolbar.add(btnBar, BorderLayout.EAST);
        return toolbar;
    }

    private JScrollPane createTable() {
        tblRestock = new JTable(restockTableModel);
        UiTheme.configureTable(tblRestock);
        tblRestock.setDefaultRenderer(Object.class, UiTheme.createStackCellRenderer());
        tblRestock.setFillsViewportHeight(true);

        TableColumnModel colModel = tblRestock.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100); // Restock #
        colModel.getColumn(1).setPreferredWidth(280); // Product
        colModel.getColumn(2).setPreferredWidth(130); // Quantity Added
        colModel.getColumn(3).setPreferredWidth(120); // Order of Entry

        JScrollPane sp = UiTheme.createModernScrollPane(tblRestock);
        sp.setBorder(new LineBorder(UiTheme.BORDER_CARD, 1));
        return sp;
    }

    private JPanel createTableFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 4, 2, 4));

        lblStackCapacity = new JLabel("Audit Log in Stack");
        lblStackCapacity.setFont(UiTheme.FONT_HINT);
        lblStackCapacity.setForeground(UiTheme.TEXT_MUTED);

        footer.add(lblStackCapacity, BorderLayout.WEST);
        return footer;
    }

    private void handleOpenAddStockDialog() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        AddStockDialog dialog = new AddStockDialog(topFrame, service, mainFrame);
        dialog.setVisible(true);

        if (dialog.isStockAdded()) {
            refreshTables();
        }
    }

    public void updateButtonStatesAndSummary() {
        boolean stackEmpty = service.getRestockStack().isEmpty();
        btnUndo.setEnabled(!stackEmpty);

        if (stackEmpty) {
            lblLastRestockSummary.setText(" No restock actions to undo.");
            lblLastRestockSummary.setBackground(UiTheme.SURFACE_ALT);
            lblLastRestockSummary.setForeground(UiTheme.TEXT_MUTED);
            lblLastRestockSummary.setBorder(new CompoundBorder(
                    new LineBorder(UiTheme.BORDER_CARD, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
            lblStackCapacity.setText("Restock Stack: 0/20 actions");
        } else {
            RestockAction top = service.getRestockStack().peek();
            Product p = service.findProduct(top.getSkuId());
            String productName = p != null ? p.getName() : "Item #" + top.getSkuId();

            lblLastRestockSummary.setText(" Last Restock: Restock #" + top.getActionId()
                    + " · Added +" + top.getQuantityAdded() + " units to " + productName);
            lblLastRestockSummary.setBackground(UiTheme.HIGHLIGHT_STACK_ROW0);
            lblLastRestockSummary.setForeground(UiTheme.TEXT_PRIMARY);
            lblLastRestockSummary.setBorder(new CompoundBorder(
                    new LineBorder(UiTheme.HIGHLIGHT_STACK_ROW0_BORDER, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
            lblStackCapacity.setText("Restock Stack: " + service.getRestockStack().size() + "/20 actions (LIFO array)");
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
        updateButtonStatesAndSummary();
    }
}
