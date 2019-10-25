package com.ppdai.das.console.enums;

public enum DataBaseEnum {

    MYSQL(1, "MySql", "com.mysql.jdbc.Driver", "mySqlProvider"),
    SQLSERVER(2, "SqlServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sqlProvider");

    private Integer type;
    private String name;
    private String driver;
    private String provider;

    DataBaseEnum(Integer type, String name, String driver, String provider) {
        this.type = type;
        this.name = name;
        this.driver = driver;
        this.provider = provider;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public static DataBaseEnum getDataBaseEnumByType(int type) {
        for (DataBaseEnum item : DataBaseEnum.values()) {
            if (type == item.type) {
                return item;
            }
        }
        throw new EnumConstantNotPresentException(DataBaseEnum.class, "type " + type + " is not exist!!");
    }
}
