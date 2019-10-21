package com.ppdai.platform.das.codegen.common.utils;

import com.ppdai.das.core.enums.DatabaseCategory;
import org.junit.Assert;
import org.junit.Test;

public class SQLUtilsTest {

    @Test
    public void testCheckMySQLNoLimit(){
        String sql = SQLUtils.checkSql("select * from t where 1=1", DatabaseCategory.MySql);
        Assert.assertEquals(
                "SELECT *\n" +
                "FROM t\n" +
                "WHERE 1 = 1\n" +
                "LIMIT 500", sql);
    }

    @Test
    public void testCheckMySQLLimit(){
        String sql = SQLUtils.checkSql("select * from t where 1=1 limit 12", DatabaseCategory.MySql);
        Assert.assertEquals(
                "SELECT *\n" +
                "FROM t\n" +
                "WHERE 1 = 1\n" +
                "LIMIT 12", sql);
    }

    @Test
    public void testCheckMySQLLimitOffset(){
        String sql = SQLUtils.checkSql("select * from t where 1=1 limit 3, 12", DatabaseCategory.MySql);
        Assert.assertEquals(
                "SELECT *\n" +
                "FROM t\n" +
                "WHERE 1 = 1\n" +
                "LIMIT 3, 12", sql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckMySQLExceedLimit(){
        String sql = SQLUtils.checkSql("select * from t where 1=1 limit 501", DatabaseCategory.MySql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckMySQLDelete(){
        String sql = SQLUtils.checkSql("delete from t where 1=1", DatabaseCategory.MySql);
    }

    @Test
    public void testCheckMySQLShowTables(){
        String sql = SQLUtils.checkSql("display tables", DatabaseCategory.MySql);
        Assert.assertEquals("display tables", sql);
    }

    @Test
    public void testCheckSQLServerNoLimit(){
        String sql = SQLUtils.checkSql("select * from t where 1=1", DatabaseCategory.SqlServer);
        Assert.assertEquals(
                "SELECT TOP 500 *\n" +
                        "FROM t\n" +
                        "WHERE 1 = 1"  ,sql);
    }

    @Test
    public void testCheckSQLServerTop(){
        String sql = SQLUtils.checkSql("select top 2 * from t where 1=1", DatabaseCategory.SqlServer);
        Assert.assertEquals(
                "SELECT TOP 2 *\n" +
                "FROM t\n" +
                "WHERE 1 = 1"  ,sql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckSQLServerExceedLimit(){
        String sql = SQLUtils.checkSql("select top 501 * from t where 1=1", DatabaseCategory.SqlServer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckSQLServerExceedFetch(){
        String sql = SQLUtils.checkSql("select * from t where 1=1 order by X OFFSET 10 ROWS FETCH NEXT 501 ROWS ONLY", DatabaseCategory.SqlServer);
    }

    @Test
    public void testCheckSQLServerOffset(){
        String sql = SQLUtils.checkSql("select * from t where 1=1 order by X OFFSET 10 ROWS FETCH NEXT 500 ROWS ONLY", DatabaseCategory.SqlServer);
        Assert.assertEquals(
                        "SELECT *\n" +
                        "FROM (\n" +
                        "\tSELECT *, ROW_NUMBER() OVER () AS ROWNUM\n" +
                        "\tFROM t\n" +
                        "\tWHERE 1 = 1\n" +
                        "\tORDER BY X\n" +
                        "\tOFFSET 10 ROWS FETCH NEXT 500 ROWS ONLY\n" +
                        ") XX\n" +
                        "WHERE ROWNUM > 10\n" +
                        "\tAND ROWNUM <= 510"  ,sql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckSQLServerInsert(){
        String sql = SQLUtils.checkSql("insert into t(id) values(1)", DatabaseCategory.MySql);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckMultipleSQL(){
        String sql = SQLUtils.checkSql("select * from t;select * from p;", DatabaseCategory.MySql);
    }
}
