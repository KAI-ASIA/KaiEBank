package com.kaiasia.model;

public class AccountInfo {
    private String customerID;
    private String accountType;
    private String shortName;
    private String shortTitle;
    private String currency;
    private String accountID;
    private String altAccount;
    private String category;
    private String company;
    private String availBal;
    private String productCode;

    // Private constructor để chỉ cho phép tạo đối tượng thông qua Builder
    private AccountInfo(Builder builder) {
        this.customerID = builder.customerID;
        this.accountType = builder.accountType;
        this.shortName = builder.shortName;
        this.shortTitle = builder.shortTitle;
        this.currency = builder.currency;
        this.accountID = builder.accountID;
        this.altAccount = builder.altAccount;
        this.category = builder.category;
        this.company = builder.company;
        this.availBal = builder.availBal;
        this.productCode = builder.productCode;
    }

    // Getters
    public String getCustomerID() { return customerID; }
    public String getAccountType() { return accountType; }
    public String getShortName() { return shortName; }
    public String getShortTitle() { return shortTitle; }
    public String getCurrency() { return currency; }
    public String getAccountID() { return accountID; }
    public String getAltAccount() { return altAccount; }
    public String getCategory() { return category; }
    public String getCompany() { return company; }
    public String getAvailBal() { return availBal; }
    public String getProductCode() { return productCode; }

    // Builder Class
    public static class Builder {
        private String customerID;
        private String accountType;
        private String shortName;
        private String shortTitle;
        private String currency;
        private String accountID;
        private String altAccount;
        private String category;
        private String company;
        private String availBal;
        private String productCode;

        public Builder customerID(String customerID) {
            this.customerID = customerID;
            return this;
        }

        public Builder accountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder shortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public Builder shortTitle(String shortTitle) {
            this.shortTitle = shortTitle;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder accountID(String accountID) {
            this.accountID = accountID;
            return this;
        }

        public Builder altAccount(String altAccount) {
            this.altAccount = altAccount;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder availBal(String availBal) {
            this.availBal = availBal;
            return this;
        }

        public Builder productCode(String productCode) {
            this.productCode = productCode;
            return this;
        }

        public AccountInfo build() {
            return new AccountInfo(this);
        }
    }

}
