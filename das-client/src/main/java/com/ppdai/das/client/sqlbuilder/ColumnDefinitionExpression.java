package com.ppdai.das.client.sqlbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ppdai.das.client.ParameterDefinition;

public class ColumnDefinitionExpression extends ColumnExpression implements ParameterDefinitionProvider {
    private ParameterDefinition definition;

    public ColumnDefinitionExpression(String template, AbstractColumn column, ParameterDefinition definition) {
        super(template, column);
        if(column.getType() != definition.getType())
            throw new IllegalArgumentException("The parameter definition's type does not match column type");
        
        Objects.requireNonNull(definition);
        this.definition = ParameterDefinition.defineByVAR(definition, column, false);
    }

    @Override
    public  List<ParameterDefinition> buildDefinitions() {
        List<ParameterDefinition> pd = new ArrayList<ParameterDefinition>();
        pd.add(definition);
        return pd;
    }
    
    public void validate(BuilderContext context) {}
    
    /**
     * Mark this expression as optional when expression's value is null.
     */
    public ColumnExpression nullable() {
        throw new IllegalStateException("The method should not be invoked for parameter definition!");
    }
}
