package com.group1.logistics.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Pure standard Java Swing styling and design tokens helper.
 * Includes resolution-independent procedural vector icons (SVG-like),
 * minimalist modern scrollbars, and focus rings.
 * Zero external libraries (no FlatLaf, no MigLayout).
 * Zero Java Collections imports.
 */
public class UiTheme {

    // Color Palette (Warm Alabaster Canvas + Crisp White Cards)
    public static final Color BG_WINDOW = new Color(0xF3, 0xF2, 0xEE); // Warm off-white linen canvas
    public static final Color SURFACE = new Color(0xFF, 0xFF, 0xFF);
    public static final Color SURFACE_ALT = new Color(0xF8, 0xF7, 0xF4); // Warm subtle tint
    public static final Color BORDER = new Color(0xD8, 0xD6, 0xCE);
    public static final Color BORDER_CARD = new Color(0xE6, 0xE4, 0xDD);
    public static final Color TABLE_GRID_LINE = new Color(0xF0, 0xEE, 0xE8);

    public static final Color TEXT_PRIMARY = new Color(0x0F, 0x17, 0x2A);
    public static final Color TEXT_MUTED = new Color(0x57, 0x53, 0x4E);

    // Button / Role Colors
    public static final Color PRIMARY_NAVY = new Color(0x0D, 0x23, 0x40); // Executive midnight navy CTA
    public static final Color PRIMARY_BLUE = new Color(0x1D, 0x4E, 0xD8); // Accent action blue
    public static final Color POSITIVE_GREEN = new Color(0x15, 0x80, 0x3D); // Emerald green (Add Stock)
    public static final Color WARNING_AMBER = new Color(0xB4, 0x53, 0x09); // Deep amber (Undo)
    public static final Color DANGER_RED = new Color(0xDC, 0x26, 0x26); // Strong red (Cancel/Delete)
    public static final Color NEUTRAL_DARK = new Color(0x1E, 0x29, 0x3B); // Slate ink

    // Row Highlights for FIFO / LIFO
    public static final Color HIGHLIGHT_QUEUE_ROW0 = new Color(0xEF, 0xF6, 0xFF); // Soft sky blue
    public static final Color HIGHLIGHT_QUEUE_ROW0_BORDER = new Color(0xBF, 0xDB, 0xFE);
    public static final Color HIGHLIGHT_STACK_ROW0 = new Color(0xFF, 0xFB, 0xEB); // Soft warm amber
    public static final Color HIGHLIGHT_STACK_ROW0_BORDER = new Color(0xFD, 0xE6, 0x8A);

    // Stock Status Text Colors
    public static final Color STOCK_IN_TEXT = new Color(0x16, 0x65, 0x34);
    public static final Color STOCK_LOW_TEXT = new Color(0xB4, 0x53, 0x09);
    public static final Color STOCK_OUT_TEXT = new Color(0xDC, 0x26, 0x26);

    // Sidebar Design Tokens (Rich Midnight Navy Blue)
    public static final Color SIDEBAR_BG = new Color(0x0B, 0x1B, 0x33); // Deep navy blue
    public static final Color SIDEBAR_HOVER = new Color(0x13, 0x2A, 0x4D); // Hover navy
    public static final Color SIDEBAR_ACTIVE = new Color(0x1A, 0x37, 0x63); // Active navy pill
    public static final Color SIDEBAR_TEXT = new Color(0xCB, 0xD5, 0xE1); // Soft slate-white
    public static final Color SIDEBAR_TEXT_ACTIVE = Color.WHITE;
    public static final Color SIDEBAR_ACCENT = new Color(0x38, 0xBD, 0xF8); // Sky-blue active indicator
    public static final Color SIDEBAR_CATEGORY = new Color(0x7C, 0x94, 0xB8); // Periwinkle-slate label
    public static final Color SIDEBAR_BORDER = new Color(0x15, 0x2B, 0x4B);

    // Inline Feedback Bar Colors
    public static final Color FEEDBACK_SUCCESS_BG = new Color(0xDC, 0xFC, 0xE7);
    public static final Color FEEDBACK_SUCCESS_TEXT = new Color(0x14, 0x53, 0x2D);
    public static final Color FEEDBACK_ERROR_BG = new Color(0xFE, 0xE2, 0xE2);
    public static final Color FEEDBACK_ERROR_TEXT = new Color(0x7F, 0x1D, 0x1D);
    public static final Color FEEDBACK_READY_BG = new Color(0xEC, 0xEB, 0xE5);
    public static final Color FEEDBACK_READY_TEXT = new Color(0x44, 0x40, 0x3C);

