package com.donutcn.memo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/2.
 */

public class CollectionUtil {

    public static <E> List<E> removeDuplicateWithOrder(List<E> list) {
        Set<E> set = new HashSet<>();
        List<E> newList = new ArrayList<>();
        for (E element : list) {
            if (set.add(element))
                newList.add(element);
        }
        return newList;
    }
}
