package com.ppdai.platform.das.codegen.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.platform.das.codegen.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.dao.base.BaseDao;
import com.ppdai.platform.das.codegen.dto.entry.das.AppGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.AppGroupView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AppGroupDao extends BaseDao {

    public Long insertAppGroup(AppGroup appGroup) throws SQLException {
        if (appGroup == null) {
            return 0L;
        }
        this.getDasClient().insert(appGroup, Hints.hints().setIdBack());
        return appGroup.getId();
    }

    public AppGroup getAppGroupById(Long id) throws SQLException {
        AppGroup appGroup = new AppGroup();
        appGroup.setId(id);
        return this.getDasClient().queryByPk(appGroup);
    }

    public int updateAppGroup(AppGroup appGroup) throws SQLException {
        if (null == appGroup || appGroup.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(appGroup);
    }

    public int deleteAppGroup(AppGroup appGroup) throws SQLException {
        if (null == appGroup || appGroup.getId() == null) {
            return 0;
        }
        return this.getDasClient().deleteByPk(appGroup);
    }

    /**
     * 全局不存在
     */
    public Long getCountByName(String name) throws SQLException {
        String sql = "SELECT count(1) FROM app_group WHERE name='" + name + "'";
        return this.getCount(sql);
    }

    /**
     * 其他行不存在此dbname
     */
    public Long getCountByIdAndName(Long id, String name) throws SQLException {
        String sql = "SELECT count(1) FROM app_group WHERE name='" + name + "' and id = " + id;
        return this.getCount(sql);
    }

    public int changeServerGroup(Long newServerGroupId, Set<Long> appGroupIds) throws SQLException {
        if (CollectionUtils.isEmpty(appGroupIds)) {
            return 1;
        }
        String sql = "UPDATE app_group SET server_group_id = ? WHERE id in (" + StringUtil.joinCollectByComma(appGroupIds) + ") ";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, newServerGroupId));
    }

    public List<AppGroup> getAppGroupsByServerGroupId(Long serverGroupId) throws SQLException {
        String sql = "SELECT id, server_enabled, server_group_id, name, comment, insert_time, update_time FROM app_group where server_group_id = " + serverGroupId;
        return this.queryBySql(sql, AppGroup.class);
    }

    public Long getAppGroupTotalCount(Paging<AppGroup> paging) throws SQLException {
        String sql = " SELECT count(1) FROM app_group t1 " +
                " left join server_group t2 on t2.id = t1.server_group_id " +
                " left join (select app_group_id, group_concat(name) as name, group_concat(id) as id from project group by app_group_id) t3 on t3.app_group_id = t1.id " +
                " left join login_users t4 on t1.update_user_no = t4.user_no " + this.appenWhere(paging);
        log.info("getAppGroupTotalCount-----> " + sql);
        return this.getCount(sql);
    }

    public List<AppGroupView> findAppGroupPageList(Paging<AppGroup> paging) throws SQLException {
        String sql = " SELECT t1.id, t1.server_enabled, t1.server_group_id, t1.name, t1.comment, t1.insert_time, t1.update_time, t2.name as server_group_name , t3.name as project_names, t3.id as project_ids,t4.user_real_name " +
                "FROM app_group t1 " +
                " left join server_group t2 on t2.id = t1.server_group_id " +
                " left join (select app_group_id, group_concat(name) as name, group_concat(id) as id from project group by app_group_id) t3 on t3.app_group_id = t1.id " +
                " left join login_users t4 on t1.update_user_no = t4.user_no" + appenCondition(paging);
        log.info("findAppGroupPageList-----> " + sql);
        return this.queryBySql(sql, AppGroupView.class);
    }

    private String appenWhere(Paging<AppGroup> paging) {
        AppGroup appGroup = paging.getData();
        if (null == appGroup) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .likeLeft("name", appGroup.getName())
                .likeLeft("comment", appGroup.getComment())
                .in("server_enabled", appGroup.getServerEnableds(), Integer.class.getClass())
                .setTab("t2.")
                .likeLeft("name", appGroup.getServerGroupName())
                .setTab("t3.")
                .like("name", appGroup.getProjectNames())
                .builer();
    }

    private String appenCondition(Paging<AppGroup> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
