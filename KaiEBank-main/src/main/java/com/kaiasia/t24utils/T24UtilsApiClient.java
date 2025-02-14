package com.kaiasia.t24utils;

import com.kaiasia.config.Config;
import com.kaiasia.util.HttpUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class T24UtilsApiClient {
    private static List<String> interbankList = new ArrayList<>();

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

    //KAI.API.CUSTOMER.GET.ACC
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

    //KAI.API.ACCOUNT.GET.INFO
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

    // Gọi API lấy danh sách ngân hàng
    public static void loadInterbankList() {
        interbankList.clear();
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("header", createHeader());

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "KAI.API.BANKS");
            enquiry.put("bankCode", "");
            body.put("enquiry", enquiry);

            requestJson.put("body", body);

            String response = HttpUtils.postJson(Config.T24_UTIL_API_URL, requestJson.toString());
            JSONObject responseJson = new JSONObject(response);

            if (responseJson != null && responseJson.getJSONObject("body").getString("status").equals("OK")) {
                JSONArray banks = responseJson.getJSONObject("body").getJSONObject("enquiry").getJSONArray("banks");

                for (int i = 0; i < banks.length(); i++) {
                    JSONObject bank = banks.getJSONObject(i);
                    String bankInfo = bank.getString("bankCode") + " - " + bank.getString("bankName");
                    interbankList.add(bankInfo);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Lấy danh sách ngân hàng
    public static List<String> getCachedInterbankList() {
        return interbankList;
    }
}
