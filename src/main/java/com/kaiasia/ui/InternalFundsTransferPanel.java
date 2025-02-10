//package com.kaiasia.ui;
//
//import com.kaiasia.FundsTransfer.FundsTransferApiClient;
//import com.kaiasia.auth.AuthApiClient;
//import com.kaiasia.model.UserInfo;
//import com.kaiasia.t24utils.T24UtilsApiClient;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import java.awt.*;
//
//public class InternalFundsTransferPanel extends JPanel {
//    private JComboBox<String> senderAccountDropdown;
//    private JTextField amountField;
//    private JTextField benAccField;
//    private JTextField transContentField;
//    private JLabel benAccNameLabel; // Thêm JLabel để hiển thị tên chủ tài khoản
//    private MainFrame mainFrame;
//    private UserInfo userInfo;
//
//    public InternalFundsTransferPanel(MainFrame mainFrame, UserInfo userInfo) {
//        this.mainFrame = mainFrame;
//        this.userInfo = userInfo;
//
//        setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(8, 8, 8, 8);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.gridx = 0;
//        gbc.gridwidth = 2;
//
//        // Tiêu đề
//        JLabel titleLabel = new JLabel("Chuyển tiền nội bộ", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
//        gbc.gridy = 0;
//        add(titleLabel, gbc);
//
//        // Nút "Quay lại"
//        JButton btnBack = new JButton("Quay lại");
//        btnBack.setFont(new Font("Arial", Font.BOLD, 16));
//        btnBack.setPreferredSize(new Dimension(140, 48));
//        gbc.gridy++;
//        gbc.gridwidth = 1;
//        add(btnBack, gbc);
//        btnBack.addActionListener(e -> mainFrame.showDashboard());
//
//        // Panel nhập liệu
//        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));
//
//        inputPanel.add(createLabel("Tài khoản gửi:"));
//        senderAccountDropdown = new JComboBox<>();
//        senderAccountDropdown.setFont(new Font("Arial", Font.PLAIN, 16));
//        inputPanel.add(senderAccountDropdown);
//
//        inputPanel.add(createLabel("Số tiền chuyển:"));
//        amountField = createLargeTextField();
//        inputPanel.add(amountField);
//
//        inputPanel.add(createLabel("Số tài khoản nhận:"));
//        benAccField = createLargeTextField();
//        inputPanel.add(benAccField);
//
//        // Thêm label để hiển thị tên chủ tài khoản
//        inputPanel.add(createLabel("Tên chủ tài khoản:"));
//        benAccNameLabel = new JLabel(" ");
//        benAccNameLabel.setFont(new Font("Arial", Font.ITALIC, 14));
//        benAccNameLabel.setForeground(Color.BLUE);
//        inputPanel.add(benAccNameLabel);
//
//        inputPanel.add(createLabel("Nội dung chuyển tiền:"));
//        transContentField = createLargeTextField();
//        inputPanel.add(transContentField);
//
//        // Thêm sự kiện lắng nghe khi nhập số tài khoản
//        benAccField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                fetchAccountName();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                fetchAccountName();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                fetchAccountName();
//            }
//        });
//
//        // Thêm panel nhập liệu
//        gbc.gridy++;
//        gbc.gridwidth = 2;
//        add(inputPanel, gbc);
//
//        // Nút gửi yêu cầu chuyển tiền
//        JButton transferButton = new JButton("Gửi yêu cầu chuyển tiền");
//        transferButton.setFont(new Font("Arial", Font.BOLD, 16));
//        transferButton.setPreferredSize(new Dimension(240, 48));
//        transferButton.addActionListener(e -> handleTransfer());
//
//        gbc.gridy++;
//        add(transferButton, gbc);
//
//        // Load tài khoản nguồn
//        loadUserAccounts();
//    }
//
//    // Tạo label
//    private JLabel createLabel(String text) {
//        JLabel label = new JLabel(text);
//        label.setFont(new Font("Arial", Font.BOLD, 16));
//        return label;
//    }
//
//    // Tạo ô nhập liệu
//    private JTextField createLargeTextField() {
//        JTextField textField = new JTextField();
//        textField.setFont(new Font("Arial", Font.PLAIN, 16));
//        textField.setPreferredSize(new Dimension(240, 38));
//        return textField;
//    }
//
//    // Load danh sách tài khoản nguồn
//    private void loadUserAccounts() {
//        if (userInfo == null || userInfo.getCustomerID() == null) {
//            System.out.println("LỖI: UserInfo không hợp lệ, không thể tải tài khoản!");
//            senderAccountDropdown.addItem("Không có tài khoản");
//            return;
//        }
//
//        String customerId = userInfo.getCustomerID();
//        System.out.println("DEBUG: Tải tài khoản cho customerId: " + customerId);
//
//        JSONObject response = T24UtilsApiClient.getCustomerAccounts(customerId);
//        if (response != null && response.getJSONObject("body").getString("status").equals("OK")) {
//            JSONArray accounts = response.getJSONObject("body").getJSONObject("enquiry").getJSONArray("accounts");
//            for (int i = 0; i < accounts.length(); i++) {
//                JSONObject account = accounts.getJSONObject(i);
//                String accountId = account.getString("accountId");
//
//                senderAccountDropdown.addItem(accountId);
//            }
//        } else {
//            System.out.println("LỖI: Không có tài khoản nào để hiển thị!");
//            senderAccountDropdown.addItem("Không có tài khoản");
//        }
//    }
//
//    // Tự động lấy tên chủ tài khoản nhận
//    private void fetchAccountName() {
//        String accountId = benAccField.getText().trim();
//
//        if (accountId.isEmpty()) {
//            benAccNameLabel.setText(" "); // Xóa nội dung nếu trống
//            return;
//        }
//
//        new Thread(() -> {
//            try {
//                JSONObject response = T24UtilsApiClient.getAccountInfo(accountId);
//                if (response != null && response.optJSONObject("body") != null &&
//                        "OK".equals(response.getJSONObject("body").optString("status"))) {
//
//                    JSONObject enquiry = response.getJSONObject("body").optJSONObject("enquiry");
//                    String accountName = (enquiry != null) ? enquiry.optString("shortName", "Không tìm thấy") : "Không tìm thấy";
//
//                    benAccNameLabel.setText(accountName); // Hiển thị tên chủ tài khoản
//                    System.out.println("DEBUG: Tên chủ tài khoản - " + accountName);
//                } else {
//                    benAccNameLabel.setText("Không tìm thấy tài khoản");
//                }
//            } catch (Exception ex) {
//                benAccNameLabel.setText("Lỗi kết nối");
//                System.err.println("LỖI: " + ex.getMessage());
//            }
//        }).start();
//    }
//
//    private void handleTransfer() {
//        String senderAccount = (String) senderAccountDropdown.getSelectedItem();
//        String amount = amountField.getText().trim();
//        String benAcc = benAccField.getText().trim();
//        String transContent = transContentField.getText().trim();
//
//        if (senderAccount == null || senderAccount.equals("Không có tài khoản") ||
//                amount.isEmpty() || benAcc.isEmpty() || transContent.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        if (userInfo == null) {
//            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: Không tìm thấy thông tin người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // Mở màn hình xác nhận OTP
//        showOtpVerificationDialog(senderAccount, benAcc, amount, transContent);
//    }
//
//    private void showOtpVerificationDialog(String senderAccount, String benAcc, String amount, String transContent) {
//        JDialog otpDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận OTP", true);
//        otpDialog.setSize(350, 250);
//        otpDialog.setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel lblOtp = new JLabel("Nhập mã OTP:");
//        JTextField txtOtp = new JTextField(10);
//
//        JButton btnGetOtp = new JButton("Lấy OTP");
//        JButton btnConfirm = new JButton("Xác nhận");
//        JButton btnCancel = new JButton("Hủy");
//
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        otpDialog.add(lblOtp, gbc);
//        gbc.gridx = 1;
//        otpDialog.add(txtOtp, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        otpDialog.add(btnGetOtp, gbc);
//        gbc.gridx = 1;
//        otpDialog.add(btnConfirm, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        otpDialog.add(btnCancel, gbc);
//
//        // Xử lý nút "Lấy OTP"
//        btnGetOtp.addActionListener(e -> {
//            JSONObject otpResponse = AuthApiClient.getOtp();
//
//            if (otpResponse == null) {
//                JOptionPane.showMessageDialog(otpDialog, "Lỗi hệ thống, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(otpDialog, "OTP đã được gửi qua email!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
//
//        // Xử lý nút "Xác nhận"
//        btnConfirm.addActionListener(e -> {
//            String otp = txtOtp.getText().trim();
//
//            if (otp.isEmpty()) {
//                JOptionPane.showMessageDialog(otpDialog, "Vui lòng nhập mã OTP!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Gọi API chuyển tiền với OTP
//            String sessionId = userInfo.getSessionId();
//            String customerID = userInfo.getCustomerID();
//
//            JSONObject response = FundsTransferApiClient.transferFunds(sessionId, customerID, otp, senderAccount, benAcc, amount, transContent);
//
//            if (response != null && response.optJSONObject("body") != null) {
//                JOptionPane.showMessageDialog(otpDialog, "Chuyển tiền thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                otpDialog.dispose();
//                mainFrame.showDashboard();
//            } else {
//                JOptionPane.showMessageDialog(otpDialog, "Chuyển tiền thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        // Xử lý nút "Hủy"
//        btnCancel.addActionListener(e -> otpDialog.dispose());
//
//        otpDialog.setLocationRelativeTo(this);
//        otpDialog.setVisible(true);
//    }
//
//}
