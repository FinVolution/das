package com.ppdai.das.console.common.utils;

import com.ppdai.das.console.common.codeGen.utils.DbUtils;
import org.junit.Test;

import java.util.List;

public class DbUtilsTest {

    @Test
    public void testCheckMySQLNoLimit() throws Exception {
        boolean bool = DbUtils.tableExists(267L, "app_group");
        System.out.println(bool);
    }

    @Test
    public void getAllTableNames() throws Exception {
        List<String> list = DbUtils.getAllTableNames(267L);
        System.out.println(list);
    }

}
