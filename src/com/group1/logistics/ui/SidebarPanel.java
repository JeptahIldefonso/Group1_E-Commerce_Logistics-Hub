package com.group1.logistics.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern dark left navigation sidebar matching SaaS ERP layout (ElectronDB style).
 * Width: 220px fixed.
 * Pure standard Java Swing; zero Java Collections.
 */
public class SidebarPanel extends JPanel {
    private final MainFrame mainFrame;
    private int activeIndex = 0;

    private NavItemBtn btnOrders;
    private NavItemBtn btnProducts;
    private NavItemBtn btnRestock;
    private NavItemBtn btnShipments;
    private JCheckBox chkTechnicalDetails;

    public static class NavItemBtn extends JPanel {
        private final String title;
        private final Icon icon;
        private int count = 0;
        private boolean active = false;
        private boolean hover = false;
        private final Runnable onClick;

        public NavItemBtn(String title, Icon icon, Runnable onClick) {
            this.title = title;
            this.icon = icon;
            this.onClick = onClick;

            setOpaque(false);
            setPreferredSize(new Dimension(220, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (onClick != null) {
                        onClick.run();
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        public void setCount(int count) {
            this.count = count;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (active) {
                g2.setColor(UiTheme.SIDEBAR_ACTIVE);
                g2.fillRoundRect(8, 2, w - 16, h - 4, 6, 6);
                // Active blue accent strip on left
                g2.setColor(UiTheme.SIDEBAR_ACCENT);
                g2.fillRoundRect(8, 6, 4, h - 12, 2, 2);
            } else if (hover) {
                g2.setColor(UiTheme.SIDEBAR_HOVER);
                g2.fillRoundRect(8, 2, w - 16, h - 4, 6, 6);
            }

            // Draw Icon
            Color itemFg = active ? UiTheme.SIDEBAR_TEXT_ACTIVE : UiTheme.SIDEBAR_TEXT;
            setForeground(itemFg);
            int iconX = 22;
            int iconY = (h - (icon != null ? icon.getIconHeight() : 0)) / 2;
            if (icon != null) {
                icon.paintIcon(this, g2, iconX, iconY);
            }

            // Draw Title Text
            g2.setFont(active ? UiTheme.FONT_SIDEBAR_ITEM_ACTIVE : UiTheme.FONT_SIDEBAR_ITEM);
            g2.setColor(itemFg);
            FontMetrics fm = g2.getFontMetrics();
            int textX = iconX + (icon != null ? icon.getIconWidth() + 10 : 0);
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(title, textX, textY);

            // Draw Live Count Badge Pill (on the right)
            String badgeText = String.valueOf(count);
            g2.setFont(UiTheme.FONT_BADGE);
            FontMetrics bfm = g2.getFontMetrics();
            int bTextW = bfm.stringWidth(badgeText);
            int badgeW = Math.max(22, bTextW + 12);
            int badgeH = 19;
            int badgeX = w - 16 - badgeW;
            int badgeY = (h - badgeH) / 2;

            if (active) {
                g2.setColor(new Color(0x25, 0x63, 0xEB));
            } else {
                g2.setColor(new Color(0x16, 0x2E, 0x4E));
            }
            g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);

            g2.setColor(active ? Color.WHITE : UiTheme.SIDEBAR_TEXT);
            int bTextX = badgeX + (badgeW - bTextW) / 2;
            int bTextY = badgeY + (badgeH - bfm.getHeight()) / 2 + bfm.getAscent();
            g2.drawString(badgeText, bTextX, bTextY);

            g2.dispose();
        }
    }

    public SidebarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setPreferredSize(new Dimension(220, 0));
        setBackground(UiTheme.SIDEBAR_BG);
        setBorder(new MatteBorder(0, 0, 0, 1, UiTheme.SIDEBAR_BORDER));
        setLayout(new BorderLayout(0, 0));

        // 1. Top Section: Brand + Module Navigation
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        // Brand Banner
        JPanel brandBox = new JPanel(new GridBagLayout());
        brandBox.setOpaque(false);
        brandBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        brandBox.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lblBrandBadge = new JLabel(new UiTheme.VectorIcon(UiTheme.IconType.STORE, 22, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1D, 0x4E, 0xD8));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBrandBadge.setPreferredSize(new Dimension(36, 36));
        lblBrandBadge.setMinimumSize(new Dimension(36, 36));
        lblBrandBadge.setMaximumSize(new Dimension(36, 36));
        lblBrandBadge.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);

        JLabel lblBrandTitle = new JLabel("Logistics Hub");
        lblBrandTitle.setFont(UiTheme.FONT_SIDEBAR_BRAND);
        lblBrandTitle.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("GROUP 1 • DSA FINAL");
        lblBrandSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBrandSub.setForeground(UiTheme.SIDEBAR_CATEGORY);

        brandText.add(lblBrandTitle);
        brandText.add(lblBrandSub);

        GridBagConstraints bgc = new GridBagConstraints();
        bgc.gridx = 0;
        bgc.gridy = 0;
        bgc.insets = new Insets(0, 0, 0, 10);
        bgc.anchor = GridBagConstraints.WEST;
        brandBox.add(lblBrandBadge, bgc);

        bgc.gridx = 1;
        bgc.weightx = 1.0;
        bgc.insets = new Insets(0, 0, 0, 0);
        bgc.fill = GridBagConstraints.HORIZONTAL;
        brandBox.add(brandText, bgc);
        topContainer.add(brandBox);

        // Section Label: MODULES
        JPanel catBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
        catBox.setOpaque(false);
        catBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        catBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel lblCat = new JLabel("MODULES");
        lblCat.setFont(UiTheme.FONT_SIDEBAR_CAT);
        lblCat.setForeground(UiTheme.SIDEBAR_CATEGORY);
        catBox.add(lblCat);
        topContainer.add(catBox);

        // Nav Buttons (inherit dynamic active/inactive foreground color)
        btnOrders = new NavItemBtn("Orders", new UiTheme.VectorIcon(UiTheme.IconType.SHIP, 14), () -> selectTab(0));
        btnProducts = new NavItemBtn("Products", new UiTheme.VectorIcon(UiTheme.IconType.BOX, 14), () -> selectTab(1));
        btnRestock = new NavItemBtn("Restock", new UiTheme.VectorIcon(UiTheme.IconType.PLUS, 14), () -> selectTab(2));
        btnShipments = new NavItemBtn("Shipments", new UiTheme.VectorIcon(UiTheme.IconType.WEIGHT, 14), () -> selectTab(3));

        topContainer.add(btnOrders);
        topContainer.add(Box.createVerticalStrut(3));
        topContainer.add(btnProducts);
        topContainer.add(Box.createVerticalStrut(3));
        topContainer.add(btnRestock);
        topContainer.add(Box.createVerticalStrut(3));
        topContainer.add(btnShipments);

        add(topContainer, BorderLayout.NORTH);

        // 2. Bottom Section: Technical Toggle + Operator Info
        JPanel bottomContainer = new JPanel();
        bottomContainer.setOpaque(false);
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UiTheme.SIDEBAR_BORDER),
                new EmptyBorder(14, 16, 16, 16)
        ));

        chkTechnicalDetails = new JCheckBox("Technical details");
        chkTechnicalDetails.setFont(UiTheme.FONT_HINT);
        chkTechnicalDetails.setForeground(UiTheme.SIDEBAR_TEXT);
        chkTechnicalDetails.setOpaque(false);
        chkTechnicalDetails.setFocusPainted(false);
        chkTechnicalDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkTechnicalDetails.setToolTipText("Show data structure metrics, algorithm names, and BST height");
        chkTechnicalDetails.addActionListener(e -> mainFrame.handleToggleTechnicalDetails(chkTechnicalDetails.isSelected()));

        JPanel operatorBox = new JPanel(new BorderLayout(4, 2));
        operatorBox.setOpaque(false);
        operatorBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        operatorBox.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblOp = new JLabel("Admin (Group 1)");
        lblOp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblOp.setForeground(Color.WHITE);

        JLabel lblEnv = new JLabel("Java 17 LTS • Zero Libs");
        lblEnv.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEnv.setForeground(UiTheme.SIDEBAR_CATEGORY);

        operatorBox.add(lblOp, BorderLayout.NORTH);
        operatorBox.add(lblEnv, BorderLayout.SOUTH);

        bottomContainer.add(chkTechnicalDetails);
        bottomContainer.add(operatorBox);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    public void selectTab(int index) {
        this.activeIndex = index;
        btnOrders.setActive(index == 0);
        btnProducts.setActive(index == 1);
        btnRestock.setActive(index == 2);
        btnShipments.setActive(index == 3);

        mainFrame.showTab(index);
    }

    public void updateBadges(int orders, int products, int restocks, int shipments) {
        btnOrders.setCount(orders);
        btnProducts.setCount(products);
        btnRestock.setCount(restocks);
        btnShipments.setCount(shipments);
    }

    public boolean isTechnicalDetailsSelected() {
        return chkTechnicalDetails != null && chkTechnicalDetails.isSelected();
    }
}
