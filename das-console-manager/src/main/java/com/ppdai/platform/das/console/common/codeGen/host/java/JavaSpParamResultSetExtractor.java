package com.ppdai.platform.das.console.common.codeGen.host.java;

import com.ppdai.das.core.client.DalResultSetExtractor;
import com.ppdai.platform.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.platform.das.console.common.codeGen.enums.ParameterDirection;
import com.ppdai.platform.das.console.common.codeGen.host.AbstractParameterHost;
import com.ppdai.platform.das.console.common.codeGen.utils.DbUtils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JavaSpParamResultSetExtractor implements DalResultSetExtractor<List<AbstractParameterHost>> {
    private Long alldbs_id;
    private String spName;

    public JavaSpParamResultSetExtractor(Long alldbs_id, String spName) {
        super();
        this.alldbs_id = alldbs_id;
        this.spName = spName;
    }

    @Override
    public List<AbstractParameterHost> extract(ResultSet rs) throws SQLException {
        List<AbstractParameterHost> parameters = new ArrayList<>();
        try {
            while (rs.next()) {
                int paramMode = rs.getShort("COLUMN_TYPE");
                if (!DbUtils.validMode.contains(paramMode)) {
                    continue;
                }

                JavaParameterHost host = new JavaParameterHost();
                host.setSqlType(rs.getInt("DATA_TYPE"));

                if (paramMode == DatabaseMetaData.procedureColumnIn) {
                    host.setDirection(ParameterDirection.Input);
                } else if (paramMode == DatabaseMetaData.procedureColumnInOut) {
                    host.setDirection(ParameterDirection.InputOutput);
                } else {
                    host.setDirection(ParameterDirection.Output);
                }

                host.setName(rs.getString("COLUMN_NAME").replace("@", ""));
                Class<?> javaClass = CodeGenConsts.jdbcSqlTypeToJavaClass.get(host.getSqlType());
                if (null == javaClass) {
                    if (-153 == host.getSqlType()) {
                        /*
                         * log.error(String.format("The Table-Valued Parameters is not support for JDBC. [%s, %s]",
                         * allInOneName, spName));
                         */
                    } else {
                        /*
                         * log.fatal(String.format("The java type cant be mapped.[%s, %s, %s, %s, %s]", host.getName(),
                         * allInOneName, spName, host.getSqlType(), javaClass));
                         */
                    }
                    return null;
                }
                host.setJavaClass(javaClass);
                parameters.add(host);
            }
        } catch (Throwable e) {
            throw e;
        }
        return parameters;
    }

}
