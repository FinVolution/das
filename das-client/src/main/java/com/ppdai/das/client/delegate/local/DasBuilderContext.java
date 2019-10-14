package com.ppdai.das.client.delegate.local;

import static com.ppdai.das.core.helper.ShardingManager.buildTableName;
import static com.ppdai.das.core.helper.ShardingManager.isTableShardingEnabled;
import static com.ppdai.das.core.helper.ShardingManager.locateTableShardId;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SegmentConstants;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.client.sqlbuilder.Table;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.enums.DatabaseCategory;
import com.ppdai.das.strategy.ConditionList;

public class DasBuilderContext implements BuilderContext {
    private String appId;
    private String logicDbName;
    private DatabaseCategory dbCategory;
    private SqlBuilder builder;
    private Hints ctripHints;
    private List<Parameter> parameters;
    private ConditionList conditions;
    private Map<Object, String> tableMap = new HashMap<>();

    public DasBuilderContext(String appId, String logicDbName){
        this.appId = appId;
        this.logicDbName = logicDbName;
    }

    public DasBuilderContext(String appId, String logicDbName, Hints ctripHints, List<Parameter> parameters) {
        this(appId, logicDbName);
        this.dbCategory = DasConfigureFactory.getDalConfigure(appId).getDatabaseSet(logicDbName).getDatabaseCategory();
        this.ctripHints = ctripHints;
        this.parameters = parameters;
    }

    public DasBuilderContext(String appId, String logicDbName, Hints ctripHints, List<Parameter> parameters, ConditionList conditions) {
        this(appId, logicDbName);
        this.dbCategory = DasConfigureFactory.getDalConfigure(appId).getDatabaseSet(logicDbName).getDatabaseCategory();
        this.ctripHints = ctripHints;
        this.parameters = parameters;
        this.conditions = conditions;
    }

    public DasBuilderContext(String appId, String logicDbName, Hints ctripHints, List<Parameter> parameters, SqlBuilder builder) {
        this(appId, logicDbName, ctripHints, parameters);
        this.builder = builder;
    }

    @Override
    public String locateTableName(TableDefinition definition) {
        if(!tableMap.containsKey(definition))
            tableMap.put(definition, locate(definition.getName(), definition.getShardId(), definition.getShardValue()));
        
        return tableMap.get(definition);
    }

    @Override
    public String locateTableName(Table table) {
        if(!tableMap.containsKey(table))
            tableMap.put(table, locate(table.getName(), table.getShardId(), table.getShardValue()));
        
        return tableMap.get(table);
    }
    
    /**
     * Wrap table or column name
     * 
     * @param name
     * @return
     */
    public String wrapName(String name) {
        return wrapField(dbCategory, name);
    }
    
    public static String wrapField(DatabaseCategory dbCategory, String fieldName){
        if("*".equalsIgnoreCase(fieldName) || fieldName.contains("ROW_NUMBER") || fieldName.contains(",")){
            return fieldName;
        }

        return dbCategory.quote(fieldName);
    }

    @Override
    public String declareTableName(String name) {
        if(dbCategory != DatabaseCategory.SqlServer || builder == null)
            return name;
        
        return builder.isWithLock()? name : name + " " + SegmentConstants.WITH_NO_LOCK.getText();
    }
    
    /**
     * This is copied from Ctrip AbstractFreeSqlBuilder.table implementation.
     * It is not good, should be centralized into ctrip dal. 
     * 
     * @param rawTableName
     * @param tableShardId
     * @param tableShardValue
     * @return
     * @throws SQLException
     */
    public String locate(String rawTableName, String tableShardId, Object tableShardValue) {
        try {
            if(!isTableShardingEnabled(appId, logicDbName, rawTableName))
                return wrapField(dbCategory, rawTableName);

            if(tableShardId == null && tableShardValue == null)
                tableShardId = locateTableShardId(appId, logicDbName, ctripHints.setParameters(parameters), conditions);
            else {
                Hints tmpHints = new Hints();
                tmpHints = tableShardId == null ? tmpHints : tmpHints.inTableShard(tableShardId);
                tmpHints = tableShardValue == null ? tmpHints : tmpHints.setTableShardValue(tableShardValue);
                tableShardId = locateTableShardId(appId, logicDbName, tmpHints, conditions);
            }

            return wrapField(dbCategory, buildTableName(appId, logicDbName, rawTableName, tableShardId));
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getPageTemplate() {
        switch (dbCategory) {
        case SqlServer:
            return "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        case MySql:
            return "LIMIT ?, ?";
        default:
            throw new IllegalArgumentException("Not supported for " + dbCategory);
        }
    }
}
