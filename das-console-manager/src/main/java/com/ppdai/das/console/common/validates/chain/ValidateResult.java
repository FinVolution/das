package com.ppdai.das.console.common.validates.chain;

import com.ppdai.das.console.common.exceptions.ValidataException;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangliang on 2017/6/22.
 */
@Getter
@Setter
public class ValidateResult implements Serializable {

    private String summarize;
    private Map<String, String> detail;

    public ValidateResult(String summarize, Map<String, String> detail) {
        this.summarize = summarize;
        this.detail = detail;
    }

    public boolean isValid() {
        return isValid(summarize, detail);
    }

    public static ValidateResult validResult() {
        return new ValidateResult("", Collections.EMPTY_MAP);
    }

    public static ValidateResult invalidResult(String summarize, Map<String, String> detail) {
        if (isValid(summarize, detail)) {
            throw new ValidataException("不可设置合法结果");
        }
        return new ValidateResult(summarize, detail);
    }

    public static ValidateResult invalidResult(String summarize) {
        return invalidResult(summarize, new HashMap<String, String>());
    }

    // 判断合法的validateResult
    private static boolean isValid(String summarize, Map<String, String> detail) {
        return (summarize == null || summarize.length() == 0) && (detail == null || detail.isEmpty());
    }
}
