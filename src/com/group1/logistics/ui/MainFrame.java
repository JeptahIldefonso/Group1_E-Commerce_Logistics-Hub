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
 * Houses 3 operational tabs, a non-blocking inline feedback bar, and a live system status bar.
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
        super("E-Commerce Fulfillment & Logistics Hub (Group 1 - BSIS DSA Final Project)");
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

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(UiTheme.BG_WINDOW);

        tabbedPane.addTab("Orders & Restock Desk (Queue & Stack)", ordersRestockPanel);
        tabbedPane.addTab("SKU Catalog Index (BST Engine)", catalogPanel);
        tabbedPane.addTab("Freight Logistics Hub (Custom Sorters)", freightPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Container: Inline Feedback Bar + System Status Bar
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 0));
        bottomContainer.setBackground(UiTheme.SURFACE);

        // 1. Non-blocking Inline Feedback Bar (Soft Green / Soft Red / Soft Blue)
        pnlFeedback = new JPanel(new BorderLayout(8, 0));
        pnlFeedback.setBackground(UiTheme.FEEDBACK_SUCCESS_BG);
        pnlFeedback.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UiTheme.BORDER),
                new EmptyBorder(6, 14, 6, 14)
        ));

        lblFeedback = new JLabel("✓ System Ready — Initialized with 14 catalog SKUs, 5 queued orders, and 5 dispatched parcels.");
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

        JLabel lblCopyright = new JLabel("BSIS 2nd Year DSA Final Lab · Group 1 (Standalone Pure Java 17)");
        lblCopyright.setFont(UiTheme.FONT_HINT);
        lblCopyright.setForeground(UiTheme.TEXT_MUTED);
        pnlStatus.add(lblCopyright, BorderLayout.EAST);

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
            lblFeedback.setText("✓ " + message);
        } else {
            pnlFeedback.setBackground(UiTheme.FEEDBACK_ERROR_BG);
            lblFeedback.setForeground(UiTheme.FEEDBACK_ERROR_TEXT);
            lblFeedback.setText("✕ " + message);
        }
        pnlFeedback.revalidate();
        pnlFeedback.repaint();
    }

    /**
     * Refreshes all 3 table models, dynamic SKU dropdowns, button states, and status bar counts.
     */
    public void refreshAll() {
        ordersRestockPanel.refreshTables();
        catalogPanel.refreshTables();
        freightPanel.refreshTables();

        lblStatusBar.setText(String.format(
                "Order Queue: %d/20   |   Restock Audit Stack: %d/20   |   Catalog: %d SKUs (Tree Height: %d)   |   Dispatched Freight Packages: %d",
                service.getOrderQueue().size(),
                service.getRestockStack().size(),
                service.getInventoryBst().size(),
                service.getInventoryBst().height(),
                service.getPackageArray().size()
        ));
    }
}
