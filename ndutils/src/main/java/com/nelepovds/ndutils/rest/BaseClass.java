package com.nelepovds.ndutils.rest;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;


import java.lang.reflect.Field;

public class BaseClass extends Model{

    @SerializedName("id")
    @Column(name = "serverId")
    public Integer serverId;

    public static  <RT extends BaseClass> RT fromJson(String json, Class<RT> classObject){
        RT retObject=null;
        retObject= new Gson().fromJson(json,classObject);
        return retObject;
    }

    /**
     * Используя кеширование данных
     * @param json
     * @param classObject
     * @param cache
     * @param <RT>
     * @return
     */
    public static  <RT extends BaseClass> RT fromJson(String json, Class<RT> classObject, Select cache){
        RT retObject = fromJson(json,classObject);
        if (retObject!=null && cache != null){
            retObject = cacheObject(retObject, cache);
        }
        return retObject;
    }

    public static <RT extends BaseClass> RT cacheObject(RT checkObject, Select cache){
        RT foundItem = cache.from(checkObject.getClass()).where("serverId = ?", checkObject.serverId).executeSingle();
        if (foundItem !=null) {
            Field[] fields = checkObject.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(SerializedName.class)){
                    try {
                        fields[i].set(foundItem,fields[i].get(checkObject));
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
