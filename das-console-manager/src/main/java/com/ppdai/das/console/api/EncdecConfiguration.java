package com.ppdai.das.console.api;

/**
 * 加密解密
 */
public interface EncdecConfiguration {

    /**
     * 物理库密码 加密
     *
     * @param input
     * @return
     */
    String encrypt(String input);

    /**
     * 物理库密码  解密
     *
     * @param input
     * @return
     */
    String decrypt(String input);

    /**
     * 用户名密码单向 加密
     *
     * @param input
     * @return
     */
    String parseUnidirection(String input);
}
