package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.Segment;

public class ColumnReferenceSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jo) {
        ColumnDefinition cd = getSerializeFactory().deserialize(jo.getAsJsonObject("column"));
        return new ColumnReference(cd);
    }

    @Override
    public JsonObject serialize(Segment obj) {
        ColumnReference cr = (ColumnReference) obj;
        AbstractColumn column = cr.getColumn();
        JsonObject columnObj = getSerializeFactory().serialize(column, column.getClass());

        JsonObject element = new JsonObject();
        element.add("column", columnObj);
        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return ColumnReference.class;
    }
}