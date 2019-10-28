package com.ppdai.das.console.api;

/**
 * das-console启动时，加载物理库信息与否
 */
public interface ConfigLoader {

    /**
     * das-console启动时，加载物理库信息是否读取配置文件，如果需要从配置中心加载das-console的物理库配置，需要实现das-client的接口方法，这里再实现接口并返回false
     *
     * @return
     */
    boolean isLoaderFile();

}
