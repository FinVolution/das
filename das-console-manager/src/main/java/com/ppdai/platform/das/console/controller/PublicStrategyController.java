package com.ppdai.platform.das.console.controller;


import com.ppdai.platform.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.console.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.console.common.validates.group.publicStrategy.AddPublicStrategy;
import com.ppdai.platform.das.console.common.validates.group.publicStrategy.DeletePublicStrategy;
import com.ppdai.platform.das.console.common.validates.group.publicStrategy.UpdatePublicStrategy;
import com.ppdai.platform.das.console.config.annotation.CurrentUser;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.PublicStrategyDao;
import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigCheckItem;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.PublicStrategy;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.view.PublicStrategyView;
import com.ppdai.platform.das.console.service.PublicStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/publicStrategy")
public class PublicStrategyController {

    @Autowired
    private Message message;

    @Autowired
    private PublicStrategyDao publicStrategyDao;

    @Autowired
    private PublicStrategyService publicStrategyService;

    /**
     * 翻页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<PublicStrategyView>> list(@RequestBody Paging<PublicStrategy> paging) throws SQLException {
        return ServiceResult.success(publicStrategyService.findPublicShardingStrategyPageList(paging));
    }

    /**
     * 全部
     */
    @RequestMapping(value = "/list")
    public ServiceResult<List<PublicStrategy>> list() throws SQLException {
        return ServiceResult.success(publicStrategyDao.getAllPublicStrateges());
    }

    /**
     * 2、新建
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddPublicStrategy.class) @RequestBody PublicStrategy publicStrategy, @CurrentUser LoginUser user, Errors errors) throws Exception {
        publicStrategy.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = publicStrategyService.validatePermision(user, errors)
                .addAssert(() -> publicStrategyDao.getCountByName(publicStrategy.getName()) == 0, publicStrategy.getName() + " 已存在！")
                .addAssert(() -> publicStrategyDao.insertPublicStrategy(publicStrategy) > 0, message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
        // TODO return apolloPublicStrategy.replace(user, publicStrategy);
    }

    /**
     * 3、更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdatePublicStrategy.class) @RequestBody PublicStrategy publicStrategy, @CurrentUser LoginUser user, Errors errors) throws Exception {
        publicStrategy.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = publicStrategyService.validatePermision(user, errors)
                .addAssert(() -> publicStrategyService.isNotExistByName(publicStrategy), publicStrategy.getName() + " 已存在！")
                .addAssert(() -> publicStrategyDao.updatePublicStrategy(publicStrategy) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
        // TODO return apolloPublicStrategy.replace(user, publicStrategy);
    }

    /**
     * 4、删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeletePublicStrategy.class) @RequestBody PublicStrategy publicStrategy, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = publicStrategyService.validatePermision(user, errors)
                .addAssert(() -> publicStrategyDao.deletePublicStrategy(publicStrategy) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
        // TODO return apolloPublicStrategy.delete(user, publicStrategy.getId());
    }

    /**
     * 同步数据到阿波罗，单条
     */
    @RequestMapping(value = "/sync")
    public ServiceResult<String> sync(@RequestParam("id") Long id, @CurrentUser LoginUser user) throws Exception {
        PublicStrategy publicStrategy = publicStrategyDao.getPublicStrategyById(id);
        return ServiceResult.success();
        // TODO return apolloPublicStrategy.replace(user, publicStrategy);
    }

    /**
     * 数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult<ConfigCheckItem> check(@RequestParam("id") Long id) throws Exception {
        PublicStrategy publicStrategy = publicStrategyDao.getPublicStrategyById(id);
        return ConfigCkeckResult.success();
        /* TODO ConfigCheckResponse configCheckResponse = apolloPublicStrategy.getApolloCheckResponse(publicStrategy);
        return ConfigCheckBase.checkData(configCheckResponse);*/
    }
}
