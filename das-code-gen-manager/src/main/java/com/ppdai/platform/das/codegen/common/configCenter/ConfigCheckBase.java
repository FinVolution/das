package com.ppdai.platform.das.codegen.common.configCenter;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.ppdai.platform.das.codegen.common.utils.DasEnv;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.*;
import com.ppdai.platform.das.codegen.enums.ConfigCheckTypeEnums;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 数据校验，配置中心取取全集
 */
public class ConfigCheckBase {

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
            configCkeckResult.fail(error("源数据不全"));
        } else if (list.size() == 2) {
            return checkData(new ConfigCheckResponse(list.get(0), list.get(1)));
        } else if (list.size() >= 3) {
            ConfigCkeckResult<ConfigCheckItem> configSr = checkData(new ConfigCheckResponse(list.get(0), list.get(1)));
            ConfigCkeckResult<ConfigCheckItem> saecSr = checkData(new ConfigCheckResponse(list.get(0), list.get(2)));
            return mergeApolloCheckResponse(configSr, saecSr);
        }
        return configCkeckResult;
    }

    /**
     * 数据校验，配置中心取取全集
     */
    public static ConfigCkeckResult<ConfigCheckItem> checkData(ConfigCheckResponse configCheckResponse) {
        ConfigCkeckResult<ConfigCheckItem> apolloDasItemResult;
        ConfigCheckItem configCheckItem = new ConfigCheckItem();
        ConfigDataResponse config = configCheckResponse.getConfig();
        ConfigDataResponse das = configCheckResponse.getDas();
        if (config == null || das == null || CollectionUtils.isEmpty(config.getTitles()) || CollectionUtils.isEmpty(das.getTitles()) || CollectionUtils.isEmpty(config.getItems()) || CollectionUtils.isEmpty(das.getItems())) {
            return ConfigCkeckResult.fail(error("标题校验失败，请检查标题列表数据！！"), configCheckItem);
        }

        apolloDasItemResult = checkTitleResponse(das.getTitles(), config.getTitles());
        if (apolloDasItemResult.getCode() == ConfigCkeckResult.ERROR) {
            return apolloDasItemResult;
        }

        List<ItemResponse> alist = sortItemResponses(config.getItems());
        List<ItemResponse> dlist = sortItemResponses(das.getItems());
        ConfigCkeckResult<List<ConfigDasItem>> acr = checkList(alist, dlist);
        List<String> columnTitle = Lists.newArrayList(das.getItemTitle(), config.getItemTitle());
        apolloDasItemResult.setCode(acr.getCode());
        apolloDasItemResult.setMsg(acr.getMsg());
        apolloDasItemResult.setItem(ConfigCheckItem.builder().columnTitle(columnTitle).list(acr.getItem()).titles(das.getTitles()).build());
        return apolloDasItemResult;
    }

    protected static ConfigCkeckResult checkTitleResponse(List<TitleResponse> das, List<TitleResponse> configs) {
        ConfigCkeckResult configCkeckResult = ConfigCkeckResult.success(StringUtils.EMPTY);
        if (CollectionUtils.isEmpty(das) || CollectionUtils.isEmpty(configs)) {
            configCkeckResult.fail("标题校验失败，请检查标题列表数据！！");
        }
        if (CollectionUtils.isNotEmpty(das) && CollectionUtils.isNotEmpty(configs) && das.size() != configs.size()) {
            configCkeckResult.fail("标题校验失败，请检查标题列表数据，标题列表长度不一致！！");
        }
        if (CollectionUtils.isNotEmpty(das) && CollectionUtils.isNotEmpty(configs) && das.size() == configs.size()) {
            for (TitleResponse dasTitle : das) {
                boolean isEq = false;
                for (TitleResponse configsTitle : configs) {
                    if (dasTitle.getKey() != null && dasTitle.getValue() != null && configsTitle.getKey() != null && configsTitle.getValue() != null && dasTitle.getKey().equals(configsTitle.getKey()) && dasTitle.getValue().equals(configsTitle.getValue())) {
                        isEq = true;
                    }
                }
                if (!isEq) {
                    configCkeckResult.fail(dasTitle.getKey() + " : " + DasEnv.getConfigCenterName() + "与das值不相等");
                }
            }
        }
        return configCkeckResult;
    }

    public static ConfigCkeckResult<ConfigSaecCheckItem> mergeApolloCheckResponse(ConfigCkeckResult<ConfigCheckItem> configSr, ConfigCkeckResult<ConfigCheckItem> saecSr) {
        ConfigCkeckResult<ConfigSaecCheckItem> configCkeckResult = ConfigCkeckResult.success(StringUtils.EMPTY);
        if (configSr.getCode() == ConfigCkeckResult.WARING) {
            configCkeckResult.setMsg(configSr.getMsg());
            configCkeckResult.setCode(ConfigCkeckResult.WARING);
        } else if (saecSr.getCode() == ConfigCkeckResult.WARING) {
            configCkeckResult.setMsg("安全串校验不通过");
            configCkeckResult.setCode(ConfigCkeckResult.WARING);
        } else if (configSr.getCode() == ConfigCkeckResult.ERROR) {
            configCkeckResult.setMsg(configSr.getMsg());
            configCkeckResult.setCode(ConfigCkeckResult.ERROR);
        } else if (saecSr.getCode() == ConfigCkeckResult.ERROR) {
            configCkeckResult.setMsg("安全串校验不通过");
            configCkeckResult.setCode(ConfigCkeckResult.ERROR);
        }
        ConfigCheckItem configCheckItem = configSr.getItem();
        ConfigCheckItem saecCheckItem = saecSr.getItem();
        List<ConfigDasItem> alist = configCheckItem.getList();
        List<ConfigDasItem> slist = saecCheckItem.getList();
        List<ConfigSaecDasItem> list = new ArrayList<>();
        List<String> columnTitle = Lists.newArrayList(configCheckItem.getColumnTitle());
        if (CollectionUtils.isNotEmpty(saecCheckItem.getColumnTitle())) {
            columnTitle.add(saecCheckItem.getColumnTitle().get(1));
        } else {
            columnTitle.add(DasEnv.defaultConfiguration.getSecurityCenterName());
        }
        ConfigSaecCheckItem configSaecCheckItem = ConfigSaecCheckItem.builder().columnTitle(columnTitle).titles(configCheckItem.getTitles()).build();
        if (CollectionUtils.isNotEmpty(alist) && CollectionUtils.isNotEmpty(slist) && alist.size() == slist.size()) {
            for (int i = 0; i < alist.size(); i++) {
                list.add(ConfigSaecDasItem.builder().das(alist.get(i).getDas()).apollo(alist.get(i).getConfig()).saec(slist.get(i).getConfig()).build());
            }
            configSaecCheckItem.setList(list);
            configCkeckResult.setItem(configSaecCheckItem);
            return configCkeckResult;
        }
        if (configSr.getCode() == ConfigCkeckResult.SUCCESS && saecSr.getCode() == ConfigCkeckResult.ERROR) {
            for (int i = 0; i < alist.size(); i++) {
                if (CollectionUtils.isNotEmpty(slist) && alist.size() == slist.size()) {
                    list.add(ConfigSaecDasItem.builder().das(alist.get(i).getDas()).apollo(alist.get(i).getConfig()).saec(slist.get(i).getConfig()).build());
                } else if (CollectionUtils.isEmpty(slist)) {
                    ItemResponse das = alist.get(i).getDas();
                    ItemResponse itemResponse = new ItemResponse(das.getKey(), "null", ConfigCheckTypeEnums.ERROR.getValue());
                    list.add(ConfigSaecDasItem.builder().das(das).apollo(alist.get(i).getConfig()).saec(itemResponse).build());
                }
            }
            configSaecCheckItem.setList(list);
            configCkeckResult.setItem(configSaecCheckItem);
            return configCkeckResult;
        }
        return configCkeckResult;
    }

    private static ConfigCkeckResult<List<ConfigDasItem>> checkList(List<ItemResponse> alist, List<ItemResponse> dlist) {
        ConfigCkeckResult<List<ConfigDasItem>> configCkeckResult = ConfigCkeckResult.success("阿波罗数据校验通过！");
        List<ConfigDasItem> list = new ArrayList<>();
        Map<String, ItemResponse> aMap = toMap(alist);
        Map<String, ItemResponse> dMap = toMap(dlist);
        Map<String, ItemResponse> arMap = new HashMap<>();
        Map<String, ItemResponse> drMap = new HashMap<>();

        for (Map.Entry<String, ItemResponse> entry : aMap.entrySet()) {
            String aKey = entry.getKey();
            ItemResponse a = entry.getValue();
            if (!dMap.containsKey(aKey)) {
                a.setType(ConfigCheckTypeEnums.WARNING.getValue());
                drMap.put(aKey, new ItemResponse(aKey, ENMTY, ConfigCheckTypeEnums.WARNING.getValue()));
                configCkeckResult.waring("阿波罗 ：参数列表为子集，校验通过！");
            }
        }

        for (Map.Entry<String, ItemResponse> entry : dMap.entrySet()) {
            String dKey = entry.getKey();
            ItemResponse d = entry.getValue();
            if (!aMap.containsKey(dKey)) {
                if (StringUtils.isNotBlank(d.getValue())) {
                    d.setType(ConfigCheckTypeEnums.ERROR.getValue());
                    arMap.put(dKey, new ItemResponse(dKey, "KEY为空", ConfigCheckTypeEnums.ERROR.getValue()));
                    configCkeckResult.fail(error("KEY为空"));
                } else {
                    d.setType(ConfigCheckTypeEnums.SUCCESS.getValue());
                    arMap.put(dKey, new ItemResponse(dKey, "值为空没有Key", ConfigCheckTypeEnums.SUCCESS.getValue()));
                    if (StringUtils.isBlank(d.getValue())) {
                        d.setValue(ENMTY);
                    }
                }
            } else {
                ItemResponse a = aMap.get(dKey);
                ConfigCkeckResult<String> sr = checkKeyValue(dKey, a.getValue(), d.getValue(), " Value");
                checkConfigCkeckResult(sr, configCkeckResult, a, d);
            }
        }
        for (Map.Entry<String, ItemResponse> entry : aMap.entrySet()) {
            arMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemResponse> entry : dMap.entrySet()) {
            drMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ItemResponse> entry : drMap.entrySet()) {
            ItemResponse config = arMap.get(entry.getKey());
            ItemResponse das = entry.getValue();
            list.add(ConfigDasItem.builder().config(config).das(das).build());
        }
        configCkeckResult.setItem(list);
        return configCkeckResult;
    }

    public static void checkConfigCkeckResult(ConfigCkeckResult<String> sr, ConfigCkeckResult<List<ConfigDasItem>> configCkeckResult, ItemResponse a, ItemResponse d) {
        if (sr.getCode() == ConfigCkeckResult.ERROR) {
            configCkeckResult.fail(sr.getMsg());
            a.setType(ConfigCheckTypeEnums.ERROR.getValue());
            d.setType(ConfigCheckTypeEnums.ERROR.getValue());
        } else if (sr.getCode() == ConfigCkeckResult.WARING) {
            configCkeckResult.waring(sr.getMsg());
            a.setType(ConfigCheckTypeEnums.WARNING.getValue());
            d.setType(ConfigCheckTypeEnums.WARNING.getValue());
        }
    }

    private static List<ItemResponse> toList(Map<String, ItemResponse> map) {
        List<ItemResponse> list = new ArrayList<>();
        for (Map.Entry<String, ItemResponse> entry : map.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    public static Map<String, ItemResponse> toMap(List<ItemResponse> list) {
        Map<String, ItemResponse> map = new HashMap<>();
        for (ItemResponse itemResponse : list) {
            map.put(itemResponse.getKey(), itemResponse);
        }
        return map;
    }

    public static ConfigCkeckResult<String> checkKeyValue(String dKey, String aVal, String dval, String msg) {
        ConfigCkeckResult configCkeckResult = ConfigCkeckResult.success(StringUtils.EMPTY);
        if (StringUtils.isNotBlank(aVal) && StringUtils.isBlank(dval)) {
            configCkeckResult.waring(msg + ", 出现空值，校验不通过");
            return configCkeckResult;
        }
        if (StringUtils.isBlank(aVal) && StringUtils.isNotBlank(dval)) {
            configCkeckResult.fail(msg + ", 出现空值，校验不通过");
            return configCkeckResult;
        }
        if (!filer(dKey, aVal, dval)) {
            configCkeckResult.fail(msg + ", 配置不等，校验不通过");
            return configCkeckResult;
        }
        return configCkeckResult;
    }

    private static boolean filer(String dKey, String aVal, String dval) {
        if (aVal.equals(dval)) {
            return true;
        }
        String key = getKey(dKey);
        if (checkFiler.containsKey(key)) {
            String separator = checkFiler.get(key);
            List<String> alist = StringUtil.toList(aVal, separator);
            List<String> dlist = StringUtil.toList(dval, separator);
            return isListEquals(alist, dlist);
        }
        return false;
    }

    private static boolean isListEquals(List<String> alist, List<String> blist) {
        Set<String> differenceSet = Sets.symmetricDifference(Sets.newHashSet(alist), Sets.newHashSet(blist));
        return CollectionUtils.isEmpty(differenceSet);
    }

    public static List<ItemResponse> mapToItemList(Map<String, String> map) {
        List<ItemResponse> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            items.add(new ItemResponse(entry.getKey(), entry.getValue()));
        }
        return ConfigCheckBase.sortItemResponses(items);
    }

    public static List<ItemResponse> sortItemResponses(List<ItemResponse> items) {
        if (CollectionUtils.isNotEmpty(items)) {
            Collections.sort(items, itemsOrdering);
        }
        return items;
    }

    private static String getKey(String key) {
        List<String> list = Splitter.on(".").omitEmptyStrings().trimResults().splitToList(key);
        return list.get(list.size() - 1);
    }

    private static Ordering<ItemResponse> itemsOrdering = Ordering.natural().nullsFirst().onResultOf(new Function<ItemResponse, String>() {
        @Override
        public String apply(ItemResponse input) {
            return input.getKey();
        }
    });

    public static String error(String val) {
        return DasEnv.getConfigCenterName() + " ：" + val + ",校验不通过！";
    }
}
