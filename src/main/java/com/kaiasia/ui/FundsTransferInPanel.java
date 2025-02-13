package com.kaiasia.ui;

import com.kaiasia.FundsTransfer.FundsTransferApiClient;
import com.kaiasia.auth.AuthApiClient;
import com.kaiasia.model.UserInfo;
import com.kaiasia.t24utils.T24UtilsApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.kaiasia.FundsTransfer.FundsTransferApiClient.formatCurrency;

public class FundsTransferInPanel extends JPanel {
    private JComboBox<String> senderAccountDropdown;
    private JTextField amountField, benAccField, transContentField;
    private JLabel benAccNameLabel, balanceLabel;
    private MainFrame mainFrame;
    private UserInfo userInfo;
    private Map<String, String> accountMap = new HashMap<>();

    private static final String INTERNAL_BANK_ID = "300"; // Mặc định chuyển nội bộ

    public FundsTransferInPanel(MainFrame mainFrame, UserInfo userInfo) {
        this.mainFrame = mainFrame;
        this.userInfo = userInfo;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Chuyển tiền nội bộ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        JButton btnBack = new JButton("Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 16));
        btnBack.setPreferredSize(new Dimension(140, 48));
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(btnBack, gbc);
        btnBack.addActionListener(e -> mainFrame.showDashboard());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));

        inputPanel.add(createLabel("Tài khoản gửi:"));
        senderAccountDropdown = new JComboBox<>();
        senderAccountDropdown.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(senderAccountDropdown);

        senderAccountDropdown.addActionListener(e -> fetchBalance());

        inputPanel.add(createLabel("Số dư khả dụng:"));
        balanceLabel = new JLabel(" ");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        balanceLabel.setForeground(Color.RED);
        inputPanel.add(balanceLabel);

        inputPanel.add(createLabel("Số tài khoản nhận:"));
        benAccField = createLargeTextField();
        inputPanel.add(benAccField);

        inputPanel.add(createLabel("Tên chủ tài khoản:"));
        benAccNameLabel = new JLabel(" ");
        benAccNameLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        benAccNameLabel.setForeground(Color.BLUE);
        inputPanel.add(benAccNameLabel);

        inputPanel.add(createLabel("Số tiền chuyển:"));
        amountField = createLargeTextField();
        inputPanel.add(amountField);

        inputPanel.add(createLabel("Nội dung chuyển tiền:"));
        transContentField = createLargeTextField();
        inputPanel.add(transContentField);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(inputPanel, gbc);

