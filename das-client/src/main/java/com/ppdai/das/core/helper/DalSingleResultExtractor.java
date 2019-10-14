package com.ppdai.das.core.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DalResultSetExtractor;
import com.ppdai.das.core.DalRowMapper;
import com.ppdai.das.core.exceptions.DalException;
import com.ppdai.das.core.exceptions.ErrorCode;

public class DalSingleResultExtractor<T> implements DalResultSetExtractor<T>, HintsAwareExtractor<T> {
	private DalRowMapper<T> mapper;
	private boolean requireSingle;
	private Hints hints;
	
	public DalSingleResultExtractor(DalRowMapper<T> mapper, boolean requireSingle) {
		this.mapper = mapper;
		this.requireSingle = requireSingle;
	}

	@Override
	public T extract(ResultSet rs) throws SQLException {
		T result = null;
		
		checkHints(rs);
		
		if(rs.next()) {
			result = mapper.map(rs, 0);
			if(rs.next() && requireSingle)
				throw new DalException(ErrorCode.AssertSingle);
		}
		return result;
	}
	
    private void checkHints(ResultSet rs) throws SQLException {
        if(hints != null && mapper instanceof CustomizableMapper) {
            mapper = ((CustomizableMapper)mapper).mapWith(rs, hints);
        }
    }

    @Override
    public DalSingleResultExtractor<T> extractWith(Hints hints) throws SQLException {
        DalSingleResultExtractor<T> customized = new DalSingleResultExtractor<>(this.mapper, this.requireSingle);
        customized.hints = hints;
        return customized;
    }

}
