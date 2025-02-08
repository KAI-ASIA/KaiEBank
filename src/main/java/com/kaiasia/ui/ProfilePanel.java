package com.kaiasia.ui;

import com.kaiasia.auth.AuthApiClient;
import com.kaiasia.model.UserInfo;
import org.json.JSONObject;

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

        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        gbc.gridy++;
        add(btnChangePassword, gbc);

        JButton btnGetOtp = new JButton("Lấy OTP");  // Nút lấy OTP
        gbc.gridy++;
        add(btnGetOtp, gbc);

        JButton btnBack = new JButton("Quay lại");
        gbc.gridy++;
        add(btnBack, gbc);

        btnChangePassword.addActionListener(e -> showChangePasswordDialog());
        btnGetOtp.addActionListener(e -> handleGetOtp()); // Thêm xử lý lấy OTP
        btnBack.addActionListener(e -> mainFrame.showDashboard(userInfo));
    }

    private void showChangePasswordDialog() {
        JDialog changePassDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đổi mật khẩu", true);
        changePassDialog.setSize(350, 250);
        changePassDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblOldPass = new JLabel("Mật khẩu cũ:");
        JPasswordField txtOldPass = new JPasswordField(15);
        JLabel lblNewPass = new JLabel("Mật khẩu mới:");
        JPasswordField txtNewPass = new JPasswordField(15);
        JLabel lblReNewPass = new JLabel("Nhập lại mật khẩu:");
        JPasswordField txtReNewPass = new JPasswordField(15);

        JButton btnSubmit = new JButton("Xác nhận");
        JButton btnCancel = new JButton("Hủy");

        gbc.gridx = 0;
        gbc.gridy = 0;
        changePassDialog.add(lblOldPass, gbc);
        gbc.gridx = 1;
        changePassDialog.add(txtOldPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        changePassDialog.add(lblNewPass, gbc);
        gbc.gridx = 1;
        changePassDialog.add(txtNewPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        changePassDialog.add(lblReNewPass, gbc);
        gbc.gridx = 1;
        changePassDialog.add(txtReNewPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        changePassDialog.add(btnCancel, gbc);
        gbc.gridx = 1;
        changePassDialog.add(btnSubmit, gbc);

        btnSubmit.addActionListener(e -> {
            String oldPassword = new String(txtOldPass.getPassword());
            String newPassword = new String(txtNewPass.getPassword());
            String reNewPassword = new String(txtReNewPass.getPassword());

            if (!newPassword.equals(reNewPassword)) {
                JOptionPane.showMessageDialog(changePassDialog, "Mật khẩu mới không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JSONObject response = AuthApiClient.changePassword(userInfo.getUsername(), oldPassword, newPassword, reNewPassword);

            if (response == null) {
                JOptionPane.showMessageDialog(changePassDialog, "Lỗi hệ thống, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = response.getJSONObject("body").optString("status", "FAILED");
            if ("OK".equals(status)) {
                JOptionPane.showMessageDialog(changePassDialog, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                changePassDialog.dispose();
            } else {
                String message = response.getJSONObject("body").optString("message", "Đổi mật khẩu thất bại!");
                JOptionPane.showMessageDialog(changePassDialog, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });


        btnCancel.addActionListener(e -> changePassDialog.dispose());

        changePassDialog.setLocationRelativeTo(this);
        changePassDialog.setVisible(true);
    }

    private void handleGetOtp() {
        JSONObject response = AuthApiClient.getOtp();

        if (response == null) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (response.has("error")) {
            String errorDesc = response.getJSONObject("error").optString("desc", "Không lấy được OTP!");
            JOptionPane.showMessageDialog(this, "Lỗi: " + errorDesc, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject body = response.optJSONObject("body");
        if (body != null && "OK".equals(body.optString("status"))) {
            String otpCode = body.optString("otp", "Không có mã OTP!");
            JOptionPane.showMessageDialog(this, "Mã OTP của bạn: " + otpCode, "OTP", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lấy OTP thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
