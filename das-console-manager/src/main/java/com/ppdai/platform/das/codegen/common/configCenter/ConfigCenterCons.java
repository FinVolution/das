package com.ppdai.platform.das.codegen.common.configCenter;

import com.ppdai.platform.das.codegen.common.exceptions.ApolloConfigException;
import com.ppdai.platform.das.codegen.common.utils.DasEnv;
import com.ppdai.platform.das.codegen.dao.PublicStrategyDao;
import com.ppdai.platform.das.codegen.dto.entry.das.DasGroup;
import com.ppdai.platform.das.codegen.dto.entry.das.PublicStrategy;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetView;
import com.ppdai.platform.das.codegen.enums.DataBaseEnum;
import com.ppdai.platform.das.codegen.enums.StrategyTypeEnum;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;

public class ConfigCenterCons {
    public static final String DBSET_DATABASESETS = "databaseSets";
    public static final String DBSET_PROVIDER = "provider";
    public static final String DBSET_SHARDINGSTRATEGY = "shardingStrategy";

    public static final String ENTRY_ENTRIES = "entries";
    public static final String ENTRY_TYPE = "type";
    public static final String ENTRY_DATASOURCE = "datasource";
    public static final String ENTRY_SHARDING = "sharding";

    public final static String SYMBOL_SEPARATOR = "=";
    public final static String SYMBOL_SEMICOLON = ";";
    public static final String SEPARATOR = ",";

    public static String getDasTeamNameSpaceByDalGroup(DasGroup dasGroup) {
        return getDasTeamNameSpaceByDalGroup(dasGroup.getGroup_name());
    }

    public static String getDasTeamNameSpaceByDalGroup(String dalGroupName) {
        return DasEnv.additionalConfiguration.getGlobalDasTeams() + dalGroupName;
    }

    public static String getNameSpaceByPublicStrategy(PublicStrategy publicStrategy) {
        return DasEnv.additionalConfiguration.getDasShardingStrategies() + publicStrategy.getName();
    }

    /**
     * Database
     *
     * @param dbType
     * @param dbName
     * @return
     */
    public static String getNameSpaceByDataBase(Integer dbType, String dbName) {
        if (dbType.equals(DataBaseEnum.MYSQL.getType())) {
            return DasEnv.additionalConfiguration.getGlobalMysqlDatasource() + dbName;
        } else if (dbType.equals(DataBaseEnum.SQLSERVER.getType())) {
            return DasEnv.additionalConfiguration.getGlobalSqlserverDatasource() + dbName;
        }
        throw new ApolloConfigException("db Type 类型不匹配");
    }

    public static String getShardingStrategy(DatabaseSetView dbset) throws SQLException {
        if (dbset.getStrategyType() == null || dbset.getStrategyType() == StrategyTypeEnum.NoStrategy.getType()) {
            return StringUtils.EMPTY;
        }
        StringBuffer shardingStrategy = new StringBuffer();
        shardingStrategy.append(getClass(dbset.getStrategyType()));
        shardingStrategy.append(SYMBOL_SEPARATOR);
        shardingStrategy.append(getStrategyName(dbset.getClassName(), dbset.getStrategyType(), dbset.getDynamicStrategyId()));
        shardingStrategy.append(SYMBOL_SEMICOLON);
        shardingStrategy.append(dbset.getStrategySource());
        return shardingStrategy.toString();
    }

    private static String getClass(Integer strategyType) {
        return StrategyTypeEnum.getStrategyTypeEnumByType(strategyType).getShardingStrategy();
    }

    private static String getStrategyName(String strategyName, Integer strategyType, Long dynamicStrategyId) throws SQLException {
        if (strategyType == StrategyTypeEnum.PublicStrategy.getType()) {
            PublicStrategyDao publicStrategyDao = new PublicStrategyDao();
            PublicStrategy publicStrategy = publicStrategyDao.getPublicStrategyById(dynamicStrategyId);
            return getNameSpaceByPublicStrategy(publicStrategy);
        }
        return strategyName;
    }
}
