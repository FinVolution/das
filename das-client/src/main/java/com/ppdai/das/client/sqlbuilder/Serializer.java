package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonObject;
import com.ppdai.das.client.Segment;
import org.apache.commons.lang.reflect.FieldUtils;

interface Serializer {
    Segment deserialize(JsonObject jsonObject);

    JsonObject serialize(Segment segment);

    Class getBuildType();

    default JsonObject addBuildType(JsonObject jsonObject) {
        jsonObject.addProperty("buildType", getBuildType().getName());
        return jsonObject;
    }

    default SerializeFactory getSerializeFactory() {
        return SerializeFactory.getInstance() ;
    }

    default void writeField(Object target, String fieldName, Object value) {
        try {
            FieldUtils.writeField(target, fieldName, value, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default Object readField(Object target, String fieldName) {
        try {
            return FieldUtils.readField(target, fieldName, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
