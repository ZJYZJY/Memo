package com.donutcn.memo.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/2.
 */
public class CollectionUtilTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void removeDuplicateWithOrder() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("4");
        list.add("5");
        list.add("3");

        ArrayList<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        list1.add("5");
        assertEquals(list1, CollectionUtil.removeDuplicateWithOrder(list));
    }
}