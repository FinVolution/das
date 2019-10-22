package com.ppdai.platform.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.platform.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import com.ppdai.platform.das.console.dto.entry.das.DatabaseSetEntry;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.DatabaseSetEntryView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class DataBaseSetEntryDao extends BaseDao {

    public Long insertDatabaseSetEntry(DatabaseSetEntry dbsetEntry) throws SQLException {
        if (null == dbsetEntry) {
            return 0L;
        }
        this.getDasClient().insert(dbsetEntry, Hints.hints().setIdBack());
        return dbsetEntry.getId();
    }

    public int[] insertDatabaseSetEntrylist(List<DatabaseSetEntry> dbsetEntryList) throws SQLException {
        if (CollectionUtils.isEmpty(dbsetEntryList)) {
            return new int[0];
        }
        return this.getDasClient().batchInsert(dbsetEntryList);
    }

    public int updateDatabaseSetEntry(DatabaseSetEntry dbsetEntry) throws SQLException {
        if (null == dbsetEntry) {
            return 0;
        }
        return this.getDasClient().update(dbsetEntry);
    }

    /**
     * 根据主键id删除entry
     */
    public int deleteDatabaseSetEntryById(Long id) throws SQLException {
        DatabaseSetEntry entry = new DatabaseSetEntry();
        entry.setId(id);
        return this.getDasClient().deleteByPk(entry);
    }

    /**
     * 全局不存在
     */
    public Long getCountByName(String name) throws SQLException {
        String sql = "SELECT count(1) FROM databasesetentry WHERE name='" + name + "'";
        return this.getCount(sql);
    }

    /**
     * 其他行不存在此name
     */
    public Long getCountByIdAndName(Long id, String name) throws SQLException {
        String sql = "SELECT count(1) FROM databasesetentry WHERE name='" + name + "' and id = " + id;
        return this.getCount(sql);
    }

    public List<DatabaseSetEntry> getAllDbSetEntryByDbSetId(Long dbset_id) throws SQLException {
        String sql = "SELECT id, name, database_type, sharding, db_Id, dbset_id, update_user_no, update_time FROM databasesetentry WHERE dbset_id = ?";
        return this.queryBySql(sql, DatabaseSetEntry.class, Parameter.integerOf(StringUtils.EMPTY, dbset_id));
    }

    public DatabaseSetEntry getDataBaseSetEntryById(Long id) throws SQLException {
        String sql = "SELECT t1.id, name, t1.database_type, t1.sharding, t1.db_Id, t1.dbset_id, t1.update_user_no, t1.update_time , t2.db_catalog FROM databasesetentry t1 " +
                "left join alldbs t2 on t2.id = t1.db_Id where t1.id = " + id;
        List<DatabaseSetEntry> list = this.queryBySql(sql, DatabaseSetEntry.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public DatabaseSetEntryView getDataBaseSetEntryByDbId(Long dataBaseId) throws SQLException {
        String sql = "SELECT t1.id, name, t1.database_type, t1.sharding, t1.db_Id, t1.dbset_id, t1.update_user_no, t1.update_time , t2.db_catalog FROM databasesetentry t1 " +
                "left join alldbs t2 on t2.id = t1.db_Id where t1.db_Id = " + dataBaseId + " limit 1";
        List<DatabaseSetEntryView> list = this.queryBySql(sql, DatabaseSetEntryView.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public List<DatabaseSetEntryView> getDataBaseSetEntryListByDbSetId(Long dbsetId) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t2.db_name, t1.database_type, t1.sharding, t1.db_Id, t1.dbset_id, t1.update_user_no, t1.update_time , t2.db_catalog FROM databasesetentry t1 " +
                "inner join alldbs t2 on t2.id = t1.db_Id where t1.dbset_id = " + dbsetId;
        return this.queryBySql(sql, DatabaseSetEntryView.class);
    }

    public List<DatabaseSetEntry> getAllDbSetEntryByDbSetIds(List<Integer> dbset_ids) throws SQLException {
        DatabaseSetEntry.DatabasesetentryDefinition databasesetentry = DatabaseSetEntry.DATABASESETENTRY;
        SqlBuilder builder = SqlBuilder.selectAllFrom(databasesetentry).where().allOf(databasesetentry.id.in(dbset_ids)).into(DatabaseSetEntry.class);
        return this.getDasClient().query(builder);
    }

    public List<DatabaseSetEntry> getDatabaseSetEntrysByDbNames(List<String> names) throws SQLException {
        DatabaseSetEntry.DatabasesetentryDefinition databasesetentry = DatabaseSetEntry.DATABASESETENTRY;
        SqlBuilder builder = SqlBuilder.selectAllFrom(databasesetentry).where().allOf(databasesetentry.name.in(names)).into(DatabaseSetEntry.class);
        return this.getDasClient().query(builder);
    }

    public Long getDbSetEntryTotalCount(Paging<DatabaseSetEntry> paging) throws SQLException {
        String sql = "SELECT count(1) FROM databasesetentry t1 " +
                "left join alldbs t2 on t1.db_Id = t2.id " + appenWhere(paging);
        log.info("getDbSetEntryTotalCount-----> " + sql);
        return this.getCount(sql);
    }

    public List<DatabaseSetEntryView> findDbSetEntryPageList(Paging<DatabaseSetEntry> paging) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t1.database_type, t1.sharding, t1.db_Id, t1.dbset_id, t1.update_user_no, t1.update_time, t2.db_name, t2.db_catalog, t2.dal_group_id as groupId, t3.user_real_name, t4.name dbset_name " +
                "FROM databasesetentry t1 " +
                "inner join alldbs t2 on t1.db_Id = t2.id " +
                "left join login_users t3 on t1.update_user_no = t3.user_no " +
                "inner join databaseset t4 on t4.id = t1.dbset_id " + appenCondition(paging);
        log.info("findDbSetEntryPageList-----> " + sql);
        return this.queryBySql(sql, DatabaseSetEntryView.class);
    }

    private String appenWhere(Paging<DatabaseSetEntry> paging) {
        DatabaseSetEntry databasesetentry = paging.getData();
        if (null == databasesetentry) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .equal("dbset_id", databasesetentry.getDbset_id())
                .likeLeft("name", databasesetentry.getName())
                .likeLeft("sharding", databasesetentry.getSharding())
                .in("database_type", databasesetentry.getDatabaseTypes(), Integer.class.getClass())
                .setTab("t2.")
                .likeLeft("db_catalog", databasesetentry.getDb_catalog())
                .builer();
    }

    private String appenCondition(Paging<DatabaseSetEntry> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance()
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
