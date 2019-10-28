package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.Server;
import com.ppdai.das.console.dto.entry.das.ServerGroup;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.ServerGroupView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class ServerGroupDao extends BaseDao {

    public Long insertServerGroup(ServerGroup serverGroup) throws SQLException {
        if (serverGroup == null) {
            return 0L;
        }
        this.getDasClient().insert(serverGroup, Hints.hints().setIdBack());
        return serverGroup.getId();
    }

    public ServerGroup getServerGroupById(Long id) throws SQLException {
        ServerGroup serverGroup = new ServerGroup();
        serverGroup.setId(id);
        return this.getDasClient().queryByPk(serverGroup);
    }

    public List<ServerGroup> getAllServerGroups() throws SQLException {
        ServerGroup.ServerGroupDefinition p = ServerGroup.SERVERGROUP;
        SqlBuilder builder = SqlBuilder.selectAllFrom(p).into(ServerGroup.class);
        return this.getDasClient().query(builder);
    }

    /**
     * 全局不存在
     */
    public Long getCountByName(String name) throws SQLException {
        String sql = "SELECT count(1) FROM server_group WHERE name='" + name + "'";
        return this.getCount(sql);
    }

    /**
     * 其他行不存在此dbname
     */
    public Long getCountByIdAndName(Long id, String name) throws SQLException {
        String sql = "SELECT count(1) FROM server_group WHERE name='" + name + "' and id = " + id;
        return this.getCount(sql);
    }

    public int updateServerGroup(ServerGroup serverGroup) throws SQLException {
        if (null == serverGroup || serverGroup.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(serverGroup);
    }

    public int deleteServerGroup(ServerGroup serverGroup) throws SQLException {
        return this.getDasClient().deleteByPk(serverGroup);
    }

    /**
     * 依据serverGroup id 删除依据serverGroup、server、serveronfig 和 server
     */
    public int deleteServerGroupAndServerAndServerConfigByServerGroupId(Long serverGroupId) throws SQLException {
        String sql = "DELETE t1, t2, t3 " +
                "from `server_group` t1 " +
                "left join `server` t2 on t1.id = t2.server_group_id " +
                "left join `server_config` t3 on t2.id = t3.server_id " +
                "where t1.id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, serverGroupId));
    }

    /**
     * 未分组的
     */
    public List<Server> serversNoGroup(Integer appGroupId) throws SQLException {
        String cond = StringUtils.EMPTY;
        if (null != appGroupId && appGroupId > 0) {
            cond = " and id != " + appGroupId;
        }
        String sql = "select id, name, comment, insert_time, update_time from server_group where id not in (select server_group_id from app_group where server_group_id != 0 " + cond + ")";
        return this.queryBySql(sql, Server.class);
    }

    public Long getServerGroupTotalCount(Paging<ServerGroup> paging) throws SQLException {
        String sql = "SELECT count(1) FROM server_group t1 " +
                "left join login_users t2 on t1.update_user_no = t2.user_no " + appenWhere(paging);
        return this.getCount(sql);
    }

    public List<ServerGroupView> findServerGroupPageList(Paging<ServerGroup> paging) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t1.comment, t1.insert_time, t1.update_time, t2.user_real_name FROM server_group t1 " +
                "left join login_users t2 on t1.update_user_no = t2.user_no " + appenCondition(paging);
        return this.queryBySql(sql, ServerGroupView.class);
    }

    private String appenWhere(Paging<ServerGroup> paging) {
        ServerGroup project = paging.getData();
        if (null == project) {
            return org.apache.commons.lang.StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .likeLeft("name", project.getName())
                .likeLeft("comment", project.getComment())
                .builer();
    }

    private String appenCondition(Paging<ServerGroup> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance()
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
