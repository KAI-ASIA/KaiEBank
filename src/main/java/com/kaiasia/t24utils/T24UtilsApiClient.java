package com.kaiasia.t24utils;

import com.kaiasia.config.Config;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

import javax.swing.*;

public class T24UtilsApiClient {

    private static JSONObject createHeader() {
        JSONObject header = new JSONObject();
        header.put("reqType", "REQUEST");
        header.put("api", "T24_UTIL_API");
        header.put("apiKey", Config.T24_UTIL_API_KEY);
        header.put("priority", "1");
        header.put("channel", "API");
        header.put("location", "PC/IOS");
        header.put("requestAPI", "FE API");
        header.put("requestNode", "node 01");
        return header;
    }

//    public static JSONObject login(String username, String password) {
//        try {
//            JSONObject requestJson = new JSONObject();
//            requestJson.put("header", createHeader());
//
//            JSONObject body = new JSONObject();
//            body.put("command", "GET_ENQUIRY");
//
//            JSONObject enquiry = new JSONObject();
//            enquiry.put("authenType", "KAI.API.AUTHEN.GET.LOGIN");
//            enquiry.put("username", username);
//            enquiry.put("password", password);
//            body.put("enquiry", enquiry);
//
//            requestJson.put("body", body);
//
//            String response = HttpUtils.postJson(Config.T24_UTIL_API_URL, requestJson.toString());
//            return new JSONObject(response);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }

    public static JSONObject getCustomerAccounts(String customerId) {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("header", createHeader());

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "KAI.API.CUSTOMER.GET.ACC");
            enquiry.put("customerId", customerId);
            body.put("enquiry", enquiry);

            requestJson.put("body", body);

            String response = HttpUtils.postJson(Config.T24_UTIL_API_URL, requestJson.toString());
            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static JSONObject getAccountInfo(String accountId) {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("header", createHeader());

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "KAI.API.ACCOUNT.GET.INFO");
            enquiry.put("accountId", accountId);
            body.put("enquiry", enquiry);

            requestJson.put("body", body);

            String response = HttpUtils.postJson(Config.T24_UTIL_API_URL, requestJson.toString());
            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

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
