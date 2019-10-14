package com.ppdai.das.core.status;

public interface HAStatusMBean {

	boolean isEnabled();

	void setEnabled(boolean enabled);

	int getRetryCount();

	void setRetryCount(int retryCount);

	String getSqlserverErrorCodes();

	void setSqlserverErrorCodes(String sqlserverErrorCodes);

	String getMysqlErrorCodes();

	void setMysqlErrorCodes(String mysqlErrorCodes);

}