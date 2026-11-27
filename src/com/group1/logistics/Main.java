package com.group1.logistics;

import com.group1.logistics.service.FulfillmentService;
import com.group1.logistics.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            FulfillmentService service = new FulfillmentService();
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}
