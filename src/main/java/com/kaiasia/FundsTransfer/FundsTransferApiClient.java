package com.kaiasia.FundsTransfer;

import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;
import com.kaiasia.config.Config;

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
                                           String debitAccount, String creditAccount,
                                           String transAmount, String transDesc) {
        try {
            JSONObject transaction = new JSONObject();
            transaction.put("authenType", "KAI.API.FT.IN");
            transaction.put("sessionId", sessionId);
            transaction.put("customerID", customerID);
            transaction.put("company", "VN0010001");
            transaction.put("OTP", otp);
            transaction.put("transactionId", System.currentTimeMillis()); // Tạo transactionId duy nhất
            transaction.put("debitAccount", debitAccount);
            transaction.put("creditAccount", creditAccount);
            transaction.put("bankId", "300");
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
}
