package com.group1.logistics.ui;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Main application frame: Order & Shipping Manager.
 * 4 Dedicated Tabs: Orders, Products, Restock, Shipments.
 * Includes "Show technical details" toggle and clean "Ready." message bar.
 * Pure standard Java Swing; zero Java Collections.
 */
public class MainFrame extends JFrame {
    private final FulfillmentService service;

    private final OrdersPanel ordersPanel;
    private final CatalogPanel catalogPanel;
    private final RestockPanel restockPanel;
    private final FreightPanel freightPanel;

    private final JPanel pnlFeedback;
    private final JLabel lblFeedback;
    private final JLabel lblStatusBar;
    private final JCheckBox chkTechnicalDetails;

    public MainFrame(FulfillmentService service) {
        super("Order & Shipping Manager");
        this.service = service;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 740);
        setMinimumSize(new Dimension(1024, 650));
        setLocationRelativeTo(null); // Center on screen

        getContentPane().setBackground(UiTheme.BG_WINDOW);
        setLayout(new BorderLayout(0, 0));

        // Initialize 4 Tab Panels (One job per tab)
        this.ordersPanel = new OrdersPanel(service, this);
        this.catalogPanel = new CatalogPanel(service, this);
        this.restockPanel = new RestockPanel(service, this);
        this.freightPanel = new FreightPanel(service, this);

        // Tabbed Pane with concise 4 tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(UiTheme.BG_WINDOW);

        tabbedPane.addTab("Orders", ordersPanel);
        tabbedPane.addTab("Products", catalogPanel);
        tabbedPane.addTab("Restock", restockPanel);
        tabbedPane.addTab("Shipments", freightPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Container: Message Bar + Status Bar with Technical Details toggle
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 0));
        bottomContainer.setBackground(UiTheme.SURFACE);

        // 1. Message Bar (Default: "Ready.", Green for Success, Red for Errors)
        pnlFeedback = new JPanel(new BorderLayout(8, 0));
        pnlFeedback.setBackground(UiTheme.FEEDBACK_READY_BG);
        pnlFeedback.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UiTheme.BORDER),
                new EmptyBorder(6, 14, 6, 14)
        ));

        lblFeedback = new JLabel("Ready.");
        lblFeedback.setFont(UiTheme.FONT_BOLD);
        lblFeedback.setForeground(UiTheme.FEEDBACK_READY_TEXT);
        pnlFeedback.add(lblFeedback, BorderLayout.CENTER);

        bottomContainer.add(pnlFeedback, BorderLayout.NORTH);

        // 2. System Status Bar + Technical Details Checkbox
        JPanel pnlStatus = new JPanel(new BorderLayout(10, 0));
        pnlStatus.setBackground(UiTheme.SURFACE_ALT);
        pnlStatus.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UiTheme.BORDER_CARD),
                new EmptyBorder(5, 14, 5, 14)
        ));

        lblStatusBar = new JLabel();
        lblStatusBar.setFont(UiTheme.FONT_REGULAR);
        lblStatusBar.setForeground(UiTheme.TEXT_PRIMARY);
        pnlStatus.add(lblStatusBar, BorderLayout.CENTER);

        // Technical details toggle (OFF by default for normal users)
        chkTechnicalDetails = new JCheckBox("Show technical details");
        chkTechnicalDetails.setFont(UiTheme.FONT_HINT);
        chkTechnicalDetails.setForeground(UiTheme.TEXT_MUTED);
        chkTechnicalDetails.setBackground(UiTheme.SURFACE_ALT);
        chkTechnicalDetails.setFocusPainted(false);
        chkTechnicalDetails.setToolTipText("Toggle visibility of algorithm names, tree height, and sequence numbers");
        chkTechnicalDetails.addActionListener(e -> handleToggleTechnicalDetails());

        pnlStatus.add(chkTechnicalDetails, BorderLayout.EAST);
        bottomContainer.add(pnlStatus, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);

        refreshAll();
    }

    private void handleToggleTechnicalDetails() {
        boolean enabled = chkTechnicalDetails.isSelected();
        catalogPanel.setTechnicalDetails(enabled);
        restockPanel.setTechnicalDetails(enabled);
        freightPanel.setTechnicalDetails(enabled);
        updateStatusBarText();
    }

    /**
     * Updates the message bar with the result of an operation.
     */
    public void setFeedback(OpResult result) {
        if (result == null) return;
        setFeedback(result.isSuccess(), result.getMessage());
    }

    /**
     * Updates the message bar with custom status and message.
     */
    public void setFeedback(boolean success, String message) {
        if (message == null || message.trim().isEmpty()) {
            pnlFeedback.setBackground(UiTheme.FEEDBACK_READY_BG);
            lblFeedback.setForeground(UiTheme.FEEDBACK_READY_TEXT);
            lblFeedback.setText("Ready.");
        } else if (success) {
            pnlFeedback.setBackground(UiTheme.FEEDBACK_SUCCESS_BG);
            lblFeedback.setForeground(UiTheme.FEEDBACK_SUCCESS_TEXT);
            lblFeedback.setText("[OK] " + message);
        } else {
            pnlFeedback.setBackground(UiTheme.FEEDBACK_ERROR_BG);
            lblFeedback.setForeground(UiTheme.FEEDBACK_ERROR_TEXT);
            lblFeedback.setText("[Notice] " + message);
        }
        pnlFeedback.revalidate();
        pnlFeedback.repaint();
    }

    private void updateStatusBarText() {
        boolean tech = chkTechnicalDetails != null && chkTechnicalDetails.isSelected();
        if (tech) {
            lblStatusBar.setText(String.format(
                    "Waiting Orders: %d/20 (Circular Queue)  |  Restocks: %d/20 (Stack)  |  Catalog: %d SKUs (Tree Height: %d)  |  Shipped Packages: %d",
                    service.getOrderQueue().size(),
                    service.getRestockStack().size(),
                    service.getInventoryBst().size(),
                    service.getInventoryBst().height(),
                    service.getPackageArray().size()
            ));
        } else {
            lblStatusBar.setText(String.format(
                    "Waiting Orders: %d  |  Restocks You Can Undo: %d  |  Products: %d  |  Shipped Packages: %d",
                    service.getOrderQueue().size(),
                    service.getRestockStack().size(),
                    service.getInventoryBst().size(),
                    service.getPackageArray().size()
            ));
        }
    }

    /**
     * Refreshes all 4 table models, dynamic dropdowns, button states, and status counts.
     */
    public void refreshAll() {
        ordersPanel.refreshTables();
        catalogPanel.refreshTables();
        restockPanel.refreshTables();
        freightPanel.refreshTables();

        updateStatusBarText();
    }
}
