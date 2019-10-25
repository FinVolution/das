package com.ppdai.das.console.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.das.console.common.utils.DasEnv;
import com.ppdai.das.console.common.utils.DataSourceUtil;
import com.ppdai.das.console.common.utils.DbUtil;
import com.ppdai.das.console.common.utils.ResourceUtil;
import com.ppdai.das.console.dao.GroupDao;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dao.SetupDatabaseDao;
import com.ppdai.das.console.dao.UserGroupDao;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.ConnectionRequest;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.enums.DataBaseEnum;
import com.ppdai.das.console.enums.OperateUserEnum;
import com.ppdai.das.console.enums.RoleTypeEnum;
import com.ppdai.das.console.constant.Consts;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wang.liang on 2018/8/23.
 */
@Slf4j
@Service
public class SetupDataBaseService {

    public static final Object LOCK = new Object();

    @Autowired
    private Consts consts;

    @Autowired
    private SetupDatabaseDao setupDBDao;

    @Autowired
    private LoginUserDao loginUserDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Getter
    @Setter
    private boolean initialized = false;

    private ObjectMapper mapper = new ObjectMapper();

    private final String APPLICATIONNAME = "application.properties";
    private final String DATASOURCE_XML = "datasource.xml";
    private final String DATASOURCE = "Datasource";
    private final String DATASOURCE_NAME = "name";
    private final String DATASOURCE_USERNAME = "userName";
    private final String DATASOURCE_PASSWORD = "password";
    private final String DATASOURCE_CONNECTION_URL = "connectionUrl";
    private final String DATASOURCE_DRIVER_CLASS = "driverClassName";
    public final static String DATASOURCE_MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private final String SCRIPT_FILE = "script.sql";
    public final static String jdbcUrlTemplate = "jdbc:mysql://%s:%s/%s";
    private static final String CREATE_TABLE = "CREATE TABLE";

    private static ClassLoader classLoader = null;

    static {
        synchronized (LOCK) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = SetupDataBaseService.class.getClassLoader();
            }
        }
    }

    public ServiceResult connectionTest(ConnectionRequest connectionRequest) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtil.getConnection(connectionRequest.getDb_address(), connectionRequest.getDb_port(), connectionRequest.getDb_user(), DasEnv.encdecConfiguration.decrypt(connectionRequest.getDb_password()), DataBaseEnum.getDataBaseEnumByType(connectionRequest.getDb_type()).getDriver());
            rs = conn.getMetaData().getCatalogs();
            Set<String> allCatalog = new HashSet<>();
            while (rs.next()) {
                allCatalog.add(rs.getString("TABLE_CAT"));
            }
            return ServiceResult.success(allCatalog);
        } catch (Throwable e) {
            log.error(e.getMessage());
            return ServiceResult.fail(e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(conn);
        }
    }


    /**
     * 1-2 判断系统表是否存在
     *
     * @param catalog
     * @return
     * @throws Exception
     */
    public boolean tableConsistent(String catalog) throws Exception {
        Set<String> catalogTableNames = setupDBDao.getCatalogTableNames(catalog);
        if (CollectionUtils.isEmpty(catalogTableNames)) {
            return false;
        }
        String scriptContent = ResourceUtil.getSingleInstance().getScriptContent(SCRIPT_FILE);
        Set<String> scriptTableNames = getScriptTableNames(scriptContent);
        if (CollectionUtils.isNotEmpty(scriptTableNames)) {
            for (String tableName : scriptTableNames) {
                if (!catalogTableNames.contains(tableName)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Set<String> getScriptTableNames(String script) {
        Set<String> set = new HashSet<>();
        if (script == null || script.length() == 0) {
            return set;
        }

        String[] array = script.toUpperCase().split(";");
        for (int i = 0; i < array.length; i++) {
            int beginIndex = array[i].indexOf(CREATE_TABLE);
            if (beginIndex == -1) {
                continue;
            }

            beginIndex += CREATE_TABLE.length();
            int endIndex = array[i].indexOf("(");
            String temp = array[i].substring(beginIndex, endIndex);
            String tableName = temp.replaceAll("`", "").trim();
            if (tableName != null && tableName.length() > 0) {
                set.add(tableName);
            }
        }
        return set;
    }


    private boolean resourceExists(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }

        URL url = classLoader.getResource(fileName);
        if (null != url) {
            return true;
        }
        return false;
    }

    public boolean setupAdmin(DasGroup dasGroup, LoginUser user) throws Exception {
        if (StringUtils.isBlank(dasGroup.getGroup_name())) {
            return false;
        }

        if (StringUtils.isBlank(user.getUserName())) {
            return false;
        }

        Long userResult = loginUserDao.insertUser(user);
        if (userResult <= 0) {
            return false;
        }
        user = loginUserDao.getUserByNo(user.getUserNo());

        dasGroup.setId(consts.SUPER_GROUP_ID);
        dasGroup.setUpdate_user_no(user.getUserNo());

        Long groupResult = groupDao.insertDasGroup(dasGroup);
        if (groupResult <= 0) {
            return false;
        }

        Long userGroupResult = userGroupDao.insertUserGroup(user.getId(), consts.SUPER_GROUP_ID, RoleTypeEnum.Admin.getType(), OperateUserEnum.Allow.getType());
        if (userGroupResult <= 0) {
            return false;
        }
        return true;
    }

    public boolean isDalInitialized() {
        if (!initialized) {
            synchronized (LOCK) {
                try {
                    initialized = resourceExists(DATASOURCE_XML);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return initialized;
    }
}
