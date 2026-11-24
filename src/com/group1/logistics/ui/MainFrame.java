package com.group1.logistics.ui;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Main application frame for the E-Commerce Fulfillment & Logistics System.
 * Clean, modern layout with non-blocking feedback and status bar.
 * Pure standard Java Swing; zero Java Collections.
 */
public class MainFrame extends JFrame {
    private final FulfillmentService service;

    private final OrdersRestockPanel ordersRestockPanel;
    private final CatalogPanel catalogPanel;
    private final FreightPanel freightPanel;

    private final JPanel pnlFeedback;
    private final JLabel lblFeedback;
    private final JLabel lblStatusBar;

    public MainFrame(FulfillmentService service) {
        super("E-Commerce Fulfillment & Logistics Hub (Group 1)");
        this.service = service;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1024, 650));
        setLocationRelativeTo(null); // Center on screen

        getContentPane().setBackground(UiTheme.BG_WINDOW);
        setLayout(new BorderLayout(0, 0));

        // Initialize Tab Panels
        this.ordersRestockPanel = new OrdersRestockPanel(service, this);
        this.catalogPanel = new CatalogPanel(service, this);
        this.freightPanel = new FreightPanel(service, this);

        // Tabbed Pane with concise, clear titles
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(UiTheme.BG_WINDOW);

        tabbedPane.addTab("Orders & Restock", ordersRestockPanel);
        tabbedPane.addTab("Product Catalog", catalogPanel);
        tabbedPane.addTab("Freight Logistics", freightPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Container: Inline Feedback Bar + System Status Bar
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 0));
        bottomContainer.setBackground(UiTheme.SURFACE);

        // 1. Non-blocking Inline Feedback Bar (Clean text, zero broken unicode boxes)
        pnlFeedback = new JPanel(new BorderLayout(8, 0));
        pnlFeedback.setBackground(UiTheme.FEEDBACK_SUCCESS_BG);
        pnlFeedback.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UiTheme.BORDER),
                new EmptyBorder(6, 14, 6, 14)
        ));

        lblFeedback = new JLabel("[OK] System Ready — 14 products in catalog, 5 pending orders, 5 dispatched packages.");
        lblFeedback.setFont(UiTheme.FONT_BOLD);
        lblFeedback.setForeground(UiTheme.FEEDBACK_SUCCESS_TEXT);
        pnlFeedback.add(lblFeedback, BorderLayout.CENTER);

        bottomContainer.add(pnlFeedback, BorderLayout.NORTH);

        // 2. System Status Bar
        JPanel pnlStatus = new JPanel(new BorderLayout(8, 0));
        pnlStatus.setBackground(UiTheme.SURFACE_ALT);
        pnlStatus.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UiTheme.BORDER_CARD),
                new EmptyBorder(5, 14, 5, 14)
        ));

        lblStatusBar = new JLabel();
        lblStatusBar.setFont(UiTheme.FONT_REGULAR);
        lblStatusBar.setForeground(UiTheme.TEXT_PRIMARY);
        pnlStatus.add(lblStatusBar, BorderLayout.WEST);

        JLabel lblFooter = new JLabel("Group 1 Logistics Hub · Java 17");
        lblFooter.setFont(UiTheme.FONT_HINT);
        lblFooter.setForeground(UiTheme.TEXT_MUTED);
        pnlStatus.add(lblFooter, BorderLayout.EAST);

        bottomContainer.add(pnlStatus, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);

        refreshAll();
    }

    /**
     * Updates the non-blocking inline feedback banner with the result of an operation.
     */
    public void setFeedback(OpResult result) {
        if (result == null) return;
        setFeedback(result.isSuccess(), result.getMessage());
    }

    /**
     * Updates the non-blocking inline feedback banner with custom status and message.
     */
    public void setFeedback(boolean success, String message) {
        if (success) {
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

    /**
     * Refreshes all table models, dropdowns, button states, and status counts.
     */
    public void refreshAll() {
        ordersRestockPanel.refreshTables();
        catalogPanel.refreshTables();
        freightPanel.refreshTables();

        lblStatusBar.setText(String.format(
                "Queue: %d/20   |   Restock Stack: %d/20   |   Catalog: %d SKUs (Tree Height: %d)   |   Freight: %d Packages",
                service.getOrderQueue().size(),
                service.getRestockStack().size(),
                service.getInventoryBst().size(),
                service.getInventoryBst().height(),
                service.getPackageArray().size()
        ));
    }
}
