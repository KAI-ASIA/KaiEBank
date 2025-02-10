package com.kaiasia.ui;

import com.kaiasia.account.AccountApiClient;
import com.kaiasia.auth.AuthApiClient;
import com.kaiasia.fundTransferIn.FundTransferInApiClient;
import com.kaiasia.model.Error.ErrorInfo;
import com.kaiasia.model.TransferIn;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FundTransferInPanel extends JPanel {
    private JTextField debitAccount;
    private JTextField creditAccount;
    private JTextField nameCreditAccount;
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



        // Số tài khoản nhận
        inputPanel.add(new JLabel("Số tài khoản nhận:"));
        creditAccount = new JTextField();
        inputPanel.add(creditAccount);

        //ten tai khoan nhan
        inputPanel.add(new JLabel("tên tài khoản nhận:"));
        nameCreditAccount = new JTextField();
        inputPanel.add(nameCreditAccount);
        nameCreditAccount.setEditable(false);


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

        //khi người dùng điền xong số tài khoản người nhận
        creditAccount.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {

                String name=callApiAccount();
                if(name!=null){
                    nameCreditAccount.setText(name);
                }

            }
        });
        // Cấu hình nút xác nhận
        btnSubmit.addActionListener(e -> {
            // Xử lý sự kiện chuyển tiền
            if(creditAccount.getText().isEmpty()||transAmount.getText().isEmpty()||transDesc.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng điền đầy đủ thông tin trước khi chuyển tiền!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
            else {
                // Mở màn hình yêu cầu mã OTP
                    showOtpDialog();
            }



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
            callApiGetOtp();
        });

        // Cấu hình nút "Xác nhận OTP"
        btnConfirmOtp.addActionListener(e -> {
            if(otpField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng điền đầy đủ thông tin trước khi chuyển tiền!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(callApiConfirmOtp()){
                transferIn = new TransferIn.TransferBuilder()
                    .setCreditAccount(creditAccount.getText())
                    .setDebitAccount(DashboardPanel.accountInfo.getAccountID())
                    .setAmount(Integer.parseInt(transAmount.getText()))
                    .setSessionId(LoginPanel.userInfoShare.getSessionId())
                    .setCustomerID(DashboardPanel.accountInfo.getCustomerID())
                    .setDesc(transDesc.getText())
                        .setOtp(otpField.getText())
                    .build();
                callFundTransferIn();
            }

        });

        otpDialog.setLocationRelativeTo(mainFrame);  // Đặt vị trí dialog ở trung tâm màn hình chính
        otpDialog.setVisible(true);  // Hiển thị dialog
    }



    private String callApiAccount(){
        ErrorInfo error=null;
        JSONObject response= AccountApiClient.getAccount(creditAccount.getText());
        if (response==null){
            System.out.println("loi");
            return null;
        }
        JSONObject errorResponse=response.optJSONObject("error");
        if (error!=null){
            error=new ErrorInfo(errorResponse.optString("code"),errorResponse.optString("desc"));
            JOptionPane.showMessageDialog(this, error.getCode()+" : "+error.getDesc(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        JSONObject body=response.optJSONObject("body");
        if(body==null|| !"OK".equals(body.optString("status"))){
            JOptionPane.showMessageDialog(this,"không thể tìm" , "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        JSONObject enquiry=body.optJSONObject("enquiry");
        if (enquiry==null) {
            JOptionPane.showMessageDialog(this, "lỗi không tìm được tên tài khoản", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        System.out.println("đã tìm được tên người nhận");
        return enquiry.optString("shortName");

    }

    private void callApiGetOtp(){
        ErrorInfo error=null;
        JSONObject response= AuthApiClient.getOtp();
        if (response==null){
            System.out.println("loi");
        }
        JSONObject errorResponse=response.optJSONObject("error");
        if (error!=null){
            error=new ErrorInfo(errorResponse.optString("code"),errorResponse.optString("desc"));
            JOptionPane.showMessageDialog(this, error.getCode()+" : "+error.getDesc(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject body=response.optJSONObject("body");
        if(body==null|| !"OK".equals(body.optString("status"))){
            JOptionPane.showMessageDialog(this,"không thể lấy mã otp" , "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject enquiry=body.optJSONObject("enquiry");
        if (enquiry==null) {
            JOptionPane.showMessageDialog(this, "lỗi không lấy được mã otp", "Lỗi", JOptionPane.ERROR_MESSAGE);

            return;
        }

        JOptionPane.showMessageDialog(this, "lấy mã opt thành công", "Lỗi", JOptionPane.ERROR_MESSAGE);

    }


    private boolean callApiConfirmOtp(){
        ErrorInfo error=null;
        JSONObject response= AuthApiClient.confirmOtp();
        if (response==null){
            System.out.println("lỗi ko call được api");
            return false;
        }
        JSONObject errorResponse=response.optJSONObject("error");
        if (error!=null){
            error=new ErrorInfo(errorResponse.optString("code"),errorResponse.optString("desc"));
            JOptionPane.showMessageDialog(this, error.getCode()+" : "+error.getDesc(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JSONObject body=response.optJSONObject("body");
        if(body==null|| !"OK".equals(body.optString("status"))){
            JOptionPane.showMessageDialog(this,"không thể xác thực" , "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JSONObject enquiry=body.optJSONObject("enquiry");
        if (enquiry==null) {
            JOptionPane.showMessageDialog(this, "không thể xác thực", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JOptionPane.showMessageDialog(this, "xác thực opt thành công", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return true;

    }
    public void callFundTransferIn(){
        ErrorInfo error=null;
        JSONObject response= FundTransferInApiClient.getFundTransferIn(transferIn);
        if (response==null){
            System.out.println("lỗi ko call được api");
            return;
        }
        JSONObject errorResponse=response.optJSONObject("error");
        if (error!=null){
            error=new ErrorInfo(errorResponse.optString("code"),errorResponse.optString("desc"));
            JOptionPane.showMessageDialog(this, error.getCode()+" : "+error.getDesc(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject body=response.optJSONObject("body");
        if(body==null|| !"OK".equals(body.optString("status"))){
            JOptionPane.showMessageDialog(this,"không thể chuyển tiền" , "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject transaction=body.optJSONObject("enquiry");
        if (transaction==null) {
            JOptionPane.showMessageDialog(this, "không thể chuyển tiền", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "chuyển tiền thành công", "", JOptionPane.INFORMATION_MESSAGE);
        return;


    }
}
