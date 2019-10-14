package com.ppdai.das.client.sqlbuilder;

public class BooleanExpression extends Expression {
    private String template;
    
    public static final BooleanExpression TRUE = new BooleanExpression("TRUE");

    public static final BooleanExpression FALSE = new BooleanExpression("FALSE");

    private BooleanExpression(String template) {
        this.template = template;;
    }

    public Expression when(boolean condition) {
        throw new IllegalStateException("when() can not be applied to TRUE/FALSE");
    }
    
    public boolean isIncluded() {
        return true;
    }

    @Override
    public String build(BuilderContext helper) {
        return template;
    }
}