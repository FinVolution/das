package com.ppdai.das.client.sqlbuilder;

public class CustomizedExpression extends Expression {
    private String template;
    
    public CustomizedExpression(String template) {
        this.template = template;;
    }

    @Override
    public String build(BuilderContext helper) {
        return template;
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }
}