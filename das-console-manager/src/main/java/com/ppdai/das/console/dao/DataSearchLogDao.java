package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.DataSearchLog;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.DataSearchLogView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class DataSearchLogDao extends BaseDao {

    public Long insertDataSearchLog(DataSearchLog dataSearchLog) throws SQLException {
        if (dataSearchLog == null) {
            return 0L;
        }
        this.getDasClient().insert(dataSearchLog, Hints.hints().setIdBack());
        return dataSearchLog.getId();
    }

    public Long getTotalCount(Paging<DataSearchLogView> paging) throws SQLException {
        String sql = "select count(1) from data_search_log t1 left join login_users t2 on t1.user_no = t2.user_no" + appenWhere(paging);
        return this.getCount(sql);
    }

    public List<DataSearchLogView> findLogPageList(Paging<DataSearchLogView> paging) throws SQLException {
        String sql = "select t1.id, t1.ip, t1.user_no, t1.request_type, t1.request, t1.success, t1.result, t1.inserttime, t2.user_real_name " +
                "from data_search_log t1 " +
                "left join login_users t2 on t1.user_no = t2.user_no " + appenCondition(paging);
        return this.queryBySql(sql, DataSearchLogView.class);
    }

    private String appenWhere(Paging<DataSearchLogView> paging) {
        DataSearchLogView dataSearchLog = paging.getData();
        if (null == dataSearchLog) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .equal("request_type", dataSearchLog.getRequest_type())
                .setTab("t2.")
                .likeLeft("user_real_name", dataSearchLog.getUser_real_name())
                .builer();
    }

    private String appenCondition(Paging<DataSearchLogView> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                .orderBy(paging)
                .limit(paging)
                .builer();
    }

    public List<DataSearchLog> findDataSearchLogList(Integer limit) throws SQLException {
        String sql = "select id, ip, user_no, request_type, request, success, result, inserttime from data_search_log order by id desc limit " + limit;
        return this.queryBySql(sql, DataSearchLog.class);
    }
}
