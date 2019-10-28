package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.das.console.dto.entry.das.DatabaseSetEntry;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.DatabaseSetView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class DatabaseSetDao extends BaseDao {

    public Long insertDatabaseSet(DatabaseSet dbset) throws SQLException {
        if (null == dbset) {
            return 0L;
        }
        this.getDasClient().insert(dbset, Hints.hints().setIdBack());
        return dbset.getId();
    }

    public int updateDatabaseSet(DatabaseSet dbset) throws SQLException {
        if (null == dbset || dbset.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(dbset);
    }

    public int deleteDatabaseSetById(Long dbsetId) throws SQLException {
        DatabaseSet dbset = new DatabaseSet();
        dbset.setId(dbsetId);
        return this.getDasClient().deleteByPk(dbset);
    }

    /**
     * 依据外键databaseSet_Id删除entry
     */
    public int deleteDatabaseSetEntryByDbsetId(Long dbsetId) throws SQLException {
        String sql = "DELETE FROM databasesetentry WHERE dbset_id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, dbsetId));
    }

    public DatabaseSet getDatabaseSetById(Long id) throws SQLException {
        DatabaseSet dbset = new DatabaseSet();
        dbset.setId(id);
        return this.getDasClient().queryByPk(dbset);
    }

    public DatabaseSet getDatabaseSetByName(String name) throws SQLException {
        String sql = "select id, name, db_type, strategy_source, class_name, group_id, update_user_no, update_time FROM databaseset WHERE name = '" + name + "' limit 1";
        List<DatabaseSet> list = this.queryBySql(sql, DatabaseSet.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public List<DatabaseSet> getAllDatabaseSetByName(String name) throws SQLException {
        String sql = "SELECT id, name, db_type, strategy_source, class_name, group_id, update_user_no, update_time FROM databaseset WHERE name = ?";
        return this.queryBySql(sql, DatabaseSet.class, Parameter.varcharOf(StringUtils.EMPTY, name));
    }

    public List<DatabaseSet> getAllDatabaseSetByNames(List<String> names) throws SQLException {
        DatabaseSet.DatabasesetDefinition databaseset = DatabaseSet.DATABASESET;
        SqlBuilder builder = SqlBuilder.selectAllFrom(databaseset).where().allOf(databaseset.name.in(names)).into(DatabaseSet.class);
        return this.getDasClient().query(builder);
    }

    public List<DatabaseSet> getAllDatabaseSetById(Long id) throws SQLException {
        String sql = "SELECT id, name, db_type, strategy_source,dynamic_strategy_id, class_name, group_id, update_user_no, update_time FROM databaseset WHERE id = ?";
        return this.queryBySql(sql, DatabaseSet.class, Parameter.integerOf(StringUtils.EMPTY, id));
    }

    public List<DatabaseSetView> getAllDatabaseSetByProjectId(Long projectId) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t1.db_type,t1.strategy_type, t1.strategy_source, t1.dynamic_strategy_id, t1.class_name, t1.group_id, t1.update_user_no, t1.update_time ,t3.group_name " +
                "FROM databaseset t1 " +
                "inner join project_dbset_relation t2 on t2.dbset_id = t1.id " +
                "inner join dal_group t3 on t1.group_id = t3.id " +
                "where t2.project_id = " + projectId;
        if (projectId == null) {
            sql = "SELECT t1.id, t1.name, t1.db_type, t1.strategy_source, t1.dynamic_strategy_id, t1.class_name, t1.group_id, t1.update_user_no, t1.update_time FROM databaseset t1 order by t1.name";
        }
        return this.queryBySql(sql, DatabaseSetView.class);
    }

    public List<DatabaseSet> getAllDatabaseSetByGroupId(Long groupId) throws SQLException {
        String sql = "SELECT id, name, db_type, strategy_type, strategy_source, dynamic_strategy_id,class_name, group_id, update_user_no, update_time FROM databaseset WHERE group_id = ? order by name";
        return this.queryBySql(sql, DatabaseSet.class, Parameter.integerOf(StringUtils.EMPTY, groupId));
    }

    public DatabaseSetEntry getMasterDatabaseSetEntryByDatabaseSetId(Long dbset_id) throws SQLException {
        String sql = "SELECT en.id, en.name, en.database_type, en.sharding, en.db_Id, en.dbset_id, en.update_user_no, en.update_time " +
                "FROM databasesetentry as en " +
                "WHERE en.database_type = 1 and en.dbset_id = ? limit 1";
        List<DatabaseSetEntry> list = this.queryBySql(sql, DatabaseSetEntry.class, Parameter.integerOf(StringUtils.EMPTY, dbset_id));
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public Long getDbSetTotalCount(Paging<DatabaseSet> paging) throws SQLException {
        String sql = "SELECT count(1)  FROM databaseset t1 " +
                " inner join login_users t2 on t1.update_user_no = t2.user_no " +
                " inner join dal_group t3 on t1.group_id = t3.id  " + appenWhere(paging);
        log.info("getDbSetTotalCount-----> " + sql);
        return this.getCount(sql);
    }

    public List<DatabaseSetView> findDbSetPageList(Paging<DatabaseSet> paging) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t1.db_type, t1.dynamic_strategy_id, t1.strategy_source, t1.class_name, t1.group_id, t1.strategy_type, t1.update_user_no, t1.update_time,t2.user_real_name, t3.group_name FROM databaseset t1 " +
                " inner join login_users t2 on t1.update_user_no = t2.user_no " +
                " inner join dal_group t3 on t1.group_id = t3.id  " + appenCondition(paging);
        log.info("findDbSetPageList----> " + sql);
        return this.queryBySql(sql, DatabaseSetView.class);
    }

    private String appenWhere(Paging<DatabaseSet> paging) {
        DatabaseSet databaseSet = paging.getData();
        if (null == databaseSet) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .equal("group_id", databaseSet.getGroupId())
                .likeLeft("strategy_source", databaseSet.getStrategySource())
                .likeLeft("name", databaseSet.getName())
                .in("strategy_type", databaseSet.getStrategyTypes(), Integer.class.getClass())
                .in("db_type", databaseSet.getDbTypes(), Integer.class.getClass())
                .builer();
    }

    private String appenCondition(Paging<DatabaseSet> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                .orderBy(paging)
                .limit(paging)
                .builer();
    }

    public Long getDbSetTotalCountByAppid(Paging<DatabaseSet> paging) throws SQLException {
        String sql = "SELECT count(1) \n" +
                "FROM databaseset t1 \n" +
                "inner join project_dbset_relation t2 on t2.dbset_id = t1.id \n" +
                "inner join dal_group t3 on t1.group_id = t3.id\n" +
                "inner join project t4 on t2.project_id = t4.id\n" +
                "where t4.app_id = " + paging.getData().getApp_id();
        return this.getCount(sql);
    }

    public List<DatabaseSetView> findDbSetPageListByAppid(Paging<DatabaseSet> paging) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t1.db_type, t1.strategy_source, t1.dynamic_strategy_id, t1.class_name, t1.group_id, t1.update_user_no, t1.update_time ,t3.group_name \n" +
                "FROM databaseset t1 \n" +
                "inner join project_dbset_relation t2 on t2.dbset_id = t1.id \n" +
                "inner join dal_group t3 on t1.group_id = t3.id\n" +
                "inner join project t4 on t2.project_id = t4.id\n" +
                "where t4.app_id = " + paging.getData().getApp_id();
        //log.info("findDbSetPageListByAppid----> " + sql);
        return this.queryBySql(sql, DatabaseSetView.class);
    }

}
