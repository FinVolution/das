package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.TableDefinition;

public class DefaultBuilderContext implements BuilderContext {

    @Override
    public String locateTableName(TableDefinition definition) {
        return definition.getName();
    }

    @Override
    public String locateTableName(Table table) {
        return table.getName();
    }

    @Override
    public String wrapName(String name) {
        return name;
    }

    @Override
    public String declareTableName(String name) {
        return name;
    }

    @Override
    public String getPageTemplate() {
        return Page.EMPTY;
    }
}
