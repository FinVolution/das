package com.ppdai.platform.das.console.controller;

import com.ppdai.platform.das.console.common.configCenter.ConfigCheckBase;
import com.ppdai.platform.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.console.common.utils.DasEnv;
import com.ppdai.platform.das.console.common.utils.StringUtil;
import com.ppdai.platform.das.console.config.annotation.CurrentUser;
import com.ppdai.platform.das.console.dao.DataBaseDao;
import com.ppdai.platform.das.console.dao.DatabaseSetDao;
import com.ppdai.platform.das.console.dao.ProjectDao;
import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigCheckItem;
import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.Project;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.view.check.CheckIDsView;
import com.ppdai.platform.das.console.service.DatabaseService;
import com.ppdai.platform.das.console.service.DatabaseSetService;
import com.ppdai.platform.das.console.service.ProjectService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/apiext")
public class ApiExtController {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DatabaseSetService databaseSetService;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private DatabaseSetDao databaseSetDao;


    @Autowired
    private DatabaseService databaseService;


    @RequestMapping(value = "/check")
    public ServiceResult check(@RequestParam("appid") String appid) {
        try {
            if (null != appid) {
                Project project = projectDao.getProjectByAppId(appid);
                CheckIDsView checkIDsView = new CheckIDsView(project.getId(), project.getDal_group_id());
                return ServiceResult.success(checkIDsView);
            } else {
                return ServiceResult.fail("ApiExtController.data appid 为空");
            }
        } catch (Exception e) {
            return ServiceResult.fail("ApiExtController.data appid : " + appid + "   " + StringUtil.getMessage(e));
        }
    }

    @RequestMapping(value = "/checkDblist")
    public ServiceResult checkDblist(@RequestParam("appid") String appid, @CurrentUser LoginUser user) {
        List<ConfigCkeckResult> srlist = new ArrayList<>();
        try {
            if (null != appid) {
                List<DataBaseInfo> list = dataBaseDao.getAllDbByAppId(appid);
                for (DataBaseInfo dataBaseInfo : list) {
                    ConfigCkeckResult<List<ConfigDataResponse>> cr = databaseService.getCheckData(user, dataBaseInfo);
                    if (cr.getCode() == ConfigCkeckResult.ERROR) {
                        return ServiceResult.fail(cr.getMsg());
                    }
                    srlist.add(ConfigCheckBase.checkData(cr.getItem()));
                }
                return ServiceResult.success(srlist);
            } else {
                return ServiceResult.fail("ApiExtController.checkDblist appid 为空");
            }
        } catch (Exception e) {
            return ServiceResult.fail("ApiExtController.checkDblist appid : " + appid + "   " + StringUtil.getMessage(e));
        }
    }

    /**
     * 外部检测接口，判断项目接入das配置正确
     *
     * @param appid
     */
    @RequestMapping(value = "/checkAll")
    public ServiceResult<String> checkAll(@RequestParam("appid") String appid, @CurrentUser LoginUser user, HttpServletRequest request) {
        String baseurl = DasEnv.getBaseUrl(request);
        String addmsg = "明细请查看 http://" + baseurl + "/das/#/api?appid=" + appid;
        try {
            if (null == appid) {
                return ServiceResult.fail("ApiExtController.checkByAppid appid " + addmsg);
            }
            Project project = projectDao.getProjectByAppId(appid);
            //项目校验
            ConfigCkeckResult<List<ConfigDataResponse>> cr = projectService.getCheckData(user, project);
            if (cr.getCode() == ConfigCkeckResult.ERROR) {
                return ServiceResult.fail("projectService.getCheckData appid " + addmsg);
            }
            List<ConfigDataResponse> configDataResponses = cr.getItem();
            ConfigCkeckResult<ConfigCheckItem> configCkeckResult = ConfigCheckBase.checkData(configDataResponses);
            if (configCkeckResult.getCode() == ConfigCkeckResult.ERROR) {
                return ServiceResult.fail("项目配置校验不通过，" + configCkeckResult.getMsg() + addmsg);
            }
            //逻辑库校验
            List<DatabaseSet> dbsets = databaseSetDao.getAllDatabaseSetByGroupId(project.getDal_group_id());
            if (CollectionUtils.isEmpty(dbsets)) {
                return ServiceResult.fail("逻辑库配置校验不通过，" + configCkeckResult.getMsg() + addmsg);
            }
            List<ConfigDataResponse> configCheckResponses = databaseSetService.getAllCheckData(user, project.getDal_group_id(), dbsets.get(0));
            configCkeckResult = ConfigCheckBase.checkData(configCheckResponses);
            //物理库校验
            if (configCkeckResult.getCode() != ConfigCkeckResult.ERROR) {
                List<DataBaseInfo> list = dataBaseDao.getAllDbByAppId(appid);
                for (DataBaseInfo dataBaseInfo : list) {
                    cr = databaseService.getCheckData(user, dataBaseInfo);
                    if (cr.getCode() == ConfigCkeckResult.SUCCESS) {
                        return ServiceResult.fail("物理库校验不通过，" + configCkeckResult.getMsg() + addmsg);
                    }
                    configCkeckResult = ConfigCheckBase.checkData(cr.getItem());
                    if (configCkeckResult.getCode() == ConfigCkeckResult.ERROR) {
                        return ServiceResult.fail("物理库校验不通过，" + configCkeckResult.getMsg() + addmsg);
                    }
                }
                return ServiceResult.success();
            }
        } catch (Exception e) {
            return ServiceResult.fail("ApiExtController.checkByAppid appid : " + appid + "   " + StringUtil.getMessage(e));
        }
        return ServiceResult.fail("物理库校验不通过, 明细请查看 http://" + baseurl + "/das/#/api?appid=" + appid);
    }

    @RequestMapping(value = "/appidExist")
    public ServiceResult appidExist(@RequestParam(value = "appid", defaultValue = "0") String appid) {
        try {
            if (null != appid && !"0".equals(appid) && projectService.isExistByAppId(appid)) {
                return ServiceResult.success();
            } else {
                return ServiceResult.fail("appid: " + appid + " 不存在");
            }
        } catch (Exception e) {
            return ServiceResult.fail("ApiExtController.appidExist appid : " + appid + "   " + StringUtil.getMessage(e));
        }
    }
}
