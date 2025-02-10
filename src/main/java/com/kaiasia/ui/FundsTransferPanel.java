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
import java.util.List;

public class FundsTransferPanel extends JPanel {
    private JComboBox<String> senderAccountDropdown, bankDropdown;
    private JTextField amountField, benAccField, transContentField;
    private JLabel benAccNameLabel;
    private JCheckBox externalTransferCheckbox;
    private MainFrame mainFrame;
    private UserInfo userInfo;

    public FundsTransferPanel(MainFrame mainFrame, UserInfo userInfo) {
        this.mainFrame = mainFrame;
        this.userInfo = userInfo;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Chuyển tiền", SwingConstants.CENTER);
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

        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 8, 8));

        inputPanel.add(createLabel("Tài khoản gửi:"));
        senderAccountDropdown = new JComboBox<>();
        senderAccountDropdown.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(senderAccountDropdown);

        inputPanel.add(createLabel("Số tiền chuyển:"));
        amountField = createLargeTextField();
        inputPanel.add(amountField);

        inputPanel.add(createLabel("Số tài khoản nhận:"));
        benAccField = createLargeTextField();
        inputPanel.add(benAccField);

        inputPanel.add(createLabel("Tên chủ tài khoản:"));
        benAccNameLabel = new JLabel(" ");
        benAccNameLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        benAccNameLabel.setForeground(Color.BLUE);
        inputPanel.add(benAccNameLabel);

        inputPanel.add(createLabel("Nội dung chuyển tiền:"));
        transContentField = createLargeTextField();
        inputPanel.add(transContentField);

        externalTransferCheckbox = new JCheckBox("Chuyển liên ngân hàng");
        externalTransferCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(externalTransferCheckbox);

        bankDropdown = new JComboBox<>();
        bankDropdown.setEnabled(false);
        inputPanel.add(bankDropdown);

        externalTransferCheckbox.addActionListener(e -> bankDropdown.setEnabled(externalTransferCheckbox.isSelected()));

        benAccField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { fetchAccountName(); }
            @Override
            public void removeUpdate(DocumentEvent e) { fetchAccountName(); }
            @Override
            public void changedUpdate(DocumentEvent e) { fetchAccountName(); }
        });

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(inputPanel, gbc);

        JButton transferButton = new JButton("Gửi yêu cầu chuyển tiền");
        transferButton.setFont(new Font("Arial", Font.BOLD, 16));
        transferButton.setPreferredSize(new Dimension(240, 48));
        transferButton.addActionListener(e -> handleTransfer());

        gbc.gridy++;
        add(transferButton, gbc);

        loadUserAccounts();
        loadInterbankList();
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

        for (int i = 0; i < accounts.length(); i++) {
            String accountId = accounts.getJSONObject(i).optString("accountId", "N/A");
            senderAccountDropdown.addItem(accountId);
        }
    }

    private void loadInterbankList() {
        List<String> banks = T24UtilsApiClient.getCachedInterbankList();
        bankDropdown.removeAllItems();

        if (banks != null && !banks.isEmpty()) {
            for (String bank : banks) {
                bankDropdown.addItem(bank);
            }
        } else {
            bankDropdown.addItem("Không có ngân hàng nào");
        }
    }

    private void fetchAccountName() {
        String accountId = benAccField.getText().trim();
        if (accountId.isEmpty()) {
            benAccNameLabel.setText(" ");
            return;
        }
        new Thread(() -> {
            JSONObject response = T24UtilsApiClient.getAccountInfo(accountId);
            if (response != null && "OK".equals(response.getJSONObject("body").optString("status"))) {
                String accountName = response.getJSONObject("body").getJSONObject("enquiry").optString("shortName", "Không tìm thấy");
                benAccNameLabel.setText(accountName);
            } else {
                benAccNameLabel.setText("Không tìm thấy tài khoản");
            }
        }).start();
    }

    private void handleTransfer() {
        String senderAccount = (String) senderAccountDropdown.getSelectedItem();
        String amount = amountField.getText().trim();
        String benAcc = benAccField.getText().trim();
        String transContent = transContentField.getText().trim();
        boolean isExternal = externalTransferCheckbox.isSelected();
        String bankId = isExternal ? (String) bankDropdown.getSelectedItem() : "300";

        if (senderAccount == null || senderAccount.isEmpty() || amount.isEmpty() || benAcc.isEmpty() || transContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showOtpVerificationDialog(senderAccount, benAcc, amount, transContent, bankId, isExternal);
    }

    private void showOtpVerificationDialog(String senderAccount, String benAcc, String amount, String transContent, String bankId, boolean isExternal) {
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
            System.out.println("Debug OTP Response: " + otpResponse.toString(2));
            JOptionPane.showMessageDialog(otpDialog, "OTP đã được gửi qua email!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnConfirm.addActionListener(e -> {
            JSONObject response = FundsTransferApiClient.transferFunds(userInfo.getSessionId(), userInfo.getCustomerID(), txtOtp.getText().trim(), senderAccount, benAcc, bankId, amount, transContent, isExternal);
            JOptionPane.showMessageDialog(this, "Chuyển tiền thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            otpDialog.dispose();
            mainFrame.showDashboard();
        });

        btnCancel.addActionListener(e -> otpDialog.dispose());

        otpDialog.setLocationRelativeTo(mainFrame);
        otpDialog.setVisible(true);
    }
}
