package com.group1.logistics.ui;

import com.group1.logistics.service.FulfillmentService;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame for the E-Commerce Fulfillment & Logistics System.
 * Houses 3 operational tabs and a live system status bar.
 */
public class MainFrame extends JFrame {
    private final FulfillmentService service;

    private final OrdersRestockPanel ordersRestockPanel;
    private final CatalogPanel catalogPanel;
    private final FreightPanel freightPanel;

    private final JLabel lblStatusBar;

    public MainFrame(FulfillmentService service) {
        super("E-Commerce Fulfillment & Logistics Hub (Group 1 - BSIS DSA Final Project)");
        this.service = service;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null); // Center on screen

        // Initialize Tab Panels
        this.ordersRestockPanel = new OrdersRestockPanel(service, this);
        this.catalogPanel = new CatalogPanel(service, this);
        this.freightPanel = new FreightPanel(service, this);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabbedPane.addTab("Orders & Restock Desk (Queue & Stack)", ordersRestockPanel);
        tabbedPane.addTab("SKU Catalog Index (BST Engine)", catalogPanel);
        tabbedPane.addTab("Freight Logistics Hub (Custom Sorters)", freightPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Status Bar
        lblStatusBar = new JLabel();
        lblStatusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        add(lblStatusBar, BorderLayout.SOUTH);

        refreshAll();
    }

    /**
     * Refreshes all 3 table models and updates the status bar counts.
     */
    public void refreshAll() {
        ordersRestockPanel.refreshTables();
        catalogPanel.refreshTables();
        freightPanel.refreshTables();

        lblStatusBar.setText(String.format(
                " Order Queue: %d/20   |   Restock Audit Stack: %d/20   |   Catalog: %d SKUs (Tree Height: %d)   |   Dispatched Freight Packages: %d",
                service.getOrderQueue().size(),
                service.getRestockStack().size(),
                service.getInventoryBst().size(),
                service.getInventoryBst().height(),
                service.getPackageArray().size()
        ));
    }
}
