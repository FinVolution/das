package com.ppdai.das.core.client;

import java.sql.Connection;
import java.util.Set;

import com.ppdai.das.core.configure.DalComponent;
import com.ppdai.das.core.configure.DataSourceConfigureProvider;

public interface DalConnectionLocator extends DalComponent {
	
	void setup(Set<String> dbNames);
	
	Connection getConnection(String name) throws Exception;

	DataSourceConfigureProvider getProvider();
}