    // Typography (13-14pt for optimal legibility)
    public static final Font FONT_PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_PAGE_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_HINT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_SIDEBAR_BRAND = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SIDEBAR_ITEM = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_ITEM_ACTIVE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SIDEBAR_CAT = new Font("Segoe UI", Font.BOLD, 10);

    // =========================================================================
    // RESOLUTION-INDEPENDENT VECTOR ICONS (SVG-LIKE PURE SWING)
    // =========================================================================

    public enum IconType {
        PLUS, SHIP, CANCEL, SEARCH, DELETE, UNDO, REFRESH, WEIGHT, SPEED, BOX, STORE, CLOSE
    }

    public static class VectorIcon implements Icon {
        private final IconType type;
        private final int size;
        private Color overrideColor;

        public VectorIcon(IconType type, int size, Color overrideColor) {
            this.type = type;
            this.size = size;
            this.overrideColor = overrideColor;
        }

        public VectorIcon(IconType type, int size) {
            this(type, size, null);
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Color paintColor = overrideColor != null ? overrideColor : (c != null ? c.getForeground() : Color.WHITE);
            g2.setColor(paintColor);

            switch (type) {
                case PLUS:
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + size / 2, y + 2, x + size / 2, y + size - 2);
                    g2.drawLine(x + 2, y + size / 2, x + size - 2, y + size / 2);
                    break;

                case SHIP: // Delivery Truck
                    g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // Cargo box
                    g2.drawRoundRect(x + 1, y + 2, size - 7, size - 6, 2, 2);
                    // Cabin front
                    g2.drawLine(x + size - 6, y + 5, x + size - 2, y + 8);
                    g2.drawLine(x + size - 2, y + 8, x + size - 2, y + size - 4);
                    // Wheels
                    g2.fillOval(x + 3, y + size - 4, 3, 3);
                    g2.fillOval(x + size - 6, y + size - 4, 3, 3);
                    break;

                case CANCEL: // X mark
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 3, y + 3, x + size - 3, y + size - 3);
                    g2.drawLine(x + size - 3, y + 3, x + 3, y + size - 3);
                    break;

                case SEARCH: // Magnifier
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int circle = size - 6;
                    g2.drawOval(x + 1, y + 1, circle, circle);
                    g2.drawLine(x + circle - 1, y + circle - 1, x + size - 2, y + size - 2);
                    break;

                case DELETE: // Trash Can
                    g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 2, y + 3, x + size - 2, y + 3); // lid
                    g2.drawRoundRect(x + 3, y + 4, size - 6, size - 6, 2, 2); // body
                    g2.drawLine(x + size / 2, y + 6, x + size / 2, y + size - 4);
                    break;

                case UNDO: // Crisp U-turn Rollback Arrow
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // Arrowhead pointing left at top-left
                    g2.drawLine(x + 2, y + 4, x + 5, y + 1);
                    g2.drawLine(x + 2, y + 4, x + 5, y + 7);
                    // Top horizontal shaft -> curved right loop -> bottom base
                    int rightArcX = x + size - 7;
                    g2.drawLine(x + 2, y + 4, rightArcX, y + 4);
                    g2.drawArc(rightArcX - 3, y + 4, 6, size - 7, 90, -180);
                    g2.drawLine(x + 4, y + size - 3, rightArcX, y + size - 3);
                    break;

                case REFRESH: // Circular Reload Arrow
                    g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(x + 2, y + 2, size - 4, size - 4, 35, 285);
                    int ax = x + size - 3;
                    int ay = y + 4;
                    g2.drawLine(ax, ay - 2, ax, ay + 2);
                    g2.drawLine(ax, ay + 2, ax - 4, ay + 2);
                    break;

