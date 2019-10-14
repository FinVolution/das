package com.ppdai.das.client.sqlbuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.Segment;

public class ParameterSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject, Parameter.class);
    }

    @Override
    public JsonObject serialize(Segment segment) {
        Parameter parameter = (Parameter) segment;
        JsonObject jsonObject = (JsonObject) new Gson().toJsonTree(parameter);
        return addBuildType(jsonObject);
    }

    @Override
    public Class getBuildType() {
        return Parameter.class;
    }
}
