package com.ppdai.das.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ppdai.das.client.sqlbuilder.SqlBuilderSerializer;
import com.ppdai.das.service.DasRequest;
import com.ppdai.das.service.Entity;
import com.ppdai.das.service.EntityMeta;
import org.apache.commons.lang.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Utility for convert:
 *
 *    List<Person> -> List<Entity>  -> List<Map> -> List<Entity> -> List<Person>
 *
 * @author Shengyuan
 */
public class ConvertUtils {

    public static Entity pojo2Entity(Object row, EntityMeta meta)  {
        checkNotNull(row);
        checkNotNull(meta);

        Set<Map.Entry<String, JsonElement>> smap =  ((JsonObject)new Gson().toJsonTree(row)).entrySet();

        Map<String, Object> kMap = new HashMap<>();
        BiMap<String, String> map = HashBiMap.create(meta.getFieldMap());


        Map<String, String> m = map.inverse() ;
        smap.stream().forEach(e->{
            kMap.put(m.get(e.getKey()), e.getValue());
        });
        return new Entity().setValue(new Gson().toJson(kMap)).setEntityMeta(meta);
    }


    public static List<Entity> pojo2Entity(List<Object> rows, EntityMeta meta) {
        checkNotNull(rows);
        checkArgument(!rows.isEmpty());
        checkNotNull(meta);

        return rows.stream().map(
                obj -> pojo2Entity(obj, meta)
        ).collect(Collectors.toList());
    }

    public static Map<String, Object> entity2Map(Object row, EntityMeta meta) {
        checkNotNull(row);
        checkArgument(row instanceof Entity);

        return getOnlyElement(entity2Map(newArrayList((Entity) row), meta));
    }

    public static List<Map<String, Object>> entity2Map(List<Entity> rows, EntityMeta meta) {
        checkNotNull(rows);
        checkNotNull(meta);

        return rows.stream().map(
                entity -> {
                    Map<String, Object> row = new LinkedHashMap<>();//Must have order
                    Map<String, Object> map = new Gson().fromJson(entity.getValue(), Map.class);
                    return map;
                }).collect(Collectors.toList());
    }

    public static Entity map2Entity(Map<String, Object> map, EntityMeta meta) {
        checkNotNull(map);
        checkNotNull(meta);

        return new Entity().setValue(new Gson().toJson(map));

    }

    public static List<Entity> map2Entity(List<Map<String, Object>> maps, EntityMeta meta) {
        checkNotNull(maps);
        checkNotNull(meta);

        return maps.stream().map(map -> map2Entity(map, meta)).collect(Collectors.toList());
    }

    public static <T> List<Object> entity2POJO(List<Entity> rows, EntityMeta meta, Class clz) {
        checkNotNull(rows);
        checkNotNull(clz);

        return rows.stream().map(r->{
            try {

                if(clz == int.class) {
                    return Integer.parseInt(r.getValue());
                }
                if(clz ==long.class) {
                    return SqlBuilderSerializer.deserializePrimitive(r.getValue());
                }

                T obj = (T) clz.newInstance();
                int i = 0;
                Map map = new Gson().fromJson(r.getValue(), Map.class);
                for (String col : meta.getColumnNames()) {
                    Field f = clz.getDeclaredField(meta.getFieldMap().get(col));
                    f.setAccessible(true);

                    if(Number.class.isAssignableFrom(f.getType())){
                        Number value = (Number)map.get(col);
                        try {
                            FieldUtils.writeField(obj, meta.getFieldMap().get(col), value, true);
                        }catch (Exception e){
                            value = value.intValue();
                            FieldUtils.writeField(obj, meta.getFieldMap().get(col), value, true);
                        }
                    }else {
                        FieldUtils.writeField(obj, meta.getFieldMap().get(col), map.get(col), true);
                    }
                }
                return obj;
            }catch (Exception e){
                throw new RuntimeException(e);
            }

        }).collect(Collectors.toList());

    }

    public static List<Entity> ints2Entities(int[] results) {
        checkArgument(results.length > 0);
         return Arrays.stream(results).mapToObj(i->
                 new Entity().setValue(new Gson().toJson(i))).collect(Collectors.toList());
    }

    public static Entity fillMeta(DasRequest request) {
        EntityMeta meta = request.getEntityList().getEntityMeta();
        Entity entity = request.getEntityList().getRows().get(0);
        return entity.setEntityMeta(meta);
    }

    public static List<Entity> fillMetas(DasRequest request) {
        EntityMeta meta = request.getEntityList().getEntityMeta();
        return request.getEntityList().getRows().stream().map(
                e -> e.setEntityMeta(meta)
        ).collect(Collectors.toList());
    }


}
