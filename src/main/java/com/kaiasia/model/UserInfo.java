package com.kaiasia.model;

public class UserInfo {
    private String customerName;
    private String customerID;
    private String username;
    private String phone;
    private String sessionId;
    private String email;
    public UserInfo(){}

    public UserInfo(String customerName, String customerID, String username, String phone, String sessionId, String email) {
        this.customerName = customerName;
        this.customerID = customerID;
        this.username = username;
        this.phone = phone;
        this.sessionId = sessionId;
        this.email = email;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setEmail(String email) {
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

    public void clearSession() {
        this.sessionId = null;
        System.out.println("Session đã được xoá!");
    }
}
