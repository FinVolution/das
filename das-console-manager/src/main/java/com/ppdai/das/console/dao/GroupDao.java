package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.DalGroupView;
import com.ppdai.das.console.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GroupDao extends BaseDao {

    public Long insertDasGroup(DasGroup group) throws SQLException {
        if (null == group) {
            return 0L;
        }
        this.getDasClient().insert(group, Hints.hints().setIdBack());
        return group.getId();
    }

    public int initAdminGroup(DasGroup group) throws SQLException {
        if (null == group) {
            return 0;
        }
        String sql = "INSERT INTO `dal_group` (`id`, `group_name`, `group_comment`, `update_user_no`) VALUES(?, ?, ?, ?)";
        return this.updataBysql(sql,
                Parameter.integerOf(StringUtils.EMPTY, group.getId()),
                Parameter.varcharOf(StringUtils.EMPTY, group.getGroup_name()),
                Parameter.varcharOf(StringUtils.EMPTY, group.getGroup_comment()),
                Parameter.varcharOf(StringUtils.EMPTY, group.getUpdate_user_no()));
    }

    public int updateDalGroup(DasGroup group) throws SQLException {
        if (null == group || group.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(group);
    }

    public int deleteDalGroup(Long groupId) throws SQLException {
        DasGroup group = new DasGroup();
        group.setId(groupId);
        return this.getDasClient().deleteByPk(group);
    }


    /**
     * 全局不存在
     */
    public Long getCountByName(String name) throws SQLException {
        return this.getCount("SELECT count(1) FROM dal_group WHERE group_name='" + name + "'");
    }

    /**
     * 其他行不存在此dbname
     */
    public Long getCountByIdAndName(Long id, String name) throws SQLException {
        return this.getCount("SELECT count(1) FROM dal_group WHERE group_name='" + name + "' and id = " + id);
    }

    public List<DasGroup> getGroupsByUserId(Long user_id) throws SQLException {
        String sql = "select t1.id, t1.group_name, t1.group_comment, t1.update_user_no, t1.insert_time from dal_group t1 " +
                "INNER JOIN user_group t2 on t1.id = t2.group_id " +
                "where t2.user_id = ? and t1.id != " + PermissionService.getADMINGROUPID() + " order by group_name";
        return this.queryBySql(sql, DasGroup.class, Parameter.integerOf(StringUtils.EMPTY, user_id));
    }

    public DasGroup getGroupsByDbSetId(Long dbsetId) throws SQLException {
        String sql = "select t1.id, t1.group_name, t1.group_comment, t1.update_user_no, t1.insert_time from dal_group t1 " +
                "inner join databaseset t2 on t2.group_id = t1.id " +
                "where t2.id = " + dbsetId + " limit 1";
        List<DasGroup> list = this.queryBySql(sql, DasGroup.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public DasGroup getDalGroupById(Long id) throws SQLException {
        DasGroup group = new DasGroup();
        group.setId(id);
        return this.getDasClient().queryByPk(group);
    }

    public DasGroup getGroupByName(String name) throws SQLException {
        String sql = "select id, group_name, group_comment, update_user_no, insert_time from dal_group where group_name = '" + name + "' limit 1";
        List<DasGroup> list = this.queryBySql(sql, DasGroup.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public List<DasGroup> getAllGroups() throws SQLException {
        String sql = "select id, group_name, group_comment, update_user_no, insert_time from dal_group order by group_name";
        return this.queryBySql(sql, DasGroup.class);
    }

    public List<DasGroup> getAllGroupsByAppoid(String appid) throws SQLException {
        String sql = "select t1.id, t1.group_name, t1.group_comment, t1.update_user_no, t1.insert_time from dal_group t1\n" +
                "inner join project t2 on t2.dal_group_id = t1.id\n" +
                "where t2.app_id = '" + appid + "'";
        return this.queryBySql(sql, DasGroup.class);
    }

    public List<DasGroup> getAllGroupsWithNotAdmin() throws SQLException {
        String sql = "select id, group_name, group_comment, update_user_no, insert_time from dal_group where id != " + PermissionService.getADMINGROUPID() + " order by group_name";
        return this.queryBySql(sql, DasGroup.class);
    }

    public Long getTotalCount(Paging<DasGroup> paging) throws SQLException {
        String sql = "select count(1) from dal_group t1 " + appenWhere(paging);
        return this.getCount(sql);
    }

    public List<DalGroupView> findGroupPageList(Paging<DasGroup> paging) throws SQLException {
        String sql = "select t1.id, t1.group_name, t1.group_comment, t1.update_user_no, t1.insert_time,t2.user_real_name " +
                "from dal_group t1 " +
                "left join login_users t2 on t1.update_user_no = t2.user_no " + appenCondition(paging);
        return this.queryBySql(sql, DalGroupView.class);
    }

    private String appenWhere(Paging<DasGroup> paging) {
        DasGroup dasGroup = paging.getData();
        if (null == dasGroup) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .likeLeft("group_name", dasGroup.getGroup_name())
                .likeLeft("group_comment", dasGroup.getGroup_comment())
                .builer();
    }

    private String appenCondition(Paging<DasGroup> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance()
                .orderBy(paging)
                .limit(paging)
                .builer();
    }

}
