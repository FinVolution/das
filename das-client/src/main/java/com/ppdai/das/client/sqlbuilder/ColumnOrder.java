package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Segment;

public class ColumnOrder implements Segment {
    private ColumnReference column;
    private boolean asc;
    
    public ColumnOrder(AbstractColumn column, boolean asc) {
        this.column = new ColumnReference(column);
        this.asc = asc;
    }

    public ColumnReference getColumn() {
        return column;
    }

    public boolean isAsc() {
        return asc;
    }

    @Override
    public String build(BuilderContext helper) {
        return column.build(helper) + (asc ? " ASC" : " DESC");
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
