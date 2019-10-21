package com.ppdai.das.core;

import static com.ppdai.das.core.FreshnessSelector.DEFAULT_QUERY_SQL;
import static com.ppdai.das.core.FreshnessSelector.DEFAULT_SELECT_FIELD;

import java.util.Map;

import com.ppdai.das.client.DasClientFactory;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.SqlBuilder;


/**
 * Query 'show slave status' to get 'Seconds_Behind_Master'.
 *
 * @see FreshnessSelector
 */
public class SlaveStatusFreshnessReader implements FreshnessReader {

    private String querySQL = DEFAULT_QUERY_SQL;
    private String selectField = DEFAULT_SELECT_FIELD;

    @Override
    public int getSlaveFreshness(String logicDbName, String slaveConnectionString, String shard) throws Exception {
        SqlBuilder sqlBuilder = new SqlBuilder().appendTemplate(querySQL).intoMap();
        Hints hints = sqlBuilder.hints();
        hints.inDatabase(slaveConnectionString);

        if (shard != null) {
            hints.inShard(shard);
        }

        Map<String, ?> result = DasClientFactory.getClient(logicDbName).queryObjectNullable(sqlBuilder.intoMap());
        if(result != null)
            return Integer.parseInt(result.get(selectField).toString());

        return INVALID;
    }

    public SlaveStatusFreshnessReader setQuerySQL(String querySQL) {
        this.querySQL = querySQL;
        return this;
    }

    public SlaveStatusFreshnessReader setSelectField(String selectField) {
        this.selectField = selectField;
        return this;
    }

}