package com.ppdai.das.console.dao;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.ServerConfig;
import com.ppdai.das.console.dto.model.Paging;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class ServerConfigDao extends BaseDao {

    public Long insertServerAppConfig(ServerConfig serverAppConfig) throws SQLException {
        if (serverAppConfig == null) {
            return 0L;
        }
        this.getDasClient().insert(serverAppConfig);
        return serverAppConfig.getId();
    }

    public int deleteServerAppConfig(ServerConfig serverAppConfig) throws SQLException {
        return this.getDasClient().deleteByPk(serverAppConfig);
    }

    public int updateServerAppConfig(ServerConfig serverAppConfig) throws SQLException {
        if (null == serverAppConfig || serverAppConfig.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(serverAppConfig);
    }

    /**
     * 依据server Group id 删除server config
     */
    public int deleteServerConfigByServerGroupId(Long serverId) throws SQLException {
        String sql = "DELETE FROM server_config WHERE server_id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, serverId));
    }

    public List<ServerConfig> getServerAppConfigByServerId(Long serverId) throws SQLException {
        String sql = "SELECT id, server_id, keya, value, comment, insert_time, update_time  FROM server_config WHERE server_id = ?";
        return this.queryBySql(sql, ServerConfig.class, Parameter.integerOf(StringUtils.EMPTY, serverId));
    }

    public Long getServerAppConfigTotalCount(Paging<ServerConfig> paging) throws SQLException {
        return this.getCount("SELECT count(1) FROM server_config " + this.appenCondition(paging));
    }

    public List<ServerConfig> findServerAppConfigPageList(Paging<ServerConfig> paging) throws SQLException {
        String sql = "SELECT id, server_id, keya, value, comment, insert_time, update_time  FROM server_config " + appenCondition(paging);
        log.info("findServerAppConfigPageList-----> " + sql);
        return this.queryBySql(sql, ServerConfig.class);
    }

    private String appenWhere(Paging<ServerConfig> paging) {
        ServerConfig serverConfig = paging.getData();
        if (null == serverConfig) {
            return org.apache.commons.lang.StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().where()
                .equal("server_id", serverConfig.getServerId())
                .builer();
    }

    private String appenCondition(Paging<ServerConfig> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance()
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
