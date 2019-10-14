package com.ppdai.das.client.sqlbuilder;

import java.sql.JDBCType;
import java.util.Optional;

import com.ppdai.das.client.Segment;

/**
 * Mutable column that can assign alias for only once
 * For dynamically constructed column bind with given table.
 * 
 * @author hejiehui
 *
 */
public final class Column extends AbstractColumn implements Segment {
    private final Table table;
    private Optional<String> alias = Optional.empty();
    
    public Column(Table table, String name, JDBCType type) {
        super(name, type);
        this.table = table;
    }
    
    public Column as(String alias) {
        if(this.alias.isPresent())
            throw new IllegalStateException("Alias can only be set once for column" + getColumnName());

        this.alias = Optional.of(alias);
        return this;
    }
    
    public Optional<String> getAlias() {
        return alias;
    }

    @Override
    public String getTableName(BuilderContext helper) {
        return table.getReferName(helper);
    }

    public Table getTable() {
        return table;
    }
}
