package com.nelepovds.ndutils.rest;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.nelepovds.ndutils.CommonUtils;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;

public class BaseClass extends Model {

    @SerializedName("id")
    @Column(name = "serverId")
    public Long serverId;


    public static final int __OBJECT_STATE_CLIENT_SIDE = 0;
    public static final int __OBJECT_STATE_SENDING = 1;
    public static final int __OBJECT_STATE_SERVER_SIDE = 2;

    @Column(name = "__object_server_state")
    public Integer __object_server_state = __OBJECT_STATE_CLIENT_SIDE;

    public Boolean isServerObject() {
        return this.__object_server_state == __OBJECT_STATE_SERVER_SIDE;
    }

    public static Gson gsonAdapter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder
                .registerTypeHierarchyAdapter(BaseClass.class,new BaseClassAdapter())
                .setDateFormat(CommonUtils.DATE_FULL_FORMAT)
                .setPrettyPrinting()
                .create();
        return gson;
    }

    @Override
    public String toString() {
        String retString = "";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(this.getClass(), new BaseClassAdapter()).setPrettyPrinting().create();
        retString = gson.toJson(this);
        return retString;
    }

    public static class BaseClassAdapter<T> implements JsonSerializer<T> {

        @Override
        public JsonElement serialize(T baseClass, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            Field[] fields = baseClass.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(SerializedName.class)) {
                    SerializedName serializedName = fields[i].getAnnotation(SerializedName.class);
                    Object valueField = null;
                    try {
                        valueField = fields[i].get(baseClass);
                        if (valueField != null) {
                            if (fields[i].getType().isAssignableFrom(String.class)) {
                                jsonObject.addProperty(serializedName.value(), valueField.toString());
                            } else if (fields[i].getType().isAssignableFrom(Integer.class)) {
                                jsonObject.addProperty(serializedName.value(), (Integer) valueField);
                            } else if (fields[i].getType().isAssignableFrom(Double.class)) {
                                jsonObject.addProperty(serializedName.value(), (Double) valueField);
                            } else if (fields[i].getType().isAssignableFrom(Date.class)) {
                                jsonObject.addProperty(serializedName.value(), CommonUtils.formatDate((Date) valueField, CommonUtils.DATE_FULL_FORMAT));
                            } else if (fields[i].getType().isAssignableFrom(Long.class)) {
                                jsonObject.addProperty(serializedName.value(), (Long) valueField);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

            return jsonObject;
        }
    }

    public static <RT extends BaseClass> RT fromJson(String json, Class<RT> classObject) {
        RT retObject = null;
        retObject = new GsonBuilder().setDateFormat(CommonUtils.DATE_FULL_FORMAT).create().fromJson(json, classObject);
        return retObject;
    }

    public static <RT extends BaseClass> RT fromJsonTreeMap(Object json, Class<RT> classObject) {
        RT retObject = null;
        String jsonString = new GsonBuilder().setDateFormat(CommonUtils.DATE_FULL_FORMAT).create().toJson(json);
        retObject = BaseClass.fromJson(jsonString, classObject);
        return retObject;
    }

    /**
     * Используя кеширование данных
     *
     * @param json
     * @param classObject
     * @param cache
     * @param <RT>
     * @return
     */
    public static <RT extends BaseClass> RT fromJson(String json, Class<RT> classObject, Select cache) {
        RT retObject = fromJson(json, classObject);
        if (retObject != null && cache != null && retObject.serverId != null) {
            //TODO: Watch
            retObject.__object_server_state = __OBJECT_STATE_SERVER_SIDE;
            retObject = cacheObject(retObject, cache);
        }
        return retObject;
    }

    public static <RT extends BaseClass> RT fromJsonTreeMap(Object json, Class<RT> classObject, Select cache) {
        String jsonString = new GsonBuilder().setDateFormat(CommonUtils.DATE_FULL_FORMAT).create().toJson(json);
        return BaseClass.fromJson(jsonString, classObject, cache);
    }


    public static <RT extends BaseClass> RT cacheObject(RT checkObject, Select cache) {
        RT foundItem = cache.from(checkObject.getClass()).where("serverId = ?", checkObject.serverId).executeSingle();
        if (foundItem != null) {
            Field[] fields = checkObject.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {


                if (fields[i].isAnnotationPresent(SerializedName.class)) {
                    try {
                        if (BaseClass.class.isAssignableFrom(fields[i].getType())) {
                            try {
                                RT objectField = (RT) fields[i].get(checkObject);
                                if (objectField != null && objectField.serverId != null) {
                                    RT cacheObject = cacheObject(objectField, cache);
                                    fields[i].set(foundItem, cacheObject);
                                }

                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else {

                            fields[i].set(foundItem, fields[i].get(checkObject));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
            foundItem.save();
        } else {
            checkObject.save();
            foundItem = checkObject;
        }
        return foundItem;
    }

}
