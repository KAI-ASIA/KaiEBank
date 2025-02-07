package com.kaiasia.ui;

import com.kaiasia.model.UserInfo;
import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private MainFrame mainFrame;
    private UserInfo userInfo;

    public ProfilePanel(MainFrame mainFrame, UserInfo userInfo) {
        this.mainFrame = mainFrame;
        this.userInfo = userInfo;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTitle = new JLabel("Thông tin cá nhân");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblTitle, gbc);

        JLabel lblName = new JLabel("Họ và tên: " + userInfo.getCustomerName());
        gbc.gridy++;
        add(lblName, gbc);

        JLabel lblCustomerID = new JLabel("Mã khách hàng: " + userInfo.getCustomerID());
        gbc.gridy++;
        add(lblCustomerID, gbc);

        JLabel lblPhone = new JLabel("Số điện thoại: " + userInfo.getPhone());
        gbc.gridy++;
        add(lblPhone, gbc);

        JLabel lblUsername = new JLabel("Username: " + userInfo.getUsername());
        gbc.gridy++;
        add(lblUsername, gbc);

        JButton btnBack = new JButton("Quay lại");
        gbc.gridy++;
        add(btnBack, gbc);

        btnBack.addActionListener(e -> mainFrame.showDashboard(userInfo)); // Quay lại dashboard
    }
}
