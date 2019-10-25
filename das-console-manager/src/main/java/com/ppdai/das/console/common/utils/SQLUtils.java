package com.ppdai.das.console.common.utils;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowTablesStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import com.google.common.base.Preconditions;
import com.ppdai.das.core.enums.DatabaseCategory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLUtils {

    static final int LIMIT_MAX = 500;

    /**
     * Append "LIMIT N" for MySQL; Insert "top N" for SQLServer;
     */
    public static String checkSql(String sql, DatabaseCategory category) {
        final boolean isMySQL = category == DatabaseCategory.MySql;

        List<SQLStatement> sqlStatements = com.alibaba.druid.sql.SQLUtils.parseStatements(sql, isMySQL? JdbcConstants.MYSQL : JdbcConstants.SQL_SERVER);
        Preconditions.checkArgument(sqlStatements.size() == 1, "请不要输入多条SQL语句");
        SQLStatement sqlStatement = sqlStatements.get(0);
        if(isMySQL) {
            if(sqlStatement instanceof SQLSelectStatement){
                AtomicInteger offset = new AtomicInteger(0);
                AtomicInteger count  = new AtomicInteger(-1);
                sqlStatement.accept(new SQLASTVisitorAdapter() {
                    @Override public boolean visit(SQLLimit x) {
                        if(x.getOffset() != null){
                            offset.set(((SQLIntegerExpr)x.getOffset()).getNumber().intValue());
                        }
                        count.set(((SQLIntegerExpr)x.getRowCount()).getNumber().intValue());
                        return true;
                    }
                });

                Preconditions.checkArgument(count.get() <= LIMIT_MAX, "请不要输入 limit > " + LIMIT_MAX);
                return PagerUtils.limit(sql, JdbcConstants.MYSQL, offset.get(), count.get() == -1 ? LIMIT_MAX  : count.get());
            }
            if (sqlStatement instanceof SQLShowTablesStatement) {
                return sql;
            }
        } else {
            if(sqlStatement instanceof SQLSelectStatement){
                AtomicInteger offset = new AtomicInteger(0);
                AtomicInteger count  = new AtomicInteger(-1);
                sqlStatement.accept(new SQLServerASTVisitorAdapter() {
                    @Override public boolean visit(SQLServerTop x) {//Check top
                        count.set(((SQLIntegerExpr)x.getExpr()).getNumber().intValue());
                        return true;
                    }
                    @Override public boolean visit(SQLServerSelectQueryBlock x) {
                        SQLLimit limit = x.getLimit();
                        if(limit != null){
                            if(limit.getOffset() != null){
                                offset.set(((SQLIntegerExpr)limit.getOffset()).getNumber().intValue());
                            }
                            if(limit.getRowCount() != null) {
                                count.set(((SQLIntegerExpr) limit.getRowCount()).getNumber().intValue());
                            }
                        }

                        return true;
                    }
                });
                Preconditions.checkArgument(count.get() <= LIMIT_MAX, "请不要输入 limit > " + LIMIT_MAX);
                return PagerUtils.limit(sql, JdbcConstants.SQL_SERVER, offset.get(), count.get() == -1 ? LIMIT_MAX  : count.get());
            }
        }
        throw new IllegalArgumentException("只支持查询SQL语句");
    }

    public static String withTableShard(String sql, String tableShardId, DatabaseCategory category) {
        if(tableShardId.isEmpty()){
            return sql;
        }
        final boolean isMySQL = category == DatabaseCategory.MySql;

        List<SQLStatement> sqlStatements = com.alibaba.druid.sql.SQLUtils.parseStatements(sql, isMySQL ? JdbcConstants.MYSQL : JdbcConstants.SQL_SERVER);
        Preconditions.checkArgument(sqlStatements.size() == 1, "请不要输入多条SQL语句");
        SQLStatement sqlStatement = sqlStatements.get(0);
        if (isMySQL) {
            if (sqlStatement instanceof SQLSelectStatement) {
                sqlStatement.accept(new SQLASTVisitorAdapter() {
                    @Override public boolean visit(SQLExprTableSource x) {
                        return replaceTableName(x, tableShardId);
                    }
                });
                return sqlStatement.toString();
            }
            if (sqlStatement instanceof SQLShowTablesStatement) {
                return sql;
            }
        } else {
            sqlStatement.accept(new SQLServerASTVisitorAdapter() {
                @Override public boolean visit(SQLExprTableSource x) {
                    return replaceTableName(x, tableShardId);
                }
            });
            return sqlStatement.toString();
        }
        throw new IllegalArgumentException("只支持查询SQL语句");
    }

    static boolean replaceTableName(SQLExprTableSource x, String tableShardId) {
        String baseName = x.getName().getSimpleName();
        x.setExpr(baseName + "_" + tableShardId);
        return true;
    }

}
