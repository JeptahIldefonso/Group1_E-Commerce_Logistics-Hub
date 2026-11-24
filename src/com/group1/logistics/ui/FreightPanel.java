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
 * Tab 3: Freight Logistics Hub (Module 3 — Custom Sorting Engine).
 * Demonstrates QuickSort (by Weight) and InsertionSort (by Priority).
 * Includes clickable column headers backed by custom sorters (zero TableRowSorter).
 * Pure standard Java Swing; zero Java Collections.
 */
public class FreightPanel extends JPanel {
    private final FulfillmentService service;
    private final MainFrame mainFrame;
    private final FreightTableModel freightTableModel;

    private boolean ascending = true;
    private String currentSortField = "Weight (kg)";
    private String currentAlgorithm = "QuickSort (Hoare Partition)";
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

        // Top 1-Line Plain-English Workflow Banner
        add(UiTheme.createWorkflowBanner(
                "Custom parcel sorting engine comparing QuickSort (Hoare partition) and InsertionSort (Stable).",
                "Sorting Engine (Custom Sorters)"
        ), BorderLayout.NORTH);

        // Center Container: Top Sorting Toolbar + Center Table
        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setBackground(UiTheme.BG_WINDOW);

        centerContainer.add(createSortingControlsSubPanel(), BorderLayout.NORTH);
        centerContainer.add(createTableSubPanel(), BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createSortingControlsSubPanel() {
        JPanel card = UiTheme.createCardPanel("Freight Cargo Sorting Controls (Strict Zero-TableRowSorter Engine)");
        card.setLayout(new BorderLayout(0, 8));

        // Button Toolbar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        btnBar.setBackground(UiTheme.SURFACE);

        JButton btnSortWeight = UiTheme.createStyledButton(
                "Sort by Weight (QuickSort - Hoare)",
                UiTheme.PRIMARY_BLUE,
                Color.WHITE,
                "Hoare partitioning with median-of-three pivot selection. O(n log n) average time complexity."
        );

        JButton btnSortPriority = UiTheme.createStyledButton(
                "Sort by Priority (InsertionSort - Stable)",
                UiTheme.PRIMARY_BLUE,
                Color.WHITE,
                "Stable in-place sorting that strictly preserves arrival sequence for equal priority parcels."
        );

        btnToggleOrder = UiTheme.createStyledButton(
                "Order: Ascending ↑",
                new Color(0x37, 0x41, 0x51),
                Color.WHITE,
                "Toggle between Ascending (↑) and Descending (↓) sort order"
        );

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
            currentAlgorithm = "QuickSort (Hoare Partition)";
            OpResult result = service.sortPackages(1, 1, ascending);
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(result);
        });

        btnSortPriority.addActionListener(e -> {
            currentSortField = "Delivery Priority";
            currentAlgorithm = "InsertionSort (Stable)";
            OpResult result = service.sortPackages(2, 2, ascending);
            updateActiveSortLabel();
            freightTableModel.fireTableDataChanged();
            mainFrame.setFeedback(result);
        });

        btnToggleOrder.addActionListener(e -> {
            ascending = !ascending;
            btnToggleOrder.setText("Order: " + (ascending ? "Ascending ↑" : "Descending ↓"));
            // Re-apply active sort
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
            mainFrame.setFeedback(true, "Sort order flipped to " + (ascending ? "Ascending" : "Descending") + ".");
        });

        return card;
    }

    private JPanel createTableSubPanel() {
        JPanel card = UiTheme.createCardPanel("Dispatched Cargo Packages (Stored in Primitive FreightPackageArray)");
        card.setLayout(new BorderLayout(0, 6));

        tblFreight = new JTable(freightTableModel);
        UiTheme.configureTable(tblFreight);
        tblFreight.setDefaultRenderer(Object.class, UiTheme.createFreightCellRenderer());
        tblFreight.setFillsViewportHeight(true);

        // Strict Compliance Safeguard: Disable Swing's built-in TableRowSorter (Trap 5)
        tblFreight.setAutoCreateRowSorter(false);
        tblFreight.getTableHeader().setReorderingAllowed(false);

        // Set explicit preferred column widths
        TableColumnModel colModel = tblFreight.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100); // Package ID
        colModel.getColumn(1).setPreferredWidth(80);  // Order ID
        colModel.getColumn(2).setPreferredWidth(160); // Destination Hub
        colModel.getColumn(3).setPreferredWidth(100); // Weight (kg)
        colModel.getColumn(4).setPreferredWidth(120); // Delivery Priority
        colModel.getColumn(5).setPreferredWidth(95);  // Dispatch Seq

        // Attach MouseAdapter to table header so clicking columns runs our custom sorters!
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
        // Map columns to custom sorters:
        // Col 0: Package ID -> QuickSort (Criteria 3)
        // Col 2: Destination -> InsertionSort (Criteria 4)
        // Col 3: Weight -> QuickSort (Criteria 1)
        // Col 4: Priority -> InsertionSort (Criteria 2)
        if (col == 3) {
            currentSortField = "Weight (kg)";
            currentAlgorithm = "QuickSort (Hoare Partition)";
            OpResult result = service.sortPackages(1, 1, ascending);
            mainFrame.setFeedback(result);
        } else if (col == 4) {
            currentSortField = "Delivery Priority";
            currentAlgorithm = "InsertionSort (Stable)";
            OpResult result = service.sortPackages(2, 2, ascending);
            mainFrame.setFeedback(result);
        } else if (col == 0) {
            currentSortField = "Package ID";
            currentAlgorithm = "QuickSort (Criteria 3)";
            OpResult result = service.sortPackages(1, 3, ascending);
            mainFrame.setFeedback(result);
        } else if (col == 2) {
            currentSortField = "Destination Hub";
            currentAlgorithm = "InsertionSort (Criteria 4)";
            OpResult result = service.sortPackages(2, 4, ascending);
            mainFrame.setFeedback(result);
        }
        updateActiveSortLabel();
        freightTableModel.fireTableDataChanged();
    }

    private void updateActiveSortLabel() {
        String arrow = ascending ? "↑" : "↓";
        String direction = ascending ? "Ascending" : "Descending";
        lblActiveSort.setText(String.format(
                "<html><b>Active Sort:</b> %s &nbsp;&nbsp;|&nbsp;&nbsp; <b>Algorithm:</b> <font color='#1A73E8'>%s</font> &nbsp;&nbsp;|&nbsp;&nbsp; <b>Direction:</b> %s %s "
                + "<span style='color: #6B7280; font-weight: normal; font-size: 11px;'>(Click column headers to sort dynamically)</span></html>",
                currentSortField, currentAlgorithm, direction, arrow
        ));
    }

    public void refreshTables() {
        freightTableModel.fireTableDataChanged();
    }
}
