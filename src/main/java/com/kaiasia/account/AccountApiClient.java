package com.kaiasia.account;

import com.kaiasia.config.Config;
import com.kaiasia.ui.LoginPanel;
import com.kaiasia.util.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccountApiClient {
    private static JSONObject createHeader() {
        JSONObject header = new JSONObject();
        header.put("reqType", "REQUEST");
        header.put("api", "ACCOUNT_API");
        header.put("apiKey", Config.ACCOUNT_API_KEY);
        header.put("priority", "1");
        header.put("channel", "API");
        header.put("location", "PC/IOS");
        header.put("requestAPI", "FE API");
        header.put("requestNode", "node 01");

        return header;
    }

    private static JSONObject createRequest(String command, JSONObject enquiry) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("header", createHeader());

        JSONObject body = new JSONObject();
        body.put("command", command);
        body.put("enquiry", enquiry);

        requestJson.put("body", body);
        return requestJson;
    }

    public static JSONObject getCurrentAccount() {
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
            enquiry.put("authenType", "getAccList");
            enquiry.put("sessionId", LoginPanel.userInfoShare.getSessionId());
            enquiry.put("customerID", LoginPanel.userInfoShare.getCustomerID());
            enquiry.put("accountType", "all");



            body.put("enquiry", enquiry);

            requestJson.put("header", header);
            requestJson.put("body", body);

            String response = HttpUtils.postJson(Config.NAPAS_API_URL, requestJson.toString());  // Lấy URL từ Config

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static JSONObject getAccount(String accountId) {
        try {
            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "getCURR_INFO");
            enquiry.put("sessionId", LoginPanel.userInfoShare.getSessionId());
            enquiry.put("accountId", accountId);

            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.ACCOUNT_API_URL, requestJson.toString());

            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse.toString(4));
            return jsonResponse;
        }
        catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }
}

