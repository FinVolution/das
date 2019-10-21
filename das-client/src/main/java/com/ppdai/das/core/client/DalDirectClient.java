package com.ppdai.das.core.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ppdai.das.core.EventEnum;
import com.ppdai.das.client.Hints;
import com.ppdai.das.core.HintEnum;
import com.ppdai.das.core.DasConfigure;
import com.ppdai.das.core.DasLogger;
import com.ppdai.das.core.KeyHolder;
import com.ppdai.das.core.LogEntry;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.exceptions.DalException;
import com.ppdai.das.core.helper.DalColumnMapRowMapper;
import com.ppdai.das.core.helper.DalRowMapperExtractor;
import com.ppdai.das.core.helper.HintsAwareExtractor;

/**
 * The direct connection implementation for DalClient.
 * 
 * @author jhhe
 */
public class DalDirectClient implements DalClient {
    private DalStatementCreator stmtCreator;
    private DalConnectionManager connManager;
    private DalTransactionManager transManager;
    private DasLogger logger;

    public DalDirectClient(DasConfigure config, String logicDbName) {
        connManager = new DalConnectionManager(logicDbName, config);
        transManager = new DalTransactionManager(connManager);
        stmtCreator = new DalStatementCreator(config.getDatabaseSet(logicDbName).getDatabaseCategory());
        logger = config.getDasLogger();
    }

    @Override
    public <T> T query(String sql, List<Parameter> parameters, final Hints hints,
            final DalResultSetExtractor<T> extractor) throws SQLException {
        ConnectionAction<T> action = new ConnectionAction<T>() {
            @Override
            public T execute() throws Exception {
                conn = getConnection(hints, this);

                preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
                beginExecute();
                rs = executeQuery(preparedStatement, entry);
                endExectue();

                T result;

                if (extractor instanceof HintsAwareExtractor)
                    result = ((DalResultSetExtractor<T>) ((HintsAwareExtractor) extractor).extractWith(hints))
                            .extract(rs);
                else
                    result = extractor.extract(rs);

                entry.setResultCount(fetchSize(rs, result));

                return result;
            }
        };
        action.populate(EventEnum.QUERY, sql, parameters);

        return doInConnection(action, hints);
    }

    @Override
    public List<?> query(String sql, List<Parameter> parameters, final Hints hints,
            final List<DalResultSetExtractor<?>> extractors) throws SQLException {
        ConnectionAction<List<?>> action = new ConnectionAction<List<?>>() {
            @Override
            public List<?> execute() throws Exception {
                conn = getConnection(hints, this);
                preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
                List<Object> result = new ArrayList<>();
                beginExecute();

                executeMultiple(preparedStatement, entry);

                int count = 0;

                for (DalResultSetExtractor<?> extractor : extractors) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    Object partResult;
                    if (extractor instanceof HintsAwareExtractor)
                        partResult = ((DalResultSetExtractor) ((HintsAwareExtractor) extractor).extractWith(hints))
                                .extract(resultSet);
                    else
                        partResult = extractor.extract(resultSet);
                    result.add(partResult);

                    count += fetchSize(resultSet, partResult);

                    preparedStatement.getMoreResults();
                }

                endExectue();

                entry.setResultCount(count);

                return result;
            }
        };
        action.populate(EventEnum.QUERY, sql, parameters);

