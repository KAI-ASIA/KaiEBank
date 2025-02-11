package com.kaiasia.ui;

import com.kaiasia.model.UserInfo;
import javax.swing.*;

public class MainFrame extends JFrame {
    private static UserInfo currentUser;

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

    public void showDashboard() {
        System.out.println("DEBUG: Lưu currentUser trong MainFrame: " + this.currentUser);
        getContentPane().removeAll();
        add(new DashboardPanel(this, currentUser)); // Hiển thị trang DashboardPanel
        revalidate();
        repaint();
    }

    public void showProfile(UserInfo userInfo) {
        getContentPane().removeAll();
        add(new ProfilePanel(this, userInfo)); // Hiển thị trang ProfilePanel
        revalidate();
        repaint();
    }

    public static UserInfo getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserInfo userInfo) {
        this.currentUser = userInfo;
        System.out.println("DEBUG: Cập nhật currentUser trong MainFrame: " + this.currentUser);
    }

    public void showTransferScreen(UserInfo userInfo) {
        getContentPane().removeAll();
        add(new FundsTransferPanel(this, userInfo)); // Hiển thị trang FundsTransferPanel
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
