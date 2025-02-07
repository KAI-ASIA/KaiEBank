package com.kaiasia.ui;

import com.kaiasia.napas.NapasApiClient;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NapasTransferPanel extends JPanel {

    private JTextField senderAccountField;
    private JTextField amountField;
    private JTextField benAccField;
    private JComboBox<String> bankComboBox;
    private JTextField transContentField;
    private JTextArea resultArea;

    public NapasTransferPanel() {
        setLayout(new BorderLayout());

        // Tiêu đề
        JLabel titleLabel = new JLabel("Chuyển tiền Napas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));

        inputPanel.add(new JLabel("Tài khoản gửi:"));
        senderAccountField = new JTextField();
        inputPanel.add(senderAccountField);

        inputPanel.add(new JLabel("Số tiền chuyển:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Số tài khoản nhận:"));
        benAccField = new JTextField();
        inputPanel.add(benAccField);

        inputPanel.add(new JLabel("Chọn ngân hàng:"));
        String[] banks = {"970406 - Vietcombank", "970407 - Techcombank", "970405 - BIDV", "970403 - Agribank"};
        bankComboBox = new JComboBox<>(banks);
        inputPanel.add(bankComboBox);

        inputPanel.add(new JLabel("Nội dung chuyển tiền:"));
        transContentField = new JTextField();
        inputPanel.add(transContentField);

        // Thêm panel nhập liệu vào
        add(inputPanel, BorderLayout.CENTER);

        // Kết quả giao dịch
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        add(resultScroll, BorderLayout.SOUTH);

        // Panel chứa nút gửi yêu cầu
        JPanel buttonPanel = new JPanel();
        JButton transferButton = new JButton("Gửi yêu cầu chuyển tiền");
        transferButton.setFont(new Font("Arial", Font.BOLD, 16));
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kiểm tra thông tin đầu vào
                String senderAccount = senderAccountField.getText().trim();
                String amount = amountField.getText().trim();
                String benAcc = benAccField.getText().trim();
                String bank = (String) bankComboBox.getSelectedItem();
                String transContent = transContentField.getText().trim();

                if (senderAccount.isEmpty() || amount.isEmpty() || benAcc.isEmpty() || transContent.isEmpty()) {
                    resultArea.setText("Vui lòng điền đầy đủ thông tin.");
                    return;
                }

                // Tách mã ngân hàng từ danh sách
                String bankId = bank.split(" - ")[0];
                System.out.println("Dữ liệu nhập: " + senderAccount + ", " + amount + ", " + benAcc + ", " + bankId + ", " + transContent);

                // Gọi API Napas để thực hiện chuyển tiền
                JSONObject response = NapasApiClient.transfer(senderAccount, amount, benAcc, bankId, transContent);

                if (response != null) {
                    System.out.println("Giao dịch thành công: " + response.toString(2));
                    resultArea.setText("Kết quả giao dịch:\n" + response.toString(2));
                } else {
                    System.out.println("Giao dịch thất bại!");
                    resultArea.setText("Giao dịch thất bại! Không nhận được phản hồi từ server.");
                }
            }
        });

        buttonPanel.add(transferButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
