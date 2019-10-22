package com.ppdai.platform.das.console.enums;

public enum OperateUserEnum {
    Allow(1, true, "有添加用户权限"),
    Prohibit(2, false, "无权限");

    private int type;
    private boolean allowAddUser;
    private String detail;

    OperateUserEnum(int type, boolean allowAddUser, String detail) {
        this.type = type;
        this.allowAddUser = allowAddUser;
        this.detail = detail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAllowAddUser() {
        return allowAddUser;
    }

    public void setAllowAddUser(boolean allowAddUser) {
        this.allowAddUser = allowAddUser;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public static OperateUserEnum getAddUserEnumByAllowAddUser(boolean allowAddUser) {
        for (OperateUserEnum item : OperateUserEnum.values()) {
            if (allowAddUser == item.allowAddUser) {
                return item;
            }
        }
        throw new EnumConstantNotPresentException(OperateUserEnum.class, allowAddUser + "is not exist!!");
    }
}
