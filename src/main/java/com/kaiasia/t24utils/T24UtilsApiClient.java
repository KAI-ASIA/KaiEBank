package com.kaiasia.t24utils;

import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

import javax.swing.*;

public class T24UtilsApiClient {
    private static final String API_URL = "http://14.225.254.212:8080/T24_UTIL_API/process";
    private static final String API_KEY = "VDI0X1VUSUxfQVBJ";

    public static JSONObject login(String username, String password) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "T24_UTIL_API");
            header.put("apiKey", API_KEY);
            header.put("priority", "1");
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "KAI.API.AUTHEN.GET.LOGIN");
            enquiry.put("username", username);
            enquiry.put("password", password);
            body.put("enquiry", enquiry);

            requestJson.put("header", header);
            requestJson.put("body", body);

            String response = HttpUtils.postJson(API_URL, requestJson.toString());

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Lấy danh sách tài khoản
    public static JSONObject getCustomerAccounts(String customerId) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "T24_UTIL_API");
            header.put("apiKey", API_KEY);
            header.put("priority", "1");
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "KAI.API.CUSTOMER.GET.ACC");
            enquiry.put("customerId", customerId);
            body.put("enquiry", enquiry);

            requestJson.put("header", header);
            requestJson.put("body", body);

            String response = HttpUtils.postJson(API_URL, requestJson.toString());

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Lấy thông tin chi tiết tài khoản
    public static JSONObject getAccountInfo(String accountId) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "T24_UTIL_API");
            header.put("apiKey", API_KEY);
            header.put("priority", "1");
            header.put("channel", "API");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "KAI.API.ACCOUNT.GET.INFO");
            enquiry.put("accountId", accountId);
            body.put("enquiry", enquiry);

            requestJson.put("header", header);
            requestJson.put("body", body);

            String response = HttpUtils.postJson(API_URL, requestJson.toString());

            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Hiển thị thông tin chi tiết tài khoản khi nhấn vào ">"
    public static void showAccountDetails(String accountId) {
        JSONObject accountInfo = getAccountInfo(accountId);
        if (accountInfo != null && accountInfo.getJSONObject("body").getString("status").equals("OK")) {
            JSONObject detail = accountInfo.getJSONObject("body").getJSONObject("enquiry");

            String message = "<html><b>Tài khoản: </b>" + detail.optString("accountId", "N/A") +
                    "<br><b>Chủ tài khoản: </b>" + detail.optString("shortName", "N/A") +
                    "<br><b>Loại tài khoản: </b>" + detail.optString("accountType", "N/A") +
                    "<br><b>Số dư khả dụng: </b>" + detail.optString("currency", "VND") + " " + detail.optString("avaiBalance", "0") +
                    "<br><b>Trạng thái: </b>" + detail.optString("accountStatus", "N/A") + "</html>";

            JOptionPane.showMessageDialog(null, message, "Chi tiết tài khoản", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Không thể lấy thông tin tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
