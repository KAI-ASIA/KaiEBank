package com.kaiasia.auth;

import com.kaiasia.config.Config;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class AuthApiClient {

    private static String sessionId;
    private static String username;
    private static String userEmail;

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

            System.out.println("Gửi request API: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());

            if (response.isEmpty()) {
                System.err.println("Lỗi: API trả về null hoặc rỗng!");
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                System.err.println("API báo lỗi: " + error.optString("code") + " - " + error.optString("desc"));
                return jsonResponse;
            }

            JSONObject responseBody = jsonResponse.optJSONObject("body");
            if (responseBody == null || !"OK".equals(responseBody.optString("status"))) {
                System.err.println("Đăng nhập thất bại! Trạng thái: " + responseBody.optString("status"));
                return jsonResponse;
            }

            JSONObject enquiryResponse = responseBody.optJSONObject("enquiry");
            if (enquiryResponse == null || !enquiryResponse.has("sessionId")) {
                System.err.println("API không trả về sessionId!");
                return jsonResponse;
            }

            sessionId = enquiryResponse.getString("sessionId");
            AuthApiClient.username = username;
            AuthApiClient.userEmail = enquiryResponse.optString("gmail", "");

            System.out.println("Đăng nhập thành công. Session ID: " + sessionId);
            return jsonResponse;

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi đăng nhập: " + ex.getMessage());
            return null;
        }
    }

    public static JSONObject getOtp() {
        try {
            if (sessionId == null || username == null) {
                System.err.println("Không thể lấy OTP: sessionId hoặc username null. Vui lòng đăng nhập lại.");
                return null;
            }

            // Khởi tạo request JSON
            JSONObject requestJson = new JSONObject();

            // Tạo Header
            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "AUTH_API");
            header.put("apiKey", Config.AUTH_API_KEY);
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
            enquiry.put("authenType", "getOTP");
            enquiry.put("sessionId", sessionId);
            enquiry.put("username", username);
            enquiry.put("gmail", "dn596209@gmail.com");
            enquiry.put("transTime", System.currentTimeMillis());
            enquiry.put("transId", "AUTHEN-getOTP-" + System.currentTimeMillis());
            enquiry.put("transInfo", "Giao dịch lấy mã OTP");

            // Tạo SMS Params
            JSONObject smsParams = new JSONObject();
            smsParams.put("tempId", "OTP");
            smsParams.put("content", "Mã xác thực: {OTP.CODE} có hiệu lực trong vòng 02 phút. KHÔNG gửi mã xác thực cho bất kỳ ai, bao gồm NHÂN VIÊN NGÂN HÀNG để tránh rủi ro. LH: 190000888888");

            enquiry.put("smsParams", smsParams);

            body.put("enquiry", enquiry);

            requestJson.put("header", header);
            requestJson.put("body", body);

            // Debug toàn bộ request JSON
            System.out.println("GỬI REQUEST LẤY OTP");
            System.out.println(requestJson.toString(4));

            // Gửi request
            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());

            // Kiểm tra phản hồi API
            if (response == null || response.isEmpty()) {
                System.err.println("API lấy OTP trả về null hoặc rỗng!");
                return null;
            }

            // Debug phản hồi từ API
            System.out.println("API PHẢN HỒI OTP");

            // Chuyển response thành JSON Object
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse.toString(4));

            // Kiểm tra lỗi trong phản hồi
            if (jsonResponse.has("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                System.err.println("Lỗi khi lấy OTP: " + error.optString("code") + " - " + error.optString("desc"));
                return jsonResponse;
            }

            System.out.println("Lấy OTP thành công!");
            return jsonResponse;

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi lấy OTP: " + ex.getMessage());
            return null;
        }
    }


    public static JSONObject changePassword(String username, String oldPassword, String newPassword, String reNewPassword) {
        try {
            if (sessionId == null) {
                System.err.println("Không thể đổi mật khẩu: sessionId null. Vui lòng đăng nhập lại.");
                return null;
            }

            JSONObject requestJson = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("reqType", "REQUEST");
            header.put("api", "AUTH_API");
            header.put("apiKey", Config.AUTH_API_KEY);
            header.put("priority", 1);
            header.put("channel", "MOBILE");
            header.put("location", "PC/IOS");
            header.put("requestAPI", "FE API");
            header.put("requestNode", "node 01");

            JSONObject body = new JSONObject();
            body.put("command", "GET_ENQUIRY");

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "changePassword");
            enquiry.put("sessionId", sessionId);
            enquiry.put("username", username);
            enquiry.put("oldPassword", oldPassword);
            enquiry.put("newPassword", newPassword);
            enquiry.put("reNewPassword", reNewPassword);
            enquiry.put("transId", "AUTHEN-changePass-" + System.currentTimeMillis());

            body.put("enquiry", enquiry);
            requestJson.put("header", header);
            requestJson.put("body", body);

            System.out.println("Gửi request đổi mật khẩu: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());

            if (response == null || response.isEmpty()) {
                System.err.println("API đổi mật khẩu trả về null hoặc rỗng!");
                return null;
            }

            System.out.println("API phản hồi đổi mật khẩu: " + response);

            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                System.err.println("Lỗi khi đổi mật khẩu: " + error.optString("code") + " - " + error.optString("desc"));
                return jsonResponse;
            }

            System.out.println("Đổi mật khẩu thành công!");
            return jsonResponse;

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi đổi mật khẩu: " + ex.getMessage());
            return null;
        }
    }
}
