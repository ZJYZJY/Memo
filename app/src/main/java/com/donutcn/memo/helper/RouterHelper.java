package com.donutcn.memo.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * com.donutcn.memo.helper
 * Created by 73958 on 2017/8/15.
 */

public class RouterHelper {

    private static final String SCHEME = "http";
    private static final String HOST = "ascexz.320.io";
    private static final String API_PATH = "/GoodPage/API/";
    private static final String PATH = "/GoodPage/";

    public static String scheme(){
        return SCHEME;
    }

    public static String host(){
        return HOST;
    }

    public static Uri getApiUri(){
        return Uri.parse(SCHEME + "://" + HOST + API_PATH);
    }

    public static Uri getBaseUri(){
        return Uri.parse(SCHEME + "://" + HOST + PATH);
    }

    public static boolean confirmIntent(Uri uri, String intent){
        return uri.getPathSegments().get(1).equals(intent);
    }

    public static void openPageWithUri(Context context, Uri uri, Class<?> cls){
        Intent intent = new Intent(context, cls);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public class APIPath {
        public static final String LOGIN = "login";
        public static final String GET_AUTH_CODE = "login/authcode";
        public static final String REGISTER = "login/register";
        public static final String MODIFY_PASSWORD = "login/edituser";
        public static final String LOGOUT = "login/logout";
        public static final String GET_UPLOAD_TOKEN = "authorised/upload";
        public static final String PUBLISH_CONTENT = "authorised/create_article";
        public static final String GET_MY_CONTENT = "authorised/index/{action}/{timestamp}";
        public static final String GET_RECOMMEND = "index/index/{action}/{timestamp}";
        public static final String GET_FOLLOWED_CONTENT = "authorised/my_follow/{action}/{timestamp}";
        public static final String GET_LATEST_CONTENT = "index/latest/{action}/{timestamp}";
        public static final String SEARCH_CONTENT = "index/search";
        public static final String GET_CONTENT = "index/see_article/{id}";
        public static final String SET_CONTENT_PRIVATE = "authorised/is_private";
        public static final String COMPLETE_INFO = "authorised/article_field";
        public static final String MATCH_CONTACTS = "authorised/myfriend";
        public static final String DELETE_CONTENT = "authorised/delete_article/{id}";
        public static final String MODIFY_MY_CONTENT = "authorised/the_article/{id}";
        public static final String SYNC_USER_INFO = "user/get_myinfo";
        public static final String MODIFY_USER_INFO = "user/edit_userinfo";
        public static final String FOLLOW_USER = "user/follow/{userId}/{action}";
        public static final String GET_USER_INFO = "index/app_myindex/{userId}";
        public static final String VERIFY_CONTENT = "index/verify_content/{id}";
    }
}
