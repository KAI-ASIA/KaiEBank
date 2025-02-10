package com.kaiasia.customer;

import com.kaiasia.config.Config;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class CustomerApiClient {

    private static JSONObject createHeader() {
        JSONObject header = new JSONObject();
        header.put("reqType", "REQUEST");
        header.put("api", "CUSTOMER_API");
        header.put("apiKey", Config.CUSTOMER_API_KEY);
        header.put("priority", "1");
        header.put("channel", "API");
        header.put("location", "PC/IOS");
        header.put("requestAPI", "FE API");
        header.put("requestNode", "node 01");
        header.put("synasyn", "false");
        return header;
    }

    public static JSONObject getCustomerInfo(String sessionId, String customerId) {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("header", createHeader());

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "getCUSTINFO");
            enquiry.put("sessionId", sessionId);
            enquiry.put("customerID", customerId);
            body.put("enquiry", enquiry);

            requestJson.put("body", body);

            String response = HttpUtils.postJson(Config.CUSTOMER_API_URL, requestJson.toString());
            return new JSONObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
