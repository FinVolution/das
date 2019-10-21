package com.ppdai.das.core.task;

import java.util.List;
import java.util.Map;

import com.ppdai.das.core.client.DalParser;

public interface DaoTask<T> {
	void initialize(DalParser<T> parser);
	
	DalParser<T> getParser();
	
	Map<String, ?> getPojoFields(T daoPojo);
	
	List<Map<String, ?>> getPojosFields(List<T> daoPojos);
}
