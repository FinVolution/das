package com.ppdai.platform.das.console.dao;

import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.platform.das.console.common.utils.DbUtil;
import com.ppdai.platform.das.console.common.utils.ResourceUtil;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class SetupDatabaseDao extends BaseDao {

    public boolean executeSqlScript(String sqlScript) throws SQLException {
        if (StringUtils.isBlank(sqlScript)) {
            return false;
        }
        return this.updataBysql(sqlScript) > 0;
    }

    public Set<String> getCatalogTableNames(String catalog) throws Exception {
        Set<String> set = new HashSet<>();
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = DasConfigureFactory.getConfigure(ResourceUtil.DAS_SET_APPID).getConnectionLocator().getConnection(ResourceUtil.DATA_BASE);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            if (databaseMetaData == null) {
                return set;
            }
            resultSet = databaseMetaData.getTables(catalog, null, null, null);
            if (resultSet != null) {
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    if (tableName != null && tableName.length() > 0) {
                        set.add(tableName.toUpperCase());
                    }
                }
            }
        } finally {
            DbUtil.close(resultSet);
            DbUtil.close(connection);
        }
        return set;
    }

}
