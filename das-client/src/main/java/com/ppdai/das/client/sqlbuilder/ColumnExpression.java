package com.ppdai.das.client.sqlbuilder;

import java.util.Objects;

public abstract class ColumnExpression extends Expression {
    protected String template;
    protected AbstractColumn rightColumn;

    public ColumnExpression(String template, AbstractColumn rightColumn) {
        this.template = Objects.requireNonNull(template);
        this.rightColumn = Objects.requireNonNull(rightColumn);
    }

    @Override
    public String build(BuilderContext context) {
        validate(context);
        return String.format(template, rightColumn.getReference(context));
    }

    /**
     * Mark this expression as optional when expression's value is null.
     */
    public abstract ColumnExpression nullable();
    
    protected abstract void validate(BuilderContext context);

    String getTemplate() {
        return template;
    }

    AbstractColumn getRightColumn() {
        return rightColumn;
    }
}
