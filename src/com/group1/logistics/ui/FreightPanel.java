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
 * Tab 3: Freight Logistics Hub.
 * Clean, modern freight sorting panel.
 * Pure standard Java Swing; zero Java Collections.
 */
public class FreightPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final FreightTableModel freightTableModel;

    private boolean ascending = true;
    private String currentSortField = "Weight (kg)";
    private String currentAlgorithm = "QuickSort";
    private JLabel lblActiveSort;
    private JButton btnToggleOrder;
    private JTable tblFreight;

    public FreightPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.freightTableModel = new FreightTableModel(service.getPackageArray());

        setLayout(new BorderLayout(0, 10));
        setBackground(UiTheme.BG_WINDOW);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        // Sorting Controls Panel
        add(createSortingControlsSubPanel(), BorderLayout.NORTH);

        // Dispatched Packages Table Panel
        add(createTableSubPanel(), BorderLayout.CENTER);
    }

    private JPanel createSortingControlsSubPanel() {
        JPanel card = UiTheme.createCardPanel("Cargo Sorting");
        card.setLayout(new BorderLayout(0, 8));

        // Button Toolbar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        btnBar.setBackground(UiTheme.SURFACE);

        JButton btnSortWeight = UiTheme.createPrimaryButton("Sort by Weight", "Sorts cargo by weight (QuickSort)");
        JButton btnSortPriority = UiTheme.createPrimaryButton("Sort by Priority", "Sorts cargo by delivery priority (InsertionSort)");
        btnToggleOrder = UiTheme.createSecondaryButton("Order: Ascending", "Toggles sort direction");

        btnBar.add(btnSortWeight);
        btnBar.add(btnSortPriority);
        btnBar.add(btnToggleOrder);

        card.add(btnBar, BorderLayout.NORTH);

        // Active Sort Status Strip
        JPanel statusStrip = new JPanel(new BorderLayout(8, 0));
        statusStrip.setBackground(UiTheme.SURFACE_ALT);
        statusStrip.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        lblActiveSort = new JLabel();
        lblActiveSort.setFont(UiTheme.FONT_SUBTITLE);
        lblActiveSort.setForeground(UiTheme.TEXT_PRIMARY);
        updateActiveSortLabel();

        statusStrip.add(lblActiveSort, BorderLayout.CENTER);
        card.add(statusStrip, BorderLayout.SOUTH);

        // Listeners
        btnSortWeight.addActionListener(e -> {
            currentSortField = "Weight (kg)";
            currentAlgorithm = "QuickSort";
            OpResult result = service.sortPackages(1, 1, ascending);
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(result);
        });

        btnSortPriority.addActionListener(e -> {
            currentSortField = "Delivery Priority";
            currentAlgorithm = "InsertionSort";
            OpResult result = service.sortPackages(2, 2, ascending);
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(result);
        });

        btnToggleOrder.addActionListener(e -> {
            ascending = !ascending;
            btnToggleOrder.setText("Order: " + (ascending ? "Ascending" : "Descending"));
            if (currentSortField.contains("Weight")) {
                service.sortPackages(1, 1, ascending);
            } else if (currentSortField.contains("Priority")) {
                service.sortPackages(2, 2, ascending);
            } else if (currentSortField.contains("Package ID")) {
                service.sortPackages(1, 3, ascending);
            } else if (currentSortField.contains("Destination")) {
                service.sortPackages(2, 4, ascending);
            }
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(true, "Sorted in " + (ascending ? "Ascending" : "Descending") + " order.");
        });

        return card;
    }

    private JPanel createTableSubPanel() {
        JPanel card = UiTheme.createCardPanel("Dispatched Freight Packages");
        card.setLayout(new BorderLayout(0, 6));

        tblFreight = new JTable(freightTableModel);
        UiTheme.configureTable(tblFreight);
        tblFreight.setDefaultRenderer(Object.class, UiTheme.createFreightCellRenderer());
        tblFreight.setFillsViewportHeight(true);

        tblFreight.setAutoCreateRowSorter(false);
        tblFreight.getTableHeader().setReorderingAllowed(false);

        TableColumnModel colModel = tblFreight.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100); // Package ID
        colModel.getColumn(1).setPreferredWidth(80);  // Order ID
        colModel.getColumn(2).setPreferredWidth(160); // Destination Hub
        colModel.getColumn(3).setPreferredWidth(100); // Weight (kg)
        colModel.getColumn(4).setPreferredWidth(120); // Delivery Priority
        colModel.getColumn(5).setPreferredWidth(95);  // Dispatch Seq

        // Clicking column header sorts dynamically
        tblFreight.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tblFreight.columnAtPoint(e.getPoint());
                handleHeaderClick(col);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblFreight);
        scrollPane.setBorder(new LineBorder(UiTheme.BORDER, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void handleHeaderClick(int col) {
        if (col == 3) {
            currentSortField = "Weight (kg)";
            currentAlgorithm = "QuickSort";
            OpResult result = service.sortPackages(1, 1, ascending);
            mainFrame.setFeedback(result);
        } else if (col == 4) {
            currentSortField = "Delivery Priority";
            currentAlgorithm = "InsertionSort";
            OpResult result = service.sortPackages(2, 2, ascending);
            mainFrame.setFeedback(result);
        } else if (col == 0) {
            currentSortField = "Package ID";
            currentAlgorithm = "QuickSort";
            OpResult result = service.sortPackages(1, 3, ascending);
            mainFrame.setFeedback(result);
        } else if (col == 2) {
            currentSortField = "Destination Hub";
            currentAlgorithm = "InsertionSort";
            OpResult result = service.sortPackages(2, 4, ascending);
            mainFrame.setFeedback(result);
        }
        updateActiveSortLabel();
        freightTableModel.fireTableDataChanged();
    }

    private void updateActiveSortLabel() {
        String direction = ascending ? "Ascending" : "Descending";
        lblActiveSort.setText(String.format(
                "Active Sort: %s  |  Algorithm: %s  |  Direction: %s  (Tip: Click table column headers to sort dynamically)",
                currentSortField, currentAlgorithm, direction
        ));
    }

    public void refreshTables() {
        freightTableModel.fireTableDataChanged();
    }
}
