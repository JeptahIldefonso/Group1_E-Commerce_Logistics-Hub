package com.group1.logistics.ui;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tab 4: Shipments (Shipment Dispatch & Logistics).
 * Modern SaaS ERP layout:
 * Page header, action toolbar with quick sort buttons and direction toggle,
 * and clean borderless shipped packages table with column header click-to-sort.
 * Pure standard Java Swing; zero Java Collections.
 */
public class FreightPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final FreightTableModel freightTableModel;

    private boolean ascending = true;
    private int currentMode = 1; // 1 = Weight, 2 = Delivery Speed, 3 = Package No, 4 = Destination
    private boolean technicalDetails = false;

    private JLabel lblActiveSort;
    private JButton btnSortWeight;
    private JButton btnSortSpeed;
    private JButton btnToggleOrder;
    private JTable tblFreight;
    private JLabel lblShipmentCount;

    public FreightPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.freightTableModel = new FreightTableModel(service.getPackageArray());

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

        updateActiveSortLabel();
    }

    public void setTechnicalDetails(boolean enabled) {
        this.technicalDetails = enabled;
        freightTableModel.setTechnicalDetails(enabled);
        updateActiveSortLabel();
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftBox = new JPanel();
        leftBox.setOpaque(false);
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Shipment Dispatch & Logistics");
        lblTitle.setFont(UiTheme.FONT_PAGE_TITLE);
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Custom QuickSort (by weight) and InsertionSort (by delivery speed) ranking engine.");
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

        // Left: Active sort banner pill
        lblActiveSort = new JLabel();
        lblActiveSort.setFont(UiTheme.FONT_SUBTITLE);
        lblActiveSort.setOpaque(true);
        lblActiveSort.setBackground(UiTheme.SURFACE_ALT);
        lblActiveSort.setForeground(UiTheme.TEXT_PRIMARY);
        lblActiveSort.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_CARD, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        toolbar.add(lblActiveSort, BorderLayout.CENTER);

        // Right: Sort buttons toolbar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        btnSortWeight = UiTheme.createToggleSortButton("Sort by Weight",
                new UiTheme.VectorIcon(UiTheme.IconType.WEIGHT, 14),
                true,
                "Order packages by parcel weight using QuickSort");
        btnSortSpeed = UiTheme.createToggleSortButton("Sort by Delivery Speed",
                new UiTheme.VectorIcon(UiTheme.IconType.SPEED, 14),
                false,
                "Order packages by delivery urgency using InsertionSort");
        btnToggleOrder = UiTheme.createSecondaryButton("Lightest First", "Toggle sort direction");

        btnBar.add(btnSortWeight);
        btnBar.add(btnSortSpeed);
        btnBar.add(btnToggleOrder);

        toolbar.add(btnBar, BorderLayout.EAST);

        btnSortWeight.addActionListener(e -> {
            currentMode = 1;
            OpResult result = service.sortPackages(1, 1, ascending);
            updateToggleText();
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(result);
        });

        btnSortSpeed.addActionListener(e -> {
            currentMode = 2;
            OpResult result = service.sortPackages(2, 2, ascending);
            updateToggleText();
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(result);
        });

        btnToggleOrder.addActionListener(e -> {
            ascending = !ascending;
            if (currentMode == 1) {
                service.sortPackages(1, 1, ascending);
            } else if (currentMode == 2) {
                service.sortPackages(2, 2, ascending);
            } else if (currentMode == 3) {
                service.sortPackages(1, 3, ascending);
            } else if (currentMode == 4) {
                service.sortPackages(2, 4, ascending);
            }
            updateToggleText();
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(true, "Sort order updated.");
        });

        return toolbar;
    }

    private void updateToggleText() {
        UiTheme.setToggleSortActive(btnSortWeight, currentMode == 1);
        UiTheme.setToggleSortActive(btnSortSpeed, currentMode == 2);

        if (currentMode == 1) {
            btnToggleOrder.setText(ascending ? "Lightest First" : "Heaviest First");
            btnToggleOrder.setToolTipText("Toggle weight sort direction");
        } else if (currentMode == 2) {
            btnToggleOrder.setText(ascending ? "Fastest First" : "Slowest First");
            btnToggleOrder.setToolTipText("Toggle delivery speed sort direction");
        } else {
            btnToggleOrder.setText(ascending ? "Ascending" : "Descending");
        }

        FontMetrics fm = btnToggleOrder.getFontMetrics(UiTheme.FONT_BOLD);
        int w = fm.stringWidth(btnToggleOrder.getText()) + 32;
        btnToggleOrder.setPreferredSize(new Dimension(w, 36));
        btnToggleOrder.revalidate();
    }

    private JScrollPane createTable() {
        tblFreight = new JTable(freightTableModel);
        UiTheme.configureTable(tblFreight);
        tblFreight.setDefaultRenderer(Object.class, UiTheme.createFreightCellRenderer());
        tblFreight.setFillsViewportHeight(true);

        TableColumnModel colModel = tblFreight.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100); // Package No.
        colModel.getColumn(1).setPreferredWidth(85);  // Order No.
        colModel.getColumn(2).setPreferredWidth(180); // Deliver To
        colModel.getColumn(3).setPreferredWidth(110); // Weight
        colModel.getColumn(4).setPreferredWidth(140); // Delivery Speed
        colModel.getColumn(5).setPreferredWidth(95);  // Shipped #

        tblFreight.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tblFreight.columnAtPoint(e.getPoint());
                handleHeaderClick(col);
            }
        });

        JScrollPane sp = UiTheme.createModernScrollPane(tblFreight);
        sp.setBorder(new LineBorder(UiTheme.BORDER_CARD, 1));
        return sp;
    }

    private JPanel createTableFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 4, 2, 4));

        lblShipmentCount = new JLabel("Click column headers to sort dynamically");
        lblShipmentCount.setFont(UiTheme.FONT_HINT);
        lblShipmentCount.setForeground(UiTheme.TEXT_MUTED);

        footer.add(lblShipmentCount, BorderLayout.WEST);
        return footer;
    }

    private void handleHeaderClick(int col) {
        if (col == 3) {
            currentMode = 1;
            service.sortPackages(1, 1, ascending);
        } else if (col == 4) {
            currentMode = 2;
            service.sortPackages(2, 2, ascending);
        } else if (col == 0) {
            currentMode = 3;
            service.sortPackages(1, 3, ascending);
        } else if (col == 2) {
            currentMode = 4;
            service.sortPackages(2, 4, ascending);
        }
        updateToggleText();
        updateActiveSortLabel();
        freightTableModel.fireTableDataChanged();
        mainFrame.setFeedback(true, "Sorted by " + tblFreight.getColumnName(col) + ".");
    }

    private void updateActiveSortLabel() {
        String description;
        String algorithmDetails = "";

        if (currentMode == 1) {
            description = ascending ? "Lightest to Heaviest" : "Heaviest to Lightest";
            if (technicalDetails) {
                algorithmDetails = " · QuickSort (Hoare)";
            }
        } else if (currentMode == 2) {
            description = ascending ? "Fastest First" : "Slowest First";
            if (technicalDetails) {
                algorithmDetails = " · InsertionSort (Stable)";
            }
        } else if (currentMode == 3) {
            description = ascending ? "Package No. (Ascending)" : "Package No. (Descending)";
            if (technicalDetails) {
                algorithmDetails = " · QuickSort";
            }
        } else {
            description = ascending ? "Destination (A to Z)" : "Destination (Z to A)";
            if (technicalDetails) {
                algorithmDetails = " · InsertionSort";
            }
        }

        if (lblActiveSort != null) {
            lblActiveSort.setText(" Showing: " + description + algorithmDetails);
        }
        if (lblShipmentCount != null) {
            lblShipmentCount.setText("Total Dispatched: " + service.getPackageArray().size() + " packages | Click column headers to sort");
        }
    }

    public void refreshTables() {
        freightTableModel.fireTableDataChanged();
        updateActiveSortLabel();
    }
}
