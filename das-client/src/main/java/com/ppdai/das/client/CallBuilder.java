package com.ppdai.das.client;

import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.client.sqlbuilder.DefaultBuilderContext;
import com.ppdai.das.client.sqlbuilder.ParameterProvider;
import com.ppdai.das.core.enums.ParameterDirection;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

public class CallBuilder implements Segment, ParameterProvider {
    private String name;
    private List<Parameter> parameters = new ArrayList<>();
    //TODO to specify if we call by index for better performance
    private boolean callByIndex;
    private Hints hints = new Hints();

    public CallBuilder(String name) {
        this.name = name;
    }
    
    public static CallBuilder call(String name) {
        return new CallBuilder(name);
    }

    public CallBuilder setHints(Hints hints) {
        this.hints = hints;
        return this;
    }

    public CallBuilder registerOutput(String name, JDBCType type) {
        parameters.add(Parameter.output(name, type));
        return this;
    }

    public CallBuilder registerInput(String name, JDBCType type, Object value) {
        parameters.add(Parameter.input(name, type, value));
        return this;
    }

    public CallBuilder registerInputOutput(String name, JDBCType type, Object value) {
        parameters.add(Parameter.inputOutput(name, type, value));
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isCallByIndex() {
        return callByIndex;
    }

    public CallBuilder setCallByIndex(boolean callByIndex) {
        this.callByIndex = callByIndex;
        return this;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Hints hints() {
        return hints;
    }
    
    @Override
    public List<Parameter> buildParameters() {
        return Parameter.reindex(parameters);
    }
    
    public <T> T getOutput(String name) {
        for(Parameter p: parameters) {
            if(p.getDirection() == ParameterDirection.Input)
                continue;
            
            if(p.getName().equals(name))
                return (T)p.getValue();
        }
        
        throw new IllegalArgumentException("Parameter: " + name + " is not found or not registered as an output/inputoutput parameter.");
    }

    @Override
    public String build(BuilderContext context) {
        SqlBuilder sb = new SqlBuilder();
        sb.append("{call", name, "(").appendPlaceHolder(parameters.size()).append(")}");
        return sb.build(context);
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
