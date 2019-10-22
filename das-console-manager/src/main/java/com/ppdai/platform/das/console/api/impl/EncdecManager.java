package com.ppdai.platform.das.console.api.impl;

import com.ppdai.platform.das.console.api.EncdecConfiguration;
import com.ppdai.platform.das.console.common.utils.MD5Util;

public class EncdecManager implements EncdecConfiguration {

    @Override
    public String encrypt(String input) {
        return input;
    }

    @Override
    public String decrypt(String input) {
        return input;
    }

    @Override
    public String parseUnidirection(String input) {
        return MD5Util.parseStrToMd5L32(input);
    }

}
