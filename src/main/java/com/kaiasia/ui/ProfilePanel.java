package com.kaiasia.ui;

import com.kaiasia.customer.CustomerApiClient;
import com.kaiasia.auth.AuthApiClient;
import com.kaiasia.model.UserInfo;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private MainFrame mainFrame;
    private UserInfo userInfo;

    private JLabel lblName;
    private JLabel lblCustomerID;
    private JLabel lblPhone;
    private JLabel lblEmail;
    private JLabel lblUsername;
    private JLabel lblAdress;

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

        lblName = new JLabel("Họ và tên: ");
        gbc.gridy++;
        add(lblName, gbc);

        lblCustomerID = new JLabel("Mã khách hàng: " + userInfo.getCustomerID());
        gbc.gridy++;
        add(lblCustomerID, gbc);

        lblPhone = new JLabel("Số điện thoại: ");
        gbc.gridy++;
        add(lblPhone, gbc);

        lblEmail = new JLabel("Email: ");
        gbc.gridy++;
        add(lblEmail, gbc);

        lblAdress = new JLabel("Địa chỉ: ");
        gbc.gridy++;
        add(lblAdress, gbc);

        lblUsername = new JLabel("Username: " + userInfo.getUsername());
        gbc.gridy++;
        add(lblUsername, gbc);

        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        gbc.gridy++;
        add(btnChangePassword, gbc);

        JButton btnBack = new JButton("Quay lại");
        gbc.gridy++;
        add(btnBack, gbc);

        btnChangePassword.addActionListener(e -> showChangePasswordDialog());
        btnBack.addActionListener(e -> mainFrame.showDashboard());

        // Gọi API để cập nhật thông tin khách hàng từ CUSTOMER_API
        loadCustomerInfo();
    }

    private void loadCustomerInfo() {
        if (userInfo == null || userInfo.getSessionId() == null || userInfo.getCustomerID() == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject response = CustomerApiClient.getCustomerInfo(userInfo.getSessionId(), userInfo.getCustomerID());

        if (response != null && response.optJSONObject("body") != null) {
            JSONObject body = response.optJSONObject("body");
            JSONObject enquiry = body.optJSONObject("enquiry");

            if (enquiry != null) {
                lblName.setText("Họ và tên: " + enquiry.optString("customerName", "Không có dữ liệu"));
                lblPhone.setText("Số điện thoại: " + enquiry.optString("phone", "Không có dữ liệu"));
                lblEmail.setText("Email: " + enquiry.optString("email", "Không có dữ liệu"));
                lblAdress.setText("Địa chỉ: " + enquiry.optString("address", "Không có dữ liệu"));
            } else {
                JOptionPane.showMessageDialog(this, "Không lấy được dữ liệu khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi gọi CUSTOMER_API!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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

            JSONObject response = AuthApiClient.changePassword(oldPassword, newPassword, reNewPassword);

            if (response == null) {
                JOptionPane.showMessageDialog(changePassDialog, "Lỗi hệ thống, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = response.getJSONObject("body").optString("status", "FAILED");
            if ("OK".equals(status)) {
                JOptionPane.showMessageDialog(changePassDialog, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại ứng dụng", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                changePassDialog.dispose();
                mainFrame.showLogin();
            } else {
                String message = response.getJSONObject("body").optString("message", "Đổi mật khẩu thất bại!");
                JOptionPane.showMessageDialog(changePassDialog, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> changePassDialog.dispose());

        changePassDialog.setLocationRelativeTo(this);
        changePassDialog.setVisible(true);
    }
}
