package com.ppdai.das.client;

import java.sql.SQLException;
import java.util.List;

import com.google.common.base.Preconditions;
import com.ppdai.das.client.delegate.CallableTransactionAdapter;
import com.ppdai.das.client.delegate.DasDelegate;

/**
 * For method with hints parameter, you should provide non or just one hints parameter.
 * @author hejiehui
 * 
 */
public class DasClient {
    private static final String DAS_CLIENT_DEBUG = "das.client.debug";
	private DasDelegate delegate;
	private final boolean debugMode;

	/**
	 * Constructor with {@code DasDelegate}.
	 *
	 * @param delegate
	 */
	public DasClient(DasDelegate delegate) {
		this.delegate = delegate;
		debugMode = Boolean.valueOf(System.getProperty(DAS_CLIENT_DEBUG, "false"));
	}

	/**
	 * Query by primary key, the key columns are pass in the pojo.
	 *
	 * @param pk The pojo used to represent primary key(s)
	 * @param hints Additional parameters
	 * @return entity of this table. Null if no result found.
	 * @throws SQLException
	 */
	public <T> T queryByPk(T pk, Hints...hints) throws SQLException {
        final Hints hint = checkHints(hints);
        return internalExecute(hint, () ->{return delegate.queryByPk(pk, hint);});
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo will be used as search criteria. If all
	 * attributes in pojo are null,an exception will be thrown.
	 *
	 * @param sample The pojo used for sampling
	 * @param hints Additional parameters
	 * @return List of pojos that have the same attributes like in the sample
	 * @throws SQLException
	 */
	public <T> List<T> queryBySample(T sample, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
	    return internalExecute(hint, ()->{return delegate.queryBySample(sample, hint);});
	}

	/**
	 * Query against sample pojo with PageRange. All not null attributes of the passed in pojo will be used as search criteria. If all
	 * attributes in pojo are null,an exception will be thrown.
	 *
	 * @param sample The pojo used for sampling
	 * @param range A value object that represent page range
	 * @param hints Additional parameters
	 * @return List of pojos that have the same attributes like in the sample
	 * @throws SQLException
	 */
	public <T> List<T> queryBySample(T sample, PageRange range, Hints...hints) throws SQLException {
        final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.queryBySample(sample, range, hint);});
	}

	/**
	 * Query count of records which satisfies sample pojo as search criteria.
	 *
	 * @param sample The pojo used for sampling
	 * @param hints Additional parameters
	 * @return count of records
	 * @throws SQLException
	 */
    public <T> long countBySample(T sample, Hints...hints) throws SQLException {
        final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.countBySample(sample, hint);});
    }

	/**
	 * Insert pojo to database.
	 *
	 * @param entity pojo to be inserted
	 * @param hints Additional parameters
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public <T> int insert(T entity, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.insert(entity, hint);});
	}

	/**
	 * Insert pojos one by one.
	 *
	 * @param entities list of pojos to be inserted
	 * @param hints Additional parameters
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public <T> int insert(List<T> entities, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.insert(entities, hint);});
	}

	/**
	 * Insert pojos in batch mode.
	 *
	 * @param entities list of pojos to be inserted
	 * @param hints Additional parameters
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public <T> int[] batchInsert(List<T> entities, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.batchInsert(entities, hint);});
	}

	/**
	 * Delete the given pojo with primary key.
	 *
	 * @param pk pojo to be deleted
	 * @param hints Additional parameters
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public <T> int deleteByPk(T pk, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.deleteByPk(pk, hint);});
	}

	/**
	 * Delete against sample pojo
	 *
	 * @param sample The pojo used for sampling
	 * @param hints Additional parameters
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public <T> int deleteBySample(T sample, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.deleteBySample(sample, hint);});
	}

	/**
	 * Delete the given pojo list in batch.
	 *
	 * @param entities list of pojos to be deleted
	 * @param hints Additional parameters
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public <T> int[] batchDelete(List<T> entities, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.batchDelete(entities, hint);});
	}

	/**
	 * Update the given pojo . By default, if a field of pojo is null value, that field will be ignored, so that it will
	 * not be updated. You can overwrite this by set updateNullField in hints.
	 *
	 * @param entity pojo to be updated
	 * @param hints Additional parameters
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public <T> int update(T entity, Hints...hints) throws SQLException {
        Preconditions.checkArgument(!(entity instanceof SqlBuilder), "Please call SqlBuilder.hints()");
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.update(entity, hint);});
	}

	/**
	 * Update the given pojo list in batch.
	 *
	 * @param entities pojo to be updated
	 * @param hints Additional parameters
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public <T> int[] batchUpdate(List<T> entities, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.batchUpdate(entities, hint);});
	}

	/**
	 * Query a record or data with SqlBuilder
	 *
	 * @param builder note: you could call allowNullResult() from Hints to allow null result, or it throws exception by default
	 * @return result satisfies the SqlBuilder
	 * @throws SQLException if no result or multiple ones found by default
	 */
    public <T> T queryObject(SqlBuilder builder) throws SQLException {
        final Hints hint = checkHints(builder.hints());
        return internalExecute(hint, ()->{return delegate.queryObject(builder);});
    }

    /**
     * Query a record or data with SqlBuilder. If not found, the result is null.
     *
     * @param builder note: you could call allowNullResult() from Hints to allow null result, or it throws exception by default
     * @return result satisfies the SqlBuilder
     * @throws SQLException if no result or multiple ones found by default
     */
    public <T> T queryObjectNullable(SqlBuilder builder) throws SQLException {
        final Hints hint = checkHints(builder.hints());
        return internalExecute(hint, ()->{return delegate.queryObjectNullable(builder);});
    }

	/**
	 * Query by given SqlBuilder
	 *
	 * @param builder SqlBuilder
	 * @return List of pojos that meet the search criteria
	 * @throws SQLException
	 */
	public <T> List<T> query(SqlBuilder builder) throws SQLException {
		final Hints hint = checkHints(builder.hints());
		return internalExecute(hint, ()->{return delegate.query(builder);});
	}

	/**
	 * Query by given BatchQueryBuilder, it returns List of List pojos.
	 *
	 * @param builder BatchQueryBuilder
	 * @return List of List pojos that meet the search criteria
	 * @throws SQLException
	 */
	public List<?> batchQuery(BatchQueryBuilder builder) throws SQLException {
        final Hints hint = checkHints(builder.hints());
        return internalExecute(hint, ()->{return delegate.batchQuery(builder);});
    }

	/**
	 * Update by the given SqlBuilder
	 *
	 * @param builder SqlBuilder
	 * @return how many rows been affected
	 * @throws SQLException
	 */
    public int update(SqlBuilder builder) throws SQLException {
        final Hints hint = checkHints(builder.hints());
        return internalExecute(hint, ()->{return delegate.update(builder);});
	}

	/**
	 * Update by batch mode with SqlBuilder
	 *
	 * @param builder
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(BatchUpdateBuilder builder) throws SQLException {
	    final Hints hint = checkHints(builder.hints());
        return internalExecute(hint, ()->{return delegate.batchUpdate(builder);});
	}
	
	/**
	 * Call stored procedure.
	 *
	 * @param builder
	 * @throws SQLException
	 */
	public void call(CallBuilder builder) throws SQLException {
	    final Hints hint = checkHints(builder.hints());
        internalExecute(hint, ()->{delegate.call(builder);return null;});
	}
	
	/**
	 * Please note:
	 * If sp contains output parameter, btchCall will not retrieve it.
	 * The inout parameter may pollute consequent call 
	 * 
	 * @param builder
	 * @throws SQLException
	 */
	public int[] batchCall(BatchCallBuilder builder) throws SQLException {
	    final Hints hint = checkHints(builder.hints());
        return internalExecute(hint, ()->{return delegate.batchCall(builder);});
	}

	/**
	 * Execute sqls in transaction style without result.
	 *
	 * @param transaction callback without result
	 * @param hints Additional parameters
	 * @throws SQLException
	 */
	public void execute(Transaction transaction, Hints...hints) throws SQLException {
	    final Hints hint = checkHints(hints);
        internalExecute(hint, ()->{delegate.execute(new CallableTransactionAdapter(transaction), hint);return null;});
	}

	/**
	 * Execute sqls in transaction style with a result.
	 *
	 * @param transaction callback a result
	 * @param hints Additional parameters
	 * @return a result from callback
	 * @throws SQLException
	 */
    public <T> T execute(CallableTransaction<T> transaction, Hints...hints) throws SQLException {
        final Hints hint = checkHints(hints);
        return internalExecute(hint, ()->{return delegate.execute(transaction, hint);});
    }

	protected Hints checkHints(Hints...hintsList) throws SQLException {
		if(hintsList.length > 1)
			throw new IllegalArgumentException("You should provide non or just one hints parameter.");
		
		return hintsList.length == 0 || hintsList[0] == null ? new Hints() : hintsList[0];
	}
	
	private <T> T internalExecute(Hints hints, Diagnosable<T> action) throws SQLException {
	    if(debugMode == false)
	        return action.execute();
	    
	    if(!hints.isDiagnose())
	        hints.diagnose();

	    try {
            return action.execute();
        } catch (Throwable e) {
            System.err.println(hints.getDiagnose());
            throw e;
        }
	}

	private static interface Diagnosable<T> {
	    T execute() throws SQLException;
	}
}
