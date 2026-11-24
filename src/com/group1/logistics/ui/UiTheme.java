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
 * Custom-painted buttons with high-contrast text and responsive hover/press states.
 */
public class UiTheme {

    // Color Palette
    public static final Color BG_WINDOW = new Color(0xF3, 0xF4, 0xF6); // Subtle neutral gray
    public static final Color SURFACE = new Color(0xFF, 0xFF, 0xFF);
    public static final Color SURFACE_ALT = new Color(0xF9, 0xFA, 0xFB);
    public static final Color BORDER = new Color(0xD1, 0xD5, 0xDB);
    public static final Color BORDER_CARD = new Color(0xE5, 0xE7, 0xEB);

    public static final Color TEXT_PRIMARY = new Color(0x11, 0x18, 0x27);
    public static final Color TEXT_MUTED = new Color(0x4B, 0x55, 0x63);

    // Button / Role Colors
    public static final Color PRIMARY_BLUE = new Color(0x1D, 0x4E, 0xD8); // Main action blue
    public static final Color POSITIVE_GREEN = new Color(0x15, 0x80, 0x3D); // Emerald green (Add)
    public static final Color WARNING_AMBER = new Color(0xD9, 0x77, 0x06); // Warm amber (Undo)
    public static final Color DANGER_RED = new Color(0xDC, 0x26, 0x26); // Strong red (Cancel/Delete)
    public static final Color NEUTRAL_DARK = new Color(0x37, 0x41, 0x51); // Slate gray

    // Row Highlights for FIFO / LIFO
    public static final Color HIGHLIGHT_QUEUE_ROW0 = new Color(0xEB, 0xF5, 0xFF); // Soft sky blue
    public static final Color HIGHLIGHT_QUEUE_ROW0_BORDER = new Color(0x93, 0xC5, 0xFD);
    public static final Color HIGHLIGHT_STACK_ROW0 = new Color(0xFE, 0xF3, 0xC7); // Soft warm amber
    public static final Color HIGHLIGHT_STACK_ROW0_BORDER = new Color(0xFC, 0xD3, 0x4D);

    // Stock Status Text Colors (Clean, universally supported)
    public static final Color STOCK_IN_TEXT = new Color(0x16, 0x65, 0x34);
    public static final Color STOCK_LOW_TEXT = new Color(0xD9, 0x77, 0x06);
    public static final Color STOCK_OUT_TEXT = new Color(0xDC, 0x26, 0x26);

    // Inline Feedback Bar Colors
    public static final Color FEEDBACK_SUCCESS_BG = new Color(0xDC, 0xFC, 0xE7);
    public static final Color FEEDBACK_SUCCESS_TEXT = new Color(0x14, 0x53, 0x2D);
    public static final Color FEEDBACK_ERROR_BG = new Color(0xFE, 0xE2, 0xE2);
    public static final Color FEEDBACK_ERROR_TEXT = new Color(0x7F, 0x1D, 0x1D);
    public static final Color FEEDBACK_READY_BG = new Color(0xF3, 0xF4, 0xF6);
    public static final Color FEEDBACK_READY_TEXT = new Color(0x37, 0x41, 0x51);

