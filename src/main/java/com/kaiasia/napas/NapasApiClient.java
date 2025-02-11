package com.kaiasia.napas;

import com.kaiasia.config.Config;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class NapasApiClient {
    public static JSONObject transfer(String senderAccount, String amount, String benAcc, String bankId, String transContent) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "NAPAS_API");
            header.put("apiKey", Config.NAPAS_API_KEY);  // Lấy từ Config
            header.put("priority", "1");
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");

            JSONObject body = new JSONObject();
            body.put("command", "GET_TRANSACTION");

            JSONObject transaction = new JSONObject();
            transaction.put("authenType", "getTransFastAcc");
            transaction.put("senderAccount", senderAccount);
            transaction.put("amount", amount);
            transaction.put("ccy", "VND");
            transaction.put("transRef", "FT" + System.currentTimeMillis());
            transaction.put("benAcc", benAcc);
            transaction.put("bankId", bankId);
            transaction.put("transContent", transContent);

            body.put("transaction", transaction);

            requestJson.put("header", header);
            requestJson.put("body", body);

            System.out.println("Gửi request chuyển tiền: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.NAPAS_API_URL, requestJson.toString());  // Lấy URL từ Config

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
