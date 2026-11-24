package com.group1.logistics.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Pure standard Java Swing styling and design tokens helper.
 * Zero external libraries (no FlatLaf, no MigLayout).
 * Zero Java Collections imports.
 * Complies strictly with course constraints.
 */
public class UiTheme {

    // Color Palette
    public static final Color BG_WINDOW = new Color(0xF5, 0xF7, 0xFA);
    public static final Color SURFACE = new Color(0xFF, 0xFF, 0xFF);
    public static final Color SURFACE_ALT = new Color(0xF8, 0xFA, 0xFC);
    public static final Color BORDER = new Color(0xD0, 0xD7, 0xDE);
    public static final Color BORDER_CARD = new Color(0xE1, 0xE4, 0xE8);

    public static final Color TEXT_PRIMARY = new Color(0x1F, 0x29, 0x37);
    public static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);

    // Button / Role Colors
    public static final Color PRIMARY_BLUE = new Color(0x1A, 0x73, 0xE8);
    public static final Color POSITIVE_GREEN = new Color(0x1E, 0x8E, 0x3E);
    public static final Color WARNING_AMBER = new Color(0xE3, 0x74, 0x00);
    public static final Color DANGER_RED = new Color(0xD9, 0x30, 0x25);

    // Row Highlights for FIFO / LIFO
    public static final Color HIGHLIGHT_QUEUE_ROW0 = new Color(0xE8, 0xF0, 0xFE); // Soft Blue
    public static final Color HIGHLIGHT_QUEUE_ROW0_BORDER = new Color(0x90, 0xCA, 0xF9);
    public static final Color HIGHLIGHT_STACK_ROW0 = new Color(0xFE, 0xF7, 0xE0); // Soft Amber
    public static final Color HIGHLIGHT_STACK_ROW0_BORDER = new Color(0xFF, 0xE0, 0x82);

    // Stock Status Pill Colors
    public static final Color STOCK_IN_TEXT = new Color(0x13, 0x73, 0x33);
    public static final Color STOCK_LOW_TEXT = new Color(0xB0, 0x60, 0x00);
    public static final Color STOCK_OUT_TEXT = new Color(0xC5, 0x22, 0x1F);

    // Inline Feedback Bar Colors
    public static final Color FEEDBACK_SUCCESS_BG = new Color(0xE6, 0xF4, 0xEA);
    public static final Color FEEDBACK_SUCCESS_TEXT = new Color(0x13, 0x73, 0x33);
    public static final Color FEEDBACK_ERROR_BG = new Color(0xFC, 0xE8, 0xE6);
    public static final Color FEEDBACK_ERROR_TEXT = new Color(0xC5, 0x22, 0x1F);
    public static final Color FEEDBACK_INFO_BG = new Color(0xEA, 0xEE, 0xF4);
    public static final Color FEEDBACK_INFO_TEXT = new Color(0x1F, 0x29, 0x37);

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_HINT = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Creates a styled, accessible button with color affordance and tooltips.
     */
    public static JButton createStyledButton(String text, Color bg, Color fg, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setToolTipText(tooltip);
        }
        return btn;
    }

    /**
     * Creates a card-style panel with a titled border and clean padding.
     */
    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(SURFACE);
        Border line = new LineBorder(BORDER, 1, true);
        Border titleBorder = BorderFactory.createTitledBorder(line, "  " + title + "  ", 0, 0, FONT_SUBTITLE, TEXT_PRIMARY);
        panel.setBorder(new CompoundBorder(titleBorder, new EmptyBorder(8, 10, 8, 10)));
        return panel;
    }

    /**
     * Creates a top 1-line plain-English workflow hint banner explaining the tab's purpose and DSA concept.
     */
    public static JPanel createWorkflowBanner(String description, String dsaBadge) {
        JPanel banner = new JPanel(new BorderLayout(10, 5));
        banner.setBackground(new Color(0xEA, 0xEE, 0xF4));
        banner.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, false),
                new EmptyBorder(7, 14, 7, 14)
        ));

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(FONT_REGULAR);
        lblDesc.setForeground(TEXT_PRIMARY);

        JLabel lblBadge = new JLabel("  " + dsaBadge + "  ");
        lblBadge.setFont(FONT_BADGE);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(PRIMARY_BLUE);
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        banner.add(lblDesc, BorderLayout.CENTER);
        banner.add(lblBadge, BorderLayout.EAST);
        return banner;
    }

    /**
     * Configures a JTable with standard row height, custom styled header, clean borders,
     * and disables built-in auto sorters (Trap 5).
     */
    public static void configureTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(FONT_REGULAR);
        table.setSelectionBackground(new Color(0xD2, 0xE3, 0xFC));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(0xEE, 0xF0, 0xF2));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        // Strict Compliance Safeguard: Disable TableRowSorter (Trap 5)
        table.setAutoCreateRowSorter(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(0xEB, 0xF1, 0xF6));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        header.setReorderingAllowed(false);
    }

    /**
     * Custom TableCellRenderer for the Order Queue table.
     * Highlights Row 0 (front of FIFO queue) in soft blue (#E8F0FE).
     * Alternates other rows (#FFFFFF / #F8FAFC).
     * Right-aligns numeric columns.
     */
    public static DefaultTableCellRenderer createQueueCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(2, 6, 2, 6));

                // Alignment: # (0), SKU (3), Qty (4) right-aligned or centered
                if (col == 0 || col == 3 || col == 4) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    if (row == 0) {
                        // Front of queue highlight
                        c.setBackground(HIGHLIGHT_QUEUE_ROW0);
                        c.setFont(FONT_BOLD);
                    } else if (row % 2 == 1) {
                        c.setBackground(SURFACE_ALT);
                        c.setFont(FONT_REGULAR);
                    } else {
                        c.setBackground(SURFACE);
                        c.setFont(FONT_REGULAR);
                    }
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        };
    }

    /**
     * Custom TableCellRenderer for the Restock Stack table.
     * Highlights Row 0 (top of LIFO stack / newest) in soft amber (#FEF7E0).
     * Alternates other rows (#FFFFFF / #F8FAFC).
     */
    public static DefaultTableCellRenderer createStackCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(2, 6, 2, 6));

                if (col == 1 || col == 2 || col == 3) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    if (row == 0) {
                        // Top of stack highlight
                        c.setBackground(HIGHLIGHT_STACK_ROW0);
                        c.setFont(FONT_BOLD);
                    } else if (row % 2 == 1) {
                        c.setBackground(SURFACE_ALT);
                        c.setFont(FONT_REGULAR);
                    } else {
                        c.setBackground(SURFACE);
                        c.setFont(FONT_REGULAR);
                    }
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        };
    }

    /**
     * Custom TableCellRenderer for the SKU Catalog table.
     * Color-codes Stock Status (Green = In Stock, Amber = Low Stock, Red = Out of Stock).
     * Right-aligns Price and Stock Quantity.
     */
    public static DefaultTableCellRenderer createCatalogCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(2, 6, 2, 6));

                if (col == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (col == 2 || col == 3) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (col == 4) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    if (row % 2 == 1) {
                        c.setBackground(SURFACE_ALT);
                    } else {
                        c.setBackground(SURFACE);
                    }

                    if (col == 4 && value != null) {
                        String status = value.toString();
                        if (status.contains("Out of Stock")) {
                            c.setForeground(STOCK_OUT_TEXT);
                            c.setFont(FONT_BOLD);
                        } else if (status.contains("Low Stock")) {
                            c.setForeground(STOCK_LOW_TEXT);
                            c.setFont(FONT_BOLD);
                        } else {
                            c.setForeground(STOCK_IN_TEXT);
                            c.setFont(FONT_BOLD);
                        }
                    } else {
                        c.setForeground(TEXT_PRIMARY);
                        c.setFont(FONT_REGULAR);
                    }
                }
                return c;
            }
        };
    }

    /**
     * Custom TableCellRenderer for the Freight Packages table.
     * Alternates rows and right-aligns numeric columns (Weight, Sequence).
     */
    public static DefaultTableCellRenderer createFreightCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(2, 6, 2, 6));

                if (col == 0 || col == 1 || col == 4 || col == 5) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (col == 3) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    if (row % 2 == 1) {
                        c.setBackground(SURFACE_ALT);
                    } else {
                        c.setBackground(SURFACE);
                    }
                    c.setForeground(TEXT_PRIMARY);
                    c.setFont(FONT_REGULAR);
                }
                return c;
            }
        };
    }
}
