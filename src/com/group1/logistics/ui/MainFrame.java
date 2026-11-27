package com.group1.logistics.ui;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Main application frame: Order & Shipping Manager.
 * Modern SaaS ERP layout (ElectronDB style):
 * Left side navigation bar (SidebarPanel), CardLayout central workspace,
 * and bottom subtle notification toast strip.
 * Pure standard Java Swing; zero Java Collections.
 */
public class MainFrame extends JFrame {
    private final FulfillmentService service;

    private final SidebarPanel sidebarPanel;
    private final CardLayout cardLayout;
    private final JPanel pnlContent;

    private final OrdersPanel ordersPanel;
    private final CatalogPanel catalogPanel;
    private final RestockPanel restockPanel;
    private final FreightPanel freightPanel;

    private final JPanel pnlFeedback;
    private final JLabel lblFeedback;

    private static final String CARD_ORDERS = "ORDERS";
    private static final String CARD_PRODUCTS = "PRODUCTS";
    private static final String CARD_RESTOCK = "RESTOCK";
    private static final String CARD_SHIPMENTS = "SHIPMENTS";

    public MainFrame(FulfillmentService service) {
        super("Order & Shipping Manager — Logistics Hub");
        this.service = service;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null); // Center on screen

        getContentPane().setBackground(UiTheme.BG_WINDOW);
        setLayout(new BorderLayout(0, 0));

        // 1. Center Content Area (CardLayout)
        this.cardLayout = new CardLayout();
        this.pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(UiTheme.BG_WINDOW);

        this.ordersPanel = new OrdersPanel(service, this);
        this.catalogPanel = new CatalogPanel(service, this);
        this.restockPanel = new RestockPanel(service, this);
        this.freightPanel = new FreightPanel(service, this);

        pnlContent.add(ordersPanel, CARD_ORDERS);
        pnlContent.add(catalogPanel, CARD_PRODUCTS);
        pnlContent.add(restockPanel, CARD_RESTOCK);
        pnlContent.add(freightPanel, CARD_SHIPMENTS);

        add(pnlContent, BorderLayout.CENTER);

        // 2. Left Sidebar Navigation
        this.sidebarPanel = new SidebarPanel(this);
        add(sidebarPanel, BorderLayout.WEST);

        // 3. Bottom Feedback Notification Strip
        pnlFeedback = new JPanel(new BorderLayout(8, 0));
        pnlFeedback.setBackground(UiTheme.FEEDBACK_READY_BG);
        pnlFeedback.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UiTheme.BORDER),
                new EmptyBorder(6, 16, 6, 16)
        ));

        lblFeedback = new JLabel("Ready.");
        lblFeedback.setFont(UiTheme.FONT_BOLD);
        lblFeedback.setForeground(UiTheme.FEEDBACK_READY_TEXT);
        pnlFeedback.add(lblFeedback, BorderLayout.CENTER);

        add(pnlFeedback, BorderLayout.SOUTH);

        // 4. Keyboard Shortcuts (Ctrl + 1..4)
        getRootPane().registerKeyboardAction(
                e -> sidebarPanel.selectTab(0),
                KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        getRootPane().registerKeyboardAction(
                e -> sidebarPanel.selectTab(1),
                KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        getRootPane().registerKeyboardAction(
                e -> sidebarPanel.selectTab(2),
                KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        getRootPane().registerKeyboardAction(
                e -> sidebarPanel.selectTab(3),
                KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        refreshAll();
        sidebarPanel.selectTab(1); // Default to Products tab
    }

    /**
     * Switches the active module view in the center CardLayout and refreshes its live data.
     */
    public void showTab(int index) {
        if (cardLayout == null || pnlContent == null) {
            return;
        }
        switch (index) {
            case 0:
                ordersPanel.refreshTables();
                cardLayout.show(pnlContent, CARD_ORDERS);
                break;
            case 1:
                catalogPanel.refreshTables();
                cardLayout.show(pnlContent, CARD_PRODUCTS);
                break;
            case 2:
                restockPanel.refreshTables();
                cardLayout.show(pnlContent, CARD_RESTOCK);
                break;
            case 3:
                freightPanel.refreshTables();
                cardLayout.show(pnlContent, CARD_SHIPMENTS);
                break;
        }
    }

    public void handleToggleTechnicalDetails(boolean enabled) {
        catalogPanel.setTechnicalDetails(enabled);
        restockPanel.setTechnicalDetails(enabled);
        freightPanel.setTechnicalDetails(enabled);
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
            lblFeedback.setText(message);
        } else {
            pnlFeedback.setBackground(UiTheme.FEEDBACK_ERROR_BG);
            lblFeedback.setForeground(UiTheme.FEEDBACK_ERROR_TEXT);
            lblFeedback.setText(message);
        }
        pnlFeedback.revalidate();
        pnlFeedback.repaint();
    }

    /**
     * Refreshes all table models, dynamic dropdowns, button states, and sidebar live count badges.
     */
    public void refreshAll() {
        ordersPanel.refreshTables();
        catalogPanel.refreshTables();
        restockPanel.refreshTables();
        freightPanel.refreshTables();

        if (sidebarPanel != null) {
            sidebarPanel.updateBadges(
                    service.getOrderQueue().size(),
                    service.getInventoryBst().size(),
                    service.getRestockStack().size(),
                    service.getPackageArray().size()
            );
        }
    }
}
