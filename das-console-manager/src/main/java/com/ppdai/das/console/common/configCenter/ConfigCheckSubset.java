package com.ppdai.das.console.common.configCenter;

import com.google.common.collect.Lists;
import com.ppdai.das.console.dto.entry.configCheck.*;
import com.ppdai.das.console.enums.ConfigCheckTypeEnums;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据校验，阿波罗取取子集
 */
public class ConfigCheckSubset {

    private static final String ENMTY = "-";
    private static final Map<String, String> checkFiler = new HashMap<String, String>() {
        {
            put(ConfigCenterCons.DBSET_DATABASESETS, ",");
            put(ConfigCenterCons.DBSET_SHARDINGSTRATEGY, ";");
        }
    };

    public static ConfigCkeckResult checkData(List<ConfigDataResponse> list) {
        ConfigCkeckResult<ConfigCheckItem> configCkeckResult = ConfigCkeckResult.success(StringUtils.EMPTY);
        if (CollectionUtils.isEmpty(list) || list.size() == 1) {
            configCkeckResult.fail(ConfigCheckBase.error("源数据不全"));
        } else if (list.size() == 2) {
            return ConfigCheckSubset.checkData(new ConfigCheckResponse(list.get(0), list.get(1)));
        } else if (list.size() >= 3) {
            ConfigCkeckResult<ConfigCheckItem> configSr = checkData(new ConfigCheckResponse(list.get(0), list.get(1)));
            ConfigCkeckResult<ConfigCheckItem> saecSr = checkData(new ConfigCheckResponse(list.get(0), list.get(2)));
            return ConfigCheckBase.mergeApolloCheckResponse(configSr, saecSr);
        }
        return configCkeckResult;
    }

    /**
     * 数据校验，阿波罗取取子集
     */
    public static ConfigCkeckResult<ConfigCheckItem> checkData(ConfigCheckResponse configCheckResponse) {
        ConfigCkeckResult<ConfigCheckItem> apolloDasItemResult;
        ConfigCheckItem configCheckItem = new ConfigCheckItem();
        ConfigDataResponse config = configCheckResponse.getConfig();
        ConfigDataResponse das = configCheckResponse.getDas();
        if (config == null || das == null) {
            return ConfigCkeckResult.fail(ConfigCheckBase.error("标题校验失败，请检查标题列表数据！！"), configCheckItem);
        }

        apolloDasItemResult = ConfigCheckBase.checkTitleResponse(das.getTitles(), config.getTitles());
        if (apolloDasItemResult.getCode() == ConfigCkeckResult.ERROR) {
            return apolloDasItemResult;
        }

        List<ItemResponse> alist = ConfigCheckBase.sortItemResponses(config.getItems());
        List<ItemResponse> dlist = ConfigCheckBase.sortItemResponses(das.getItems());
        ConfigCkeckResult<List<ConfigDasItem>> acr = checkList(alist, dlist);
        List<String> columnTitle = Lists.newArrayList(das.getItemTitle(), config.getItemTitle());
        apolloDasItemResult.setCode(acr.getCode());
        apolloDasItemResult.setMsg(acr.getMsg());
        apolloDasItemResult.setItem(ConfigCheckItem.builder().columnTitle(columnTitle).list(acr.getItem()).titles(das.getTitles()).build());
        return apolloDasItemResult;
    }

    private static ConfigCkeckResult<List<ConfigDasItem>> checkList(List<ItemResponse> alist, List<ItemResponse> dlist) {
        ConfigCkeckResult<List<ConfigDasItem>> configCkeckResult = ConfigCkeckResult.success("阿波罗数据校验通过！");
        List<ConfigDasItem> list = new ArrayList<>();
        Map<String, ItemResponse> aMap = ConfigCheckBase.toMap(alist);
        Map<String, ItemResponse> dMap = ConfigCheckBase.toMap(dlist);
        Map<String, ItemResponse> arMap = new HashMap<>();
        Map<String, ItemResponse> drMap = new HashMap<>();

        for (Map.Entry<String, ItemResponse> entry : dMap.entrySet()) {
            String dKey = entry.getKey();
            ItemResponse d = entry.getValue();
            if (!aMap.containsKey(dKey)) {
                if (StringUtils.isNotBlank(d.getValue())) {
                    d.setType(ConfigCheckTypeEnums.ERROR.getValue());
                    aMap.put(dKey, new ItemResponse(dKey, "KEY为空", ConfigCheckTypeEnums.ERROR.getValue()));
                    configCkeckResult.fail(ConfigCheckBase.error("KEY为空"));
                } else {
                    d.setType(ConfigCheckTypeEnums.SUCCESS.getValue());
                    aMap.put(dKey, new ItemResponse(dKey, "阿波罗值为空时则没有Key，正确", ConfigCheckTypeEnums.SUCCESS.getValue()));
                    if (StringUtils.isBlank(d.getValue())) {
                        d.setValue(ENMTY);
                    }
                }
            } else {
                ItemResponse a = aMap.get(dKey);
                ConfigCkeckResult<String> sr = ConfigCheckBase.checkKeyValue(dKey, a.getValue(), d.getValue(), " Value");
                ConfigCheckBase.checkConfigCkeckResult(sr, configCkeckResult, a, d);
            }
        }
        for (Map.Entry<String, ItemResponse> entry : dMap.entrySet()) {
            String key = entry.getKey();
            ItemResponse aItemResponse = aMap.get(key);
            arMap.put(key, aItemResponse);
            drMap.put(key, entry.getValue());
        }
        for (Map.Entry<String, ItemResponse> entry : drMap.entrySet()) {
            ItemResponse apollo = arMap.get(entry.getKey());
            ItemResponse das = entry.getValue();
            list.add(ConfigDasItem.builder().config(apollo).das(das).build());
        }
        configCkeckResult.setItem(list);
        return configCkeckResult;
    }
}
