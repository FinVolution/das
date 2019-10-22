package com.ppdai.platform.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.platform.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import com.ppdai.platform.das.console.dto.entry.das.PublicStrategy;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.PublicStrategyView;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
public class PublicStrategyDao extends BaseDao {

    public Long insertPublicStrategy(PublicStrategy publicStrategy) throws SQLException {
        if (null == publicStrategy) {
            return 0L;
        }
        this.getDasClient().insert(publicStrategy, Hints.hints().setIdBack());
        return publicStrategy.getId();
    }

    public int updatePublicStrategy(PublicStrategy publicStrategy) throws SQLException {
        if (null == publicStrategy || publicStrategy.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(publicStrategy);
    }

    public PublicStrategy getPublicStrategyById(Long id) throws SQLException {
        PublicStrategy publicStrategy = new PublicStrategy();
        publicStrategy.setId(id);
        return this.getDasClient().queryByPk(publicStrategy);
    }

    /**
     * 全局不存在
     */
    public Long getCountByName(String name) throws SQLException {
        return this.getCount("SELECT count(1) FROM public_strategy WHERE name='" + name + "'");
    }

    /**
     * 其他行不存在此dbname
     */
    public Long getCountByIdAndName(Long id, String name) throws SQLException {
        return this.getCount("SELECT count(1) FROM public_strategy WHERE name='" + name + "' and id = " + id);
    }

    public int deletePublicStrategy(PublicStrategy publicStrategy) throws SQLException {
        if (null == publicStrategy || publicStrategy.getId() == null) {
            return 0;
        }
        return this.getDasClient().deleteByPk(publicStrategy);
    }

    public List<PublicStrategy> getAllPublicStrateges() throws SQLException {
        PublicStrategy.PublicStrategyDefinition p = PublicStrategy.PUBLICSTRATEGY;
        SqlBuilder builder = SqlBuilder.selectAllFrom(p).into(PublicStrategy.class);
        return this.getDasClient().query(builder);
    }

    public Long getPublicStrategyTotalCount(Paging<PublicStrategy> paging) throws SQLException {
        return this.getCount("SELECT count(1) FROM public_strategy t1" + appenWhere(paging));
    }

    public List<PublicStrategyView> findPublicStrategyPageList(Paging<PublicStrategy> paging) throws SQLException {
        String sql = "select t1.id, t1.name, t1.strategy_loading_type, t1.class_name, t1.strategy_source, " +
                "t1.strategy_params, t1.comment, t1.insert_time, t1.update_time,t2.user_real_name " +
                "from public_strategy t1 " +
                "left join login_users t2 on t1.update_user_no = t2.user_no " + appenCondition(paging);
        return this.queryBySql(sql, PublicStrategyView.class);
    }

    private String appenWhere(Paging<PublicStrategy> paging) {
        PublicStrategy publicStrategy = paging.getData();
        if (null == publicStrategy) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .likeLeft("name", publicStrategy.getName())
                .likeLeft("class_name", publicStrategy.getClassName())
                .in("strategy_loading_type", publicStrategy.getStrategyLoadingTypes(), Integer.class.getClass())
                .builer();
    }

    private String appenCondition(Paging<PublicStrategy> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance()
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
