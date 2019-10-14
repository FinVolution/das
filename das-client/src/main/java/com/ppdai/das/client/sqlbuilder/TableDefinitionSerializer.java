package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.ppdai.das.client.Segment;
import com.ppdai.das.client.TableDefinition;

public class TableDefinitionSerializer implements Serializer {

    @Override
    public Segment deserialize(JsonObject jo) {
        TableDefinition td = new TableDefinition(jo.get("name").getAsString());

        writeField(td, "shardId", jo.get("shardId").isJsonNull() ? null : jo.get("shardId").getAsString());
        writeField(td, "shardValue", jo.get("shardValue").isJsonNull() ? null : jo.get("shardValue").getAsString());
        writeField(td, "alias", jo.get("alias").isJsonNull() ? null : jo.get("alias").getAsString());
        return td;
    }

    @Override
    public JsonObject serialize(Segment obj) {
        JsonObject element = new JsonObject();
        TableDefinition td = (TableDefinition) obj;
        element.addProperty("alias", td.getAlias());
        element.addProperty("name", td.getName());
        element.addProperty("shardId", td.getShardId());
        element.addProperty("shardValue", td.getShardValue());
        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return TableDefinition.class;
    }
}