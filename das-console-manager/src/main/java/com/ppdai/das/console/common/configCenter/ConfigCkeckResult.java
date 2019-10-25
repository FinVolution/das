package com.ppdai.das.console.common.configCenter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wanglinag
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCkeckResult<T> {

    public static final int ERROR = 500;
    public static final int SUCCESS = 200;
    public static final int WARING = 300;
    private int code = SUCCESS;
    private String msg;
    private T item;

    public void fail(String msg) {
        this.code = ConfigCkeckResult.ERROR;
        this.msg = this.msg + ", " + msg;
    }

    public void waring(String msg) {
        this.code = ConfigCkeckResult.WARING;
        this.msg = msg;
    }

    public static ConfigCkeckResult success(String msg) {
        ConfigCkeckResult configCkeckResult = new ConfigCkeckResult();
        configCkeckResult.setCode(ConfigCkeckResult.SUCCESS);
        configCkeckResult.setMsg(msg);
        return configCkeckResult;
    }

    public static ConfigCkeckResult success() {
        ConfigCkeckResult configCkeckResult = new ConfigCkeckResult();
        configCkeckResult.setMsg("success");
        return configCkeckResult;
    }

    public static ConfigCkeckResult fail() {
        ConfigCkeckResult configCkeckResult = new ConfigCkeckResult();
        configCkeckResult.setCode(ConfigCkeckResult.ERROR);
        configCkeckResult.setMsg("fail");
        return configCkeckResult;
    }

    public static ConfigCkeckResult fail(String msg, Object item) {
        ConfigCkeckResult configCkeckResult = new ConfigCkeckResult();
        configCkeckResult.setCode(ConfigCkeckResult.ERROR);
        configCkeckResult.setMsg(msg);
        configCkeckResult.setItem(item);
        return configCkeckResult;
    }

    public static ConfigCkeckResult success(String msg, Object item) {
        ConfigCkeckResult configCkeckResult = new ConfigCkeckResult();
        configCkeckResult.setItem(item);
        configCkeckResult.setMsg(msg);
        return configCkeckResult;
    }
}
