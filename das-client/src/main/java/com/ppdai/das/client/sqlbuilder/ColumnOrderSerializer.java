package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ppdai.das.client.Segment;

public class ColumnOrderSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jo) {
        AbstractColumn ac = getSerializeFactory().deserialize(jo.getAsJsonObject("column"));
        return new ColumnOrder(ac, jo.get("asc").getAsBoolean());
    }

    @Override
    public JsonObject serialize(Segment obj) {
        ColumnOrder co = (ColumnOrder) obj;
        AbstractColumn column = co.getColumn().getColumn();
        JsonObject columnObj = getSerializeFactory().serialize(column, column.getClass());

        JsonObject element = new JsonObject();
        element.add("column", columnObj);
        element.add("asc", new JsonPrimitive(co.isAsc()));
        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return ColumnOrder.class;
    }
}
