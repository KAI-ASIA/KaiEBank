package com.kaiasia.fundTransferOut;

import com.kaiasia.config.Config;
import com.kaiasia.model.TransferOut;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class FundTransferOutApiClient (TransferOut transferOut){
    try {
        JSONObject requestJson = new JSONObject();

        // Tạo Header
        JSONObject header = new JSONObject();
        header.put("reqType", "REQUEST");
        header.put("api", "FUNDS_TRANSFER_API");
        header.put("apiKey", Config.FUNDTRANSFERIN_API_KEY);
        header.put("priority", "1");
        header.put("channel", "API");
        header.put("location", "PC/IOS");
        header.put("requestAPI", "FE API");
        header.put("requestNode", "node 01");

        // Tạo Body
        JSONObject body = new JSONObject();
        body.put("command", "GET_TRANSACTION");

        // Tạo Enquiry
        JSONObject transaction = new JSONObject();
        transaction.put("authenType", "KAI.API.FT.IN");
        transaction.put("sessionId",transferIn.getSessionId());
        transaction.put("customerID", transferIn.getCustomerID());
        transaction.put("OTP", transferIn.getOtp());
        transaction.put("transactionId", System.currentTimeMillis());
        transaction.put("debitAccount", "AUTHEN-getOTP-" + System.currentTimeMillis());
        transaction.put("creditAccount", "Giao dịch lấy mã OTP");

        transaction.put("bankId", "203");

        transaction.put("transAmount",transferIn.getAmount());

        transaction.put("transDesc",transferIn.getDesc());

        String response = HttpUtils.postJson(Config.FUNDTRANSFERIN_API_URL, requestJson.toString());

        JSONObject jsonResponse = new JSONObject(response);
        System.out.println( jsonResponse.toString(4));

        return jsonResponse;

    }
        catch (Exception e) {
        e.printStackTrace();
        return null;
    }

}
