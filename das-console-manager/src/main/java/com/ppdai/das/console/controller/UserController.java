package com.ppdai.das.console.controller;

import com.ppdai.das.console.common.validates.chain.ValidateResult;
import com.ppdai.das.console.common.validates.group.user.AddUser;
import com.ppdai.das.console.common.validates.group.user.DeleteUser;
import com.ppdai.das.console.common.validates.group.user.UpdateUser;
import com.ppdai.das.console.config.annotation.CurrentUser;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.service.UserService;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dto.view.LoginUserView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by wang.liang on 2018/8/23.
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private Message message;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginUserDao loginUserDao;


    /**
     * 全部用户列表 （添加组员等用）
     */
    @RequestMapping(value = "/group/users")
    public ServiceResult<List<LoginUser>> findGroupUserList(@RequestParam(value = "groupId", defaultValue = "0") Long groupId) throws Exception {
        return ServiceResult.success(loginUserDao.getUserByGroupId(groupId));
    }

    /**
     * 全部用户列表 （添加组员等用）
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<LoginUserView>> findUserPageList(@RequestBody Paging<LoginUser> paging) throws Exception {
        return ServiceResult.success(userService.findUserPageList(paging));
    }

    /**
     * 2、根据域用户名获取用户信息
     */
    @RequestMapping(value = "/getWorkInfo")
    public ServiceResult<LoginUser> getWorkInfo(@RequestParam(value = "name", defaultValue = "") String name, @CurrentUser LoginUser user) {
        return userService.getUserInfoByWorkName(user, name);
    }

    /**
     * 3、添加用户
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> addUser(@Validated(AddUser.class) @RequestBody LoginUser user, @CurrentUser LoginUser currentUser, Errors errors) throws SQLException {
        user.setUpdate_user_no(currentUser.getUserNo());
        ValidateResult validateRes = userService.validatePermision(currentUser, errors)
                .addAssert(() -> loginUserDao.getUserByNo(user.getUserNo()) == null, message.user_add_fail_is_exist)
                .addAssert(() -> userService.addUser(user), message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 4、修改用户
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateUser.class) @RequestBody LoginUser user, @CurrentUser LoginUser currentUser, Errors errors) throws Exception {
        user.setUpdate_user_no(currentUser.getUserNo());
        ValidateResult validateRes = userService.validatePermision(currentUser, errors)
                .addAssert(() -> loginUserDao.getUserById(user.getId()) != null, message.user_add_fail_not_exist)
                .addAssert(() -> userService.canUpdateOrDeleteUser(currentUser, user), message.permisson_user_crud)
                .addAssert(() -> userService.update(user), message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 5、删除用户
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteUser.class) @RequestBody LoginUser user, @CurrentUser LoginUser currentUser, Errors errors) throws Exception {
        ValidateResult validateRes = userService.validatePermision(currentUser, errors)
                .addAssert(() -> userService.canUpdateOrDeleteUser(currentUser, user), message.permisson_user_crud)
                .addAssert(() -> loginUserDao.deleteUser(user.getId()) > 1, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    @RequestMapping("/buttons")
    public ServiceResult getDataBaseButton(@CurrentUser LoginUser user) {
        return userService.getUserManageButton(user);
    }
}
