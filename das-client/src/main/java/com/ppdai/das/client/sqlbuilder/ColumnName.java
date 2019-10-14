package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Segment;

public class ColumnName implements Segment {
    private AbstractColumn column;
    
    public ColumnName(AbstractColumn column) {
        this.column = column;
    }

    @Override
    public String build(BuilderContext helper) {
        return column.getColumnName();
    }
    
    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
