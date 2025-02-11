package com.kaiasia.ui;

import com.kaiasia.account.AccountApiClient;
import com.kaiasia.auth.AuthApiClient;
import com.kaiasia.fundTransferIn.FundTransferInApiClient;
import com.kaiasia.model.Error.ErrorInfo;
import com.kaiasia.model.NapasInfo;
import com.kaiasia.model.TransferIn;
import com.kaiasia.model.UserInfo;
import com.kaiasia.napas.NapasApiClient;
import com.kaiasia.t24utils.T24UtilsApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class NapasTransferPanel extends JPanel {
    private JComboBox<String> senderAccountDropdown;
    private JTextField amountField;
    private JTextField benAccField;
    private JTextField transContentField;
    private JTextField nameBenAccField;
    private JComboBox<String> bankComboBox;
    private MainFrame mainFrame;
    private UserInfo userInfo;
    private Vector<String> listBank=new Vector<>();
    private NapasInfo napasInfo;

    public NapasTransferPanel(MainFrame mainFrame, UserInfo userInfo) {
        this.mainFrame = mainFrame;
        this.userInfo = userInfo;

        // call api
        callT24ApiGetBank();

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
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 8, 8));

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

        inputPanel.add(createLabel("Tên tài khoản nhận:"));
        nameBenAccField = createLargeTextField();
        inputPanel.add(nameBenAccField);
        nameBenAccField.setEditable(false);





// Tạo JComboBox
        bankComboBox = new JComboBox<>(listBank);
        bankComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

// Thêm vào panel
        inputPanel.add(new JLabel("Tên ngân hàng:"));
        inputPanel.add(bankComboBox);


        inputPanel.add(createLabel("Nội dung chuyển tiền:"));
        transContentField = createLargeTextField();
        inputPanel.add(transContentField);

        //xử lí khi nhập xong số tài khoản người nhận
        benAccField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                //gọi api Napas1
                String bankId= Arrays.stream(((String)bankComboBox.getSelectedItem()).split(" "))
                        .findFirst().orElse("");
               try {
                   napasInfo=new NapasInfo.Builder()
                           .senderAccount((String)senderAccountDropdown.getSelectedItem())
                           .senderName(LoginPanel.ebankInfo.getName())
                           .accountId(benAccField.getText())
                           .bankId(bankId)
                           .build();

                   nameBenAccField.setText(callApigetInterBankAccount());

               }
               catch (Exception ex) {
                   ex.printStackTrace();
               }

            }
        });
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

        showOtpDialog();

//        String bankId = "970406";
//
//        JSONObject response = NapasApiClient.transfer(senderAccount, amount, benAcc, bankId, transContent);
//        JSONObject transaction = response.optJSONObject("body").optJSONObject("transaction");
//
//        if (response != null) {
//            System.out.println("Kết quả giao dịch:\n" + response.toString(2));
//
//            if (transaction != null && "00".equals(transaction.optString("responseCode"))) {
//                JOptionPane.showMessageDialog(this, "Chuyển tiền thành công! Nhấn OK để trờ về màn hình chính", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                mainFrame.showDashboard();
//            } else {
//                JOptionPane.showMessageDialog(this, "Chuyển tiền thất bại! Kiểm tra lại thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Không nhận được phản hồi từ server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            System.out.println("Giao dịch thất bại! Không có phản hồi từ server.");
//        }
    }
    private void callT24ApiGetBank(){
        ErrorInfo error=null;
        JSONObject response= T24UtilsApiClient.getListBank();

        if (response==null){
            System.out.println("loi");
            return ;
        }
        JSONObject errorResponse=response.optJSONObject("error");
        if (error!=null){
            error=new ErrorInfo(errorResponse.optString("code"),errorResponse.optString("desc"));
            JOptionPane.showMessageDialog(this, error.getCode()+" : "+error.getDesc(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return ;
        }

        JSONObject body=response.optJSONObject("body");
        if(body==null|| !"OK".equals(body.optString("status"))){
            JOptionPane.showMessageDialog(this,"không thể tìm" , "Lỗi", JOptionPane.ERROR_MESSAGE);
            return ;
        }

        JSONObject enquiry=body.optJSONObject("enquiry");
        if (enquiry==null) {
            JOptionPane.showMessageDialog(this, "lỗi không tìm danh sach ngan hang", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("đã tìm thấy danh sách ngân hàng");
        JSONArray banksArray=enquiry.optJSONArray("banks");

        for(int i=0;i<banksArray.length();i++){
            JSONObject bankObject = banksArray.getJSONObject(i);
            String bankCode = bankObject.getString("bankCode");
            String bankName = bankObject.getString("bankName");
            listBank.add(bankCode + " - " + bankName);
        }

    }
    private String callApigetInterBankAccount(){
        ErrorInfo error=null;
        JSONObject response= NapasApiClient.getInterBankAccount(napasInfo);

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
            JOptionPane.showMessageDialog(this, "lỗi không tìm ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return enquiry.optString("accountName");
    }
    //màn hình lấy mã otp
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
            if(otpField.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "vui lòng điền mã otp ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(callApiConfirmOtp(otpField.getText())){

            }

        });

        otpDialog.setLocationRelativeTo(mainFrame);  // Đặt vị trí dialog ở trung tâm màn hình chính
        otpDialog.setVisible(true);  // Hiển thị dialog
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

        JOptionPane.showMessageDialog(this, "lấy mã opt thành công", "Lỗi", JOptionPane.INFORMATION_MESSAGE);

    }


    private boolean callApiConfirmOtp(String otp){
        ErrorInfo error=null;
        JSONObject response= AuthApiClient.confirmOtp(otp);
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
}
