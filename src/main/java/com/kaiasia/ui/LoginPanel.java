package com.kaiasia.ui;

import com.kaiasia.auth.AuthApiClient;
import com.kaiasia.model.UserInfo;
import com.kaiasia.t24utils.T24UtilsApiClient;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblForgotPassword;
    private MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE); // Nền trắng
        GridBagConstraints gbc = new GridBagConstraints();

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chào mừng bạn đến với E-Bank Application");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        add(lblTitle, gbc);

        // Username label
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 5, 5, 5);
        add(lblUsername, gbc);

        // Username input
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 16));
        txtUsername.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password label
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblPassword, gbc);

        // Password input
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Forgot Password link
        lblForgotPassword = new JLabel("<html><u>Quên tài khoản/mật khẩu?</u></html>");
        lblForgotPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblForgotPassword.setForeground(Color.RED);
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblForgotPassword, gbc);

        // Login Button
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(0, 105, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnLogin.setPreferredSize(new Dimension(250, 45));
        btnLogin.setFocusPainted(false);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 20, 0);
        add(btnLogin, gbc);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        lblForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(null, "Vui lòng liên hệ tổng đài để lấy lại mật khẩu.");
            }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        JSONObject jsonResponse = AuthApiClient.login(username, password);

        if (jsonResponse == null) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến server. Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("API Response: " + jsonResponse.toString(4)); // Debug response

        // Kiểm tra lỗi từ API
        JSONObject error = jsonResponse.optJSONObject("error");
        if (error != null) {
            String errorCode = error.optString("code", "Unknown");
            String errorDesc = error.optString("desc", "Lỗi không xác định!");

            if ("04".equals(errorCode)) {
                JOptionPane.showMessageDialog(this, "Sai mật khẩu! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + errorDesc + " (Mã: " + errorCode + ")", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        // Kiểm tra phản hồi hợp lệ
        JSONObject body = jsonResponse.optJSONObject("body");
        if (body == null || !"OK".equals(body.optString("status"))) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thất bại! Trạng thái: " + (body != null ? body.optString("status") : "null"), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject enquiry = body.optJSONObject("enquiry");
        if (enquiry == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserInfo userInfo = new UserInfo(
                enquiry.optString("customerName", "N/A"),
                enquiry.optString("customerID", "N/A"),
                enquiry.optString("username", "N/A"),
                enquiry.optString("phone", "N/A"),
                enquiry.optString("sessionId", "N/A")
        );

        JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
        mainFrame.showDashboard(userInfo);
    }

}
