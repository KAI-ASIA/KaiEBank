package com.kaiasia.model;

public class ResetPasswordInfo {
    private final String username;
    private final String resetCode;
    private final String newPassword;

    private ResetPasswordInfo(Builder builder) {
        this.username = builder.username;
        this.resetCode = builder.resetCode;
        this.newPassword = builder.newPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getResetCode() {
        return resetCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // Builder Pattern
    public static class Builder {
        private String username;
        private String resetCode;
        private String newPassword;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder resetCode(String resetCode) {
            this.resetCode = resetCode;
            return this;
        }

        public Builder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ResetPasswordInfo build() {
            return new ResetPasswordInfo(this);
        }
    }
}
