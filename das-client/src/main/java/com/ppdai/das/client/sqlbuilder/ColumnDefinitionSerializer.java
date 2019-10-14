package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.Segment;
import com.ppdai.das.client.TableDefinition;

import java.sql.JDBCType;
import java.util.Optional;

public class ColumnDefinitionSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jo) {
        TableDefinition td = getSerializeFactory().deserialize(jo.getAsJsonObject("table"));
        ColumnDefinition cd = new ColumnDefinition(td, jo.get("columnName").getAsString(), JDBCType.valueOf(jo.get("type").getAsInt()));
        writeField(cd, "alias", jo.get("alias").isJsonNull() ? Optional.empty() : Optional.<String>of(jo.get("alias").getAsString()));
        return cd;
    }

    @Override
    public JsonObject serialize(Segment obj) {
        JsonObject element = new JsonObject();
        ColumnDefinition cd = (ColumnDefinition) obj;
        element.addProperty("alias", cd.getAlias().orElse(null));
        element.addProperty("name", cd.getTable().getName());
        element.addProperty("columnName", cd.getColumnName());
        element.addProperty("type", cd.getType().getVendorTypeNumber());

        TableDefinition td = cd.getTable();
        JsonObject tdObject = getSerializeFactory().serialize(td, td.getClass());
        element.add("table", tdObject);
        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return ColumnDefinition.class;
    }
}
