package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.core.client.DalClient;

public class UpdateSqlBuilderTask implements SqlBuilderTask<Integer>{
    private String appId;
    private String logicDbName;

    public UpdateSqlBuilderTask(String appId, String logicDbName) {
        this.appId = appId;
        this.logicDbName = logicDbName;
    }

    @Override
    public Integer execute(DalClient client, StatementConditionProvider provider, List<Parameter> parameters, Hints hints) throws SQLException {
        SqlBuilder builder = provider.getRawRequest();
        String sql = builder.build(new DasBuilderContext(appId, logicDbName, hints, parameters));
        
        // If there is no in clause, just return
        if(Parameter.containsInParameter(parameters)) {
            sql = SQLCompiler.compile(sql, getAllInParameters(parameters));
            Parameter.compile(parameters);
        }
        
        return client.update(sql, parameters, hints);
    }

    public List<List<?>> getAllInParameters(List<Parameter> parameters) {
        List<List<?>> inParams = new ArrayList<>();
        for(Parameter parameter: parameters)
            if(parameter.isInParam())
                inParams.add((List<?>)parameter.getValue());

        return inParams;
    }
}