    // Typography (Slightly larger ~13-14pt for better readability)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_HINT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Creates a solid, accessible custom-painted button.
     */
    public static JButton createStyledButton(String text, Color bg, Color fg, String tooltip) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                if (!isEnabled()) {
                    g2.setColor(new Color(0xE5, 0xE7, 0xEB));
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                    g2.setColor(new Color(0xD1, 0xD5, 0xDB));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);
                } else if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                } else if (getModel().isRollover()) {
                    g2.setColor(brighten(bg, 0.15f));
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                } else {
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                }

                // Render centered text
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(getText());
                int textH = fm.getAscent();
                int x = (w - textW) / 2;
                int y = (h + textH) / 2 - 2;

                if (!isEnabled()) {
                    g2.setColor(new Color(0x9C, 0xA3, 0xAF));
                } else {
                    g2.setColor(fg);
                }
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        btn.setFont(FONT_BOLD);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 34));

        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setToolTipText(tooltip);
        }
        return btn;
    }

    /**
     * Creates a smaller, outlined danger button (white background with red border & text).
     * Perfect for secondary destructive actions like "Cancel Next Order" and "Delete Product".
     */
    public static JButton createOutlineDangerButton(String text, String tooltip) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                if (!isEnabled()) {
                    g2.setColor(SURFACE);
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                    g2.setColor(new Color(0xE5, 0xE7, 0xEB));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);
                } else if (getModel().isPressed()) {
                    g2.setColor(new Color(0xFE, 0xCD, 0xCD));
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                    g2.setColor(DANGER_RED);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0xFE, 0xE2, 0xE2));
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                    g2.setColor(DANGER_RED);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);
                } else {
                    g2.setColor(SURFACE);
                    g2.fillRoundRect(0, 0, w, h, 6, 6);
                    g2.setColor(new Color(0xFD, 0xBA, 0xBA));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);
                }

                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(getText());
                int textH = fm.getAscent();
                int x = (w - textW) / 2;
                int y = (h + textH) / 2 - 2;

                if (!isEnabled()) {
                    g2.setColor(new Color(0x9C, 0xA3, 0xAF));
                } else {
                    g2.setColor(DANGER_RED);
                }
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        btn.setFont(FONT_BOLD);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 32));

        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setToolTipText(tooltip);
        }
        return btn;
    }

    private static Color brighten(Color c, float factor) {
        int r = Math.min(255, (int) (c.getRed() + (255 - c.getRed()) * factor));
        int g = Math.min(255, (int) (c.getGreen() + (255 - c.getGreen()) * factor));
        int b = Math.min(255, (int) (c.getBlue() + (255 - c.getBlue()) * factor));
        return new Color(r, g, b);
    }

    public static JButton createPrimaryButton(String text, String tooltip) {
        return createStyledButton(text, PRIMARY_BLUE, Color.WHITE, tooltip);
    }

    public static JButton createSuccessButton(String text, String tooltip) {
        return createStyledButton(text, POSITIVE_GREEN, Color.WHITE, tooltip);
    }

    public static JButton createWarningButton(String text, String tooltip) {
        return createStyledButton(text, WARNING_AMBER, Color.WHITE, tooltip);
    }

    public static JButton createDangerButton(String text, String tooltip) {
        return createStyledButton(text, DANGER_RED, Color.WHITE, tooltip);
    }

    public static JButton createSecondaryButton(String text, String tooltip) {
        return createStyledButton(text, NEUTRAL_DARK, Color.WHITE, tooltip);
    }

    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(SURFACE);
        Border line = new LineBorder(BORDER, 1, true);
        Border titleBorder = BorderFactory.createTitledBorder(line, " " + title + " ", 0, 0, FONT_TITLE, TEXT_PRIMARY);
        panel.setBorder(new CompoundBorder(titleBorder, new EmptyBorder(10, 12, 10, 12)));
        return panel;
    }

    public static void configureTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_REGULAR);
        table.setSelectionBackground(new Color(0xDB, 0xEA, 0xFE));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(0xF3, 0xF4, 0xF6));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        // Strict Compliance Safeguard: Disable TableRowSorter (Trap 5)
        table.setAutoCreateRowSorter(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(0xF3, 0xF4, 0xF6));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setReorderingAllowed(false);
    }

    /**
     * Custom TableCellRenderer for the Orders table.
     * Marks Row 0 with soft sky blue and renders numeric/badge alignment.
     */
    public static DefaultTableCellRenderer createQueueCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(3, 8, 3, 8));

                if (col == 0 || col == 1 || col == 6) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (col == 4) { // Qty
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    if (row == 0) {
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
     * Highlights Row 0 in soft warm amber.
     */
    public static DefaultTableCellRenderer createStackCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(3, 8, 3, 8));

                if (col == 0 || col == 3) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (col == 2) { // Quantity Added
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    if (row == 0) {
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
     * Custom TableCellRenderer for the Product Catalog table.
     * Distinguishes: In Stock (> 20), Low Stock (<= 20), Out of Stock (== 0).
     */
    public static DefaultTableCellRenderer createCatalogCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(3, 8, 3, 8));

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
     * Custom TableCellRenderer for the Shipped Packages table.
     */
    public static DefaultTableCellRenderer createFreightCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(3, 8, 3, 8));

                if (col == 0 || col == 1 || col == 4 || col == 5) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (col == 3) { // Weight
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
