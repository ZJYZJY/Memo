package com.donutcn.memo.utils;

import com.donutcn.memo.entity.SimpleResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.junit.Test;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/31.
 */
public class HttpUtilsTest {
    @Test
    public void testGson() throws Exception {
        try {
            SimpleResponse response = new SimpleResponse("{\n" +
                    "    \"code\": 200,\n" +
                    "    \"message\": \"登录成功\",\n" +
                    "    \"data\": {\n" +
                    "        \"user_id\": \"17\",\n" +
                    "        \"username\": \"18767549068\",\n" +
                    "        \"email\": \"\",\n" +
                    "        \"tel_number\": \"18767549068\",\n" +
                    "        \"name\": \"18767549068\",\n" +
                    "        \"sex\": null,\n" +
                    "        \"head_portrait\": \"user_icon.png\",\n" +
                    "        \"self_introduction\": null,\n" +
                    "        \"create_time\": \"2017-07-31 10:32:07\",\n" +
                    "        \"is_autonym\": \"0\",\n" +
                    "        \"cookie\": \"ef243b24c110987ab218708f4850f8e3\"\n" +
                    "    }\n" +
                    "}");
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String json = gson.toJson(response);
            System.out.println(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}