package com.ppdai.das.client;

import java.sql.JDBCType;
import java.util.Objects;
import java.util.Optional;

import com.ppdai.das.client.sqlbuilder.AbstractColumn;
import com.ppdai.das.client.sqlbuilder.BuilderContext;

/**
 * Immutable column that will create a new ColumnDefinition with given alias
 * 
 * Column definition and all column related sql building methods
 * 
 * @author hejiehui
 *
 */
public final class ColumnDefinition extends AbstractColumn {
    private final TableDefinition table;

    /**
     * Column alias
     */
    private Optional<String> alias = Optional.empty();

    public ColumnDefinition(TableDefinition table, String name, JDBCType type) {
        super(name, type);
        this.table = Objects.requireNonNull(table);
    }

    public ColumnDefinition as(String alias) {
        if(this.alias.isPresent())
            throw new IllegalStateException("Alias can only be set once for this column instance: " + getColumnName());

        ColumnDefinition newColumn = new ColumnDefinition(table, getColumnName(), getType());
        newColumn.alias = Optional.of(alias);

        return newColumn;
    }

    public Optional<String> getAlias() {
        return alias;
    }

    public TableDefinition getTable() {
        return table;
    }

    @Override
    public String getTableName(BuilderContext helper) {
        return table.getReference(helper);
    }
}
