package com.ppdai.platform.das.console.enums;

public enum RoleTypeEnum {
    Admin(1, "管理员"),
    Limited(2, "普通用户");

    private int type;
    private String detail;

    RoleTypeEnum(int type, String detail) {
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
