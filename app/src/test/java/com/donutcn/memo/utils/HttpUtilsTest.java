package com.donutcn.memo.utils;

import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.type.PublishType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.ArrayList;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/31.
 */
public class HttpUtilsTest {

//    @Test
//    public void completeInfoTest(){
//        ArrayList<String> list = new ArrayList<>();
//        list.add("投票1");
//        list.add("投票2");
//        list.add("投票3");
//        list.add("投票4");
//        HttpUtils.completeInfo("25", PublishType.RECRUIT, "field1", "field2", "field3", false, 1, 1, list, false);
//    }

    @Test
    public void matchContactsTest(){
        ArrayList<String> list = new ArrayList<>();
        list.add("投票1");
        list.add("投票2");
        list.add("投票3");
        list.add("投票4");
        HttpUtils.matchContacts(list);
    }
}