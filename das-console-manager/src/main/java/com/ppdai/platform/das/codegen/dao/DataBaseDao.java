package com.ppdai.platform.das.codegen.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.platform.das.codegen.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.codegen.dao.base.BaseDao;
import com.ppdai.platform.das.codegen.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.DataBaseView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class DataBaseDao extends BaseDao {

    public Long insertDataBaseInfo(DataBaseInfo dataBaseInfo) throws SQLException {
        if (null == dataBaseInfo) {
            return 0L;
        }
        this.getDasClient().insert(dataBaseInfo, Hints.hints().setIdBack());
        return dataBaseInfo.getId();
    }

    public int[] insertDatabaselist(List<DataBaseInfo> dataBaseInfoList) throws SQLException {
        if (CollectionUtils.isEmpty(dataBaseInfoList)) {
            return new int[0];
        }
        return this.getDasClient().batchInsert(dataBaseInfoList);
    }

    public int updateDataBaseInfo(DataBaseInfo dataBaseInfo) throws SQLException {
        return this.updateDataBaseInfo(dataBaseInfo.getId(), dataBaseInfo.getDbname(), dataBaseInfo.getDb_address(), dataBaseInfo.getDb_port(), dataBaseInfo.getDb_user(), dataBaseInfo.getDb_password(), dataBaseInfo.getDb_catalog(), dataBaseInfo.getDb_type(), dataBaseInfo.getComment(), dataBaseInfo.getUpdateUserNo());
    }

    public int updateDataBaseInfo(Long id, String dbname, String db_address, String db_port, String db_user,
                                  String db_password, String db_catalog, Integer db_type, String comment, String updateUserNo) throws SQLException {
        String sql = "UPDATE alldbs SET db_name=?, db_address=?, db_port=?, db_user=?, db_password=?, db_catalog=?, db_type=?, comment=?, update_user_no=? WHERE id=?";
        return this.updataBysql(sql,
                Parameter.varcharOf(StringUtils.EMPTY, dbname),
                Parameter.varcharOf(StringUtils.EMPTY, db_address),
                Parameter.varcharOf(StringUtils.EMPTY, db_port),
                Parameter.varcharOf(StringUtils.EMPTY, db_user),
                Parameter.varcharOf(StringUtils.EMPTY, db_password),
                Parameter.varcharOf(StringUtils.EMPTY, db_catalog),
                Parameter.integerOf(StringUtils.EMPTY, db_type),
                Parameter.varcharOf(StringUtils.EMPTY, comment),
                Parameter.varcharOf(StringUtils.EMPTY, updateUserNo),
                Parameter.integerOf(StringUtils.EMPTY, id));
    }

    public int updateDataBaseInfo(Long id, Long groupId, String comment) throws SQLException {
        String sql = "UPDATE alldbs SET dal_group_id=?, comment=? WHERE id=?";
        return this.updataBysql(sql,
                Parameter.integerOf(StringUtils.EMPTY, groupId),
                Parameter.varcharOf(StringUtils.EMPTY, comment),
                Parameter.integerOf(StringUtils.EMPTY, id));
    }

    public int updateDataBaseInfo(Long id, String comment) throws SQLException {
        String sql = "UPDATE alldbs SET comment=? WHERE id=?";
        return this.updataBysql(sql,
                Parameter.varcharOf(StringUtils.EMPTY, comment),
                Parameter.integerOf(StringUtils.EMPTY, id));
    }

    public int updateDataBaseInfo(Long id, Long groupId) throws SQLException {
        String sql = "UPDATE alldbs SET dal_group_id=? WHERE id=?";
        return this.updataBysql(sql,
                Parameter.integerOf(StringUtils.EMPTY, groupId),
                Parameter.integerOf(StringUtils.EMPTY, id));
    }

    public int deleteDataBaseInfo(Long id) throws SQLException {
        DataBaseInfo dataBaseInfo = new DataBaseInfo();
        dataBaseInfo.setId(id);
        return this.getDasClient().deleteByPk(dataBaseInfo);
    }

    public DataBaseInfo getDataBaseInfoByDbId(Long id) throws SQLException {
        DataBaseInfo dataBaseInfo = new DataBaseInfo();
        dataBaseInfo.setId(id);
        return this.getDasClient().queryByPk(dataBaseInfo);
    }

    public DataBaseInfo getDatabaseByName(String name) throws SQLException {
        String sql = "select id, db_name, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_type from alldbs where db_name = '" + name + "' limit 1";
        List<DataBaseInfo> list = this.queryBySql(sql, DataBaseInfo.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public List<DataBaseInfo> getAllDbByProjectId(long projectId) throws SQLException {
        String sql = "select t1.id, t1.db_name, t1.comment,t1.dal_group_id, t1.db_address, t1.db_port, t1.db_user, t1.db_password, t1.db_catalog, t1.db_type from alldbs t1\n" +
                "inner join (\n" +
                "select distinct a1.db_Id from databasesetentry a1\n" +
                "inner join databaseset a2 on a1.dbset_id = a2.id\n" +
                "inner join project_dbset_relation a3 on a3.dbset_id = a2.id\n" +
                "inner join project a4 on a4.id = project_id\n" +
                "where a4.id = " + projectId +
                ") t2 on t1.id= t2.db_Id";
        return this.queryBySql(sql, DataBaseInfo.class);
    }

    public List<DataBaseInfo> getAllDbByAppId(String appid) throws SQLException {
        String sql = "select t1.id, t1.db_name, t1.comment,t1.dal_group_id, t1.db_address, t1.db_port, t1.db_user, t1.db_password, t1.db_catalog, t1.db_type from alldbs t1\n" +
                "inner join (\n" +
                "select distinct a1.db_Id from databasesetentry a1\n" +
                "inner join databaseset a2 on a1.dbset_id = a2.id\n" +
                "inner join project_dbset_relation a3 on a3.dbset_id = a2.id\n" +
                "inner join project a4 on a4.id = project_id\n" +
                "where a4.app_id = '" + appid +
                "') t2 on t1.id= t2.db_Id";
        return this.queryBySql(sql, DataBaseInfo.class);
    }

    public List<String> getAllDbAllinOneNames() throws SQLException {
        String sql = "SELECT db_name FROM alldbs";
        return this.queryBySql(sql, String.class);
    }

    public List<DataBaseInfo> getGroupDBsByGroup(Long groupId) throws SQLException {
        String sql = "SELECT id, db_name, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_type FROM alldbs WHERE dal_group_id=? order by db_name";
        return this.queryBySql(sql, DataBaseInfo.class, Parameter.integerOf(StringUtils.EMPTY, groupId));
    }

    /**
     * 全局不存在
     */
    public Long getCountByName(String dbname) throws SQLException {
        return this.getCount("SELECT count(1) FROM alldbs WHERE db_name='" + dbname + "'");
    }

    /**
     * 其他行不存在此dbname
     */
    public Long getCountByIdAndName(Long id, String dbname) throws SQLException {
        return this.getCount("SELECT count(1) FROM alldbs WHERE db_name='" + dbname + "' and id = " + id);
    }

    public List<DataBaseInfo> getAllDbsByDbNames(List<String> dbNames) throws SQLException {
        DataBaseInfo.DataBaseInfoDefinition databaseinfo = DataBaseInfo.DATABASEINFO;
        SqlBuilder builder = SqlBuilder.selectAllFrom(databaseinfo).where().allOf(databaseinfo.dbName.in(dbNames)).into(DataBaseInfo.class);
        return this.getDasClient().query(builder);
    }

    public List<DataBaseInfo> getAllDbsByIdss(List<Long> ids) throws SQLException {
        DataBaseInfo.DataBaseInfoDefinition databaseinfo = DataBaseInfo.DATABASEINFO;
        SqlBuilder builder = SqlBuilder.selectAllFrom(databaseinfo).where().allOf(databaseinfo.id.in(ids)).into(DataBaseInfo.class);
        return this.getDasClient().query(builder);
    }

    public DataBaseInfo getMasterCologByDatabaseSetId(Long dbset_id) throws SQLException {
        String sql = "SELECT db.id, db.db_name, db.dal_group_id, db.db_address, db.db_port, db.db_user, db.db_catalog, db.db_password, db.db_type, db.insert_time, db.update_time, db.comment " +
                "FROM alldbs as db " +
                "inner join databasesetentry as en on en.db_Id = db.id " +
                "WHERE en.database_type = 1 and en.dbset_id = " + dbset_id + " order by en.id asc limit 1 ";
        List<DataBaseInfo> list = this.queryBySql(sql, DataBaseInfo.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public Long getTotalCount(Paging<DataBaseInfo> paging) throws SQLException {
        String sql = "SELECT count(1) FROM alldbs a LEFT JOIN dal_group b ON b.id = a.dal_group_id " + appenWhere(paging);
        log.info("getTotalCount--->" + sql);
        return this.getCount(sql);
    }

    public List<DataBaseView> findDbPageList(Paging<DataBaseInfo> paging) throws SQLException {
        String sql = "SELECT a.id,a.db_name,a.dal_group_id,a.db_address,a.db_port,a.db_user,a.db_password,a.db_catalog,a.db_type,a.comment,b.group_name,a.insert_time, c.user_real_name,a.update_time " +
                "FROM alldbs a LEFT JOIN dal_group b ON b.id = a.dal_group_id " +
                "left join login_users c on a.update_user_no = c.user_no " + appenCondition(paging);
        return this.queryBySql(sql, DataBaseView.class);
    }

    public Long getTotalCountByUserId(Paging<DataBaseInfo> paging, Long userId) throws SQLException {
        String sql = "SELECT count(1) FROM alldbs a " +
                "inner join user_group d on d.group_id =  a.dal_group_id and d.user_id =" + userId +
                " LEFT JOIN dal_group b ON b.id = a.dal_group_id " + appenWhere(paging);
        return this.getCount(sql);
    }

    public List<DataBaseView> findDbPageListByUserId(Paging<DataBaseInfo> paging, Long userId) throws SQLException {
        String sql = "SELECT a.id,a.db_name,a.dal_group_id,a.db_address,a.db_port,a.db_user,a.db_password,a.db_catalog,a.db_type,a.comment,b.group_name,a.insert_time, c.user_real_name,a.update_time " +
                "FROM alldbs a " +
                "inner join user_group d on d.group_id =  a.dal_group_id and d.user_id =" + userId +
                " LEFT JOIN dal_group b ON b.id = a.dal_group_id " +
                "left join login_users c on a.update_user_no = c.user_no " + appenCondition(paging);
        return this.queryBySql(sql, DataBaseView.class);
    }

    private String appenWhere(Paging<DataBaseInfo> paging) {
        DataBaseInfo dataBaseInfo = paging.getData();
        if (null == dataBaseInfo) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("a.").where()
                .equal("dal_group_id", dataBaseInfo.getDal_group_id())
                .likeLeft("db_name", dataBaseInfo.getDbname())
                .likeLeft("db_address", dataBaseInfo.getDb_address())
                .likeLeft("db_port", dataBaseInfo.getDb_port())
                .likeLeft("db_catalog", dataBaseInfo.getDb_catalog())
                .likeLeft("comment", dataBaseInfo.getComment())
                .in("db_type", dataBaseInfo.getDb_types(), Integer.class.getClass())
                .rangeData("insert_time", dataBaseInfo.getInsert_times())
                .setTab("b.")
                .likeLeft("group_name", dataBaseInfo.getGroup_name())
                .builer();
    }

    private String appenCondition(Paging<DataBaseInfo> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("a.")
                .orderBy(paging)
                .limit(paging)
                .builer();
    }

    public List<DataBaseInfo> getDatabaseListByLikeName(String dbname) throws SQLException {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT id, db_name, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_type FROM alldbs ");
        if (StringUtils.isNotBlank(dbname)) {
            sql.append("WHERE db_name like '" + dbname + "%' limit 10");
        } else {
            sql.append("limit 10");
        }
        return this.queryBySql(sql.toString(), DataBaseInfo.class);
    }
}
