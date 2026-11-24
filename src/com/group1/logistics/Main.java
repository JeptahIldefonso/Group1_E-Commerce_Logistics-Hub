package com.group1.logistics;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.ui.MainFrame;

import javax.swing.*;

/**
 * Main application entry point for Group 1: E-Commerce Fulfillment & Logistics System.
 */
public class Main {
    public static void main(String[] args) {
        // Set System Look & Feel so Swing uses native modern OS rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback cleanly to default Swing Look & Feel
        }

        // Launch Desktop GUI strictly on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            FulfillmentService service = new FulfillmentService();
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}
