package com.ppdai.platform.das.console.common.codeGen.utils;

import com.ppdai.platform.das.console.dao.DataBaseDao;
import com.ppdai.platform.das.console.dao.DatabaseSetDao;
import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dao.TableEntityDao;

public class BeanGetter {

    private static TableEntityDao tableEntityDao = null;

    private static LoginUserDao daoOfLoginUser = null;

    private static DataBaseDao daoOfDalGroupDB = null;

    private static DatabaseSetDao daoOfDatabaseSet = null;

    public synchronized static DataBaseDao getDaoOfDalGroupDB() {
        if (daoOfDalGroupDB == null) {
            if (daoOfDalGroupDB == null) {
                daoOfDalGroupDB = new DataBaseDao();
            }
        }
        return daoOfDalGroupDB;
    }

    public synchronized static TableEntityDao getTableEntityDao() {
        if (tableEntityDao == null) {
            if (tableEntityDao == null) {
                tableEntityDao = new TableEntityDao();
            }
        }
        return tableEntityDao;
    }

    public synchronized static DatabaseSetDao getDaoOfDatabaseSet() {
        if (daoOfDatabaseSet == null) {
            if (daoOfDatabaseSet == null) {
                daoOfDatabaseSet = new DatabaseSetDao();
            }
        }
        return daoOfDatabaseSet;
    }

    public synchronized static LoginUserDao getDaoOfLoginUser() {
        if (daoOfLoginUser == null) {
            if (daoOfLoginUser == null) {
                daoOfLoginUser = new LoginUserDao();
            }
        }
        return daoOfLoginUser;
    }
}
