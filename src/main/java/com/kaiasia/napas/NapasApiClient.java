package com.kaiasia.napas;

import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class NapasApiClient {
    private static final String API_URL = "http://14.225.254.212:8083/NAPAS_API/process";
    private static final String API_KEY = "napas861klfoni1ezxl5f2ck771"; // API Key của Napas

    public static JSONObject transfer(String senderAccount, String amount, String benAcc, String bankId, String transContent) {
        try {
            JSONObject requestJson = new JSONObject();

            // Header
            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "NAPAS_API");
            header.put("apiKey", API_KEY);
            header.put("priority", "1");
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");

            // Body
            JSONObject body = new JSONObject();
            body.put("command", "GET_TRANSACTION");

            // Transaction details
            JSONObject transaction = new JSONObject();
            transaction.put("authenType", "getTransFastAcc");
            transaction.put("senderAccount", senderAccount);
            transaction.put("amount", amount);
            transaction.put("ccy", "VND");
            transaction.put("transRef", "FT" + System.currentTimeMillis()); // Sinh mã giao dịch duy nhất
            transaction.put("benAcc", benAcc);
            transaction.put("bankId", bankId);
            transaction.put("transContent", transContent);

            body.put("transaction", transaction);

            // Gộp tất cả vào request JSON
            requestJson.put("header", header);
            requestJson.put("body", body);

            // Gửi request POST đến Napas API
            String response = HttpUtils.postJson(API_URL, requestJson.toString());

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
