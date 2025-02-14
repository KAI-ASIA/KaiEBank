package com.kaiasia.napas;

import com.kaiasia.config.Config;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class NapasApiClient {

    private static JSONObject createHeader() {
        JSONObject header = new JSONObject();
        header.put("reqType", "REQUEST");
        header.put("api", "NAPAS_API");
        header.put("apiKey", Config.NAPAS_API_KEY);
        header.put("priority", "1");
        header.put("channel", "API");
        header.put("location", "PC/IOS");
        header.put("requestAPI", "FE API");
        header.put("requestNode", "node 01");
        return header;
    }

    public static JSONObject transfer(String senderAccount, String amount, String benAcc, String bankId, String transContent) {
        try {
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

            JSONObject requestJson = new JSONObject();
            requestJson.put("header", createHeader());
            requestJson.put("body", body);

            System.out.println("Gửi request chuyển tiền: " + requestJson.toString(4));
            String response = HttpUtils.postJson(Config.NAPAS_API_URL, requestJson.toString());

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static JSONObject checkAcc(String senderAccount, String senderName, String accountId, String bankId) {
        try {
            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "enqCheckAcc");
            enquiry.put("senderAccount", senderAccount);
            enquiry.put("senderName", senderName);
            enquiry.put("accountId", accountId);
            enquiry.put("bankId", bankId);

            body.put("enquiry", enquiry);

            JSONObject requestJson = new JSONObject();
            requestJson.put("header", createHeader());
            requestJson.put("body", body);

            System.out.println("Gửi request kiểm tra tài khoản: " + requestJson.toString(4));
            String response = HttpUtils.postJson(Config.NAPAS_API_URL, requestJson.toString());

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
