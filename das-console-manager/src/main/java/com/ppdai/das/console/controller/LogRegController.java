package com.ppdai.das.console.controller;

import com.ppdai.das.console.api.EncdecConfiguration;
import com.ppdai.das.console.common.user.UserContext;
import com.ppdai.das.console.common.validates.chain.ValidateResult;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.common.validates.group.ValidateUtil;
import com.ppdai.das.console.common.validates.group.comm.Login;
import com.ppdai.das.console.common.validates.group.user.AddUser;
import com.ppdai.das.console.config.annotation.CurrentUser;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.UserService;
import com.ppdai.das.console.constant.CommMsg;
import com.ppdai.das.console.constant.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wang.liang on 2019/8/18.
 */
@Slf4j
@RestController
@RequestMapping(value = "/logReg")
public class LogRegController {

    @Autowired
    private Message message;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginUserDao loginUserDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private EncdecConfiguration encdecConfiguration;

    @RequestMapping(value = "/initAdminInfo", method = RequestMethod.POST)
    public ServiceResult initAdminInfo(@RequestBody LoginUser user, HttpServletRequest request, Errors errors) throws Exception {
        ValidateResult validateRes = ValidatorChain.newInstance()
                .addAssert(() -> StringUtils.isNotBlank(user.getPassword()), message.user_add_password_not_null)
                .addAssert(() -> userService.initAdminInfo(user), CommMsg.loginFailMessage)
                .controllerValidate(errors).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        UserContext.setUser(request, user);
        return ServiceResult.success(user);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ServiceResult register(@Validated(AddUser.class) @RequestBody LoginUser user, HttpServletRequest request, Errors errors) throws Exception {
        ValidateResult validateRes = ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> loginUserDao.getUserByUserName(user.getUserName()) == null, message.user_add_fail_is_exist)
                .addAssert(() -> loginUserDao.getUserByNo(user.getUserNo()) == null, message.user_add_fail_no_is_exist)
                .addAssert(() -> userService.register(user), message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ServiceResult login(@Validated(Login.class) @RequestBody LoginUser loginUser, HttpServletRequest request, Errors errors) throws Exception {
        ValidateUtil.controllerValidate(errors);
        LoginUser user = loginUserDao.getUserByUserName(loginUser.getUserName());
        if (user != null) {
            String pw = user.getPassword();
            if (StringUtils.isNotBlank(pw) && pw.equals(encdecConfiguration.parseUnidirection(loginUser.getPassword()))) {
                request.getSession().removeAttribute("isConfigNeedDasLogin");
                request.getSession().setAttribute("isNeedDasLogin", "false");
                UserContext.setUser(request, user);
                return ServiceResult.success(LoginUser.builder().userRealName(user.getUserRealName()).id(user.getId()).userEmail(user.getUserEmail()).build());
            }
        }
        return ServiceResult.fail(CommMsg.loginFailMessage);
    }

    @RequestMapping(value = "/simulateLogin", method = RequestMethod.POST)
    public ServiceResult simulateLogin(@RequestBody LoginUser loginUser, @CurrentUser LoginUser currentUser, HttpServletRequest request, HttpServletResponse response, Errors errors) throws Exception {
        ValidateUtil.controllerValidate(errors);
        if (permissionService.isManagerById(currentUser.getId())) {
            LoginUser user = loginUserDao.getUserById(loginUser.getId());
            if (user != null) {
                UserContext.setUser(request, user);
                return ServiceResult.success(user);
            }
        }
        return ServiceResult.fail(CommMsg.loginFailMessage);
    }
}
