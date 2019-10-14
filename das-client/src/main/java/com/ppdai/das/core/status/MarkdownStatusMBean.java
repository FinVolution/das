package com.ppdai.das.core.status;

public interface MarkdownStatusMBean {

	boolean isAppMarkdown();

	void setAppMarkdown(boolean markdown);

	boolean isEnableAutoMarkdown();

	void setEnableAutoMarkdown(boolean enableAutoMarkdown);

	int getAutoMarkupDelay();

	void setAutoMarkupDelay(int autoMarkUpDelay);

	String getMarkdownKeys();

	String getAutoMarkdownKeys();
	
}