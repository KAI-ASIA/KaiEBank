package com.kaiasia.ui;

import com.kaiasia.auth.AuthApiClient;
import com.kaiasia.fundTransferIn.FundTransferInApiClient;
import com.kaiasia.model.TransferIn;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class FundTransferInPanel extends JPanel {
    private JTextField debitAccount;
    private JTextField creditAccount;
    private JTextField transAmount;
    private JTextField transDesc;
    private MainFrame mainFrame;
    public static TransferIn transferIn;

    public FundTransferInPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // Thiết lập layout chính
        setLayout(new BorderLayout());

        // Tiêu đề
        JLabel lbTitle = new JLabel("Chuyển tiền nội bộ", SwingConstants.CENTER);
        lbTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        lbTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Cách khoảng trên và dưới tiêu đề
        add(lbTitle, BorderLayout.NORTH);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10)); // 6 hàng và 2 cột, cách nhau 10px

        // Số tài khoản gửi
        inputPanel.add(new JLabel("Số tài khoản gửi:"));
        debitAccount = new JTextField();
        inputPanel.add(debitAccount);

        // Số tài khoản nhận
        inputPanel.add(new JLabel("Số tài khoản nhận:"));
        creditAccount = new JTextField();
        inputPanel.add(creditAccount);

        // Số tiền chuyển
        inputPanel.add(new JLabel("Số tiền chuyển:"));
        transAmount = new JTextField();
        inputPanel.add(transAmount);

        // Nội dung
        inputPanel.add(new JLabel("Nội dung chuyển tiền:"));
        transDesc = new JTextField();
        inputPanel.add(transDesc);

        // Nút xác nhận chuyển tiền
        JButton btnSubmit = new JButton("Xác nhận chuyển tiền");
        inputPanel.add(new JLabel()); // Khoảng trống cho hàng cuối
        inputPanel.add(btnSubmit);

        // Nút quay lại
        JButton btnBack = new JButton("Quay lại");
        inputPanel.add(new JLabel()); // Khoảng trống
        inputPanel.add(btnBack);

        // Thêm input panel vào BorderLayout.CENTER
        add(inputPanel, BorderLayout.CENTER);

        // Thiết lập kích thước nút xác nhận nhỏ
        btnSubmit.setPreferredSize(new Dimension(150, 30));
        btnBack.setPreferredSize(new Dimension(150, 30));  // Kích thước nút quay lại

        // Cấu hình nút xác nhận
        btnSubmit.addActionListener(e -> {
            // Xử lý sự kiện chuyển tiền

//            transferIn = new TransferIn.TransferBuilder()
//                    .setCreditAccount(creditAccount.getText())
//                    .setDebitAccount(DashboardPanel.accountInfo.getAccountID())
//                    .setAmount(Integer.parseInt(transAmount.getText()))
//                    .setSessionId(LoginPanel.userInfoShare.getSessionId())
//                    .setCustomerID(DashboardPanel.accountInfo.getCustomerID())
//                    .setDesc(transDesc.getText())
//                    .build();

            // Mở màn hình yêu cầu mã OTP
            showOtpDialog();
        });

        // Cấu hình nút quay lại
        btnBack.addActionListener(e -> {

        });
    }

    private void showOtpDialog() {
        // Tạo một dialog yêu cầu người dùng nhập OTP
        JDialog otpDialog = new JDialog(mainFrame, "Nhập mã OTP", true);
        otpDialog.setLayout(new FlowLayout());
        otpDialog.setSize(300, 200);

        // Tạo label và trường nhập OTP
        JLabel otpLabel = new JLabel("Nhập mã OTP:");
        JTextField otpField = new JTextField(10);
        JButton btnGetOtp = new JButton("Lấy mã OTP");
        JButton btnConfirmOtp = new JButton("Xác nhận OTP");

        otpDialog.add(otpLabel);
        otpDialog.add(otpField);
        otpDialog.add(btnGetOtp);
        otpDialog.add(btnConfirmOtp);

        // Cấu hình nút "Lấy mã OTP"
        btnGetOtp.addActionListener(e -> {
            String debitAcc = debitAccount.getText().trim();
            if (!debitAcc.isEmpty()) {
                // Gọi API hoặc logic lấy mã OTP
                System.out.println("Đang lấy mã OTP cho tài khoản: " + debitAcc);
                JSONObject response= AuthApiClient.getOtp();

                if(response==null){
                    JOptionPane.showMessageDialog(otpDialog, "không lấy được mã otp");
                }
                JOptionPane.showMessageDialog(otpDialog, "Mã OTP đã được gửi!");

            } else {
                JOptionPane.showMessageDialog(otpDialog, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cấu hình nút "Xác nhận OTP"
        btnConfirmOtp.addActionListener(e -> {
            String otp = otpField.getText().trim();
            if (!otp.isEmpty()) {
                // Xử lý mã OTP và hoàn tất giao dịch
                System.out.println("Mã OTP xác nhận: " + otp);
                otpDialog.dispose(); // Đóng dialog OTP

                // gọi api chuyển tiền
                submitTransferIn();
            } else {
                JOptionPane.showMessageDialog(otpDialog, "Vui lòng nhập mã OTP.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        otpDialog.setLocationRelativeTo(mainFrame);  // Đặt vị trí dialog ở trung tâm màn hình chính
        otpDialog.setVisible(true);  // Hiển thị dialog
    }

    private void submitTransferIn() {
        // Xử lý thực hiện chuyển tiền sau khi OTP hợp lệ
        JSONObject response= FundTransferInApiClient.getFundTransferIn(transferIn);
        if (response==null){
            System.out.println("Giao dịch chuyển tiền thất bại.");
        }
        System.out.println("Giao dịch chuyển tiền đã được xác nhận và hoàn tất.");
    }
}
