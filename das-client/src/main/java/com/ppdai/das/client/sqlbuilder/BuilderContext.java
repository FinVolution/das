package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.TableDefinition;

public interface BuilderContext {
    String locateTableName(TableDefinition definition);

    String locateTableName(Table table);
    
    /**
     * To wrap table, column name with DB specific chractors. E.g. MySql using ``, SqlServer using []
     * @param name
     * @return
     */
    String wrapName(String name);
    
    /**
     * This is to provide an opportunity for append WITH (NOLOCK) for MS SqlServer when doing SELCTE
     * @param name
     * @return
     */
    String declareTableName(String name);
    
    String getPageTemplate();
}
