package com.donutcn.memo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/29.
 */

public class StringUtil {

    /**
     * get the image src from html text.
     * @param content html text
     * @return src list
     */
    public static List<String> getImgSrcList(String content) {
        List<String> list = new ArrayList<>();
        //start match <img /> tag.
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(content);
        boolean result_img = m_img.find();
        if (result_img) {
            while (result_img) {
                //get the content int <img /> tag.
                String str_img = m_img.group(2);
                //start match 'src' in <img /> tag.
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(str_img);
                if (m_src.find()) {
                    String str_src = m_src.group(3);
                    list.add(str_src);
                }
                //match the next <img /> tag int this content.
                result_img = m_img.find();
            }
        }
        return list;
    }
}
