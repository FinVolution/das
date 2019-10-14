package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.ppdai.das.client.Segment;


public class PageSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jsonObject) {
        return new Page(0,0);
    }

    @Override
    public JsonObject serialize(Segment segment) {
        JsonObject element = new JsonObject();
        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return Page.class;
    }
}
