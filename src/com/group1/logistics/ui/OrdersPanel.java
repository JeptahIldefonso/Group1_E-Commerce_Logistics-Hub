package com.group1.logistics.ui;

import com.group1.logistics.models.Order;
import com.group1.logistics.models.Product;
import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class OrdersPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final QueueTableModel queueTableModel;

    private JButton btnPlaceOrder;
    private JButton btnShipNext;
    private JButton btnCancelNext;
    private JLabel lblNextOrderSummary;
    private JLabel lblQueueCapacity;
    private JTable tblOrders;
    private boolean dialogOpen = false;
    private boolean processingAction = false;

    public static class ProductComboItem {
        private final int skuId;
        private final String name;
        private final int stock;

        public ProductComboItem(int skuId, String name, int stock) {
            this.skuId = skuId;
            this.name = name;
            this.stock = stock;
        }

        public int getSkuId() {
            return skuId;
        }

        public String getName() {
            return name;
        }

        public int getStock() {
            return stock;
        }

        @Override
        public String toString() {
            return name + " - " + stock + " available";
        }
    }

    public OrdersPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.queueTableModel = new QueueTableModel(service.getOrderQueue(), service);

        setLayout(new BorderLayout(0, 14));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        add(createPageHeader(), BorderLayout.NORTH);

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

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftBox = new JPanel();
        leftBox.setOpaque(false);
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Order Fulfillment");
        lblTitle.setFont(UiTheme.FONT_PAGE_TITLE);
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("FIFO customer dispatch queue, order processing, and warehouse fulfillment.");
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

        lblNextOrderSummary = new JLabel(" Next order: Loading...");
        lblNextOrderSummary.setFont(UiTheme.FONT_SUBTITLE);
        lblNextOrderSummary.setOpaque(true);
        lblNextOrderSummary.setBackground(UiTheme.HIGHLIGHT_QUEUE_ROW0);
        lblNextOrderSummary.setForeground(UiTheme.TEXT_PRIMARY);
        lblNextOrderSummary.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.HIGHLIGHT_QUEUE_ROW0_BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        toolbar.add(lblNextOrderSummary, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        btnCancelNext = UiTheme.createOutlineDangerButton("Cancel Next",
                new UiTheme.VectorIcon(UiTheme.IconType.CANCEL, 13),
                "Cancel the next order in line without deducting stock");
        btnCancelNext.addActionListener(e -> handleCancelNext());

        btnShipNext = UiTheme.createAccentButton("Ship Next Order",
                new UiTheme.VectorIcon(UiTheme.IconType.SHIP, 15),
                "Ship the next order in line and prompt for weight");
        btnShipNext.addActionListener(e -> handleShipNext());

        btnPlaceOrder = UiTheme.createPrimaryButton("Place Order",
                new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 13),
                "Open modal to intake a new customer order");
        btnPlaceOrder.addActionListener(e -> handleOpenPlaceDialog());

        btnBar.add(btnCancelNext);
        btnBar.add(btnShipNext);
        btnBar.add(btnPlaceOrder);

        toolbar.add(btnBar, BorderLayout.EAST);
        return toolbar;
    }

    private JScrollPane createTable() {
        tblOrders = new JTable(queueTableModel);
        UiTheme.configureTable(tblOrders);
        tblOrders.setDefaultRenderer(Object.class, UiTheme.createQueueCellRenderer());
        tblOrders.setFillsViewportHeight(true);

        TableColumnModel colModel = tblOrders.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(55);
        colModel.getColumn(1).setPreferredWidth(80);
        colModel.getColumn(2).setPreferredWidth(170);
        colModel.getColumn(3).setPreferredWidth(220);
        colModel.getColumn(4).setPreferredWidth(60);
        colModel.getColumn(5).setPreferredWidth(170);
        colModel.getColumn(6).setPreferredWidth(140);

        JScrollPane sp = UiTheme.createModernScrollPane(tblOrders);
        sp.setBorder(new LineBorder(UiTheme.BORDER_CARD, 1));
        return sp;
    }

    private JPanel createTableFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 4, 2, 4));

        lblQueueCapacity = new JLabel("Waiting Orders in Queue");
        lblQueueCapacity.setFont(UiTheme.FONT_HINT);
        lblQueueCapacity.setForeground(UiTheme.TEXT_MUTED);

        footer.add(lblQueueCapacity, BorderLayout.WEST);
        return footer;
    }

    private void handleOpenPlaceDialog() {
        if (dialogOpen) {
            return;
        }
        dialogOpen = true;
        try {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            PlaceOrderDialog dialog = new PlaceOrderDialog(topFrame, service, mainFrame);
            dialog.setVisible(true);

            if (dialog.isOrderPlaced()) {
                refreshTables();
            }
        } finally {
            dialogOpen = false;
        }
    }

    public void updateButtonStatesAndSummary() {
        boolean queueEmpty = service.getOrderQueue().isEmpty();
        btnShipNext.setEnabled(!queueEmpty);
        btnCancelNext.setEnabled(!queueEmpty);

        if (queueEmpty) {
            lblNextOrderSummary.setText(" All caught up! No pending orders waiting in queue.");
            lblNextOrderSummary.setBackground(UiTheme.SURFACE_ALT);
            lblNextOrderSummary.setForeground(UiTheme.TEXT_MUTED);
            lblNextOrderSummary.setBorder(new CompoundBorder(
                    new LineBorder(UiTheme.BORDER_CARD, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
            lblQueueCapacity.setText("Queue: 0/20 orders waiting");
        } else {
            Order front = service.getOrderQueue().peek();

            lblNextOrderSummary.setText(" Next to Ship: #" + front.getOrderId() + " (" + front.getCustomerName() + ")");
            lblNextOrderSummary.setBackground(UiTheme.HIGHLIGHT_QUEUE_ROW0);
            lblNextOrderSummary.setForeground(UiTheme.TEXT_PRIMARY);
            lblNextOrderSummary.setBorder(new CompoundBorder(
                    new LineBorder(UiTheme.HIGHLIGHT_QUEUE_ROW0_BORDER, 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
            lblQueueCapacity.setText("Queue: " + service.getOrderQueue().size() + "/20 orders waiting (FIFO circular array)");
        }
    }

    private void handleShipNext() {
        if (processingAction) {
            return;
        }
        processingAction = true;
        try {
            if (service.getOrderQueue().isEmpty()) {
                mainFrame.setFeedback(false, "No orders waiting to ship.");
                return;
            }

            Order front = service.getOrderQueue().peek();
            Product p = service.findProduct(front.getSkuId());
            if (p == null) {
                String msg = "Dispatch blocked: SKU #" + front.getSkuId() + " was discontinued! (Click 'Cancel Next' to remove).";
                mainFrame.setFeedback(false, msg);
                JOptionPane.showMessageDialog(this, msg, "Shipping Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p.getStockQty() < front.getQuantity()) {
                String msg = "Insufficient stock for SKU #" + front.getSkuId() +
                        " (Available: " + p.getStockQty() + ", Requested: " + front.getQuantity() + "). Order remains in queue.";
                mainFrame.setFeedback(false, msg);
                JOptionPane.showMessageDialog(this, msg, "Shipping Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String productName = p.getName();
            String input = JOptionPane.showInputDialog(this,
                    "Shipping Order #" + front.getOrderId() + " (" + front.getCustomerName() + ")\n" +
                    "Item: " + front.getQuantity() + " × " + productName + "\n" +
                    "Deliver To: " + front.getDestination() + "\n\n" +
                    "Enter parcel weight in kg (0.01 - 50.00 kg):",
                    "Ship Order", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                return;
            }

            try {
                double weight = Double.parseDouble(input.trim());
                if (weight < 0.01 || weight > 50.00) {
                    mainFrame.setFeedback(false, "Parcel weight must be between 0.01 and 50.00 kg.");
                    JOptionPane.showMessageDialog(this, "Parcel weight must be between 0.01 and 50.00 kg.", "Invalid Weight", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                OpResult result = service.dispatchNext(weight);
                if (result.isSuccess()) {
                    mainFrame.setFeedback(true, "Order #" + front.getOrderId() + " shipped to " + front.getDestination() + ".");
                    mainFrame.refreshAll();
                } else {
                    mainFrame.setFeedback(false, result.getMessage());
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Shipping Blocked", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                mainFrame.setFeedback(false, "Weight must be a valid number (e.g. 2.45).");
            }
        } finally {
            processingAction = false;
        }
    }

    private void handleCancelNext() {
        if (processingAction) {
            return;
        }
        processingAction = true;
        try {
            if (service.getOrderQueue().isEmpty()) {
                mainFrame.setFeedback(false, "No orders waiting to cancel.");
                return;
            }

            Order front = service.getOrderQueue().peek();
            Product p = service.findProduct(front.getSkuId());
            String productName = p != null ? p.getName() : "Item #" + front.getSkuId();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel the next order for " + front.getCustomerName() + " (" + productName + ")?",
                    "Confirm Cancel Order", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                OpResult result = service.rejectFrontOrder();
                if (result.isSuccess()) {
                    mainFrame.setFeedback(true, "Order #" + front.getOrderId() + " for " + front.getCustomerName() + " was cancelled.");
                    mainFrame.refreshAll();
                } else {
                    mainFrame.setFeedback(false, result.getMessage());
                }
            }
        } finally {
            processingAction = false;
        }
    }

    public void refreshTables() {
        queueTableModel.fireTableDataChanged();
        updateButtonStatesAndSummary();
    }
}
