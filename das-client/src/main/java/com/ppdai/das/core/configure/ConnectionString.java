package com.ppdai.das.core.configure;

import com.ppdai.das.core.helper.ConnectionStringKeyHelper;

public class ConnectionString {
    private ConnectionStringParser parser = ConnectionStringParser.getInstance();

    private String name;
    private String ipConnectionString;
    private String domainConnectionString;

    public ConnectionString() {}

    public ConnectionString(String name, String ipConnectionString, String domainConnectionString) {
        this.name = name;
        this.ipConnectionString = ipConnectionString;
        this.domainConnectionString = domainConnectionString;
    }

    public String getName() {
        return name;
    }

    public String getIPConnectionString() {
        return ipConnectionString;
    }

    public String getDomainConnectionString() {
        return domainConnectionString;
    }

    public ConnectionStringConfigure getIPConnectionStringConfigure() {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return parser.parse(keyName, ipConnectionString);
    }

    public ConnectionStringConfigure getDomainConnectionStringConfigure() {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return parser.parse(keyName, domainConnectionString);
    }

}
