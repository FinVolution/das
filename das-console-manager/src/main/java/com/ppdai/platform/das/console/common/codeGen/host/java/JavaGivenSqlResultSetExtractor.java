package com.ppdai.platform.das.console.common.codeGen.host.java;

import com.ppdai.das.core.client.DalResultSetExtractor;
import com.ppdai.platform.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.platform.das.console.common.codeGen.host.AbstractParameterHost;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JavaGivenSqlResultSetExtractor implements DalResultSetExtractor<List<AbstractParameterHost>> {
    @Override
    public List<AbstractParameterHost> extract(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        List<AbstractParameterHost> paramHosts = new ArrayList<>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            JavaParameterHost paramHost = new JavaParameterHost();
            paramHost.setName(rsmd.getColumnLabel(i));
            paramHost.setSqlType(rsmd.getColumnType(i));

            Class<?> javaClass = null;
            try {
                javaClass = Class.forName(rsmd.getColumnClassName(i));
            } catch (Exception e) {
                e.printStackTrace();
                javaClass = CodeGenConsts.jdbcSqlTypeToJavaClass.get(paramHost.getSqlType());
            }
            paramHost.setJavaClass(javaClass);
            paramHost.setIdentity(false);
            paramHost.setNullable(rsmd.isNullable(i) == 1 ? true : false);
            paramHost.setPrimary(false);
            paramHost.setLength(rsmd.getColumnDisplaySize(i));
            paramHosts.add(paramHost);
        }
        return paramHosts;
    }

}
