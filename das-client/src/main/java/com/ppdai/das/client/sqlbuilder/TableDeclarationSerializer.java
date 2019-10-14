package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.ppdai.das.client.Segment;

public  class TableDeclarationSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jo) {
        TableReference tr = (TableReference) new TableDefinitionSerializer().deserialize(jo.getAsJsonObject("tableRef"));
        return (Segment) TableDeclaration.filter(tr);
    }

    @Override
    public JsonObject serialize(Segment obj) {
        TableDeclaration tableDeclaration = (TableDeclaration) obj;
        TableReference tr = tableDeclaration.getTableRef();
        JsonObject trObj = new TableDefinitionSerializer().serialize(tr);

        JsonObject element = new JsonObject();
        element.add("tableRef", trObj);
        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return TableDeclaration.class;
    }
}
