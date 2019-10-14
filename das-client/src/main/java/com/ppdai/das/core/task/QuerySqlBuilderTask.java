package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.core.DalClient;
import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DalResultSetExtractor;

public class QuerySqlBuilderTask<T> implements SqlBuilderTask<T>{
    private String appId;
    private String logicDbName;
    private DalResultSetExtractor<T> extractor;

    public QuerySqlBuilderTask(String appId, String logicDbName, DalResultSetExtractor<T> extractor) {
        this.appId = appId;
        this.logicDbName = logicDbName;
        this.extractor = extractor;        
    }

    @Override
    public T execute(DalClient client, StatementConditionProvider provider, List<Parameter> parameters, Hints hints) throws SQLException {
        SqlBuilder builder = provider.getRawRequest();
        String sql = builder.build(new DasBuilderContext(appId, logicDbName, hints, parameters));
        
        // If there is no in clause, just return
        if(Parameter.containsInParameter(parameters)) {
            sql = SQLCompiler.compile(sql, getAllInParameters(parameters));
            Parameter.compile(parameters);
        }
        
        return client.query(sql, parameters, hints, extractor);
    }

    public List<List<?>> getAllInParameters(List<Parameter> parameters) {
        List<List<?>> inParams = new ArrayList<>();
        for(Parameter parameter: parameters)
            if(parameter.isInParam())
                inParams.add((List<?>)parameter.getValue());

        return inParams;
    }

}
