package com.ppdai.das.core.status;

import java.util.Date;

public interface DataSourceStatusMBean {

	boolean isManualMarkdown();

	void setManualMarkdown(boolean manualMarkdown);

	boolean isAutoMarkdown();

	String getName();

	Date getManualMarkdownTime();

	Date getAutoMarkdownTime();

}