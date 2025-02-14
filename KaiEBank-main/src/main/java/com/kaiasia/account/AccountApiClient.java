package com.kaiasia.account;

import com.kaiasia.config.Config;
import com.kaiasia.ui.LoginPanel;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class AccountApiClient {

    // Tạo Header chung cho API
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
        header.put("synasyn", "false");
        return header;
    }

    // Tạo request chung cho API
    private static JSONObject createRequest(String command, JSONObject enquiry) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("header", createHeader());

        JSONObject body = new JSONObject();
        body.put("command", command);
        body.put("enquiry", enquiry);

        requestJson.put("body", body);
        return requestJson;
    }

    // Xử lý phản hồi API
    private static JSONObject parseResponse(String response, String apiType) {
        if (response == null || response.isEmpty()) {
            System.err.println(apiType + " - API trả về null hoặc rỗng!");
            return null;
        }

        JSONObject jsonResponse = new JSONObject(response);
        System.out.println(apiType + " - API Response: " + jsonResponse.toString(4));

        if (jsonResponse.has("error")) {
            JSONObject error = jsonResponse.getJSONObject("error");
            System.err.println(apiType + " - Lỗi API: " + error.optString("code") + " - " + error.optString("desc"));
            return jsonResponse;
        }

        return jsonResponse;
    }

    // getAccList
    public static JSONObject getAccList() {
        try {
            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "getAccList");
            enquiry.put("sessionId", LoginPanel.userInfoShare.getSessionId());
            enquiry.put("customerID", LoginPanel.userInfoShare.getCustomerID());
            enquiry.put("accountType", "All");

            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request lấy danh sách tài khoản: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.ACCOUNT_API_URL, requestJson.toString());
            return parseResponse(response, "GET_ACC_LIST");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi lấy danh sách tài khoản: " + ex.getMessage());
            return null;
        }
    }

    // getCURR_INFO
    public static JSONObject getCurrInfo(String sessionId, String accountId) {
        try {
            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "getCURR_INFO");
            enquiry.put("sessionId", sessionId);
            enquiry.put("accountId", accountId);

            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request lấy thông tin tài khoản: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.ACCOUNT_API_URL, requestJson.toString());
            return parseResponse(response, "GET_CURR_INFO");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi lấy thông tin tài khoản: " + ex.getMessage());
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
