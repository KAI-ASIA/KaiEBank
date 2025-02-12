package com.kaiasia.auth;

import com.kaiasia.config.Config;
import com.kaiasia.customer.CustomerApiClient;
import com.kaiasia.model.ResetPasswordInfo;
import com.kaiasia.model.UserInfo;
import com.kaiasia.ui.MainFrame;
import com.kaiasia.util.HttpUtils;
import org.json.JSONObject;

public class AuthApiClient {

    private static String sessionId;
    private static String username;
    private static String userEmail;

    // Tạo header chung cho API
    private static JSONObject createHeader() {
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

    // LOGIN
    public static JSONObject login(String username, String password) {
        try {
            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "login");
            enquiry.put("username", username);
            enquiry.put("password", password);

            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request đăng nhập: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());
            JSONObject jsonResponse = parseResponse(response, "LOGIN");

            if (jsonResponse != null && "OK".equals(jsonResponse.optJSONObject("body").optString("status"))) {
                JSONObject enquiryResponse = jsonResponse.getJSONObject("body").getJSONObject("enquiry");
                sessionId = enquiryResponse.getString("sessionId");
                AuthApiClient.username = username;
                AuthApiClient.userEmail = enquiryResponse.optString("gmail", "");
            }

            return jsonResponse;

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi đăng nhập: " + ex.getMessage());
            return null;
        }
    }

    // CHANGE_PASSWORD
    public static JSONObject changePassword(String oldPassword, String newPassword, String reNewPassword) {
        try {
            if (sessionId == null) {
                System.err.println("Không thể đổi mật khẩu: sessionId null. Vui lòng đăng nhập lại.");
                return null;
            }

            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "changePassword");
            enquiry.put("sessionId", sessionId);
            enquiry.put("username", username);
            enquiry.put("oldPassword", oldPassword);
            enquiry.put("newPassword", newPassword);
            enquiry.put("reNewPassword", reNewPassword);
            enquiry.put("transId", "AUTHEN-changePass-" + System.currentTimeMillis());

            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request đổi mật khẩu: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());
            return parseResponse(response, "CHANGE_PASSWORD");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi ngoại lệ khi đổi mật khẩu: " + ex.getMessage());
            return null;
        }
    }

    // GET_OTP với email cho sẵn
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
            enquiry.put("gmail", "qthe23572@gmail.com");
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

            // Debug request JSON
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

    //confirm OTP
    public static JSONObject confirmOtp(String otp,String transId){
        try {


            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "confirmOTP");
            enquiry.put("sessionId", sessionId);
            enquiry.put("username", username);
            enquiry.put("otp", otp);
            enquiry.put("transTime", "20161108122000");
            enquiry.put("transId", "AUTHEN-confirmOTP-45122211");


            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());
            return parseResponse(response, "Confirm otp");

        } catch (Exception ex) {
            ex.printStackTrace();

            return null;
        }

    }

//    // GET_OTP với email riêng của từng customer
//    public static JSONObject getOtp() {
//        try {
//            if (sessionId == null || username == null) {
//                System.err.println("Không thể lấy OTP: sessionId hoặc username null. Vui lòng đăng nhập lại.");
//                return null;
//            }
//
//            // Lấy email từ CUSTOMER_API
//            String email = fetchEmailFromCustomerApi();
//            if (email == null || email.trim().isEmpty()) {
//                System.err.println("Lỗi: Không thể lấy email từ CUSTOMER_API. OTP không thể được gửi!");
//                return null;
//            }
//
//            System.out.println("DEBUG: Gửi request lấy OTP với email: " + email);
//
//            JSONObject enquiry = new JSONObject();
//            enquiry.put("authenType", "getOTP");
//            enquiry.put("sessionId", sessionId);
//            enquiry.put("username", username);
//            enquiry.put("gmail", email);
//            enquiry.put("transTime", System.currentTimeMillis());
//            enquiry.put("transId", "AUTHEN-getOTP-" + System.currentTimeMillis());
//            enquiry.put("transInfo", "Giao dịch lấy mã OTP");
//
//            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
//
//            // Debug log request
//            System.out.println("Gửi request lấy OTP: " + requestJson.toString(4));
//
//            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());
//            return parseResponse(response, "GET_OTP");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.err.println("Lỗi ngoại lệ khi lấy OTP: " + ex.getMessage());
//            return null;
//        }
//    }

    // Lấy email từ CUSTOMER_API (dùng cho GET_OTP theo mail riêng của từng customer)
    private static String fetchEmailFromCustomerApi() {
        try {
            UserInfo currentUser = MainFrame.getCurrentUser();
            if (currentUser == null || currentUser.getSessionId() == null || currentUser.getCustomerID() == null) {
                System.err.println("Lỗi: Không thể lấy email, thông tin người dùng null!");
                return null;
            }

            JSONObject response = CustomerApiClient.getCustomerInfo(currentUser.getSessionId(), currentUser.getCustomerID());

            if (response != null && response.optJSONObject("body") != null) {
                JSONObject body = response.optJSONObject("body");
                JSONObject enquiry = body.optJSONObject("enquiry");

                if (enquiry != null) {
                    return enquiry.optString("email", null);
                }
            }

            System.err.println("Lỗi: Không tìm thấy email trong CUSTOMER_API!");
            return null;

        } catch (Exception e) {
            System.err.println("Lỗi khi gọi CUSTOMER_API để lấy email: " + e.getMessage());
            return null;
        }
    }

    // REQUEST_RESET_CODE
    public static JSONObject requestResetCode(String username) {
        try {
            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "resetPassword");
            enquiry.put("transId", "AUTHEN-resetPassword-" + System.currentTimeMillis());
            enquiry.put("username", username);

            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request quên mật khẩu: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());
            return parseResponse(response, "REQUEST_RESET_CODE");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi khi gửi yêu cầu quên mật khẩu: " + ex.getMessage());
            return null;
        }
    }

    // RESET_PASSWORD
    public static JSONObject resetPassword(ResetPasswordInfo resetPasswordInfo) {
        try {
            JSONObject enquiry = new JSONObject();
            enquiry.put("authenType", "setPassword");
            enquiry.put("transId", "AUTHEN-setPassword-" + System.currentTimeMillis());
            enquiry.put("username", resetPasswordInfo.getUsername());
            enquiry.put("resetCode", resetPasswordInfo.getResetCode());
            enquiry.put("newPassword", resetPasswordInfo.getNewPassword());

            JSONObject requestJson = createRequest("GET_ENQUIRY", enquiry);
            System.out.println("Gửi request đặt lại mật khẩu: " + requestJson.toString(4));

            String response = HttpUtils.postJson(Config.AUTH_API_URL, requestJson.toString());
            return parseResponse(response, "RESET_PASSWORD");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi khi đặt lại mật khẩu: " + ex.getMessage());
            return null;
        }
    }


}
