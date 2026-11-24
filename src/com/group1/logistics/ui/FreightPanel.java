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
 * Tab 4: Shipments.
 * Sort shipments by weight or speed with plain-English direction toggles.
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
    private JButton btnToggleOrder;
    private JTable tblFreight;

    public FreightPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.freightTableModel = new FreightTableModel(service.getPackageArray());

        setLayout(new BorderLayout(0, 10));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        // Sorting Controls Panel
        add(createSortingControlsCard(), BorderLayout.NORTH);

        // Shipped Packages Table Panel
        add(createTableCard(), BorderLayout.CENTER);
    }

    public void setTechnicalDetails(boolean enabled) {
        this.technicalDetails = enabled;
        freightTableModel.setTechnicalDetails(enabled);
        updateActiveSortLabel();
    }

    private JPanel createSortingControlsCard() {
        JPanel card = UiTheme.createCardPanel("Sort Shipments");
        card.setLayout(new BorderLayout(0, 8));

        // Button Toolbar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        btnBar.setBackground(UiTheme.SURFACE);

        JButton btnSortWeight = UiTheme.createPrimaryButton("Sort by Weight",
                new UiTheme.VectorIcon(UiTheme.IconType.WEIGHT, 14),
                "Order packages by parcel weight");
        JButton btnSortSpeed = UiTheme.createPrimaryButton("Sort by Delivery Speed",
                new UiTheme.VectorIcon(UiTheme.IconType.SPEED, 14),
                "Order packages by delivery urgency");
        btnToggleOrder = UiTheme.createSecondaryButton("Lightest First", "Toggle between lightest first and heaviest first");

        btnBar.add(btnSortWeight);
        btnBar.add(btnSortSpeed);
        btnBar.add(btnToggleOrder);

        card.add(btnBar, BorderLayout.NORTH);

        // Active Sort Status Strip
        JPanel statusStrip = new JPanel(new BorderLayout(8, 0));
        statusStrip.setBackground(UiTheme.SURFACE_ALT);
        statusStrip.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER, 1, true),
                new EmptyBorder(7, 12, 7, 12)
        ));

        lblActiveSort = new JLabel();
        lblActiveSort.setFont(UiTheme.FONT_SUBTITLE);
        lblActiveSort.setForeground(UiTheme.TEXT_PRIMARY);
        updateActiveSortLabel();

        statusStrip.add(lblActiveSort, BorderLayout.CENTER);
        card.add(statusStrip, BorderLayout.SOUTH);

        // Listeners
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

        return card;
    }

    private void updateToggleText() {
        if (currentMode == 1) {
            btnToggleOrder.setText(ascending ? "Lightest First" : "Heaviest First");
            btnToggleOrder.setToolTipText("Toggle weight sort direction");
        } else if (currentMode == 2) {
            btnToggleOrder.setText(ascending ? "Fastest First" : "Slowest First");
            btnToggleOrder.setToolTipText("Toggle delivery speed sort direction");
        } else {
            btnToggleOrder.setText(ascending ? "Ascending" : "Descending");
        }
    }

    private JPanel createTableCard() {
        JPanel card = UiTheme.createCardPanel("Shipped Packages");
        card.setLayout(new BorderLayout(0, 6));

        tblFreight = new JTable(freightTableModel);
        UiTheme.configureTable(tblFreight);
        tblFreight.setDefaultRenderer(Object.class, UiTheme.createFreightCellRenderer());
        tblFreight.setFillsViewportHeight(true);

        tblFreight.setAutoCreateRowSorter(false);
        tblFreight.getTableHeader().setReorderingAllowed(false);

        TableColumnModel colModel = tblFreight.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100); // Package No.
        colModel.getColumn(1).setPreferredWidth(85);  // Order No.
        colModel.getColumn(2).setPreferredWidth(180); // Deliver To
        colModel.getColumn(3).setPreferredWidth(110); // Weight
        colModel.getColumn(4).setPreferredWidth(140); // Delivery Speed
        colModel.getColumn(5).setPreferredWidth(95);  // Shipped #

        // Header click listener
        tblFreight.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tblFreight.columnAtPoint(e.getPoint());
                handleHeaderClick(col);
            }
        });

        JScrollPane scrollPane = UiTheme.createModernScrollPane(tblFreight);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
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
                algorithmDetails = " · Algorithm: QuickSort (Hoare Partition)";
            }
        } else if (currentMode == 2) {
            description = ascending ? "Fastest First (Express to Economy)" : "Slowest First (Economy to Express)";
            if (technicalDetails) {
                algorithmDetails = " · Algorithm: InsertionSort (Stable)";
            }
        } else if (currentMode == 3) {
            description = ascending ? "Package No. (Ascending)" : "Package No. (Descending)";
            if (technicalDetails) {
                algorithmDetails = " · Algorithm: QuickSort";
            }
        } else {
            description = ascending ? "Destination (A to Z)" : "Destination (Z to A)";
            if (technicalDetails) {
                algorithmDetails = " · Algorithm: InsertionSort";
            }
        }

        lblActiveSort.setText("Showing: " + description + algorithmDetails);
    }

    public void refreshTables() {
        freightTableModel.fireTableDataChanged();
    }
}
