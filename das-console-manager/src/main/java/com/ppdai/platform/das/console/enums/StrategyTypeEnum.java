package com.ppdai.platform.das.console.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 策略类型
 */
public enum StrategyTypeEnum {

    NoStrategy(0, "无策略", StringUtils.EMPTY),
    PriverStrategy(1, "私有策略", "class"),
    PublicStrategy(2, "公共策略", "strategyName");

    private int type;
    private String detail;
    private String shardingStrategy;

    StrategyTypeEnum(int type, String detail, String shardingStrategy) {
        this.type = type;
        this.detail = detail;
        this.shardingStrategy = shardingStrategy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getShardingStrategy() {
        return shardingStrategy;
    }

    public void setShardingStrategy(String shardingStrategy) {
        this.shardingStrategy = shardingStrategy;
    }

    public static StrategyTypeEnum getStrategyTypeEnumByType(int type) {
        for (StrategyTypeEnum item : StrategyTypeEnum.values()) {
            if (type == item.type) {
                return item;
            }
        }
        throw new EnumConstantNotPresentException(StrategyTypeEnum.class, "type " + type + " is not exist!!");
    }
}
