package com.ppdai.platform.das.codegen.enums;

public enum ServerEnabledEnum {

    NOTUSE(0, "不使用远程连接"),
    USE(1, "使用远程连接");

    private int type;
    private String detail;

    ServerEnabledEnum(int type, String detail) {
        this.type = type;
        this.detail = detail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