        return doInConnection(action, hints);
    }

    @Override
    public int update(String sql, List<Parameter> parameters, final Hints hints) throws SQLException {
        final KeyHolder generatedKeyHolder = hints.getKeyHolder();
        ConnectionAction<Integer> action = new ConnectionAction<Integer>() {
            @Override
            public Integer execute() throws Exception {
                conn = getConnection(hints, this);
                // For old generated free update, the parameters is not compiled before invoke direct client
                Parameter.compile(parameters);
                if (generatedKeyHolder == null)
                    preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
                else
                    preparedStatement = createPreparedStatement(conn, sql, parameters, hints, generatedKeyHolder);

                beginExecute();
                int rows = executeUpdate(preparedStatement, entry);
                endExectue();

                if (generatedKeyHolder == null)
                    return rows;

                rs = preparedStatement.getGeneratedKeys();
                int actualKeySize = 0;
                if (rs != null) {
                    DalRowMapperExtractor<Map<String, Object>> rse =
                            new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
                    List<Map<String, Object>> keys = rse.extract(rs);
                    generatedKeyHolder.addKeys(keys);
                    actualKeySize = keys.size();
                }

                generatedKeyHolder.addEmptyKeys(rows - actualKeySize);

                return rows;
            }
        };
        action.populate(generatedKeyHolder == null ? EventEnum.UPDATE_SIMPLE : EventEnum.UPDATE_KH, sql,
                parameters);

        return doInConnection(action, hints);
    }

    @Override
    public int[] batchUpdate(String[] sqls, final Hints hints) throws SQLException {
        ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
            @Override
            public int[] execute() throws Exception {
                conn = getConnection(hints, this);

                statement = createStatement(conn, hints);
                for (String sql : sqls)
                    statement.addBatch(sql);

                beginExecute();
                int[] ret = executeBatch(statement, entry);
                endExectue();

                return ret;
            }
        };
        action.populate(sqls);

        return executeBatch(action, hints);
    }

    @Override
    public int[] batchUpdate(String sql, List<Parameter>[] parametersList, final Hints hints)
            throws SQLException {
        ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
            @Override
            public int[] execute() throws Exception {
                conn = getConnection(hints, this);

                statement = createPreparedStatement(conn, sql, parametersList, hints);

                beginExecute();
                int[] ret = executeBatch(statement, entry);
                endExectue();

                return ret;
            }
        };
        action.populate(sql, parametersList);

        return executeBatch(action, hints);
    }

    @Override
    public void execute(DalCommand command, Hints hints) throws SQLException {
        final DalClient client = this;
        ConnectionAction<?> action = new ConnectionAction<Object>() {
            @Override
            public Object execute() throws Exception {
                command.execute(client);
                return null;
            }
        };
        action.populate(command);

        doInTransaction(action, hints);
    }

    @Override
    public void execute(final List<DalCommand> commands, final Hints hints) throws SQLException {
        final DalClient client = this;
        ConnectionAction<?> action = new ConnectionAction<Object>() {
            @Override
            public Object execute() throws Exception {
                for (DalCommand cmd : commands) {
                    if (!cmd.execute(client))
                        break;
                }

                return null;
            }
        };
        action.populate(commands);

        doInTransaction(action, hints);
    }

    @Override
    public Map<String, ?> call(String callString, List<Parameter> parameters, final Hints hints)
            throws SQLException {
        ConnectionAction<Map<String, ?>> action = new ConnectionAction<Map<String, ?>>() {
            @Override
            public Map<String, ?> execute() throws Exception {
                List<Parameter> resultParameters = new ArrayList<>();
                List<Parameter> callParameters = new ArrayList<>();
                for (Parameter parameter : parameters) {
                    if (parameter.isResultsParameter()) {
                        resultParameters.add(parameter);
                    } else if (parameter.isOutParameter()) {
                        callParameters.add(parameter);
                    }
                }

                if (hints.is(HintEnum.retrieveAllSpResults) && resultParameters.size() > 0)
                    throw new DalException(
                            "Dal hint 'autoRetrieveAllResults' should only be used when there is no special result parameter specified");

                conn = getConnection(hints, this);

                callableStatement = createCallableStatement(conn, callString, parameters, hints);

                beginExecute();
                boolean retVal = executeCall(callableStatement, entry);
                int updateCount = callableStatement.getUpdateCount();

                endExectue();

                Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
                if (retVal || updateCount != -1) {
                    returnedResults
                            .putAll(extractReturnedResults(callableStatement, resultParameters, updateCount, hints));
                }
                returnedResults.putAll(extractOutputParameters(callableStatement, callParameters));
                return returnedResults;
            }
        };
        action.populateSp(callString, parameters);

        return doInConnection(action, hints);
    }

    @Override
    public int[] batchCall(String callString, List<Parameter>[] parametersList, final Hints hints)
            throws SQLException {
        ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
            @Override
            public int[] execute() throws Exception {
                conn = getConnection(hints, this);

                callableStatement = createCallableStatement(conn, callString, parametersList, hints);

                beginExecute();
                int[] ret = executeBatch(callableStatement, entry);
                endExectue();

                return ret;
            }
        };
        action.populateSp(callString, parametersList);

        return executeBatch(action, hints);
    }

    /**
     * First try getRow(), then try parse result
     * 
     * @param rs
     * @param result
     * @return
     * @throws SQLException
     */
    private int fetchSize(ResultSet rs, Object result) throws SQLException {
        // int rowCount = 0;
        // try {
        // rowCount = rs.getRow();
        // if(rowCount == 0 && rs.isAfterLast()) {
        // rs.last();
        // rowCount = rs.getRow();
        // }
        // } catch (Throwable e) {
        // // In case not support this feature
        // }
        //
        // if(rowCount > 0)
        // return rowCount;
        //
        if (result == null)
            return 0;

        if (result instanceof Collection<?>)
            return ((Collection<?>) result).size();

        return 1;
    }

    private Map<String, Object> extractReturnedResults(CallableStatement statement,
            List<Parameter> resultParameters, int updateCount, Hints hints) throws SQLException {
        Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
        if (hints.is(HintEnum.skipResultsProcessing))
            return returnedResults;

        if (hints.is(HintEnum.retrieveAllSpResults))
            return autoExtractReturnedResults(statement, updateCount);

        if (resultParameters.size() == 0)
            return returnedResults;

        boolean moreResults;
        int index = 0;
        do {
            // If resultParameters is not the same as what exactly returned, there will be exception. You just
            // need to add enough result parameter to avoid this or you can set skipResultsProcessing
            Parameter resultParameter = resultParameters.get(index);
            String key = resultParameter.getName();
            Object value = updateCount == -1
                    ? resultParameters.get(index).getResultSetExtractor().extract(statement.getResultSet())
                    : updateCount;
            resultParameter.setValue(value);
            moreResults = statement.getMoreResults();
            updateCount = statement.getUpdateCount();
            index++;
            returnedResults.put(key, value);
        } while (moreResults || updateCount != -1);

        return returnedResults;
    }

    private Map<String, Object> autoExtractReturnedResults(CallableStatement statement, int updateCount)
            throws SQLException {
        Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
        boolean moreResults;
        int index = 0;
        DalRowMapperExtractor<Map<String, Object>> extractor;
        do {
            extractor = new DalRowMapperExtractor<>(new DalColumnMapRowMapper());
            String key = (updateCount == -1 ? "ResultSet_" : "UpdateCount_") + index;
            Object value = updateCount == -1 ? extractor.extract(statement.getResultSet()) : updateCount;
            moreResults = statement.getMoreResults();
            updateCount = statement.getUpdateCount();
            index++;
            returnedResults.put(key, value);
        } while (moreResults || updateCount != -1);

        return returnedResults;
    }

    private Map<String, Object> extractOutputParameters(CallableStatement statement,
            List<Parameter> callParameters) throws SQLException {

        Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
        for (Parameter parameter : callParameters) {
            Object value = parameter.getName() == null ? statement.getObject(parameter.getIndex())
                    : statement.getObject(parameter.getName());

            parameter.setValue(value);
            if (value instanceof ResultSet) {
                value = parameter.getResultSetExtractor().extract(statement.getResultSet());
            }
            returnedResults.put(parameter.getName(), value);
        }
        return returnedResults;
    }

    private <T> T executeBatch(ConnectionAction<T> action, Hints hints) throws SQLException {
        if (hints.is(HintEnum.forceAutoCommit)) {
            return doInConnection(action, hints);
        } else {
            return doInTransaction(action, hints);
        }
    }

    private <T> T doInConnection(ConnectionAction<T> action, Hints hints) throws SQLException {
        return connManager.doInConnection(action, hints);
    }

    private <T> T doInTransaction(ConnectionAction<T> action, Hints hints) throws SQLException {
        return transManager.doInTransaction(action, hints);
    }

    public Connection getConnection(Hints hints, ConnectionAction<?> action) throws SQLException {
        action.beginConnect();

        long connCost = System.currentTimeMillis();
        action.connHolder = transManager.getConnection(hints, action.operation);
        Connection conn = action.connHolder.getConn();
        connCost = System.currentTimeMillis() - connCost;
        action.entry.setConnectionCost(connCost);

        action.endConnect();
        return conn;
    }

    private Statement createStatement(Connection conn, Hints hints) throws Exception {
        return stmtCreator.createStatement(conn, hints);
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, List<Parameter> parameters,
            Hints hints) throws Exception {
        return stmtCreator.createPreparedStatement(conn, sql, parameters, hints);
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, List<Parameter> parameters,
            Hints hints, KeyHolder keyHolder) throws Exception {
        return stmtCreator.createPreparedStatement(conn, sql, parameters, hints, keyHolder);
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, List<Parameter>[] parametersList,
            Hints hints) throws Exception {
        return stmtCreator.createPreparedStatement(conn, sql, parametersList, hints);
    }

    private CallableStatement createCallableStatement(Connection conn, String sql, List<Parameter> parameters,
            Hints hints) throws Exception {
        return stmtCreator.createCallableStatement(conn, sql, parameters, hints);
    }

    private CallableStatement createCallableStatement(Connection conn, String sql, List<Parameter>[] parametersList,
            Hints hints) throws Exception {
        return stmtCreator.createCallableStatement(conn, sql, parametersList, hints);
    }

    private ResultSet executeQuery(final PreparedStatement preparedStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<ResultSet>() {
            public ResultSet call() throws Exception {
                return preparedStatement.executeQuery();
            }
        }, entry);
    }

    private void executeMultiple(final PreparedStatement preparedStatement, final LogEntry entry) throws Exception {
        execute(new Callable<Object>() {
            public Object call() throws Exception {
                preparedStatement.execute();
                return null;
            }
        }, entry);
    }

    private int executeUpdate(final PreparedStatement preparedStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<Integer>() {
            public Integer call() throws Exception {
                return entry.setAffectedRows(preparedStatement.executeUpdate());
            }
        }, entry);
    }

    private int[] executeBatch(final Statement statement, final LogEntry entry) throws Exception {
        return execute(new Callable<int[]>() {
            public int[] call() throws Exception {
                return entry.setAffectedRowsArray(statement.executeBatch());
            }
        }, entry);
    }

    private Boolean executeCall(final CallableStatement callableStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return callableStatement.execute();
            }
        }, entry);
    }

    private int[] executeBatch(final CallableStatement callableStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<int[]>() {
            public int[] call() throws Exception {
                return entry.setAffectedRowsArray(callableStatement.executeBatch());
            }
        }, entry);
    }

    private <T> T execute(Callable<T> statementTask, LogEntry entry) throws Exception {
        Throwable error = null;
        logger.startStatement(entry);

        try {
            return statementTask.call();
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            logger.endStatement(entry, error);
        }
    }
}
