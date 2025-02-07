package com.kaiasia.ui;

import com.kaiasia.model.UserInfo;
import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("E-Bank Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 600);
        setLocationRelativeTo(null);

        showLogin();
    }

    public void showLogin() {
        getContentPane().removeAll();
        add(new LoginPanel(this)); // Hiển thị trang LoginPanel
        revalidate();
        repaint();
    }

    public void showDashboard(UserInfo userInfo) {
        getContentPane().removeAll();
        add(new DashboardPanel(this, userInfo)); // Hiển thị trang DashboardPanel
        revalidate();
        repaint();
    }

    public void showProfile(UserInfo userInfo) {
        getContentPane().removeAll();
        add(new ProfilePanel(this, userInfo)); // Hiển thị trang ProfilePanel
        revalidate();
        repaint();
    }

    // Phương thức chuyển đến màn hình Napas Transfer
    public void showNapasTransferScreen() {
        getContentPane().removeAll();
        add(new NapasTransferPanel()); // Hiển thị trang NapasTransferPanel
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
