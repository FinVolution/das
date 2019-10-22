package com.ppdai.platform.das.console.api;

/**
 * 跨环境同步数据
 */
public interface SyncConfiguration {

    /**
     * 同步数据地址，如果需要从其他环境同步物理库，逻辑库配置信息，可以实现当前接口，并返回url地址
     *
     * @return
     */
    String getSyncUrl();
}
