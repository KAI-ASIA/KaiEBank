package com.kaiasia.ebank;

import com.kaiasia.config.Config;
import com.kaiasia.model.UserInfo;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class EbankApiClient {

    private static JSONObject createHeader() {
        JSONObject header = new JSONObject();
        header.put("reqType", "REQUEST");
        header.put("api", "EBANK_API");
        header.put("apiKey", Config.EBANK_API_KEY); // API Key từ Config
        header.put("priority", 1);
        header.put("channel", "API");
        header.put("location", "PC/IOS");
        header.put("requestAPI", "FE API");
        header.put("requestNode", "node 01");
        return header;
    }

    public static JSONObject getAccountEbank(UserInfo userInfo) {
        try {

            JSONObject requestJson = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "EBANK_API");
            header.put("apiKey", Config.EBANK_API_KEY);
            header.put("priority", 1);
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestNode", "node 01");
            header.put("requestAPI", "FE API");

            requestJson.put("header", header);

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "getUSER_PROFILE");
            enquiry.put("sessionId", userInfo.getSessionId());
            enquiry.put("userID", userInfo.getUsername());

            body.put("enquiry", enquiry);
            requestJson.put("body", body);

            System.out.println("DEBUG: Sending request to eBank API: " + requestJson.toString());

            String response = HttpUtils.postJson(Config.EBANK_API_URL, requestJson.toString());

            if (response == null) {
                System.out.println("ERROR: API response is null!");
                return null;
            }

            System.out.println("DEBUG: API Response: " + response);

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
