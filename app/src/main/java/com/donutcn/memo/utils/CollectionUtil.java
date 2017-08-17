package com.donutcn.memo.utils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/2.
 */

public class CollectionUtil {

    /**
     * remove the same object in a list.
     * @param list
     * @param <E> list object type
     * @return a list without duplicate item
     */
    public static <E> List<E> removeDuplicateWithOrder(List<E> list) {
        Set<E> set = new HashSet<>();
        List<E> newList = new ArrayList<>();
        for (E element : list) {
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        return list;
    }

    /**
     * covert {@link LinkedTreeMap} list to an object list.
     * @param data LinkedTreeMap list
     * @param clazz class of T
     * @param <T> list object type
     * @return list of T
     * @throws JSONException
     */
    public static <T> List<T> covertLinkedTreeMap(List<LinkedTreeMap> data, Class<T> clazz) throws JSONException {
        List<T> list = new ArrayList<>();
        Gson gson = new Gson();
        for(LinkedTreeMap map : data){
            // get key list
            List<String> keys = new ArrayList<>();
            for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();){
                keys.add((String) iterator.next());
            }
            // covert LinkedTreeMap to JSONObject
            JSONObject json = new JSONObject();
            for(String key : keys){
                json.put(key, map.get(key));
            }
            T obj = gson.fromJson(json.toString(), clazz);
            list.add(obj);
        }
        return list;
    }
}
