package com.ppdai.platform.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.platform.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import com.ppdai.platform.das.console.dto.entry.das.Server;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.ServerView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class ServerDao extends BaseDao {

    public Server getServerById(Long id) throws SQLException {
        Server server = new Server();
        server.setId(id);
        return this.getDasClient().queryByPk(server);
    }

    public Long insertServer(Server server) throws SQLException {
        if (server == null) {
            return 0L;
        }
        this.getDasClient().insert(server, Hints.hints().setIdBack());
        return server.getId();
    }

    public int updateServer(Server server) throws SQLException {
        if (null == server || server.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(server);
    }

    public int deleteServer(Server server) throws SQLException {
        return this.getDasClient().deleteByPk(server);
    }

    /**
     * 依据server id 删除server config 和 server
     */
    public int deleteServerAndServerConfigByServerId(Long serverId) throws SQLException {
        String sql = "DELETE t1, t2 from `server` t1 " +
                "left join `server_config` t2 on  t1.id = t2.server_id " +
                "where t1.id = " + serverId;
        return this.updataBysql(sql);
    }

    public boolean isNotExistByIpAndPort(Server server) throws SQLException {
        String sql = "select count(1) from server where ip = '" + server.getIp() + "' and port = " + server.getPort();
        return this.getCount(sql) <= 0;
    }

    /**
     * 依据serverGroup id 删除server
     */
    public int deleteServerByServerGroupId(Long serverGroupId) throws SQLException {
        String sql = "DELETE FROM server WHERE server_group_id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, serverGroupId));
    }

    public int changeServerGroup(long serverId, long serverGroupId) throws SQLException {
        String sql = "UPDATE server SET server_group_id = ? WHERE id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, serverGroupId), Parameter.integerOf(StringUtils.EMPTY, serverId));
    }

    public List<Server> getServersByServerGroupId(Long serverGroupId) throws SQLException {
        String sql = "SELECT id, server_group_id, ip, port, comment, insert_time, update_time FROM server where server_group_id = ?";
        return this.queryBySql(sql, Server.class, Parameter.integerOf("", serverGroupId));
    }

    public Long getServerTotalCount(Paging<Server> paging) throws SQLException {
        String sql = "SELECT count(1) FROM server t1 " + appenWhere(paging);
        log.info("getServerTotalCount : " + sql);
        return this.getCount(sql);
    }

    public List<ServerView> findServerPageList(Paging<Server> paging) throws SQLException {
        String sql = " SELECT t1.id, t1.server_group_id, t1.ip, t1.port, t1.comment, t1.insert_time, t1.update_time, t2.user_real_name  FROM server t1 " +
                " left join login_users t2 on t1.update_user_no = t2.user_no " + appenCondition(paging);
        return this.queryBySql(sql, ServerView.class);
    }

    //TODO 添加条件
    private String appenWhere(Paging<Server> paging) {
        Server server = paging.getData();
        if (null == server) {
            return org.apache.commons.lang.StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where().equal("server_group_id", server.getServerGroupId()).builer();
    }

    private String appenCondition(Paging<Server> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
