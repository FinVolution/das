package com.ppdai.das.console.enums;

public enum DataFieldTypeEnum {
    SQL_DATE(1, "java.sql.Date", "Date", java.sql.Date.class),
    SQL_TIMESTAMP(11, "java.sql.Timestamp", "Timestamp", java.sql.Timestamp.class),
    UTIL_DATE(12, "java.util.Date", "Date", java.util.Date.class);

    private int type;
    private String detail;
    private String className;
    private Class<?> javaClass;

    DataFieldTypeEnum(int type, String detail, String className, Class<?> javaClass) {
        this.type = type;
        this.detail = detail;
        this.className = className;
        this.javaClass = javaClass;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
    }
}
