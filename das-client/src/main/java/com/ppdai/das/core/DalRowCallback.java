package com.ppdai.das.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface MUST consider potential multiple-thread concurrent access. 
 * @author jhhe
 *
 */
public interface DalRowCallback {
	void process(ResultSet rs) throws SQLException;
}
