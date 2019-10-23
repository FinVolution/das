package com.ppdai.das.client;

import java.util.ArrayList;
import java.util.List;

import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.client.sqlbuilder.DefaultBuilderContext;
import com.ppdai.das.client.sqlbuilder.ParameterDefinitionProvider;
import com.ppdai.das.client.sqlbuilder.Template;

public class BatchUpdateBuilder implements Segment, ParameterDefinitionProvider {
    private SqlBuilder builder;

    private List<Object[]> valuesList = new ArrayList<>();
    private Hints hints = new Hints();

    private String[] statements;

    public BatchUpdateBuilder(SqlBuilder builder) {
        this.builder = builder;
    }

    public BatchUpdateBuilder(String statement, ParameterDefinition...definitions) {
        builder = new SqlBuilder();
        builder.append(new Template(statement, definitions));
    }

    public SqlBuilder getBuilder() {
        return builder;
    }

    public BatchUpdateBuilder(String statement, ColumnDefinition...columns) {
        builder = new SqlBuilder();
        ParameterDefinition[] definitions = new ParameterDefinition[columns.length];
        int i = 0;
        for(ColumnDefinition c: columns)
            definitions[i++] = ParameterDefinition.var(c.getColumnName(), c.getType());

        builder.append(new Template(statement, definitions));
    }

    public BatchUpdateBuilder(String[] statements) {
        this.statements = statements;
    }

    public void addBatch(Object...values) {
        checkPermission();
        valuesList.add(values);
    }

    public boolean isMultipleStatements() {
        return statements != null;
    }

    public String[] getStatements() {
        return statements;
    }

    public BatchUpdateBuilder setStatements(String[] statements) {
        this.statements = statements;
        return this;
    }

    public List<Object[]> getValuesList() {
        return valuesList;
    }

    public Hints hints() {
        return builder == null ? hints : builder.hints();
    }

    private void checkPermission() {
        if(statements != null)
            throw new IllegalArgumentException("Parameter should not be defined for multiple SQL update");
    }

    @Override
    public String build(BuilderContext context) {
        return builder.build(context);
    }

    @Override
    public List<ParameterDefinition> buildDefinitions() {
        return builder.buildDefinitions();
    }

    public BatchUpdateBuilder setHints(Hints hints) {
        this.hints = hints;
        return this;
    }

    public Hints getHints() {
        return hints;
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
