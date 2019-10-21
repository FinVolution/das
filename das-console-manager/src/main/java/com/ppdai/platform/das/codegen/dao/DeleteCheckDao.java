package com.ppdai.platform.das.codegen.dao;


import com.ppdai.platform.das.codegen.dao.base.BaseDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DeleteCheckDao extends BaseDao {

    /**
     * task_sql 表 dbset_id
     */
    public boolean isDbsetIdInTaskSQL(Long dbsetId) throws SQLException {
        return isIdInTable("task_sql", "dbset_id", dbsetId);
    }

    /**
     * task_sql 表 project_id , dbset_id
     */
    public boolean isProjectAndDbsetIdInTaskSQL(Long projectId, Long dbsetId) throws SQLException {
        return isIdInTable("task_sql", new HashMap<String , Long>(){{
            put("project_id", projectId);
            put("dbset_id", dbsetId);
        }});
    }

    public boolean isDbsetIdInTaskSQL(Long projectId, Set<Long> dbsetIds) throws SQLException {
        if (CollectionUtils.isNotEmpty(dbsetIds)) {
            for (Long id : dbsetIds) {
                if (this.isProjectAndDbsetIdInTaskSQL(projectId, id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDbsetIdInTaskSQL(Set<Long> dbsetIds) throws SQLException {
        if (CollectionUtils.isNotEmpty(dbsetIds)) {
            for (Long id : dbsetIds) {
                if (this.isDbsetIdInTaskSQL(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * task_table 表 dbset_id
     */
    public boolean isDbsetIdInTaskTable(Long dbsetId) throws SQLException {
        return isIdInTable("task_table", "dbset_id", dbsetId);
    }

    /**
     * task_table 表 project_id, dbset_id
     */
    public boolean isDbsetIdInTaskTable(Long projectId, Long dbsetId) throws SQLException {
        return isIdInTable("task_table", new HashMap<String , Long>(){{
            put("project_id", projectId);
            put("dbset_id", dbsetId);
        }});
    }

    public boolean isDbsetIdInTaskTable(Long projectId, Set<Long> dbsetIds) throws SQLException {
        if (CollectionUtils.isNotEmpty(dbsetIds)) {
            for (Long id : dbsetIds) {
                if (this.isDbsetIdInTaskTable(projectId, id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDbsetIdInTaskTable(Set<Long> dbsetIds) throws SQLException {
        if (CollectionUtils.isNotEmpty(dbsetIds)) {
            for (Long id : dbsetIds) {
                if (this.isDbsetIdInTaskTable(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * project_dbset_relation 表 dbset_id
     */
    public boolean isDbsetIdInProjectDbsetRelation(Long dbsetId) throws SQLException {
        return isIdInTable("project_dbset_relation", "dbset_id", dbsetId);
    }

    /**
     * databasesetentry 表 dbset_id
     */
    public boolean isDbsetIdInDatabasesetentry(Long dbsetId) throws SQLException {
        return isIdInTable("databasesetentry", "dbset_id", dbsetId);
    }

    /**
     * app_group 表 server_group_id
     */
    public boolean isServerGroupIdInAppGroup(Long serverGroupId) throws SQLException {
        return isIdInTable("app_group", "server_group_id", serverGroupId);
    }

    /**
     * server 表 server_group_id
     */
    public boolean isServerGroupIdInServer(Long serverGroupId) throws SQLException {
        return isIdInTable("server", "server_group_id", serverGroupId);
    }

    /**
     * databasesetentry 表 db_Id
     */
    public boolean isDbsetEntryIdInProject(Long dbId) throws SQLException {
        return isIdInTable("databasesetentry", "db_Id", dbId);
    }

    /**
     * project 表 app_group_id
     */
    public boolean isDbsetIdInProject(Long appGroupId) throws SQLException {
        return isIdInTable("project", "app_group_id", appGroupId);
    }

    /**
     * project_dbset_relation  表 project_id
     */
    public boolean isProjectIdInProjectDbsetRelatio(Long projectId) throws SQLException {
        return isIdInTable("project_dbset_relation", "project_id", projectId);
    }

    /**
     * task_table  表 project_id
     */
    public boolean isProjectIdInTaskTable(Long projectId) throws SQLException {
        return isIdInTable("task_table", "project_id", projectId);
    }

    /**
     * task_sql 表 project_id
     */
    public boolean isProjectIdInTaskSQL(Long projectId) throws SQLException {
        return isIdInTable("task_sql", "project_id", projectId);
    }

    private boolean isIdInTable(String table, String name, Long id) throws SQLException {
        String sql = getIsIdInTableSql(table, name, id);
        return this.getCount(sql) > 0;
    }

    private boolean isIdInTable(String table, Map<String, Long> fields) throws SQLException {
        String sql = getIsIdInTableSql(table, fields);
        return this.getCount(sql) > 0;
    }

    private String getIsIdInTableSql(String table, String name, Long id) {
        return "select count(1) from " + table + " where " + name + "=" + id;
    }

    private String getIsIdInTableSql(String table, Map<String, Long> fields) {
        StringBuffer sql = new StringBuffer("select count(1) from " + table + " where 1=1");
        for (Map.Entry<String, Long> entry : fields.entrySet()) {
            sql.append(" and " + entry.getKey() + "=" + entry.getValue());
        }
        return sql.toString();
    }

}
