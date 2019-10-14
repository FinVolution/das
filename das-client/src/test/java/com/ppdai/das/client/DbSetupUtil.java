package com.ppdai.das.client;

public class DbSetupUtil {
    private final static String DROP_TABLE_MYSQL = "DROP TABLE IF EXISTS %s";

    private final static String CREATE_TABLE_MYSQL = "CREATE TABLE %s("
            + "PeopleID int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
            + "Name VARCHAR(64),"
            + "CityID int,"
            + "ProvinceID int,"
            + "CountryID int, "
            + "DataChange_LastTime timestamp default CURRENT_TIMESTAMP)";

    private final static String DROP_TABLE_SQLServer = "IF EXISTS ("
            + "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
            + "WHERE TABLE_NAME = '%s') "
            + "DROP TABLE %s";
    
    //Create the the table
    private final static String CREATE_TABLE_SQLServer = "CREATE TABLE %s("
            + "PeopleID int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
            + "Name varchar(64) not null,"
            + "CityID int,"
            + "ProvinceID int,"
            + "CountryID int,"
            + "DataChange_LastTime datetime default getdate())";
    
    public String getDropPersonSqlServer(String tableName) {
        return String.format(DROP_TABLE_SQLServer, tableName, tableName);
    }
    
    public String getCreatePersonSqlServer(String tableName) {
        return String.format(CREATE_TABLE_SQLServer, tableName);
    }
    
    public String getDropPersonMySql(String tableName) {
        return String.format(DROP_TABLE_MYSQL, tableName);
    }
    
    public String getCreatePersonMySql(String tableName) {
        return String.format(CREATE_TABLE_MYSQL, tableName);
    }
    
    public static String[] handle(String table, String[] insertSql) {
        String[] sql = new String[insertSql.length + 2];
        sql[0] = "SET IDENTITY_INSERT "+ table +" ON";
        int i = 1;
        for(String isql: insertSql)
            sql[i++] = isql;
        
        sql[i] = "SET IDENTITY_INSERT "+ table +" OFF";
        return sql;
    }

    public interface DbSetuper {
        String create(String table);
        String drop(String table);
        String turnOnIdentityInsert(String table);
        String turnOffIdentityInsert(String table);
        void range(SqlBuilder sb, int start, int count);
    }
    
    public static class SqlServerSetuper implements DbSetuper {

        @Override
        public String create(String table) {
            return String.format(CREATE_TABLE_SQLServer, table);
        }

        @Override
        public String drop(String table) {
            return String.format(DROP_TABLE_SQLServer, table, table);
        }

        public String turnOnIdentityInsert(String table) {
            return "SET IDENTITY_INSERT "+ table + " ON";
        }
        
        public String turnOffIdentityInsert(String table) {
            return "SET IDENTITY_INSERT "+ table + " OFF";
        }

        @Override
        public void range(SqlBuilder sb, int start, int count) {
            sb.offset(start, count);
        }
    }
    
    public static class MySqlSetuper implements DbSetuper {

        @Override
        public String create(String table) {
            return String.format(CREATE_TABLE_MYSQL, table);
        }

        @Override
        public String drop(String table) {
            return String.format(DROP_TABLE_MYSQL, table);
        }

        public String turnOnIdentityInsert(String table) {
            return null;
        }
        
        public String turnOffIdentityInsert(String table) {
            return null;
        }
        @Override
        public void range(SqlBuilder sb, int start, int count) {
            sb.limit(start, count);
        }
    }
}
