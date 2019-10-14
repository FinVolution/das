package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ppdai.das.client.BatchCallBuilder;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.core.DalClient;

public class BatchCallBuilderTask implements SqlBuilderTask<int[]>{
    private String appId;
    private String logicDbName;

    public BatchCallBuilderTask(String appId, String logicDbName) {
        this.appId = appId;
        this.logicDbName = logicDbName;
    }
            
    @Override
    public int[] execute(DalClient client, StatementConditionProvider provider, List<Parameter> parameters, Hints hints) throws SQLException {
        BatchCallBuilder builder = provider.getRawRequest();
        BuilderContext context = new DasBuilderContext(appId, logicDbName, hints, new ArrayList<>());
        String callSql = builder.build(context);
        
        List<ParameterDefinition> defList = builder.buildDefinitions();
        List<Object[]> valuesList = builder.getValuesList();

        List<Parameter>[] paramList = new ArrayList[valuesList.size()];
        int i = 0;
        for(Object[] values: valuesList)
            paramList[i++] = ParameterDefinition.bind(defList, values);
        
        return client.batchCall(callSql, paramList, hints);

    }
}
