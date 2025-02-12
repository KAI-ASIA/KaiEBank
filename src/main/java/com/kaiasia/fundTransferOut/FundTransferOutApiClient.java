package com.kaiasia.fundTransferOut;

import com.kaiasia.config.Config;
import com.kaiasia.model.TransferOut;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class FundTransferOutApiClient {

    public static JSONObject fundTransferOut(TransferOut transferOut) {
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
            transaction.put("sessionId",transferOut.getSessionId());
            transaction.put("customerID", transferOut.getCustomerID());
            transaction.put("OTP", transferOut.getOTP());
            transaction.put("transactionId", System.currentTimeMillis());
            transaction.put("debitAccount", transferOut.getDebitAccount());
            transaction.put("creditAccount", transferOut.getCreditAccount());

            transaction.put("bankId", transferOut.getBankId());

            transaction.put("transAmount",transferOut.getTransAmount());

            transaction.put("transDesc",transferOut.getTransDesc());

            System.out.println("gui request: "+ requestJson.toString(4));

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

}
