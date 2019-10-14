package com.ppdai.das.client;

import java.sql.SQLException;

import com.ppdai.das.client.DbSetupUtil.DbSetuper;
import com.ppdai.das.core.enums.DatabaseCategory;

public abstract class DataPreparer {
    public static final int DB_MODE = 2;
    public static final int TABLE_MODE = 4;
    public static final String TABLE_NAME = "person";

    protected DatabaseCategory dbCategory;
    protected String dbName;
    protected int batchRet;
    protected DasClient dao;
    protected DbSetuper setuper;
    
    public DataPreparer(DatabaseCategory dbCategory) throws SQLException {
        this.dbCategory = dbCategory;
        this.dbName = getDbName(dbCategory);
        this.setuper = dbCategory.equals(DatabaseCategory.MySql)? new DbSetupUtil.MySqlSetuper() : new DbSetupUtil.SqlServerSetuper();
        this.batchRet = dbCategory.equals(DatabaseCategory.MySql)? -2 : 1;
        dao = DasClientFactory.getClient(dbName);
    }

    public DatabaseCategory getDbCategory() {
        return dbCategory;
    }
    
    public String getDbName() {
        return getDbName(dbCategory);
    }
    
    public abstract String getDbName(DatabaseCategory dbCategory);
    
    public abstract void setup() throws Exception;
    
    public abstract void tearDown() throws Exception;
}
