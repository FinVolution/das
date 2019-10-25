package com.ppdai.das.console.enums;

public enum TaskTypeEnum {
    Table(1, "表实体"),
    FreeSql(2, "查询实体");

    private int type;
    private String detail;

    TaskTypeEnum(int type, String detail) {
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
