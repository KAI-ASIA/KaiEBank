package com.kaiasia.napas;

import com.kaiasia.config.Config;
import com.kaiasia.model.NapasInfo;
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

            String response = HttpUtils.postJson(Config.NAPAS_API_URL, requestJson.toString());  // Lấy URL từ Config

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static JSONObject getInterBankAccount(NapasInfo napasInfo) {
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
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "enqCheckAcc");
            enquiry.put("senderAccount", napasInfo.getSenderAccount());
            enquiry.put("senderName", napasInfo.getSenderName());
            enquiry.put("accountID", napasInfo.getAccountId());
            enquiry.put("bankID", napasInfo.getBankId());


            body.put("transaction", enquiry);

            requestJson.put("header", header);
            requestJson.put("body", body);

            String response = HttpUtils.postJson(Config.NAPAS_API_URL, requestJson.toString());  // Lấy URL từ Config

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
