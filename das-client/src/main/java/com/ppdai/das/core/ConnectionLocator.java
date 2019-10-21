package com.ppdai.das.core;

import java.sql.Connection;
import java.util.Set;

import com.ppdai.das.core.configure.DataSourceConfigureProvider;

public interface ConnectionLocator extends DasComponent {
	
	void setup(Set<String> dbNames);
	
	Connection getConnection(String name) throws Exception;

	DataSourceConfigureProvider getProvider();
}
