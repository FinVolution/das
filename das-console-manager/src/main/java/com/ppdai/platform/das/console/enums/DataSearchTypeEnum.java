package com.ppdai.platform.das.console.enums;

public enum DataSearchTypeEnum {
    SUCCESS(1, "成功"),
    FAIL(0, "失败"),
    SELECT(1, "查询"),
    DOWNLOAD(0, "下载");

    private int type;
    private String detail;

    DataSearchTypeEnum(int type, String detail) {
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
