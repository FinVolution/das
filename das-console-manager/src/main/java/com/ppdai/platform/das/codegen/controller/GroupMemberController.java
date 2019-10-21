package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.member.AddMember;
import com.ppdai.platform.das.codegen.common.validates.group.member.DeleteMember;
import com.ppdai.platform.das.codegen.common.validates.group.member.UpdateMember;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.UserGroupDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.UserGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.MemberView;
import com.ppdai.platform.das.codegen.service.GroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/member")
public class GroupMemberController {

    @Resource
    private Message message;

    @Autowired
    private UserGroupDao userGroupDao;

    @Resource
    private GroupMemberService groupMemberService;

    /**
     * 1、根据name模糊查询成员列表
     */
    @RequestMapping(value = "/users")
    public ServiceResult<List<MemberView>> getUsers(@RequestParam(value = "name", defaultValue = "") String name) throws SQLException {
        return ServiceResult.success(userGroupDao.getUserListByLikeUserName(name));
    }

    /**
     * 2、根据组ID获取组员列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<MemberView>> getGroupUsers(@RequestBody Paging<UserGroup> paging) throws SQLException {
        return ServiceResult.success(groupMemberService.findGroupMemberPageList(paging));
    }

    /**
     * 4、添加组员
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddMember.class) @RequestBody UserGroup userGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        userGroup.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = groupMemberService.validatePermisionAdd(user, userGroup, errors)
                .addAssert(() -> !groupMemberService.isUserInGroup(userGroup.getUser_id(), userGroup.getGroup_id()), message.group_message_user_ingroup)
                .addAssert(() -> userGroupDao.insertUserGroup(userGroup) > 0, message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 5、删除组员
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteMember.class) @RequestBody UserGroup userGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = groupMemberService.validatePermisionUpdateAndDelete(user, userGroup, errors)
                .addAssert(() -> userGroupDao.deleteUserFromGroup(userGroup.getUser_id(), userGroup.getGroup_id()) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 6、权限修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateMember.class) @RequestBody UserGroup userGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        userGroup.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = groupMemberService.validatePermisionUpdateAndDelete(user, userGroup, errors)
                .addAssert(() -> userGroupDao.updateUserPersimion(userGroup) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 7、批量添加组员 TODO
     */
}
