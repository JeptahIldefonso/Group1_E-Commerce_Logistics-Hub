package com.group1.logistics.ui;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.service.OpResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tab 3: Freight Logistics Hub (Module 3 — Custom Sorting Engine).
 * Demonstrates QuickSort (by Weight) and InsertionSort (by Priority).
 * Includes clickable column headers backed by custom sorters (zero TableRowSorter).
 */
public class FreightPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final FreightTableModel freightTableModel;

    private boolean ascending = true;
    private JLabel lblActiveSort;

    public FreightPanel(FulfillmentService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        this.freightTableModel = new FreightTableModel(service.getPackageArray());

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Freight Cargo Sorting Controls (Zero Built-in Sorters)"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnSortWeight = new JButton("Sort by Weight (QuickSort - Hoare)");
        JButton btnSortPriority = new JButton("Sort by Priority (InsertionSort - Stable)");
        JButton btnToggleOrder = new JButton("Toggle: Ascending");

        btnSortWeight.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSortPriority.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnPanel.add(btnSortWeight);
        btnPanel.add(btnSortPriority);
        btnPanel.add(btnToggleOrder);

        topPanel.add(btnPanel, BorderLayout.NORTH);

        lblActiveSort = new JLabel("Active Sort: Natural Dispatch Order (Click a button or table header to re-sort)");
        lblActiveSort.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblActiveSort.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        topPanel.add(lblActiveSort, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Center Table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Dispatched Cargo Packages (Stored in Primitive FreightPackageArray)"));

        JTable tblFreight = new JTable(freightTableModel);
        tblFreight.setFillsViewportHeight(true);

        // Strict Compliance Safeguard: Disable Swing's built-in TableRowSorter (Trap 5)
        tblFreight.setAutoCreateRowSorter(false);
        tblFreight.getTableHeader().setReorderingAllowed(false);

        // Attach MouseAdapter to table header so clicking columns runs our custom sorters!
        tblFreight.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tblFreight.columnAtPoint(e.getPoint());
                handleHeaderClick(col);
            }
        });

        tablePanel.add(new JScrollPane(tblFreight), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Event listeners
        btnSortWeight.addActionListener(e -> {
            OpResult result = service.sortPackages(1, 1, ascending);
            lblActiveSort.setText("Active Sort: Weight (kg) | Algorithm: QuickSort (Hoare) | " + (ascending ? "Ascending" : "Descending"));
            freightTableModel.fireTableDataChanged();
        });

        btnSortPriority.addActionListener(e -> {
            OpResult result = service.sortPackages(2, 2, ascending);
            lblActiveSort.setText("Active Sort: Delivery Priority | Algorithm: InsertionSort (Stable) | " + (ascending ? "Ascending" : "Descending"));
            freightTableModel.fireTableDataChanged();
        });

        btnToggleOrder.addActionListener(e -> {
            ascending = !ascending;
            btnToggleOrder.setText("Toggle: " + (ascending ? "Ascending" : "Descending"));
        });
    }

    private void handleHeaderClick(int col) {
        // Map columns to custom sorters:
        // Col 0: Package ID -> QuickSort (Criteria 3)
        // Col 2: Destination -> InsertionSort (Criteria 4)
        // Col 3: Weight -> QuickSort (Criteria 1)
        // Col 4: Priority -> InsertionSort (Criteria 2)
        if (col == 3) {
            service.sortPackages(1, 1, ascending);
            lblActiveSort.setText("Active Sort: Weight (kg) via Header Click | QuickSort | " + (ascending ? "Ascending" : "Descending"));
        } else if (col == 4) {
            service.sortPackages(2, 2, ascending);
            lblActiveSort.setText("Active Sort: Priority via Header Click | InsertionSort | " + (ascending ? "Ascending" : "Descending"));
        } else if (col == 0) {
            service.sortPackages(1, 3, ascending);
            lblActiveSort.setText("Active Sort: Package ID via Header Click | QuickSort | " + (ascending ? "Ascending" : "Descending"));
        } else if (col == 2) {
            service.sortPackages(2, 4, ascending);
            lblActiveSort.setText("Active Sort: Destination Hub via Header Click | InsertionSort | " + (ascending ? "Ascending" : "Descending"));
        }
        freightTableModel.fireTableDataChanged();
    }

    public void refreshTables() {
        freightTableModel.fireTableDataChanged();
    }
}
