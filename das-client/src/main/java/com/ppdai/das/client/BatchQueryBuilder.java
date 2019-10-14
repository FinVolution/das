package com.ppdai.das.client;

import com.ppdai.das.client.sqlbuilder.DefaultBuilderContext;
import com.ppdai.das.service.EntityMeta;

import java.util.ArrayList;
import java.util.List;

public class BatchQueryBuilder {
    private List<SqlBuilder> queries = new ArrayList<>();
    private EntityMeta entityMeta;
    private Hints hints = new Hints();

    public void addBatch(SqlBuilder builder) {
        queries.add(builder);
    }

    public List<SqlBuilder> getQueries() {
        return queries;
    }

    public Hints hints() {
        return hints;
    }

    public EntityMeta getEntityMeta() {
        return entityMeta;
    }

    public BatchQueryBuilder setEntityMeta(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
        return this;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(SqlBuilder query: queries)
            sb.append(query.build(new DefaultBuilderContext())).append("\n");
        return sb.toString();
    }
}
