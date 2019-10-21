package com.ppdai.platform.das.codegen.enums;


public enum StrategyLoadingTypeEnum {

    STATICLOADINGSTRATEGY(1, "静态加载的策略"),
    DYNAMICLOADINGSTRATEGY(2, "动态加载策略");

    private int type;
    private String detail;

    StrategyLoadingTypeEnum(int type, String detail) {
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
