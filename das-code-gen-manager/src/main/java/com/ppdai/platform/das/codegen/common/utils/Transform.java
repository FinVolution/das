package com.ppdai.platform.das.codegen.common.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ppdai.platform.das.codegen.api.model.UserIdentity;
import com.ppdai.platform.das.codegen.dao.DataBaseDao;
import com.ppdai.platform.das.codegen.dao.ProjectDao;
import com.ppdai.platform.das.codegen.dto.entry.das.*;
import com.ppdai.platform.das.codegen.dto.view.ProjectView;
import com.ppdai.platform.das.codegen.enums.DataBaseEnum;
import com.ppdai.platform.das.codegen.enums.DbMasterSlaveEnum;
import com.ppdai.platform.das.codegen.enums.StrategyTypeEnum;
import com.ppdai.platform.das.codegen.openapi.vo.DataBaseVO;
import com.ppdai.platform.das.codegen.openapi.vo.DatabaseSetEntryVO;
import com.ppdai.platform.das.codegen.openapi.vo.DatabaseSetVO;
import com.ppdai.platform.das.codegen.openapi.vo.ProjectVO;
import com.ppdai.platform.das.codegen.service.PublicStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Transform {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private PublicStrategyService publicStrategyService;

    public LoginUser toLoginUser(UserIdentity userIdentity) {
        return LoginUser.builder()
                .userName(userIdentity.getUserName())
                .userNo(userIdentity.getWorkNumber())
                .userEmail(userIdentity.getUserEmail())
                .userRealName(userIdentity.getUserRealName())
                .active(userIdentity.getActive())
                .build();
    }

    public List<DataBaseVO> toDataBaseVOList(List<DataBaseInfo> list) {
        List<DataBaseVO> dataBaseVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (DataBaseInfo dataBaseInfo : list) {
                dataBaseVOList.add(toDataBaseVO(dataBaseInfo));
            }
        }
        return dataBaseVOList;
    }

    public DataBaseVO toDataBaseVO(DataBaseInfo dataBaseInfo) {
        return DataBaseVO.builder()
                .dataBaseEnum(DataBaseEnum.getDataBaseEnumByType(dataBaseInfo.getDb_type()))
                .dbName(dataBaseInfo.getDbname())
                .userName(dataBaseInfo.getDb_user())
                .password(dataBaseInfo.getDb_password())
                .port(dataBaseInfo.getDb_port())
                .host(dataBaseInfo.getDb_address())
                .build();
    }

    public DatabaseSetVO toDatabaseSetVO(DatabaseSet dbset) {
        DatabaseSetVO databaseSetVO = DatabaseSetVO.builder()
                .databaseSetName(dbset.getName())
                .strategyType(StrategyTypeEnum.getStrategyTypeEnumByType(dbset.getStrategyType()))
                .dataBaseType(DataBaseEnum.getDataBaseEnumByType(dbset.getDbType()))
                .build();
        String trategySource = dbset.getStrategySource();
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(trategySource)) {
            List<String> list = Splitter.on(";").omitEmptyStrings().trimResults().splitToList(trategySource);
            if (CollectionUtils.isNotEmpty(list)) {
                for (String trategy : list) {
                    List<String> trategies = Splitter.on("=").omitEmptyStrings().trimResults().splitToList(trategy);
                    if (CollectionUtils.isNotEmpty(trategies) && trategies.size() == 2) {
                        map.put(trategies.get(0), trategies.get(1));
                    }
                }
            }
        }
        databaseSetVO.setShardingStrategy(map);
        databaseSetVO.setStrategyClassName(publicStrategyService.getStrategyClassName(dbset));
        return databaseSetVO;
    }


    public List<DatabaseSetEntryVO> toDatabaseSetEntryVOList(List<DatabaseSetEntry> list) throws SQLException {
        List<DatabaseSetEntryVO> dataBaseVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (DatabaseSetEntry databaseSetEntryd : list) {
                dataBaseVOList.add(toDatabaseSetEntryVO(databaseSetEntryd));
            }
        }
        return dataBaseVOList;
    }

    public DatabaseSetEntryVO toDatabaseSetEntryVO(DatabaseSetEntry dbset) throws SQLException {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDataBaseInfoByDbId(dbset.getDb_Id());
        return DatabaseSetEntryVO.builder()
                .databasesetEntryName(dbset.getName())
                .databaseMasterSlaveType(DbMasterSlaveEnum.getDbMasterSlaveEnumByType(dbset.getDatabaseType()))
                .sharding(dbset.getSharding())
                .databaseName(dataBaseInfo.getDbname())
                .build();
    }

    public ProjectVO toProjectVO(Project project) {
        try {
            ProjectView projectView = projectDao.getProjectViewById(project.getId());
            return toProjectVO(projectView);
        } catch (SQLException se) {
            log.error(StringUtil.getMessage(se));
            return new ProjectVO();
        }
    }

    public ProjectVO toProjectVO(ProjectView projectView) {
        List<String> databaseSets = StringUtils.isBlank(projectView.getDbsetNamees()) ? Lists.newArrayList() : Splitter.on(",").omitEmptyStrings().trimResults().splitToList(projectView.getDbsetNamees());
        return ProjectVO.builder()
                .appId(projectView.getApp_id())
                .projectName(projectView.getName())
                .teamName(projectView.getGroupName())
                .databaseSetNames(databaseSets)
                .build();
    }
}
