package com.ppdai.das.client.delegate;

import java.sql.SQLException;
import java.util.List;

import com.ppdai.das.client.BatchCallBuilder;
import com.ppdai.das.client.BatchQueryBuilder;
import com.ppdai.das.client.BatchUpdateBuilder;
import com.ppdai.das.client.CallBuilder;
import com.ppdai.das.client.CallableTransaction;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.PageRange;
import com.ppdai.das.client.SqlBuilder;

public interface DasDelegate {
    String getAppId();

    String getLogicDbName();

    <T> T queryByPk(T id, Hints hints) throws SQLException;
    
    <T> List<T> queryBySample(T sample, Hints hints) throws SQLException;
    
    <T> List<T> queryBySample(T sample, PageRange range, Hints hints) throws SQLException;
    
    <T> long countBySample(T sample, Hints hints) throws SQLException;
    
    <T> int insert(T entity, Hints hints) throws SQLException;

    <T> int insert(List<T> entities, Hints hints) throws SQLException;

    <T> int deleteByPk(T pk, Hints hints) throws SQLException;
    
    <T> int deleteBySample(T sample, Hints hints) throws SQLException;

    <T> int update(T entity, Hints hints) throws SQLException;

    <T> int[] batchInsert(List<T> entities, Hints hints) throws SQLException;

    <T> int[] batchDelete(List<T> entities, Hints hints) throws SQLException;

    <T> int[] batchUpdate(List<T> entities, Hints hints) throws SQLException;

    int update(SqlBuilder builder) throws SQLException;

    int[] batchUpdate(BatchUpdateBuilder builder) throws SQLException;

    void call(CallBuilder builder) throws SQLException;

    int[] batchCall(BatchCallBuilder builder) throws SQLException;

    <T> T queryObject(SqlBuilder builder) throws SQLException;
    
    <T> T queryObjectNullable(SqlBuilder builder) throws SQLException;
    
    <T> List<T> query(SqlBuilder builder) throws SQLException;

    List<?> batchQuery(BatchQueryBuilder builder) throws SQLException;

    <T> T execute(CallableTransaction<T> transaction, Hints hints) throws SQLException;

}
