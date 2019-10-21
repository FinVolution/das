package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.List;

import com.ppdai.das.client.CallBuilder;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.core.client.DalClient;
import com.ppdai.das.core.enums.ParameterDirection;


public class CallBuilderTask implements SqlBuilderTask<Object>{
    private String appId;
    private String logicDbName;

    public CallBuilderTask(String appId, String logicDbName) {
        this.appId = appId;
        this.logicDbName = logicDbName;
    }
            
    @Override
    public Object execute(DalClient client, StatementConditionProvider provider, List<Parameter> parameters, Hints hints) throws SQLException {
        CallBuilder builder = provider.getRawRequest();
        parameters = builder.buildParameters();
        String callSql = builder.build(new DasBuilderContext(appId, logicDbName, hints, parameters));
        client.call(callSql, parameters, hints);

        populateOutput(parameters, builder.getParameters());

        //Just obey the framwork. This result will not be used
        return new Object();
    }

    private void populateOutput(List<Parameter> ctripParameters, List<Parameter> parameters) {
        for (Parameter p : parameters) {
            if (p.getDirection() == ParameterDirection.Input)
                continue;

            if (p.getDirection() == ParameterDirection.Output ||
                    p.getDirection() == ParameterDirection.InputOutput) {
                Parameter sp = get(ctripParameters, p.getName(), p.getDirection());
                p.setOutputValue(sp.getValue());
            }
        }
    }

    private Parameter get(List<Parameter> parameters, String name, ParameterDirection direction) {
        if(name == null)
            return null;

        for(Parameter parameter: parameters) {
            if(parameter.getName() != null && parameter.getName().equalsIgnoreCase(name) && direction == parameter.getDirection())
                return parameter;
        }
        return null;
    }
}
