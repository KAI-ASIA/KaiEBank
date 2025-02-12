package com.kaiasia.model;

public class TransferOut {
    private String sessionId;
    private String customerID;
    private String company;
    private String OTP;

    private String debitAccount;
    private String creditAccount;
    private String bankId;
    private String transAmount;
    private String transDesc;

    // Constructor private để chỉ có thể tạo bằng Builder
    private TransferOut(Builder builder) {
        this.sessionId = builder.sessionId;
        this.customerID = builder.customerID;
        this.company = builder.company;
        this.OTP = builder.OTP;

        this.debitAccount = builder.debitAccount;
        this.creditAccount = builder.creditAccount;
        this.bankId = builder.bankId;
        this.transAmount = builder.transAmount;
        this.transDesc = builder.transDesc;
    }

    // Getter methods
    public String getSessionId() {
        return sessionId;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCompany() {
        return company;
    }

    public String getOTP() {
        return OTP;
    }



    public String getDebitAccount() {
        return debitAccount;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public String getBankId() {
        return bankId;
    }

    public String getTransAmount() {
        return transAmount;
    }

    public String getTransDesc() {
        return transDesc;
    }

    // Builder Class
    public static class Builder {
        private String sessionId;
        private String customerID;
        private String company;
        private String OTP;
        private String transactionId;
        private String debitAccount;
        private String creditAccount;
        private String bankId;
        private String transAmount;
        private String transDesc;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder customerID(String customerID) {
            this.customerID = customerID;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder OTP(String OTP) {
            this.OTP = OTP;
            return this;
        }



        public Builder debitAccount(String debitAccount) {
            this.debitAccount = debitAccount;
            return this;
        }

        public Builder creditAccount(String creditAccount) {
            this.creditAccount = creditAccount;
            return this;
        }

        public Builder bankId(String bankId) {
            this.bankId = bankId;
            return this;
        }

        public Builder transAmount(String transAmount) {
            this.transAmount = transAmount;
            return this;
        }

        public Builder transDesc(String transDesc) {
            this.transDesc = transDesc;
            return this;
        }

        public TransferOut build() {
            return new TransferOut(this);
        }
    }


}
