package com.ppdai.das.client;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.client.sqlbuilder.DefaultBuilderContext;
import com.ppdai.das.client.sqlbuilder.ParameterDefinitionProvider;
import com.ppdai.das.core.enums.ParameterDirection;

public class BatchCallBuilder implements Segment, ParameterDefinitionProvider {
    private String name;
    private List<ParameterDefinition> parameterDefinitions = new ArrayList<>();
    private List<Object[]> valuesList = new ArrayList<>();
    private Hints hints = new Hints();

    public BatchCallBuilder(String name) {
        this.name = name;
    }

    public static BatchCallBuilder call(String name) {
        return new BatchCallBuilder(name);
    }

    public BatchCallBuilder registerOutput(String name, JDBCType type) {
        parameterDefinitions.add(new ParameterDefinition(ParameterDirection.Output, name, type, false));
        return this;
    }

    public BatchCallBuilder registerInput(String name, JDBCType type) {
        parameterDefinitions.add(new ParameterDefinition(ParameterDirection.Input, name, type, false));
        return this;
    }

    public BatchCallBuilder registerInputOutput(String name, JDBCType type) {
        parameterDefinitions.add(new ParameterDefinition(ParameterDirection.InputOutput, name, type, false));
        return this;
    }

    /**
     * If there is output parameter in the middle, just skip it and continue with next input value
     * @param values
     */
    public void addBatch(Object...values) {
        valuesList.add(values);
    }

    public Hints hints() {
        return hints;
    }
    
    public String getName() {
        return name;
    }

    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    public List<Object[]> getValuesList() {
        return valuesList;
    }

    @Override
    public List<ParameterDefinition> buildDefinitions() {
        return parameterDefinitions;
    }

    @Override
    public String build(BuilderContext context) {
        SqlBuilder sb = new SqlBuilder();
        sb.append("{call", name, "(").appendPlaceHolder(parameterDefinitions.size()).append(")}");
        return sb.build(context);
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
