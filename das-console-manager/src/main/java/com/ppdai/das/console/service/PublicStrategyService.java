package com.ppdai.das.console.service;

import com.ppdai.das.console.common.utils.StringUtil;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.PublicStrategyDao;
import com.ppdai.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.PublicStrategy;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.model.page.PagerUtil;
import com.ppdai.das.console.dto.view.PublicStrategyView;
import com.ppdai.das.console.enums.StrategyTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class PublicStrategyService {

    @Autowired
    private Message message;

    @Autowired
    private PublicStrategyDao publicStrategyDao;

    @Autowired
    private PermissionService permissionService;

    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    public ListResult<PublicStrategyView> findPublicShardingStrategyPageList(Paging<PublicStrategy> paging) throws SQLException {
        Long count = publicStrategyDao.getPublicStrategyTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<PublicStrategyView> list = publicStrategyDao.findPublicStrategyPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public boolean isNotExistByName(PublicStrategy publicStrategy) throws SQLException {
        Long n = publicStrategyDao.getCountByName(publicStrategy.getName());
        Long i = publicStrategyDao.getCountByIdAndName(publicStrategy.getId(), publicStrategy.getName());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }

    public String getStrategyClassName(DatabaseSet databaseSet) {
        try {
            if (databaseSet.getStrategyType() == StrategyTypeEnum.NoStrategy.getType()) {
                return StringUtils.EMPTY;
            } else if (databaseSet.getStrategyType() == StrategyTypeEnum.PriverStrategy.getType()) {
                return databaseSet.getClassName();
            } else if (databaseSet.getStrategyType() == StrategyTypeEnum.PublicStrategy.getType()) {
                return publicStrategyDao.getPublicStrategyById(databaseSet.getDynamicStrategyId()).getName();
            }
        } catch (SQLException se) {
            log.error(StringUtil.getMessage(se));
        }
        return StringUtils.EMPTY;
    }
}
