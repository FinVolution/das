package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ppdai.das.client.BatchUpdateBuilder;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.core.DalClient;

public class BatchUpdateBuilderTask implements SqlBuilderTask<int[]>{
    private String appId;
    private String logicDbName;

    public BatchUpdateBuilderTask(String appId, String logicDbName) {
        this.appId = appId;
        this.logicDbName = logicDbName;
    }
            
    @Override
    public int[] execute(DalClient client, StatementConditionProvider provider, List<Parameter> parameters, Hints hints) throws SQLException {
        BatchUpdateBuilder builder = provider.getRawRequest();
        if(builder.isMultipleStatements())
            return client.batchUpdate(builder.getStatements(), hints);

        List<ParameterDefinition> defList = builder.buildDefinitions();
        List<Object[]> valuesList = builder.getValuesList();
        List<List<Parameter>> parametersList = new ArrayList<>(valuesList.size());
        
        for(Object[] values: valuesList) {
            List<Parameter> tmpParameters = new ArrayList<>();
            int i = 0;
            for(ParameterDefinition def: defList) {
                Parameter p = def.createParameter(values[i++]);
                tmpParameters.add(p);
            }
            parametersList.add(Parameter.reindex(tmpParameters));
        }
        
        DasBuilderContext ctx = new DasBuilderContext(appId, logicDbName, hints, new ArrayList());
        
        return client.batchUpdate(builder.build(ctx), parametersList.toArray(new ArrayList[parametersList.size()]), hints);
    }
}
