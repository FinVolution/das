package com.ppdai.das.client.delegate;

import java.sql.JDBCType;

public class ColumnMeta {
    private String name;
    private JDBCType type;
    private boolean autoIncremental;
    private boolean primaryKey;
    private boolean insertable;
    private boolean updatable;
    private boolean version;

    public ColumnMeta(String name, JDBCType type, boolean autoIncremental, boolean primaryKey, boolean insertable,
            boolean updatable, boolean version) {
        this.name = name;
        this.type = type;
        this.autoIncremental = autoIncremental;
        this.primaryKey = primaryKey;
        this.insertable = insertable;
        this.updatable = updatable;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public JDBCType getType() {
        return type;
    }

    public boolean isAutoIncremental() {
        return autoIncremental;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public boolean isVersion() {
        return version;
    }
}
