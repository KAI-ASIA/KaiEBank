package com.kaiasia.account;

import com.kaiasia.config.Config;
import com.kaiasia.ui.LoginPanel;
import com.kaiasia.util.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountApiClient {
    public static JSONObject getAccount(String accountId) {
        try {
            JSONObject requestJson = new JSONObject();

            // Tạo Header
            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "ACCOUNT_API");
            header.put("apiKey", Config.ACCOUNT_API_KEY);
            header.put("priority", "1");
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");
            header.put("synasyn", "false");


            // Tạo Body
            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            // Tạo Enquiry
            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "getAccList");
            enquiry.put("sessionId", LoginPanel.userInfoShare.getSessionId());
            enquiry.put("customerID", LoginPanel.userInfoShare.getCustomerID());
            enquiry.put("accountType", "All");

            body.put("enquiry", enquiry);
            requestJson.put("header", header);
            requestJson.put("body", body);


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
