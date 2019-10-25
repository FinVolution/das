package com.ppdai.platform.das.console.common.codeGen.host.java;

import com.ppdai.das.core.client.DalResultSetExtractor;
import com.ppdai.platform.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.platform.das.console.common.codeGen.enums.DatabaseCategory;
import com.ppdai.platform.das.console.common.codeGen.host.AbstractParameterHost;
import com.ppdai.platform.das.console.common.codeGen.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import microsoft.sql.DateTimeOffset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JavaColumnNameResultSetExtractor implements DalResultSetExtractor<List<AbstractParameterHost>> {
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String ORDINAL_POSITION = "ORDINAL_POSITION";
    private static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
    private static final String REMARKS = "REMARKS";
    private static final String COLUMN_DEF = "COLUMN_DEF";
    private static final String DATA_TYPE = "DATA_TYPE";
    private static final String COLUMN_SIZE = "COLUMN_SIZE";

    private Long alldbs_id;
    private String tableName;
    private DatabaseCategory dbCategory;

    public JavaColumnNameResultSetExtractor(Long alldbs_id, String tableName, DatabaseCategory dbCategory) {
        super();
        this.alldbs_id = alldbs_id;
        this.tableName = tableName;
        this.dbCategory = dbCategory;
    }

    @Override
    public List<AbstractParameterHost> extract(ResultSet rs) throws SQLException {
        List<AbstractParameterHost> allColumns = new ArrayList<>();
        try {
            boolean isMySql = DbUtils.isMySqlServer(alldbs_id);
            Map<String, Integer> columnSqlType = DbUtils.getColumnSqlType(alldbs_id, tableName);
            Map<String, Class<?>> typeMapper = DbUtils.getSqlType2JavaTypeMaper(alldbs_id, tableName);
            Map<String, String> columnComment;
            columnComment = DbUtils.getSqlserverColumnComment(alldbs_id, tableName);

            if (columnSqlType != null && columnSqlType.size() > 0) {
                while (rs.next()) {
                    JavaParameterHost host = new JavaParameterHost();
                    String typeName = rs.getString(TYPE_NAME);
                    String columnName = rs.getString(COLUMN_NAME);
                    host.setName(columnName);
                    host.setSqlType(columnSqlType.get(host.getName()));
                    Class<?> javaClass = null;
                    if (typeMapper != null && typeMapper.containsKey(host.getName())) {
                        javaClass = typeMapper.get(host.getName());
                    } else {
                        javaClass = CodeGenConsts.jdbcSqlTypeToJavaClass.get(host.getSqlType());
                    }

                    int columnSize = rs.getInt(COLUMN_SIZE);
                    // host.setLength(columnSize);
                    if (javaClass == null) {
                        if (typeName != null && typeName.equalsIgnoreCase("sql_variant")) {
                            return null;
                        } else if (typeName != null && typeName.equalsIgnoreCase("datetimeoffset")) {
                            javaClass = DateTimeOffset.class;
                        } else {
                            return null;
                        }
                    }

                    // bit to byte[]
                    if (isMySql && typeName.equals("BIT") && columnSize > 1) {
                        javaClass = byte[].class;
                    }

                    host.setJavaClass(javaClass);
                    host.setIndex(rs.getInt(ORDINAL_POSITION));
                    host.setIdentity(rs.getString(IS_AUTOINCREMENT).equalsIgnoreCase("YES"));
                    String remarks = rs.getString(REMARKS);
                    if (remarks == null) {
                        String description = columnComment.get(columnName.toLowerCase());
                        remarks = description == null ? "" : description;
                    }

                    host.setComment(remarks.replace("\n", " "));
                    host.setDefaultValue(rs.getString(COLUMN_DEF));
                    host.setDbCategory(dbCategory);
                    int dataType = rs.getInt(DATA_TYPE);
                    host.setDataType(dataType);
                    allColumns.add(host);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return allColumns;
    }

}
