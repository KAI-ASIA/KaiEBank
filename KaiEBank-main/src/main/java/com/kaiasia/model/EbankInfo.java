package com.kaiasia.model;

public class EbankInfo {
    private String customerId;
    private String responseCode;
    private String customerType;
    private String company;
    private String nationality;
    private String phone;
    private String email;
    private String mainAccount;
    private String name;
    private String trustedType;
    private String lang;
    private String startDate;
    private String endDate;
    private String pwDate;
    private String userLock;
    private String packAge;
    private String userStatus;

    public String getCompany() {
        return company;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getNationality() {
        return nationality;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getMainAccount() {
        return mainAccount;
    }

    public String getName() {
        return name;
    }

    public String getTrustedType() {
        return trustedType;
    }

    public String getLang() {
        return lang;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPwDate() {
        return pwDate;
    }

    public String getUserLock() {
        return userLock;
    }

    public String getPackAge() {
        return packAge;
    }

    public String getUserStatus() {
        return userStatus;
    }

    // Private constructor
    private EbankInfo(Builder builder) {
        this.customerId = builder.customerId;
        this.responseCode = builder.responseCode;
        this.customerType = builder.customerType;
        this.nationality = builder.nationality;
        this.phone = builder.phone;
        this.email = builder.email;
        this.mainAccount = builder.mainAccount;
        this.name = builder.name;
        this.trustedType = builder.trustedType;
        this.lang = builder.lang;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.pwDate = builder.pwDate;
        this.userLock = builder.userLock;
        this.packAge = builder.packAge;
        this.userStatus = builder.userStatus;
        this.company = builder.company;
    }

    // Builder Class
    public static class Builder {
        private String customerId;
        private String responseCode;
        private String customerType;
        private String company;
        private String nationality;
        private String phone;
        private String email;
        private String mainAccount;
        private String name;
        private String trustedType;
        private String lang;
        private String startDate;
        private String endDate;
        private String pwDate;
        private String userLock;
        private String packAge;
        private String userStatus;

        public Builder setCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setResponseCode(String responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder setCustomerType(String customerType) {
            this.customerType = customerType;
            return this;
        }
        public Builder setCompany(String company) {
            this.company = company;
            return this;
        }

        public Builder setNationality(String nationality) {
            this.nationality = nationality;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setMainAccount(String mainAccount) {
            this.mainAccount = mainAccount;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTrustedType(String trustedType) {
            this.trustedType = trustedType;
            return this;
        }

        public Builder setLang(String lang) {
            this.lang = lang;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setPwDate(String pwDate) {
            this.pwDate = pwDate;
            return this;
        }

        public Builder setUserLock(String userLock) {
            this.userLock = userLock;
            return this;
        }

        public Builder setPackAge(String packAge) {
            this.packAge = packAge;
            return this;
        }

        public Builder setUserStatus(String userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public EbankInfo build() {
            return new EbankInfo(this);
        }
    }

    @Override
    public String toString() {
        return "EbankInfo{" +
                "customerId='" + customerId + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", customerType='" + customerType + '\'' +
                ", nationality='" + nationality + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", mainAccount='" + mainAccount + '\'' +
                ", name='" + name + '\'' +
                ", trustedType='" + trustedType + '\'' +
                ", lang='" + lang + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", pwDate='" + pwDate + '\'' +
                ", userLock='" + userLock + '\'' +
                ", packAge='" + packAge + '\'' +
                ", userStatus='" + userStatus + '\'' +
                '}';
    }
}