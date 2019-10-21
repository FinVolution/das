package com.ppdai.das.core;

import java.util.Map;

public interface DasComponent {
	void initialize(Map<String, String> settings) throws Exception;
}
