package com.ppdai.das.client.sqlbuilder;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ppdai.das.client.Segment;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SerializeFactory {

    private static final SerializeFactory instance = new SerializeFactory();

    static SerializeFactory getInstance() {
        return instance;
    }

    private SerializeFactory() {}

    void add(Serializer serializer) {
        builderMap.put(serializer.getBuildType(), serializer);
    }

    void addAll(List<Serializer> serializers) {
        for (Serializer s : serializers) {
            add(s);
        }
    }

    Map<Class, Serializer> builderMap = new HashMap<>();

    private Serializer choose(Type type) {
        try {
            Class clz = Class.forName(type.getTypeName());
            Serializer segmentBuilder = builderMap.get(clz);
            if (segmentBuilder != null) {
                return segmentBuilder;
            }

            //Check subclasses
            Optional<Serializer> optional = builderMap.entrySet().stream().filter(e -> {
                return e.getKey().isAssignableFrom(clz);
            }).map(et -> et.getValue()).findFirst();

            Preconditions.checkArgument(optional.isPresent(), "Cannot find serializer for: " + type);
            return optional.get();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Serializer choose(String clz) {
        try {
            return choose(Class.forName(clz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(JsonElement jsonObject) {
        return (T) choose(jsonObject.getAsJsonObject().get("buildType").getAsString())
                .deserialize(jsonObject.getAsJsonObject());
    }

    public JsonObject serialize(Segment segment, Type type) {
        return choose(type).serialize(segment);
    }
}
