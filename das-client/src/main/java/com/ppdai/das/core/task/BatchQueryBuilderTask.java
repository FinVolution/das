package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ppdai.das.client.BatchQueryBuilder;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.core.client.DalClient;
import com.ppdai.das.core.client.DalResultSetExtractor;
import com.ppdai.das.strategy.ConditionList;

public class BatchQueryBuilderTask implements SqlBuilderTask<List<?>>{
    private String appId;
    private String logicDbName;
    private List<DalResultSetExtractor<?>> extractors;

    public BatchQueryBuilderTask(String appId, String logicDbName, List<DalResultSetExtractor<?>> extractors) {
        this.appId = appId;
        this.logicDbName = logicDbName;
        this.extractors = extractors;
    }

    @Override
    public List<?> execute(DalClient client, StatementConditionProvider provider, List<Parameter> parameters, Hints hints) throws SQLException {
        BatchQueryBuilder builder = provider.getRawRequest();
        
        StringBuilder sb = new StringBuilder();
        for(SqlBuilder entry: builder.getQueries()) {
            List<Parameter> tempParameters = entry.buildParameters();
            ConditionList conditions = entry.buildQueryConditions();
            BuilderContext bc = new DasBuilderContext(appId, logicDbName, hints, tempParameters, conditions);
            String sql = entry.build(bc);
            sb.append(sql);
            if(!sql.endsWith(";"))
                sb.append(';');
        }
        
        String sql = sb.toString();
        
        // If there is no in clause, just return
        if(Parameter.containsInParameter(parameters)) {
            sql = SQLCompiler.compile(sql, getAllInParameters(parameters));
            Parameter.compile(parameters);
        }
        
        return client.query(sql, parameters, hints, extractors);
    }

    public List<List<?>> getAllInParameters(List<Parameter> parameters) {
        List<List<?>> inParams = new ArrayList<>();
        for(Parameter parameter: parameters)
            if(parameter.isInParam())
                inParams.add((List<?>)parameter.getValue());

        return inParams;
    }
}