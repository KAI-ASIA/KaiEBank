package com.kaiasia.ui;

import com.kaiasia.auth.AuthApiClient;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordPanel extends JPanel {
    private JTextField usernameField;
    private JTextField resetCodeField;
    private JPasswordField newPasswordField;
    private JButton requestResetButton, confirmResetButton;
    private MainFrame mainFrame;
    private String lastTransId;

    public ForgotPasswordPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Tiêu đề
        JLabel titleLabel = new JLabel("Quên Mật Khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Nhập Username
        gbc.gridy++;
        add(createLabel("Nhập tên đăng nhập:"), gbc);
        usernameField = createTextField();
        gbc.gridy++;
        add(usernameField, gbc);

        // Nút yêu cầu mã reset (AUTH-5)
        requestResetButton = new JButton("Gửi yêu cầu mã xác nhận");
        requestResetButton.setFont(new Font("Arial", Font.BOLD, 16));
        requestResetButton.addActionListener(e -> requestResetCode());
        gbc.gridy++;
        add(requestResetButton, gbc);

        // Nhập mã xác nhận
        gbc.gridy++;
        add(createLabel("Nhập mã xác nhận:"), gbc);
        resetCodeField = createTextField();
        gbc.gridy++;
        add(resetCodeField, gbc);

        // Nhập mật khẩu mới
        gbc.gridy++;
        add(createLabel("Nhập mật khẩu mới:"), gbc);
        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 16));
        newPasswordField.setPreferredSize(new Dimension(240, 40));
        gbc.gridy++;
        add(newPasswordField, gbc);

        // Nút đặt lại mật khẩu (AUTH-6)
        confirmResetButton = new JButton("Đặt lại mật khẩu");
        confirmResetButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmResetButton.addActionListener(e -> resetPassword());
        gbc.gridy++;
        add(confirmResetButton, gbc);

        // Nút quay lại
        JButton backButton = new JButton("Quay lại");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(e -> mainFrame.showLogin());
        gbc.gridy++;
        add(backButton, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(240, 40));
        return textField;
    }

    private void requestResetCode() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Gửi request API
        System.out.println("Gửi request quên mật khẩu (AUTH-5) cho username: " + username);
        JSONObject response = AuthApiClient.requestResetCode(username);

        // Kiểm tra response null
        if (response == null) {
            System.out.println("Không nhận được phản hồi từ API!");
            JOptionPane.showMessageDialog(this, "Không nhận được phản hồi từ hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Debug: In toàn bộ JSON response
        System.out.println("REQUEST_RESET_CODE - API Response: " + response.toString(4));

        String status = response.optJSONObject("body").optString("status", "UNKNOWN");
        System.out.println("REQUEST_RESET_CODE - Status: " + status);

        // Lấy resetCode ngay sau khi có response
        JSONObject enquiry = response.optJSONObject("body").optJSONObject("enquiry");
        String resetCode = (enquiry != null) ? enquiry.optString("resetCode", "Không có mã") : "Không có mã";

        // Debug: In resetCode
        System.out.println("REQUEST_RESET_CODE - Reset Code: " + resetCode);

        // Kiểm tra nếu API trả về lỗi
        if ("FAILE".equals(status)) {
            JSONObject error = response.optJSONObject("error");
            String errorMsg = (error != null) ? error.optString("desc", "Lỗi không xác định") : "Lỗi không xác định";
            System.out.println("REQUEST_RESET_CODE - Lỗi từ API: " + errorMsg);

            JOptionPane.showMessageDialog(this, "Lỗi từ hệ thống: " + errorMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra nếu API trả về thành công
        if ("OK".equals(status)) {
            String responseCode = enquiry.optString("responseCode", "99");
            System.out.println("REQUEST_RESET_CODE - Response Code: " + responseCode);

            if ("00".equals(responseCode) && !resetCode.isEmpty()) {
                lastTransId = enquiry.optString("transId", "");
                JOptionPane.showMessageDialog(this, "Mã xác nhận: " + resetCode, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Không thể gửi yêu cầu! Hãy thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void resetPassword() {
        String username = usernameField.getText().trim();
        String resetCode = resetCodeField.getText().trim(); // Mã reset nhập từ UI
        String newPassword = new String(newPasswordField.getPassword());

        if (username.isEmpty() || resetCode.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Debug: Kiểm tra giá trị resetCode trước khi gửi API
        System.out.println("Reset Code nhập từ UI: " + resetCode);
        JSONObject response = AuthApiClient.resetPassword(username, resetCode, newPassword);

        if (response == null) {
            System.out.println("Không nhận được phản hồi từ API!");
            return;
        }

        // Kiểm tra nếu có lỗi từ API
        JSONObject error = response.optJSONObject("error");
        if (error != null) {
            String errorMsg = error.optString("desc", "Lỗi không xác định");
            System.out.println("Lỗi API: " + errorMsg);
        } else {
            System.out.println("Đặt lại mật khẩu thành công!");
        }
    }

}