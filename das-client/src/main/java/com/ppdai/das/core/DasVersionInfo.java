package com.ppdai.das.core;

public class DasVersionInfo {
    private String dasClientVersion;
    private String dasServerVersion;
    private String customerClientVersion;
    private String customerServerVersion;
    
    public String getDasClientVersion() {
        return dasClientVersion;
    }
    public void setDasClientVersion(String dasClientVersion) {
        this.dasClientVersion = dasClientVersion;
    }
    public String getDasServerVersion() {
        return dasServerVersion;
    }
    public void setDasServerVersion(String dasServerVersion) {
        this.dasServerVersion = dasServerVersion;
    }
    public String getCustomerClientVersion() {
        return customerClientVersion;
    }
    public void setCustomerClientVersion(String customerClientVersion) {
        this.customerClientVersion = customerClientVersion;
    }
    public String getCustomerServerVersion() {
        return customerServerVersion;
    }
    public void setCustomerServerVersion(String customerServerVersion) {
        this.customerServerVersion = customerServerVersion;
    }
}
