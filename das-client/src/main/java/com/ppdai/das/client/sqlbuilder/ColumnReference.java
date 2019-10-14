package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Segment;

public class ColumnReference implements Segment {
    private AbstractColumn column;
    
    public ColumnReference(AbstractColumn column) {
        this.column = column;
    }

    public AbstractColumn getColumn() {
        return column;
    }

    @Override
    public String build(BuilderContext helper) {
        return column.getReference(helper);
    }
    
    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
