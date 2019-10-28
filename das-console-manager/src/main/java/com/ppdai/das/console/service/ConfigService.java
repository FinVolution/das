package com.ppdai.das.console.service;

import com.ppdai.das.console.api.ConfigLoader;
import com.ppdai.das.console.common.utils.ResourceUtil;
import com.ppdai.das.console.common.utils.StringUtil;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.model.DasEnvModel;
import com.ppdai.das.console.dto.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Autowired
    private ConfigLoader configLoader;

    public ServiceResult<String> ckeckLoader() {
        try {
            if (!configLoader.isLoaderFile()) {
                return ServiceResult.success();
            }
            if (ResourceUtil.getSingleInstance().isDasExist() && ResourceUtil.getSingleInstance().isDatasourceExist() && ResourceUtil.getSingleInstance().datasourceXmlValid()) {
                return ServiceResult.success();
            }
            return ServiceResult.fail();
        } catch (Exception e) {
            return ServiceResult.fail("配置校验异常：" + StringUtil.getMessage(e));
        }
    }

    public ServiceResult<String> addConfig(DataBaseInfo dataBaseInfo) {
        try {
            this.addConfig(dataBaseInfo.getDbname(), dataBaseInfo.getDb_address(), dataBaseInfo.getDb_port(), dataBaseInfo.getDb_user(), dataBaseInfo.getDb_password());
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("初始化数据异常:操作数据库需要DROP TABLE，和 CTEATE TABLE权限！！" + StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    private void addConfig(String db_name, String db_address, String db_port, String db_user, String db_password) throws Exception {
        ResourceUtil.getSingleInstance().initializeDasSetXml();
        ResourceUtil.getSingleInstance().initializeDatasourceXml(db_name, db_address, db_port, db_user, db_password);
        ResourceUtil.getSingleInstance().initTables();
    }

    public DasEnvModel removeSensitiveInfo(DasEnvModel dasEnvModel) {
        dasEnvModel.getUser().setPassword(null);
        return dasEnvModel;
    }
}
