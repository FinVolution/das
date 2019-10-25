package com.ppdai.das.console.common.validates.sql;

public class SQLValidateResult {
    private Integer dbType;
    private boolean passed;
    private String sql;
    private int affectRows;
    private StringBuffer msg = new StringBuffer();

    public SQLValidateResult(String sql) {
        this.sql = sql;
    }

    public boolean isPassed() {
        return passed;
    }

    public SQLValidateResult setPassed(boolean passed) {
        this.passed = passed;
        return this;
    }

    public String getMessage() {
        return msg.toString();
    }

    public String getSQL() {
        return this.sql;
    }

    public int getAffectRows() {
        return affectRows;
    }

    public void setAffectRows(int affectRows) {
        this.affectRows = affectRows;
    }

    public SQLValidateResult append(String msg) {
        this.msg.append(msg);
        return this;
    }

    public SQLValidateResult appendFormat(String format, Object... args) {
        this.msg.append(String.format(format, args));
        return this;
    }

    public SQLValidateResult appendLineFormat(String format, Object... args) {
        this.msg.append(String.format(format, args)).append(System.lineSeparator());
        return this;
    }

    public SQLValidateResult clearAppend(String msg) {
        this.msg = new StringBuffer();
        this.msg.append(msg);
        return this;
    }

    public Integer getDbType() {
        return dbType;
    }

    public void setDbType(Integer dbType) {
        this.dbType = dbType;
    }

    @Override
    public String toString() {
        return String.format("[Passed: %s, Message: %s]", this.passed, this.msg.toString());
    }
}
