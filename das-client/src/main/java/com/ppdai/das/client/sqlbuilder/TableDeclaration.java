package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.TableDefinition;

public class TableDeclaration implements TableReference {
    private TableReference tableRef;
    
    public static Object filter(Object table) {
        return table instanceof TableDefinition || table instanceof Table ? 
            new TableDeclaration((TableReference)table) : table;
    }
    
    private TableDeclaration(TableReference tableRef) {
        this.tableRef = tableRef;
    }

    @Override
    public String build(BuilderContext context) {
        return context.declareTableName(tableRef.build(context));
    }

    public TableReference getTableRef() {
        return tableRef;
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }

    @Override
    public String getName() {
        return tableRef.getName();
    }

    @Override
    public String getShardId() {
        return tableRef.getShardId();
    }

    @Override
    public String getShardValue() {
        return tableRef.getShardValue();
    }
}