                case WEIGHT: // Scale / Balance
                    g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 2, y + 4, x + size - 2, y + 4);
                    g2.drawLine(x + size / 2, y + 2, x + size / 2, y + size - 2);
                    g2.drawLine(x + 3, y + size - 2, x + size - 3, y + size - 2);
                    break;

                case SPEED: // Fast Double Chevron >>
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 3, y + 2, x + 7, y + size / 2);
                    g2.drawLine(x + 7, y + size / 2, x + 3, y + size - 2);
                    g2.drawLine(x + 8, y + 2, x + 12, y + size / 2);
                    g2.drawLine(x + 12, y + size / 2, x + 8, y + size - 2);
                    break;

                case BOX: // Parcel
                    g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawRect(x + 2, y + 2, size - 4, size - 4);
                    g2.drawLine(x + 2, y + 6, x + size - 2, y + 6);
                    g2.drawLine(x + size / 2, y + 6, x + size / 2, y + size - 2);
                    break;

                case STORE: { // Storefront with striped awning, 4 scallops, left window, right door
                    Graphics2D gs = (Graphics2D) g2.create();
                    gs.translate(x, y);
                    double s = size / 24.0;
                    gs.scale(s, s);

                    // 4 Awning roof stripes (slanted trapezoid panels)
                    double[][] panels = {
                            {4.2, 3.0, 7.6, 3.0, 6.4, 8.7, 2.0, 8.7},
                            {8.5, 3.0, 11.55, 3.0, 11.55, 8.7, 7.3, 8.7},
                            {12.45, 3.0, 15.5, 3.0, 16.7, 8.7, 12.45, 8.7},
                            {16.4, 3.0, 19.8, 3.0, 22.0, 8.7, 17.6, 8.7}
                    };
                    for (int i = 0; i < panels.length; i++) {
                        Path2D.Double panel = new Path2D.Double();
                        panel.moveTo(panels[i][0], panels[i][1]);
                        panel.lineTo(panels[i][2], panels[i][3]);
                        panel.lineTo(panels[i][4], panels[i][5]);
                        panel.lineTo(panels[i][6], panels[i][7]);
                        panel.closePath();
                        gs.fill(panel);
                    }

                    // 4 Scalloped awning arches underneath
                    double[] scallopX = {2.0, 7.2, 12.4, 17.6};
                    for (int i = 0; i < scallopX.length; i++) {
                        double sx = scallopX[i];
                        Area scallop = new Area(new java.awt.geom.Rectangle2D.Double(sx, 9.25, 4.4, 0.7));
                        scallop.add(new Area(new Arc2D.Double(sx, 7.55, 4.4, 4.3, 180, 180, Arc2D.CHORD)));
                        gs.fill(scallop);
                    }

                    // Store body base with transparent window (left) and open doorway (right)
                    Area base = new Area(new RoundRectangle2D.Double(3.3, 12.2, 17.4, 8.8, 1.2, 1.2));
                    base.subtract(new Area(new RoundRectangle2D.Double(5.2, 14.3, 6.4, 4.1, 0.9, 0.9)));
                    base.subtract(new Area(new java.awt.geom.Rectangle2D.Double(13.9, 14.3, 4.3, 6.8)));
                    gs.fill(base);

                    gs.dispose();
                    break;
                }

                case CLOSE: // Modal close X
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 3, y + 3, x + size - 3, y + size - 3);
                    g2.drawLine(x + size - 3, y + 3, x + 3, y + size - 3);
                    break;
            }
            g2.dispose();
        }
    }

    // =========================================================================
    // BUTTON BUILDERS WITH EMBEDDED ICONS (TACTILE PURE-SWING)
    // =========================================================================

    private static void paintButtonLabel(JButton btn, Graphics2D g2, Icon icon, Color activeFg, int yOffset) {
        int w = btn.getWidth();
        int h = btn.getHeight();
        String text = btn.getText();

        g2.setFont(btn.getFont());
        FontMetrics fm = g2.getFontMetrics();

        int iconW = (icon != null) ? icon.getIconWidth() : 0;
        int gap = (icon != null && text != null && !text.isEmpty()) ? 7 : 0;
        int textW = (text != null && !text.isEmpty()) ? fm.stringWidth(text) : 0;
        int totalW = iconW + gap + textW;

        int startX = (w - totalW) / 2;
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent() + yOffset;

        Color drawColor = btn.isEnabled() ? activeFg : new Color(0x9C, 0xA3, 0xAF);
        btn.setForeground(drawColor);

        if (icon != null) {
            int iconY = (h - icon.getIconHeight()) / 2 + yOffset;
            icon.paintIcon(btn, g2, startX, iconY);
        }

        if (text != null && !text.isEmpty()) {
            g2.setColor(drawColor);
            g2.drawString(text, startX + iconW + gap, textY);
        }
    }

    private static void configureBaseButton(JButton btn, Icon icon, String tooltip) {
        btn.setFont(FONT_BOLD);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setRolloverEnabled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        FontMetrics fm = btn.getFontMetrics(FONT_BOLD);
        String txt = btn.getText();
        int textW = (txt != null && !txt.isEmpty()) ? fm.stringWidth(txt) : 0;
        int iconW = (icon != null) ? icon.getIconWidth() + (textW > 0 ? 7 : 0) : 0;
        btn.setPreferredSize(new Dimension(textW + iconW + 32, 36));

        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setToolTipText(tooltip);
        }
    }

    public static JButton createStyledButton(String text, Icon icon, Color bg, Color fg, String tooltip) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                boolean pressed = isEnabled() && getModel().isPressed();
                boolean hover = isEnabled() && getModel().isRollover();

                if (!isEnabled()) {
                    g2.setColor(new Color(0xE7, 0xE5, 0xE0));
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(new Color(0xD6, 0xD3, 0xCC));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else {
                    Color fill = pressed ? darken(bg, 0.16f) : (hover ? brighten(bg, 0.14f) : bg);
                    g2.setColor(fill);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);

                    // Crisp outer edge & subtle top sheen
                    g2.setColor(darken(bg, 0.25f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                    if (!pressed) {
                        g2.setColor(new Color(255, 255, 255, 36));
                        g2.drawLine(4, 1, w - 5, 1);
                    }
                }

                paintButtonLabel(this, g2, icon, fg, pressed ? 1 : 0);
                g2.dispose();
            }
        };

        configureBaseButton(btn, icon, tooltip);
        return btn;
    }

    public static JButton createOutlinedButton(String text, Icon icon, Color baseBg, Color hoverBg,
                                               Color pressedBg, Color borderColor, Color hoverBorder,
                                               Color fg, String tooltip) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                boolean pressed = isEnabled() && getModel().isPressed();
                boolean hover = isEnabled() && getModel().isRollover();

                if (!isEnabled()) {
                    g2.setColor(new Color(0xFA, 0xF9, 0xF6));
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(new Color(0xE6, 0xE4, 0xDD));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else if (pressed) {
                    g2.setColor(pressedBg);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(hoverBorder);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else if (hover) {
                    g2.setColor(hoverBg);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(hoverBorder);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else {
                    g2.setColor(baseBg);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(borderColor);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                }

                paintButtonLabel(this, g2, icon, fg, pressed ? 1 : 0);
                g2.dispose();
            }
        };

        configureBaseButton(btn, icon, tooltip);
        return btn;
    }

    /**
     * Button that cleanly flips between Navy primary (active) and Crisp White outlined (inactive).
     */
    public static JButton createToggleSortButton(String text, Icon icon, boolean initialActive, String tooltip) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                boolean active = Boolean.TRUE.equals(getClientProperty("ui.active"));
                boolean pressed = isEnabled() && getModel().isPressed();
                boolean hover = isEnabled() && getModel().isRollover();

                if (active) {
                    Color fill = pressed ? darken(PRIMARY_NAVY, 0.16f) : (hover ? brighten(PRIMARY_NAVY, 0.14f) : PRIMARY_NAVY);
                    g2.setColor(fill);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(darken(PRIMARY_NAVY, 0.25f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                    paintButtonLabel(this, g2, icon, Color.WHITE, pressed ? 1 : 0);
                } else {
                    Color fill = pressed ? new Color(0xE8, 0xE6, 0xDF) : (hover ? new Color(0xF5, 0xF4, 0xEF) : SURFACE);
                    g2.setColor(fill);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(hover ? new Color(0xB8, 0xB5, 0xAC) : BORDER);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                    paintButtonLabel(this, g2, icon, NEUTRAL_DARK, pressed ? 1 : 0);
                }
                g2.dispose();
            }
        };

        btn.putClientProperty("ui.active", Boolean.valueOf(initialActive));
        configureBaseButton(btn, icon, tooltip);
        return btn;
    }

    public static void setToggleSortActive(JButton btn, boolean active) {
        if (btn != null) {
            btn.putClientProperty("ui.active", Boolean.valueOf(active));
            btn.repaint();
        }
    }

    private static Color brighten(Color c, float factor) {
        int r = Math.min(255, (int) (c.getRed() + (255 - c.getRed()) * factor));
        int g = Math.min(255, (int) (c.getGreen() + (255 - c.getGreen()) * factor));
        int b = Math.min(255, (int) (c.getBlue() + (255 - c.getBlue()) * factor));
        return new Color(r, g, b);
    }

    private static Color darken(Color c, float factor) {
        int r = Math.max(0, (int) (c.getRed() * (1.0f - factor)));
        int g = Math.max(0, (int) (c.getGreen() * (1.0f - factor)));
        int b = Math.max(0, (int) (c.getBlue() * (1.0f - factor)));
        return new Color(r, g, b);
    }

    public static JButton createPrimaryButton(String text, Icon icon, String tooltip) {
        return createStyledButton(text, icon, PRIMARY_NAVY, Color.WHITE, tooltip);
    }

    public static JButton createPrimaryButton(String text, String tooltip) {
        return createPrimaryButton(text, null, tooltip);
    }

    public static JButton createAccentButton(String text, Icon icon, String tooltip) {
        return createStyledButton(text, icon, PRIMARY_BLUE, Color.WHITE, tooltip);
    }

    public static JButton createSuccessButton(String text, Icon icon, String tooltip) {
        return createStyledButton(text, icon, POSITIVE_GREEN, Color.WHITE, tooltip);
    }

    public static JButton createSuccessButton(String text, String tooltip) {
        return createSuccessButton(text, null, tooltip);
    }

    public static JButton createWarningButton(String text, Icon icon, String tooltip) {
        return createOutlinedButton(
                text, icon,
                new Color(0xFF, 0xFB, 0xEB), // Soft warm amber surface
                new Color(0xFE, 0xF3, 0xC7), // Hover amber
                new Color(0xFD, 0xE6, 0x8A), // Pressed amber
                new Color(0xF5, 0x9E, 0x0B), // Crisp amber border
                new Color(0xD9, 0x77, 0x06),
                WARNING_AMBER, tooltip
        );
    }

    public static JButton createWarningButton(String text, String tooltip) {
        return createWarningButton(text, null, tooltip);
    }

    public static JButton createSecondaryButton(String text, Icon icon, String tooltip) {
        return createOutlinedButton(
                text, icon,
                SURFACE,                     // Crisp white button matching ElectronDB
                new Color(0xF5, 0xF4, 0xEF), // Warm stone hover
                new Color(0xE8, 0xE6, 0xDF), // Warm stone pressed
                BORDER,
                new Color(0xB8, 0xB5, 0xAC),
                NEUTRAL_DARK, tooltip
        );
    }

    public static JButton createSecondaryButton(String text, String tooltip) {
        return createSecondaryButton(text, null, tooltip);
    }

    public static JButton createOutlineDangerButton(String text, Icon icon, String tooltip) {
        return createOutlinedButton(
                text, icon,
                SURFACE,
                new Color(0xFE, 0xF2, 0xF2),
                new Color(0xFE, 0xE2, 0xE2),
                new Color(0xFC, 0xA5, 0xA5),
                DANGER_RED,
                DANGER_RED, tooltip
        );
    }

    public static JButton createOutlineDangerButton(String text, String tooltip) {
        return createOutlineDangerButton(text, null, tooltip);
    }

    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(SURFACE);
        Border line = new LineBorder(BORDER, 1, true);
        Border titleBorder = BorderFactory.createTitledBorder(line, " " + title + " ", 0, 0, FONT_TITLE, TEXT_PRIMARY);
        panel.setBorder(new CompoundBorder(titleBorder, new EmptyBorder(10, 12, 10, 12)));
        return panel;
    }

    // =========================================================================
    // MODERN MINIMALIST SCROLLBAR UI
    // =========================================================================

    public static class MinimalistScrollBarUI extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(new Color(0xF9, 0xFA, 0xFB));
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() ? new Color(0x9C, 0xA3, 0xAF) : new Color(0xD1, 0xD5, 0xDB));
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(8, 8); // Slim 8px width
        }
    }

    public static JScrollPane createModernScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(new LineBorder(BORDER, 1));
        sp.getVerticalScrollBar().setUI(new MinimalistScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new MinimalistScrollBarUI());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // =========================================================================
    // INTERACTIVE INPUT FOCUS RINGS
    // =========================================================================

    public static void installFocusRing(JTextField field) {
        Border normalBorder = new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        );
        Border focusedBorder = new CompoundBorder(
                new LineBorder(new Color(0x3B, 0x82, 0xF6), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        );
        field.setBorder(normalBorder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(focusedBorder);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(normalBorder);
            }
        });
    }

    // =========================================================================
    // TABLE CONFIGURATION & RENDERERS
    // =========================================================================

    public static void configureTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(FONT_REGULAR);
        table.setBackground(SURFACE);
        table.setSelectionBackground(new Color(0xEB, 0xF5, 0xFF));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(TABLE_GRID_LINE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Strict Compliance Safeguard: Disable TableRowSorter (Trap 5)
        table.setAutoCreateRowSorter(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(SURFACE_ALT);
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CARD));
    }

    /**
     * Modern SaaS status pill cell renderer.
     * Renders centered rounded badges matching ElectronDB design.
     */
    public static class StatusPillRenderer extends DefaultTableCellRenderer {
        private String pillText = "";
        private Color rowBg = SURFACE;
        private Color pillBg = new Color(0xDC, 0xFC, 0xE7);
        private Color pillFg = new Color(0x16, 0x65, 0x34);
        private Color pillBorder = new Color(0xBB, 0xF7, 0xD0);

        public StatusPillRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, col);
            this.pillText = value != null ? value.toString() : "";
            this.rowBg = isSelected ? table.getSelectionBackground() : (row % 2 == 1 ? SURFACE_ALT : SURFACE);

            if (pillText.contains("Out of Stock")) {
                pillBg = new Color(0xFE, 0xE2, 0xE2);
                pillFg = new Color(0x99, 0x1B, 0x1B);
                pillBorder = new Color(0xFE, 0xCA, 0xCA);
            } else if (pillText.contains("Low Stock")) {
                pillBg = new Color(0xFE, 0xF3, 0xC7);
                pillFg = new Color(0xB4, 0x53, 0x09);
                pillBorder = new Color(0xFD, 0xE6, 0x8A);
            } else if (pillText.contains("NEXT")) {
                pillBg = new Color(0xDB, 0xEA, 0xFE);
                pillFg = new Color(0x1D, 0x4E, 0xD8);
                pillBorder = new Color(0xBF, 0xDB, 0xFE);
            } else { // "In Stock", "Active", or default
                pillBg = new Color(0xDC, 0xFC, 0xE7);
                pillFg = new Color(0x16, 0x65, 0x34);
                pillBorder = new Color(0xBB, 0xF7, 0xD0);
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(rowBg);
            g2.fillRect(0, 0, w, h);

            if (pillText != null && !pillText.isEmpty()) {
                g2.setFont(FONT_BADGE);
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(pillText);
                int pillW = textW + 18;
                int pillH = 22;
                int pillX = (w - pillW) / 2;
                int pillY = (h - pillH) / 2;

                g2.setColor(pillBg);
                g2.fillRoundRect(pillX, pillY, pillW, pillH, 12, 12);
                g2.setColor(pillBorder);
                g2.drawRoundRect(pillX, pillY, pillW, pillH, 12, 12);

                g2.setColor(pillFg);
                int textY = pillY + (pillH + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(pillText, pillX + 9, textY);
            }

            g2.dispose();
        }
    }

    private static final Border CELL_PADDING = new EmptyBorder(3, 10, 3, 10);

    public static DefaultTableCellRenderer createQueueCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(CELL_PADDING);

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

    public static DefaultTableCellRenderer createStackCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(CELL_PADDING);

                if (col == 0 || col == 3) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (col == 2) {
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

    public static DefaultTableCellRenderer createCatalogCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(CELL_PADDING);

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

    public static DefaultTableCellRenderer createFreightCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(CELL_PADDING);

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

    /**
     * Maps internal delivery priority codes (1..5) to concise UI labels.
     */
    public static String formatDeliverySpeed(int deliveryPriority) {
        switch (deliveryPriority) {
            case 1: return "Express (fastest)";
            case 2: return "Priority";
            case 3: return "Standard";
            case 4: return "Economy (slowest)";
            default: return "Bulk Saver";
        }
    }
}