        benAccField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { fetchAccountName(); }
            @Override
            public void removeUpdate(DocumentEvent e) { fetchAccountName(); }
            @Override
            public void changedUpdate(DocumentEvent e) { fetchAccountName(); }
        });

        JButton transferButton = new JButton("Gửi yêu cầu chuyển tiền");
        transferButton.setFont(new Font("Arial", Font.BOLD, 16));
        transferButton.setPreferredSize(new Dimension(240, 48));
        transferButton.addActionListener(e -> handleTransfer());

        gbc.gridy++;
        add(transferButton, gbc);

        loadUserAccounts();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private JTextField createLargeTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(240, 38));
        return textField;
    }

    private void loadUserAccounts() {
        String customerId = userInfo.getCustomerID();
        JSONObject response = T24UtilsApiClient.getCustomerAccounts(customerId);

        if (response == null) {
            System.out.println("API trả về NULL, không thể lấy tài khoản!");
            return;
        }

        JSONObject body = response.optJSONObject("body");
        if (body == null || !"OK".equals(body.optString("status"))) {
            System.out.println("Lỗi phản hồi từ API: " + response.toString(4));
            return;
        }

        JSONObject enquiry = body.optJSONObject("enquiry");
        if (enquiry == null || !enquiry.has("accounts")) {
            System.out.println("Không tìm thấy danh sách tài khoản!");
            return;
        }

        JSONArray accounts = enquiry.optJSONArray("accounts");
        if (accounts == null || accounts.length() == 0) {
            System.out.println("API không trả về tài khoản nào!");
            return;
        }

        System.out.println("Danh sách tài khoản lấy được: " + accounts.toString(4));

        senderAccountDropdown.removeAllItems();
        accountMap.clear();

        for (int i = 0; i < accounts.length(); i++) {
            JSONObject account = accounts.getJSONObject(i);
            String accountId = account.optString("accountId", "N/A");
            String altAccount = account.optString("altAccount", "N/A");

            senderAccountDropdown.addItem(accountId);
            accountMap.put(altAccount, accountId); // Map từ altAccount → accountId
            accountMap.put(accountId, accountId);  // Map từ accountId → accountId
        }

        if (senderAccountDropdown.getItemCount() > 0) {
            senderAccountDropdown.setSelectedIndex(0); // Chọn tài khoản đầu tiên
            fetchBalance(); // Gọi để hiển thị số dư
        }
    }

    private void fetchAccountName() {
        String inputAccount = benAccField.getText().trim();
        if (inputAccount.isEmpty()) {
            benAccNameLabel.setText(" ");
            return;
        }

        // Lấy altAccount hoặc accountId nếu có
        String altAccount = accountMap.getOrDefault(inputAccount, inputAccount);

        // Kiểm tra tài khoản nội bộ
        new Thread(() -> {
            JSONObject response = T24UtilsApiClient.getAccountInfo(altAccount);
            if (response != null && "OK".equals(response.getJSONObject("body").optString("status"))) {
                String accountName = response.getJSONObject("body").getJSONObject("enquiry").optString("shortTitle", "Không tìm thấy");
                SwingUtilities.invokeLater(() -> benAccNameLabel.setText(accountName));
            }
        }).start();
    }

    private void fetchBalance() {
        String selectedAccount = (String) senderAccountDropdown.getSelectedItem();
        if (selectedAccount == null || selectedAccount.isEmpty()) {
            balanceLabel.setText("Không xác định");
            return;
        }

        new Thread(() -> {
            JSONObject response = T24UtilsApiClient.getAccountInfo(selectedAccount);
            if (response != null && "OK".equals(response.getJSONObject("body").optString("status"))) {
                String balance = response.getJSONObject("body").getJSONObject("enquiry").optString("avaiBalance", "0");
                try {
                    double balanceValue = Double.parseDouble(balance);
                    String formattedBalance = formatCurrency(balanceValue);
                    SwingUtilities.invokeLater(() -> balanceLabel.setText(formattedBalance + " VND"));
                } catch (NumberFormatException e) {
                    SwingUtilities.invokeLater(() -> balanceLabel.setText("Không xác định"));
                }
            }
        }).start();
    }

    private void handleTransfer() {
        String senderAccount = (String) senderAccountDropdown.getSelectedItem();
        String benAcc = benAccField.getText().trim();
        String amount = amountField.getText().trim();
        String transContent = transContentField.getText().trim();

        if (senderAccount == null || senderAccount.isEmpty() || benAcc.isEmpty() || amount.isEmpty() || transContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject sessionResponse = AuthApiClient.takeSession(userInfo.getSessionId());
        if (sessionResponse == null || !"OK".equals(sessionResponse.optJSONObject("body").optString("status"))) {
            JOptionPane.showMessageDialog(this, "Lỗi xác thực phiên làm việc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showOtpVerificationDialog(senderAccount, benAcc, amount, transContent);
    }

    private void showOtpVerificationDialog(String senderAccount, String benAcc, String amount, String transContent) {
        JDialog otpDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận OTP", true);
        otpDialog.setSize(350, 250);
        otpDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblOtp = new JLabel("Nhập mã OTP:");
        JTextField txtOtp = new JTextField(10);

        JButton btnGetOtp = new JButton("Lấy OTP");
        JButton btnConfirm = new JButton("Xác nhận");
        JButton btnCancel = new JButton("Hủy");

        gbc.gridx = 0;
        gbc.gridy = 0;
        otpDialog.add(lblOtp, gbc);
        gbc.gridx = 1;
        otpDialog.add(txtOtp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        otpDialog.add(btnGetOtp, gbc);
        gbc.gridx = 1;
        otpDialog.add(btnConfirm, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        otpDialog.add(btnCancel, gbc);

        btnGetOtp.addActionListener(e -> {
            JSONObject otpResponse = AuthApiClient.getOtp();

            if (otpResponse == null) {
                JOptionPane.showMessageDialog(otpDialog, "Lỗi: Không nhận được phản hồi từ server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (otpResponse.has("error")) {
                JSONObject error = otpResponse.getJSONObject("error");
                String errorMessage = error.optString("desc", "Đã xảy ra lỗi khi lấy OTP!");
                JOptionPane.showMessageDialog(otpDialog, "Lỗi: " + errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(otpDialog, "OTP đã được gửi qua email!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnConfirm.addActionListener(e -> {
            String otpCode = txtOtp.getText().trim();
            if (otpCode.isEmpty()) {
                JOptionPane.showMessageDialog(otpDialog, "Vui lòng nhập mã OTP!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

//            JSONObject otpConfirmResponse = AuthApiClient.confirmOtp(userInfo.getSessionId(), userInfo.getUsername(), otpCode);
//            if (otpConfirmResponse == null || !"SUCCESS".equals(otpConfirmResponse.optString("status"))) {
//                JOptionPane.showMessageDialog(otpDialog, "Xác thực OTP thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }

            JSONObject transferResponse = FundsTransferApiClient.transferFunds(userInfo.getSessionId(), userInfo.getCustomerID(), otpCode,
                    senderAccount, benAcc, INTERNAL_BANK_ID, amount, transContent, false
            );

            if (transferResponse == null) {
                JOptionPane.showMessageDialog(otpDialog, "Lỗi: Không nhận được phản hồi từ hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(otpDialog, "Giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            otpDialog.dispose();
            mainFrame.showDashboard();
        });

        btnCancel.addActionListener(e -> otpDialog.dispose());

        otpDialog.setLocationRelativeTo(mainFrame);
        otpDialog.setVisible(true);
    }
}
