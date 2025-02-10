package com.kaiasia.ui;

import com.kaiasia.model.UserInfo;
import javax.swing.*;

public class MainFrame extends JFrame {
    private static UserInfo currentUser;
    private ProfilePanel ProfilePanel;

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
    public void showFunTranferIn(){
        getContentPane().removeAll();
        add(new FundTransferInPanel(this)); // Hiển thị trang LoginPanel
        revalidate();
        repaint();
    }
    public void showProfile(UserInfo userInfo) {
        getContentPane().removeAll();
        add(new ProfilePanel(this, userInfo)); // Hiển thị trang ProfilePanel
        revalidate();
        repaint();
    }

    public void showNapasTransferScreen(UserInfo userInfo) {
        if (userInfo == null) {
            System.out.println("DEBUG: userInfo truyền vào null, dùng currentUser trong MainFrame");
            userInfo = this.currentUser;
        }

        if (userInfo == null || userInfo.getCustomerID() == null || userInfo.getCustomerID().isEmpty()) {
            System.out.println("LỖI: UserInfo không hợp lệ khi mở Napas Transfer! currentUser = " + userInfo);
            JOptionPane.showMessageDialog(this, "Không thể mở Napas Transfer, UserInfo không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("UserInfo hợp lệ! Mở Napas Transfer");

        getContentPane().removeAll();
        add(new NapasTransferPanel(this, userInfo)); // Hiển thị trang NapasTransferPanel
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

    public void showInternalTransferScreen(UserInfo userInfo) {
        getContentPane().removeAll();
        add(new InternalFundsTransferPanel(this, userInfo)); // Chuyển sang giao diện chuyển tiền nội bộ
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
