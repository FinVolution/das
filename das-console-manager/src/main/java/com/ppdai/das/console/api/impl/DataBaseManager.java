package com.ppdai.das.console.api.impl;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.utils.Transform;
import com.ppdai.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.das.console.dto.entry.configCheck.ItemResponse;
import com.ppdai.das.console.dto.entry.configCheck.TitleResponse;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.openapi.ConfigProvider;
import com.ppdai.das.console.openapi.vo.DataBaseVO;
import com.ppdai.das.console.api.DataBaseConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class DataBaseManager implements DataBaseConfiguration {

    @Autowired
    private Transform transform;

    @Autowired
    private ConfigProvider configProvider;

    @Override
    public void batchAddDataBase(LoginUser user, List<DataBaseInfo> list) throws Exception {
        configProvider.addDataBase(transform.toDataBaseVOList(list));
    }

    @Override
    public void updateDataBase(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception {
        configProvider.updateDataBase(transform.toDataBaseVO(dataBaseInfo));
    }

    @Override
    public void deleteDataBase(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception {
        configProvider.deleteDataBase(dataBaseInfo.getDbname());
    }

    @Override
    public void syncDataBase(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception {
        configProvider.updateDataBase(transform.toDataBaseVO(dataBaseInfo));
    }

    @Override
    public List<ConfigDataResponse> getCheckData(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception {
        DataBaseVO dataBaseVO = configProvider.getDataBase(dataBaseInfo.getDbname());
        if (null == dataBaseVO || StringUtils.isBlank(dataBaseVO.getDbName())) {
            throw new Exception("数据错误，物理库信息为空！！！");
        }
        List<TitleResponse> titles = Lists.newArrayList(new TitleResponse("DataBase Name", dataBaseVO.getDbName()));
        ConfigDataResponse das = new ConfigDataResponse("DAS", titles, toList(dataBaseInfo));
        ConfigDataResponse con = new ConfigDataResponse(configProvider.getConfigCenterName(), titles, toList(dataBaseVO));
        return Lists.newArrayList(das, con);
    }

    private List<ItemResponse> toList(DataBaseInfo dataBaseInfo) {
        return toList(transform.toDataBaseVO(dataBaseInfo));
    }

    private List<ItemResponse> toList(DataBaseVO dataBaseVO) {
        List<ItemResponse> list = Lists.newArrayList(
                new ItemResponse("driverClassName", dataBaseVO.getDataBaseEnum().getDriver()),
                new ItemResponse("dbName", dataBaseVO.getDbName()),
                new ItemResponse("userName", dataBaseVO.getUserName()),
                new ItemResponse("password", dataBaseVO.getPassword()),
                new ItemResponse("port", dataBaseVO.getPort()),
                new ItemResponse("host", dataBaseVO.getHost())
        );
        return list;
    }
}
