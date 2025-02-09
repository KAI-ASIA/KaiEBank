package com.kaiasia.model;

public class UserInfo {
    private String customerName;
    private String customerID;
    private String username;
    private String phone;
    private String sessionId;
    private String email;

    public UserInfo(String customerName, String customerID, String username, String phone, String sessionId, String email) {
        this.customerName = customerName;
        this.customerID = customerID;
        this.username = username;
        this.phone = phone;
        this.sessionId = sessionId;
        this.email = email;
    }


    public String getSessionId() {
        return sessionId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email;}

    public void clearSession() {
        this.sessionId = null;
        System.out.println("Session đã được xoá!");
    }
}
