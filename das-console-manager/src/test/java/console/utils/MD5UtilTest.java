package com.ppdai.platform.das.console.utils;

import com.ppdai.platform.das.console.common.utils.MD5Util;
import org.junit.Test;

public class MD5UtilTest {

    @Test
    public void parseObjectTest(){
        String s1 = MD5Util.parseStrToMd5L32("87679214");
        System.out.println(s1);
        String s2 = MD5Util.parseStrToMd5U32(s1);
        System.out.println(s2);
    }
}
