package com.ppdai.das.client.sqlbuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ppdai.das.client.BatchUpdateBuilder;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Segment;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.core.HintEnum;

import java.util.Map;
import java.util.function.Supplier;

public class BatchUpdateBuilderSerializer implements Serializer {
    @Override
    public Segment deserialize(JsonObject jo) {
        SqlBuilder innerBuilder = null;
        BatchUpdateBuilder sqlBuilder = new BatchUpdateBuilder(innerBuilder);

        JsonObject hintsElement = (JsonObject)jo.get("hints");
        Hints hints = deserializeHints(hintsElement);
        writeField(sqlBuilder, "hints", hints);
        return sqlBuilder;
    }

    Hints deserializeHints(JsonObject jo) {
        Hints hints = new Hints();
        boolean diagnoseMode = (boolean) SqlBuilderSerializer.instance.primitiveGson.fromJson(jo.get("diagnoseMode"), Supplier.class).get();
        if(diagnoseMode) {
            hints.diagnose();
        }

        JsonObject h = (JsonObject) jo.get("hints");
        if(h.has("allowPartial")){
            hints.allowPartial();
        }
        Map map = (Map) readField(hints, "hints");
        h.entrySet().forEach(kv->{
            if(!kv.getKey().equals("allowPartial")){
                map.put(HintEnum.valueOf(kv.getKey()), SqlBuilderSerializer.instance.primitiveGson.fromJson(kv.getValue(), Supplier.class).get());
            }
        });
        return hints;
    }

    @Override
    public JsonObject serialize(Segment segment) {
        BatchUpdateBuilder sqlBuilder = (BatchUpdateBuilder) segment;
        JsonObject root = new JsonObject();

        JsonElement hints = SqlBuilderSerializer.instance.primitiveGson.toJsonTree(sqlBuilder.hints());
        root.add("hints", hints);
        return addBuildType(root);
    }

    @Override
    public Class getBuildType() {
        return BatchUpdateBuilder.class;
    }
}
