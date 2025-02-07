package com.kaiasia.model;

public class UserInfo {
    private String customerName;
    private String customerID;
    private String username;
    private String phone;

    public UserInfo(String customerName, String customerID, String username, String phone) {
        this.customerName = customerName;
        this.customerID = customerID;
        this.username = username;
        this.phone = phone;
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
}
