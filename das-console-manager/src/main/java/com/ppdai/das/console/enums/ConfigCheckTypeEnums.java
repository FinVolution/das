package com.ppdai.das.console.enums;


public enum ConfigCheckTypeEnums {

    SUCCESS(1, "success"),
    INFO(2, "infoSaec"),
    WARNING(3, "warning"),
    ERROR(4, "error");

    private int type;
    private String value;

    ConfigCheckTypeEnums(int type, String value) {
        this.type = type;
        this.value = value;

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
