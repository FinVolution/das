package com.ppdai.das.client.sqlbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.client.Segment;

/**
 * Represent a template with parameter placeholder in it and a list of 
 * 
 * @author hejiehui
 *
 */
public class Template implements Segment, Includable<Template>, ParameterProvider, ParameterDefinitionProvider {
    private String template;
    private boolean included = true;
    private List<Parameter> parameters = new ArrayList<>();
    private List<ParameterDefinition> parameterDefinitions = new ArrayList<>();

    Template (String template){
        this.template = Objects.requireNonNull(template, "template should be provided");
    }

    public Template(String template, Parameter...parameters) {
        this(template);
        this.parameters.addAll(Arrays.asList(parameters));
        validate(parameters.length);
    }

    public Template(String template, ParameterDefinition...parameterDefinitions) {
        this.template = Objects.requireNonNull(template, "template should be provided");
        this.parameterDefinitions.addAll(Arrays.asList(parameterDefinitions));
        validate(parameterDefinitions.length);
    }
    
    private void validate(int size) {
        int placeHolderCount = 0;
        for(char c: template.toCharArray())
            if(c == '?')
                placeHolderCount++;
        
        if(size != placeHolderCount)
            throw new IllegalStateException("This count of parameter place holder ? does not match the count of parameters or definition for " + template);
    }
    
    /**
     * Mark this expression as as optional when condition is not meet.
     */
    public Template when(boolean condition) {
        included = condition;
        return this;
    }
    
    public boolean isIncluded() {
        return included;
    }

    @Override
    public String build(BuilderContext helper) {
        if(included == false)
            throw new IllegalStateException(template + " should not be included in final statement.");
        
        return template;
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }

    /**
     * @return the parameters with direction, name, type and value 
     */
    @Override
    public List<Parameter> buildParameters() {
        return parameters;
    }
    
    /**
     * @return the parameter definition with direction, name and type
     */
    @Override
    public List<ParameterDefinition> buildDefinitions() {
        return parameterDefinitions;
    }
}
