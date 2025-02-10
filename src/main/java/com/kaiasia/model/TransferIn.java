package com.kaiasia.model;

public class TransferIn {
    private final String sessionId;
    private final String debitAccount;
    private final String creditAccount;
    private final int amount;
    private final String desc;
    private final String customerID;
    private final String otp;

    // Constructor private để chỉ có thể tạo đối tượng qua Builder
    private TransferIn(TransferBuilder builder) {
        this.sessionId = builder.sessionId;
        this.debitAccount = builder.debitAccount;
        this.creditAccount = builder.creditAccount;
        this.amount = builder.amount;
        this.desc = builder.desc;
        this.customerID = builder.customerID;
        this.otp = builder.otp;
    }

    // Chỉ có getter để đảm bảo tính bất biến
    public String getSessionId() {
        return sessionId;
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public int getAmount() {
        return amount;
    }

    public String getDesc() {
        return desc;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getOtp() {
        return otp;
    }

    @Override
    public String toString() {
        return "TransferIn{" +
                "sessionId='" + sessionId + '\'' +
                ", debitAccount='" + debitAccount + '\'' +
                ", creditAccount='" + creditAccount + '\'' +
                ", amount=" + amount +
                ", desc='" + desc + '\'' +
                ", customerID='" + customerID + '\'' +
                ", otp='" + otp + '\'' +
                '}';
    }

    // Builder Pattern
    public static class TransferBuilder {
        private String sessionId;
        private String debitAccount;
        private String creditAccount;
        private int amount;
        private String desc;
        private String customerID;
        private String otp;

        public TransferBuilder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public TransferBuilder setDebitAccount(String debitAccount) {
            this.debitAccount = debitAccount;
            return this;
        }

        public TransferBuilder setCreditAccount(String creditAccount) {
            this.creditAccount = creditAccount;
            return this;
        }

        public TransferBuilder setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public TransferBuilder setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public TransferBuilder setCustomerID(String customerID) {
            this.customerID = customerID;
            return this;
        }

        public TransferBuilder setOtp(String otp) {
            this.otp = otp;
            return this;
        }

        // Phương thức build() để tạo đối tượng TransferIn
        public TransferIn build() {
            return new TransferIn(this);
        }
    }
}