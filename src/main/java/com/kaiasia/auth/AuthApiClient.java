package com.kaiasia.auth;

import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class AuthApiClient {
    private static final String API_URL = "http://14.225.254.212:8087/AUTH_API/process";
    private static final String API_KEY = "authq51klfoni1ezxl5f2ckpfx248";

    public static JSONObject login(String username, String password) {
        try {
            // Xây dựng JSON request theo đặc tả API
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "AUTH_API");
            header.put("apiKey", API_KEY);
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

            // Gửi request POST với payload JSON
            String response = HttpUtils.postJson(API_URL, requestJson.toString());

            // Phân tích kết quả trả về
            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;  // Trả về null nếu có lỗi
        }
    }

}
