package com.ppdai.das.core.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ppdai.das.core.client.DalRowMapper;

public class ShortRowMapper implements DalRowMapper<Short> {

	@Override
	public Short map(ResultSet rs, int rowNum) throws SQLException {
		return rs.getShort(1);
	}
}
