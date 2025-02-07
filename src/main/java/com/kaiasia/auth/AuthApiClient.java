package com.kaiasia.auth;

import com.kaiasia.config.Config;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class AuthApiClient {
    public static JSONObject login(String username, String password) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "AUTH_API");
            header.put("apiKey", Config.AUTH_API_KEY);
            header.put("priority", "1");
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "login");
            enquiry.put("username", username);
            enquiry.put("password", password);
            body.put("enquiry", enquiry);

            requestJson.put("header", header);
            requestJson.put("body", body);

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());  // Lấy URL từ Config

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
