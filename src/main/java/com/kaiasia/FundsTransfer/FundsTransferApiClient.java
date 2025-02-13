package com.kaiasia.FundsTransfer;

import com.kaiasia.napas.NapasApiClient;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;
import com.kaiasia.config.Config;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FundsTransferApiClient {

    private static JSONObject createHeader() {
        JSONObject header = new JSONObject();
        header.put("reqType", "REQUEST");
        header.put("api", "FUNDS_TRANSFER_API");
        header.put("apiKey", Config.FUNDSTRANSFER_API_KEY);
        header.put("priority", "1");
        header.put("channel", "API");
        header.put("location", "PC/IOS");
        header.put("requestAPI", "FE API");
        header.put("requestNode", "node 01");
        return header;
    }

    public static JSONObject transferFunds(String sessionId, String customerID, String otp,
                                           String debitAccount, String creditAccount, String bankId,
                                           String transAmount, String transDesc, boolean isInterbank) {
        try {
            if ("304".equals(bankId)) {
                System.out.println("Đổi BankID từ 304 thành NapasID 970406");
                bankId = "970406";
            }

            // Nếu là giao dịch Napas, gọi FT Out trước
            if ("970406".equals(bankId) && "001".equals(creditAccount)) {
                System.out.println("Giao dịch sẽ được chuyển qua Napas");

                // Gọi FT Out API
                JSONObject ftOutTransaction = new JSONObject();
                ftOutTransaction.put("authenType", "KAI.API.FT.OUT");
                ftOutTransaction.put("sessionId", sessionId);
                ftOutTransaction.put("customerID", customerID);
                ftOutTransaction.put("company", "VN0010001");
                ftOutTransaction.put("OTP", otp);
                ftOutTransaction.put("transactionId", System.currentTimeMillis());
                ftOutTransaction.put("debitAccount", debitAccount);
                ftOutTransaction.put("creditAccount", creditAccount);
                ftOutTransaction.put("bankId", "970406");
                ftOutTransaction.put("transAmount", transAmount);
                ftOutTransaction.put("transDesc", transDesc);

                JSONObject ftOutBody = new JSONObject();
                ftOutBody.put("command", "GET_TRANSACTION");
                ftOutBody.put("transaction", ftOutTransaction);

                JSONObject ftOutRequest = new JSONObject();
                ftOutRequest.put("header", createHeader());
                ftOutRequest.put("body", ftOutBody);

                System.out.println("Gửi request FT Out: " + ftOutRequest.toString(4));

                // Gọi API FT Out
                String ftOutResponse = HttpUtils.postJson(Config.FUNDSTRANSFER_API_URL, ftOutRequest.toString());

                // Nếu FT Out thành công, gọi Napas API
                System.out.println("Gọi Napas API để hoàn tất giao dịch liên ngân hàng");
                JSONObject napasResponse = NapasApiClient.transfer(debitAccount, transAmount, creditAccount, bankId, transDesc);
                System.out.println("Response từ Napas API: " + napasResponse.toString(4));

                return napasResponse;  // Trả về phản hồi từ Napas API
            }

            // Nếu không phải Napas, xử lý như bình thường
            JSONObject transaction = new JSONObject();
            transaction.put("authenType", isInterbank ? "KAI.API.FT.OUT" : "KAI.API.FT.IN");
            transaction.put("sessionId", sessionId);
            transaction.put("customerID", customerID);
            transaction.put("company", "VN0010001");
            transaction.put("OTP", otp);
            transaction.put("transactionId", System.currentTimeMillis());
            transaction.put("debitAccount", debitAccount);
            transaction.put("creditAccount", creditAccount);
            transaction.put("bankId", isInterbank ? bankId : "300"); // "300" là mã nội bộ
            transaction.put("transAmount", transAmount);
            transaction.put("transDesc", transDesc);

            JSONObject body = new JSONObject();
            body.put("command", "GET_TRANSACTION");
            body.put("transaction", transaction);

            JSONObject requestJson = new JSONObject();
            requestJson.put("header", createHeader());
            requestJson.put("body", body);

            System.out.println("Gửi request chuyển tiền: " + requestJson.toString(4));

            // Gửi request API
            String response = HttpUtils.postJson(Config.FUNDSTRANSFER_API_URL, requestJson.toString());
            return new JSONObject(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi gửi yêu cầu chuyển tiền: " + ex.getMessage());
            return null;
        }
    }

    public static String formatCurrency(double amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
        return formatter.format(amount);
    }

}
