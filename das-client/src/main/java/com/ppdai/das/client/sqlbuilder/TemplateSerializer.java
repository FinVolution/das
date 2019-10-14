package com.ppdai.das.client.sqlbuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.Segment;

import java.util.List;

public class TemplateSerializer implements Serializer  {

    @Override
    public Segment deserialize(JsonObject jsonObject) {
        Template template = new Template(jsonObject.get("template").getAsString());
        List parameters =  new Gson().fromJson(jsonObject.get("parameters"), new TypeToken<List<Parameter>>(){}.getType());
        writeField(template, "parameters", parameters);
        writeField(template, "included", jsonObject.get("included").getAsBoolean());
        return template;
    }

    @Override
    public JsonObject serialize(Segment segment) {
        Template template = (Template) segment;
        JsonObject element = new JsonObject();
        element.addProperty("included", template.isIncluded());
        element.addProperty("template", readField(template, "template").toString());
        JsonElement parameters = new Gson().toJsonTree(readField(template, "parameters"));
        element.add("parameters", parameters);

        return addBuildType(element);
    }

    @Override
    public Class getBuildType() {
        return Template.class;
    }
}
