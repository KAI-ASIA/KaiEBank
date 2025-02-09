package com.kaiasia.ui;

import com.kaiasia.model.UserInfo;
import com.kaiasia.napas.NapasApiClient;
import com.kaiasia.t24utils.T24UtilsApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class NapasTransferPanel extends JPanel {
    private JComboBox<String> senderAccountDropdown;
    private JTextField amountField;
    private JTextField benAccField;
    private JTextField transContentField;
    private MainFrame mainFrame;
    private UserInfo userInfo;

    public NapasTransferPanel(MainFrame mainFrame, UserInfo userInfo) {
        this.mainFrame = mainFrame;
        this.userInfo = userInfo;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Tiêu đề
        JLabel titleLabel = new JLabel("Chuyển tiền Napas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Nút "Quay lại"
        JButton btnBack = new JButton("Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 16));
        btnBack.setPreferredSize(new Dimension(140, 48));
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(btnBack, gbc);
        btnBack.addActionListener(e -> {
            System.out.println("DEBUG: Ấn quay lại Dashboard, currentUser = " + mainFrame.getCurrentUser());
            mainFrame.showDashboard();
        });

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 8, 8));

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

        inputPanel.add(createLabel("Ngân hàng:"));
        JLabel bankLabel = new JLabel("970406 - Vietcombank");
        bankLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inputPanel.add(bankLabel);

        inputPanel.add(createLabel("Nội dung chuyển tiền:"));
        transContentField = createLargeTextField();
        inputPanel.add(transContentField);

        // Thêm panel nhập liệu
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(inputPanel, gbc);

        // Nút gửi yêu cầu chuyển tiền
        JButton transferButton = new JButton("Gửi yêu cầu chuyển tiền");
        transferButton.setFont(new Font("Arial", Font.BOLD, 16));
        transferButton.setPreferredSize(new Dimension(240, 48));
        transferButton.addActionListener(e -> handleTransfer());

        gbc.gridy++;
        add(transferButton, gbc);

        // Load tài khoản ngân hàng
        loadUserAccounts();
    }

    // Tạo label
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    // Tạo ô nhập liệu
    private JTextField createLargeTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(240, 38));
        return textField;
    }

    // Load danh sách tài khoản ngân hàng
    private void loadUserAccounts() {
        if (userInfo == null || userInfo.getCustomerID() == null) {
            System.out.println("LỖI: UserInfo không hợp lệ, không thể tải tài khoản!");
            senderAccountDropdown.addItem("Không có tài khoản");
            return;
        }

        String customerId = userInfo.getCustomerID();
        System.out.println("DEBUG: Tải tài khoản cho customerId: " + customerId);

        JSONObject response = T24UtilsApiClient.getCustomerAccounts(customerId);
        if (response != null && response.getJSONObject("body").getString("status").equals("OK")) {
            JSONArray accounts = response.getJSONObject("body").getJSONObject("enquiry").getJSONArray("accounts");
            for (int i = 0; i < accounts.length(); i++) {
                JSONObject account = accounts.getJSONObject(i);
                String accountId = account.getString("accountId");

                senderAccountDropdown.addItem(accountId);
            }
        } else {
            System.out.println("LỖI: Không có tài khoản nào để hiển thị!");
            senderAccountDropdown.addItem("Không có tài khoản");
        }
    }

    // Xử lý chuyển tiền
    private void handleTransfer() {
        String senderAccount = (String) senderAccountDropdown.getSelectedItem();
        String amount = amountField.getText().trim();
        String benAcc = benAccField.getText().trim();
        String transContent = transContentField.getText().trim();

        if (senderAccount == null || senderAccount.equals("Không có tài khoản") || amount.isEmpty() || benAcc.isEmpty() || transContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.out.println("LỖI: Thiếu thông tin nhập vào.");
            return;
        }

        String bankId = "970406";

        JSONObject response = NapasApiClient.transfer(senderAccount, amount, benAcc, bankId, transContent);
        JSONObject transaction = response.optJSONObject("body").optJSONObject("transaction");

        if (response != null) {
            System.out.println("Kết quả giao dịch:\n" + response.toString(2));

            if (transaction != null && "00".equals(transaction.optString("responseCode"))) {
                JOptionPane.showMessageDialog(this, "Chuyển tiền thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển tiền thất bại! Kiểm tra lại thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không nhận được phản hồi từ server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.out.println("Giao dịch thất bại! Không có phản hồi từ server.");
        }
    }
}
