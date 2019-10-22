package com.ppdai.platform.das.console.common.validates.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.platform.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.platform.das.console.common.codeGen.utils.DataSourceUtil;
import com.ppdai.platform.das.console.common.codeGen.utils.SqlBuilder;
import com.ppdai.platform.das.console.common.utils.DbUtil;
import com.ppdai.platform.das.console.common.utils.StringUtil;
import com.ppdai.platform.das.console.enums.DataBaseEnum;
import microsoft.sql.DateTimeOffset;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SQLValidation {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Mock a series of String value for SQL CheckTypes
     *
     * @param sqlTypes The SQL CheckTypes
     * @return Mocked String values.
     */
    public static String[] mockStringValues(int[] sqlTypes) {
        if (sqlTypes == null || sqlTypes.length == 0)
            return new String[] {};
        String[] mockedVals = new String[sqlTypes.length];
        for (int i = 0; i < mockedVals.length; i++) {
            Object obj = mockSQLValue(sqlTypes[i]);
            if (null == obj)
                mockedVals[i] = "null";
            else
                mockedVals[i] = obj.toString();
            // mockedVals[i] = obj instanceof String && sqlTypes[i] != 10001 ? //10001 <---> uniqueidentifier
            // "'" + obj.toString() + "'" : obj.toString();
        }
        return mockedVals;
    }

    /**
     * Mock a series of Java Object value for SQL CheckTypes
     *
     * @param sqlTypes The SQL CheckTypes
     * @return Mocked Java Object values.
     */
    public static Object[] mockObjectValues(int[] sqlTypes) {
        if (sqlTypes == null || sqlTypes.length == 0)
            return new Object[] {};
        Object[] mockedVals = new Object[sqlTypes.length];
        for (int i = 0; i < mockedVals.length; i++) {
            mockedVals[i] = mockSQLValue(sqlTypes[i]);
        }
        return mockedVals;
    }

    /**
     * Validate the SQL Statement Parameters will be auto-mocked according to the specified SQL CheckTypes
     */
    public static SQLValidateResult validate(Long alldbs_id, String sql, int[] paramsTypes) {
        Object[] mockedVals = mockObjectValues(paramsTypes);
        return validate(alldbs_id, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the SQL Statement Parameters will be parsed form specified String values.
     */
    public static SQLValidateResult validate(Long alldbs_id, String sql, int[] paramsTypes, String[] vals) {
        Object[] mockedVals = parseSQLValue(paramsTypes, vals);
        return validate(alldbs_id, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the SQL Statement
     */
    private static SQLValidateResult validate(Long alldbs_id, String sql, int[] paramsTypes, Object[] vals) {
        if (StringUtils.startsWithIgnoreCase(sql, "SELECT")) {
            return queryValidate(alldbs_id, sql, paramsTypes, vals);
        } else {
            return updateValidate(alldbs_id, sql, paramsTypes, vals);
        }
    }

    /**
     * Validate the Non-Query SQL Statement
     */
    public static SQLValidateResult updateValidate(Long alldbs_id, String sql, int[] paramsTypes, String[] vals) {
        Object[] mockedVals = parseSQLValue(paramsTypes, vals);
        return updateValidate(alldbs_id, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the Non-Query SQL Statement Parameters will be auto-mocked according to the specified SQL CheckTypes
     */
    public static SQLValidateResult updateValidate(Long alldbs_id, String sql, int[] paramsTypes) {
        Object[] mockedVals = mockObjectValues(paramsTypes);
        return updateValidate(alldbs_id, sql, paramsTypes, mockedVals);
    }


    /**
     * Validate the Non-Query SQL Statement

     */
    private static SQLValidateResult updateValidate(Long alldbs_id, String sql, int[] paramsTypes, Object[] mockedVals) {
        SQLValidateResult status = new SQLValidateResult(sql);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DataSourceUtil.getConnection(alldbs_id);
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(SqlBuilder.net2Java(sql));
            if (paramsTypes != null) {
                for (int i = 1; i <= paramsTypes.length; i++) {
                    if (paramsTypes[i - 1] == 10001) {
                        preparedStatement.setObject(i, mockedVals[i - 1], Types.CHAR);
                    } else {
                        preparedStatement.setObject(i, mockedVals[i - 1], paramsTypes[i - 1]);
                    }
                }
            }
            int rows = preparedStatement.executeUpdate();
            status.setAffectRows(rows);
            status.setPassed(true).append("Validate Successfully");
        } catch (Exception e) {
            status.append(StringUtil.getMessage(e));
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.rollback(connection);
            DbUtil.close(connection);
        }

        return status;
    }

    /**
     * Validate the Query SQL Statement. Parameters will be auto-mocked according to the specified SQL CheckTypes
     */

    public static SQLValidateResult queryValidate(Long alldbs_id, String sql, int[] paramsTypes) {
        Object[] mockedVals = mockObjectValues(paramsTypes);
        return queryValidate(alldbs_id, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the Query SQL Statement. Parameters will be be parsed from specified String values
     */
    public static SQLValidateResult queryValidate(Long alldbs_id, String sql, int[] paramsTypes, String[] vals) {
        Object[] mockedVals = parseSQLValue(paramsTypes, vals);
        return queryValidate(alldbs_id, sql, paramsTypes, mockedVals);
    }

    /**
     * Validate the Query SQL Statement.
     */
    private static SQLValidateResult queryValidate(Long alldbs_id, String sql, int[] paramsTypes, Object[] mockedVals) {
        SQLValidateResult status = new SQLValidateResult(sql);
        Connection connection = null;
        try {
            connection = DataSourceUtil.getConnection(alldbs_id);
            String dbType = getDBType(connection, alldbs_id);
            if ("MySQL".equals(dbType)) {
                status.setDbType(DataBaseEnum.MYSQL.getType());
                mysqlQuery(connection, sql, status, paramsTypes, mockedVals);
            } else if (dbType.equals("Microsoft SQL Server")) {
                status.setDbType(DataBaseEnum.SQLSERVER.getType());
                sqlserverQueryWithoutExplain(connection, sql, status, paramsTypes, mockedVals);
            }
        } catch (Exception e) {
            status.clearAppend(StringUtil.getMessage(e));
        } finally {
            DbUtil.close(connection);
        }
        return status;
    }

    /**
     * Validate the SQL Server Query SQL Statement.
     *
     * @param connection SQL Connection
     * @param sql SQL Statement
     * @param status Result to be updated
     * @param paramsTypes SQL CheckTypes of parameters
     */

    private static void sqlserverQueryWithoutExplain(Connection connection, String sql, SQLValidateResult status,
            int[] paramsTypes, Object[] vals) {
        sqlserverExplain(connection, sql, status, paramsTypes, vals);
        if (status.isPassed()) {
            ResultSet rs = null;
            PreparedStatement stat = null;
            try {
                stat = connection.prepareStatement(SqlBuilder.net2Java(sql));
                for (int i = 1; i <= paramsTypes.length; i++) {
                    if (paramsTypes[i - 1] == 10001)
                        stat.setObject(i, vals[i - 1], Types.CHAR);
                    else
                        stat.setObject(i, vals[i - 1], paramsTypes[i - 1]);
                }
                rs = stat.executeQuery();
                int affectRows = 0;
                while (rs.next()) {
                    affectRows++;
                }
                status.setAffectRows(affectRows);
            } catch (SQLException e) {
                status.setPassed(false);
                status.append(StringUtil.getMessage(e));
            } catch (Exception e) {
                status.setPassed(false);
                status.append(StringUtil.getMessage(e));
            } finally {
                DbUtil.close(rs);
                DbUtil.close(stat);
            }
        }
    }

    private static void sqlserverExplain(Connection connection, String sql, SQLValidateResult status, int[] paramsTypes,
            Object[] vals) {
        status.append("The SQL Server explain is not supported!");
        status.setPassed(true);
    }

    /**wangl
     * Validate the MySQL Query SQL Statement.
     *
     * @param connection SQL Connection
     * @param sql SQL Statement
     * @param status Result to be updated
     * @param paramsTypes SQL CheckTypes of parameters
     */

    private static void mysqlQuery(Connection connection, String sql, SQLValidateResult status, int[] paramsTypes,
            Object[] vals) {
        mysqlExplain(connection, sql, status, paramsTypes, vals);

        if (status.isPassed()) {
            ResultSet rs = null;
            PreparedStatement stat = null;
            try {
                String sql_content = SqlBuilder.net2Java(sql);
                stat = connection.prepareStatement(sql_content);

                for (int i = 1; i <= paramsTypes.length; i++) {
                    stat.setObject(i, vals[i - 1], paramsTypes[i - 1]);
                }

                rs = stat.executeQuery();
                int affectRows = 0;
                while (rs.next()) {
                    affectRows++;
                }
                status.setAffectRows(affectRows);
            } catch (SQLException e) {
                status.setPassed(false);
                status.append(StringUtil.getMessage(e));
            } catch (Exception e) {
                status.setPassed(false);
                status.append(StringUtil.getMessage(e));
            } finally {
                DbUtil.close(rs);
                DbUtil.close(stat);
            }
        }
    }


    private static void mysqlExplain(Connection connection, String sql, SQLValidateResult status, int[] paramsTypes,
            Object[] vals) {
        ResultSet rs = null;
        PreparedStatement stat = null;
        try {
            String sql_content = "EXPLAIN " + SqlBuilder.net2Java(sql);
            stat = connection.prepareStatement(sql_content);

            for (int i = 1; i <= paramsTypes.length; i++) {
                stat.setObject(i, vals[i - 1], paramsTypes[i - 1]);
            }

            rs = stat.executeQuery();
            List<MySQLExplain> explains = new ArrayList<MySQLExplain>();
            while (rs.next()) {
                explains.add(ORMUtils.map(rs, MySQLExplain.class));
            }
            status.append(objectMapper.writeValueAsString(explains));
            status.setPassed(true);
        } catch (SQLException e) {
            status.append(StringUtil.getMessage(e));
        } catch (JsonProcessingException e) {
            status.append(StringUtil.getMessage(e));
        } catch (Exception e) {
            status.append(StringUtil.getMessage(e));
        } finally {
            DbUtil.close(rs);
            DbUtil.close(stat);
        }
    }

    /**
     * Get the database category, which is SQL Server or MySQL
     */

    private static String getDBType(Connection conn, Long alldbs_id) throws SQLException {
        String dbType = null;
        if (CodeGenConsts.databaseType.containsKey(alldbs_id)) {
            dbType = CodeGenConsts.databaseType.get(alldbs_id);
        } else {
            dbType = conn.getMetaData().getDatabaseProductName();
            CodeGenConsts.databaseType.put(alldbs_id, dbType);
        }
        return dbType;
    }

    /**
     * Mock a object according to the SQL Type
     *
     * @param javaSqlTypes The specified SQL Type @see java.sql.CheckTypes
     * @return Mocked object
     */

    private static Object mockSQLValue(int javaSqlTypes) {
        switch (javaSqlTypes) {
            case Types.BIT:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                return 0;
            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
            case Types.DECIMAL:
                return 0.0;
            case Types.NUMERIC:
                return BigDecimal.ZERO;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.NULL:
            case Types.OTHER:
                return null;
            case Types.CHAR:
                return "X";
            case Types.DATE:
                return Date.valueOf("2012-01-01");
            case Types.TIME:
                return Time.valueOf("10:00:00");
            case Types.TIMESTAMP:
                return Timestamp.valueOf("2012-01-01 10:00:00");
            case microsoft.sql.Types.DATETIMEOFFSET:
                return DateTimeOffset.valueOf(Timestamp.valueOf("2012-01-01 10:00:00"), 0);
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
                return "TT";
            case 10001: // uniqueidentifier
                return "C4AECF65-1D5C-47B6-BFFC-0C9550C4E158";
            default:
                return null;

        }
    }

    private static Object[] parseSQLValue(int[] sqlTypes, String[] vals) {
        if (sqlTypes == null || vals == null || sqlTypes.length != vals.length || sqlTypes.length == 0)
            return new Object[] {};
        else {
            Object[] objs = new Object[sqlTypes.length];
            for (int i = 0; i < objs.length; i++) {
                objs[i] = parseSQLValue(sqlTypes[i], vals[i]);
            }
            return objs;
        }
    }

    /**
     * Parse the String value to java Object according to different SQL Type
     *
     * @param javaSqlTypes The SQL CheckTypes @see java.sql.CheckTypes
     * @param val The string value
     * @return Java Object
     */
    private static Object parseSQLValue(int javaSqlTypes, String val) {
        if (null == val || val.equalsIgnoreCase("null")){
            return null;
        }
        switch (javaSqlTypes) {
            case Types.BIT:
                return Integer.parseInt(val) == 0 ? 0 : 1;
            case Types.TINYINT:
                return Byte.parseByte(val);
            case Types.SMALLINT:
                return Short.parseShort(val);
            case Types.INTEGER:
                return Integer.parseInt(val);
            case Types.BIGINT:
                return Long.parseLong(val);
            case Types.REAL:
                return Float.parseFloat(val);
            case Types.FLOAT:
            case Types.DOUBLE:
                return Double.parseDouble(val);
            case Types.DECIMAL:
            case Types.NUMERIC:
                return BigDecimal.valueOf(Double.parseDouble(val));
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return val.getBytes();
            case Types.NULL:
            case Types.OTHER:
                return null;
            case Types.CHAR:
                return val;
            case Types.DATE:
                // return Date.valueOf(val);
                return parseDate(val);
            case Types.TIME:
                return Time.valueOf(val);
            case Types.TIMESTAMP:
                return Timestamp.valueOf(val);
            case microsoft.sql.Types.DATETIMEOFFSET:
                return DateTimeOffset.valueOf(Timestamp.valueOf(val), 0);
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
                return val;
            case 10001: // uniqueidentifier
                return val;
            default:
                return null;

        }
    }

    private static Date parseDate(String val) {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date cur = format1.parse(val);
            return new Date(cur.getTime());
        } catch (ParseException e) {
        }
        return null;
    }
}
