package com.kaiasia.model.Error;

public class ErrorInfo {
    private String code;
    private String desc;
    public ErrorInfo(String code, String desc) {}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
