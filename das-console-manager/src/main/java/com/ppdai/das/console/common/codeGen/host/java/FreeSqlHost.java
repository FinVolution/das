package com.ppdai.das.console.common.codeGen.host.java;

import com.ppdai.das.console.common.codeGen.enums.DatabaseCategory;

import java.util.ArrayList;
import java.util.List;

public class FreeSqlHost {
    private String packageName;
    private String dbSetName;
    private String className;
    private List<JavaMethodHost> methods = new ArrayList<>();
    private List<JavaParameterHost> fields;
    private DatabaseCategory databaseCategory;
    private boolean length;
    private String projectName;

    public String pageBegain() {
        return "(pageNo - 1) * pageSize";
    }

    public String pageEnd() {
        return "pageSize";
    }

    public List<JavaParameterHost> getFields() {
        return fields;
    }

    public void setFields(List<JavaParameterHost> fields) {
        this.fields = fields;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDbSetName() {
        return dbSetName;
    }

    public void setDbSetName(String dbSetName) {
        this.dbSetName = dbSetName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<JavaMethodHost> getMethods() {
        return methods;
    }

    public void setMethods(List<JavaMethodHost> methods) {
        this.methods = methods;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public void setDatabaseCategory(DatabaseCategory databaseCategory) {
        this.databaseCategory = databaseCategory;
    }

    public boolean isLength() {
        return length;
    }

    public void setLength(boolean length) {
        this.length = length;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
