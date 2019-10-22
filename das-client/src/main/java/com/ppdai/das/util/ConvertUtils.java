package com.ppdai.das.util;

import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.ppdai.das.service.DasRequest;
import com.ppdai.das.service.Entity;
import com.ppdai.das.service.EntityMeta;
import org.apache.commons.lang.reflect.FieldUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

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

        if(row instanceof Entity) {
            return (Entity) row;
        }

        if(meta == null || row instanceof Map) {
            String json = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, (JsonSerializer<Date>) (date, typeOfSrc, context) ->
                    new JsonPrimitive(date.getTime())
            ).create().toJson(row);
            return new Entity().setValue(json);
        }

        Set<Map.Entry<String, JsonElement>> tree = ((JsonObject)
                new GsonBuilder()
                        .registerTypeHierarchyAdapter(Date.class, (JsonSerializer<Date>) (date, typeOfSrc, context) ->
                                new JsonPrimitive(date.getTime())
                        ).create().toJsonTree(row)).entrySet();

        Map<String, Object> entity = new HashMap<>();
        Map<String, String> map = HashBiMap.create(meta.getFieldMap()).inverse();
        tree.forEach(e-> entity.put(map.get(e.getKey()), e.getValue()));
        return new Entity().setValue(new Gson().toJson(entity)).setEntityMeta(meta);
    }

    public static List<Entity> pojo2Entities(List rows, EntityMeta meta) {
        checkNotNull(rows);

        return ((List<Object>)rows).stream().map(obj -> pojo2Entity(obj, meta)).collect(Collectors.toList());
    }

    public static <T> T entity2POJO(Entity r, EntityMeta meta, Class clz) {
        try {
            if(clz == long.class) {
                Long l = new Gson().fromJson(r.getValue(), Long.class);
                return (T) l;
            } else if(clz == int.class) {
                Integer i = new Gson().fromJson(r.getValue(), Integer.class);
                return (T) i;
            }else if (clz == Map.class) {
                return (T) new Gson().fromJson(r.getValue(), Map.class);
            }  else if (clz == String.class) {
                return (T) new Gson().fromJson(r.getValue(), String.class);
            }else if(clz == Object.class) {
                String str = new Gson().fromJson(r.getValue(), String.class);
                Long l = Longs.tryParse(str);
                if(l != null) {
                    return (T) l;
                }
                Double d = Doubles.tryParse(str);
                if(d != null) {
                    return (T) d;
                }
                return (T) str;
            }

            T obj = (T) clz.newInstance();
            Map map = new Gson().fromJson(r.getValue(), Map.class);
            for (String col : meta.getColumnNames()) {
                Field f = clz.getDeclaredField(meta.getFieldMap().get(col));
                f.setAccessible(true);

                if(Number.class.isAssignableFrom(f.getType())) {
                    Number value = (Number) map.get(col);
                    if (value == null) {
                        continue;
                    }
                    String fieldName = meta.getFieldMap().get(col);
                    Class fieldClz = FieldUtils.getField(clz, fieldName, true).getType();

                    if (fieldClz == long.class || fieldClz == Long.class) {
                        value = value.longValue();
                    } else if (fieldClz == int.class || fieldClz == Integer.class) {
                        value = value.intValue();
                    } else if (fieldClz == float.class || fieldClz == Float.class) {
                        value =  value.floatValue();
                    } else if (fieldClz == double.class || fieldClz == Double.class) {
                        value = value.doubleValue();
                    }
                    FieldUtils.writeField(obj, fieldName, value, true);
                }else {
                    Object value = map.get(col);
                    if(java.util.Date.class.isAssignableFrom(f.getType()) && value != null){
                        Constructor constructor = f.getType().getDeclaredConstructor(long.class);
                        constructor.setAccessible(true);
                        value = (Date) constructor.newInstance(((Number)value).longValue());
                    }
                    FieldUtils.writeField(obj, meta.getFieldMap().get(col), value, true);
                }
            }
            return obj;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> entity2POJOs(List<Entity> rows, EntityMeta meta, Class clz) {
        checkNotNull(rows);
        checkNotNull(clz);

        return rows.stream().map(r -> (T) entity2POJO(r, meta, clz)).collect(Collectors.toList());
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
