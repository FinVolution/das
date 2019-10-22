package com.ppdai.platform.das.console.common.utils;

import com.ppdai.das.core.configure.DalConfigureFactory;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.springframework.boot.system.ApplicationHome;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ResourceUtil {

    public static final String SCRIPT_FILE = "script.sql";
    public static final String DATASOURCE_XML = "datasource.xml";
    public static final String DAL_XML = "dal.xml";
    public static final String jdbcUrlTemplate = "jdbc:mysql://%s:%s/%s";

    public static final String DATASOURCE = "Datasource";
    public static final String DATASOURCE_NAME = "name";
    public static final String DATASOURCE_USERNAME = "userName";
    public static final String DATASOURCE_PASSWORD = "password";
    public static final String DATASOURCE_CONNECTION_URL = "connectionUrl";
    public static final String DATASOURCE_DRIVER_CLASS = "driverClassName";
    public static final String DATASOURCE_MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    public static final String DATA_SET_BASE = "das_code_gen";
    public static final String DATA_SET_ROOT = "dal";
    public static final String DATA_BASE = "dao";
    public static final String DAS_SET_APPID = "das_console";

    private static ResourceUtil singleInstance = null;

    public static ResourceUtil getSingleInstance() {
        if (singleInstance == null) {
            synchronized (ResourceUtil.class) {
                if (singleInstance == null) {
                    singleInstance = new ResourceUtil();
                }
            }
        }
        return singleInstance;
    }

    public String getClasspath() {
        ApplicationHome h = new ApplicationHome(DbUtil.class);
        File jarF = h.getSource();
        return jarF.getParentFile().toString();
    }

    public boolean isDatasourceExist() {
        String path = getDbXmlPath();
        File file = new File(path);
        return file.exists();
    }

    public boolean isDasExist() {
        String path = getDasXmlPath();
        File file = new File(path);
        return file.exists();
    }

    public String getDbXmlPath() {
        return getClasspath() + "/" + DATASOURCE_XML;
    }

    public String getDasXmlPath() {
        return getClasspath() + "/" + DAL_XML;
    }

    /**
     * 判断格式
     *
     * @return
     * @throws Exception
     */
    public boolean datasourceXmlValid() throws Exception {
        boolean result = true;
        Document document = XmlUtil.getDocumentByPath(ResourceUtil.getSingleInstance().getDbXmlPath());
        if (document == null) {
            return false;
        }
        Element root = document.getRootElement();
        List<Element> nodes = XmlUtil.getChildElements(root, DATASOURCE);
        if (CollectionUtils.isEmpty(nodes)) {
            return false;
        }
        Element node = nodes.get(0);
        String userName = XmlUtil.getAttribute(node, DATASOURCE_USERNAME);
        result &= StringUtils.isNotBlank(userName);
        String password = XmlUtil.getAttribute(node, DATASOURCE_PASSWORD);
        result &= StringUtils.isNotBlank(password);
        String url = XmlUtil.getAttribute(node, DATASOURCE_CONNECTION_URL);
        result &= StringUtils.isNotBlank(url);
        return result;
    }

    /**
     * 1-1 创建db xml 并初始化dao
     *
     * @param dbaddress
     * @param dbport
     * @param dbuser
     * @param dbpassword
     * @param db_name
     * @return
     * @throws Exception
     */
    public boolean initializeDatasourceXml(String db_name, String dbaddress, String dbport, String dbuser, String dbpassword) throws Exception {
        String connectionUrl = String.format(jdbcUrlTemplate, dbaddress, dbport, db_name);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("Datasources");
        root.addElement(DATASOURCE).addAttribute(DATASOURCE_NAME, DATA_BASE)
                .addAttribute(DATASOURCE_USERNAME, dbuser).addAttribute(DATASOURCE_PASSWORD, dbpassword)
                .addAttribute(DATASOURCE_CONNECTION_URL, connectionUrl)
                .addAttribute(DATASOURCE_DRIVER_CLASS, DATASOURCE_MYSQL_DRIVER);
        try (FileWriter fileWriter = new FileWriter(getDbXmlPath())) {
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.write(document);
            writer.close();
        }
        return true;
    }

    public boolean initializeDasSetXml() throws Exception {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(DATA_SET_ROOT).addAttribute("name", DAS_SET_APPID);
        root.addElement("databaseSets").addElement("databaseSet")
                .addAttribute("name", DATA_SET_BASE).addAttribute("provider", "mysqlProvider")
                .addElement("add")
                .addAttribute("name", DATA_BASE)
                .addAttribute("connectionString", DATA_BASE)
                .addAttribute("databaseType", "Master")
                .addAttribute("sharding", "1");
        Element connectionLocator =  root.addElement("ConnectionLocator");
        connectionLocator.addElement("locator").addText("com.ppdai.das.core.datasource.DefaultDalConnectionLocator");
        connectionLocator.addElement("settings").addElement("dataSourceConfigureProvider").addText("com.ppdai.platform.das.console.config.init.ConsoleDataSourceConfigureProvider");
        try (FileWriter fileWriter = new FileWriter(getDasXmlPath())) {
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.write(document);
            writer.close();
        }
        return true;
    }

    public boolean initTables() throws Exception {
        boolean scriptExists = resourceExists(SCRIPT_FILE);
        if (!scriptExists) {
            throw new Exception("script.sql not found.");
        }
        String scriptContent = getScriptContent(SCRIPT_FILE);
        return executeSqlScript(scriptContent);
    }

    public static boolean executeSqlScript(String sqlScript) throws SQLException {
        if (StringUtils.isBlank(sqlScript)) {
            return false;
        }
        return new BaseDao().updataBysql(sqlScript) > 0;
    }

    private boolean resourceExists(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        URL url = ResourceUtil.class.getClassLoader().getResource(fileName);
        if (null != url) {
            return true;
        }
        return false;
    }

    public String getScriptContent(String scriptPath) throws Exception {
        if (StringUtils.isBlank(scriptPath)) {
            return null;
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(scriptPath);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return stringBuffer.toString();
    }

    public void resetDalConfigUrl() {
        log.info(" resetDalConfigUrl ---- > " + getDasXmlPath());
        DalConfigureFactory.setDalConfigUrl(new File(getDasXmlPath()));
    }
}
