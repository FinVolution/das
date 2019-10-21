package com.ppdai.platform.das.codegen.enums;

public enum DbMasterSlaveEnum {
    MASTER(1, "Master"),
    SLAVE(2, "Slave");

    DbMasterSlaveEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static DbMasterSlaveEnum getDbMasterSlaveEnumByType(int type) {
        for (DbMasterSlaveEnum item : DbMasterSlaveEnum.values()) {
            if (type == item.type) {
                return item;
            }
        }
        throw new EnumConstantNotPresentException(DbMasterSlaveEnum.class, "type " + type + " is not exist!!");
    }
}
