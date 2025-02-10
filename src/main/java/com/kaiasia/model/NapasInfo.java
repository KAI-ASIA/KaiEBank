package com.kaiasia.model;

public class NapasInfo {
    private String senderAccount;
    private String senderName;
    private String accountId;
    private String bankId;

    public String getSenderAccount() {
        return senderAccount;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBankId() {
        return bankId;
    }

    private NapasInfo(Builder builder) {
        this.senderAccount = builder.senderAccount;
        this.senderName = builder.senderName;
        this.accountId = builder.accountId;
        this.bankId = builder.bankId;
    }

    public static class Builder {
        private String senderAccount;
        private String senderName;
        private String accountId;
        private String bankId;

        public Builder senderAccount(String senderAccount) {
            this.senderAccount = senderAccount;
            return this;
        }

        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder bankId(String bankId) {
            this.bankId = bankId;
            return this;
        }

        public NapasInfo build() {
            return new NapasInfo(this);
        }
    }

    @Override
    public String toString() {
        return "NapasInfo{" +
                "senderAccount='" + senderAccount + '\'' +
                ", senderName='" + senderName + '\'' +
                ", accountId='" + accountId + '\'' +
                ", bankId='" + bankId + '\'' +
                '}';
    }
}
