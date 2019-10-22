package com.ppdai.das.client.sqlbuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.client.Segment;

public class ParameterDefinitionSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject, ParameterDefinition.class);
    }

    @Override
    public JsonObject serialize(Segment segment) {
        ParameterDefinition pd = (ParameterDefinition) segment;
        JsonObject jsonObject = (JsonObject) new Gson().toJsonTree(pd);
        return addBuildType(jsonObject);
    }

    @Override
    public Class getBuildType() {
        return ParameterDefinition.class;
    }
}
