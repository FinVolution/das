package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.ppdai.das.client.Segment;

public class TableSerializer implements Serializer {

    @Override
    public Segment deserialize(JsonObject jo) {
        String name = jo.get("name").getAsString();
        Table table = new Table(name);
        writeField(table, "alias", jo.get("alias").isJsonNull() ? null : jo.get("alias").getAsString());
        writeField(table, "shardId", jo.get("shardId").isJsonNull() ? null : jo.get("shardId").getAsString());
        writeField(table, "shardValue", jo.get("shardValue").isJsonNull() ? null : jo.get("shardValue").getAsString());
        return table;
    }

    @Override
    public JsonObject serialize(Segment obj) {
        JsonObject element = new JsonObject();
        Table tb = (Table) obj;
        element.addProperty("alias", tb.getAlias());
        element.addProperty("name", tb.getName());
        element.addProperty("shardId", tb.getShardId());
        element.addProperty("shardValue", tb.getShardValue());

        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return Table.class;
    }
}
