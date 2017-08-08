package com.donutcn.memo.utils;

import android.content.Context;
import android.util.Log;

import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.entity.Contact;
import com.donutcn.memo.entity.ContentResponse;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.listener.UploadCallback;
import com.donutcn.memo.type.PublishType;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/27.
 */

public class HttpUtils {

    /** server address. */
    private static final String SERVER_HOST = "ascexz.320.io";
    private static final String PATH = "http://" + SERVER_HOST + "/GoodPage/API/";

    private static Retrofit instance;
    private static ClearableCookieJar cookieJar;
    private static UploadManager uploadManager;

    private static List<String> fileKeys;
    private static AtomicInteger uploadCount = new AtomicInteger(0);

    public static synchronized void create(Context context) {
        if (instance == null) {
            cookieJar = new PersistentCookieJar(
                    new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            instance = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(PATH)
                    .build();
        }
    }

    private static synchronized RRPageService create() {
        return instance.create(RRPageService.class);
    }

    public static void clearCookies() {
        cookieJar.clear();
    }

    private static UploadManager getUploadManager() {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }
        return uploadManager;
    }

    public interface RRPageService {

        /**
         * user login.
         *
         * @param user user info
         */
        @POST(APIPath.LOGIN)
        Call<SimpleResponse> login(@Body RequestBody user);

        /**
         * request for message Verification Code.
         *
         * @param phone phone number
         */
        @POST(APIPath.GET_AUTH_CODE)
        Call<SimpleResponse> getVerifiedCode(@Body RequestBody phone);

        /**
         * user register.
         *
         * @param phone user info
         */
        @POST(APIPath.REGISTER)
        Call<SimpleResponse> register(@Body RequestBody phone);

        /**
         * modify user password
         *
         * @param phone user info
         */
        @POST(APIPath.MODIFY_PASSWORD)
        Call<SimpleResponse> modifyPassword(@Body RequestBody phone);

        /**
         * user logout
         *
         * @param phone phone number
         */
        @Deprecated
        @POST(APIPath.LOGOUT)
        Call<SimpleResponse> logout(@Body RequestBody phone);

        /**
         * get the file upload token.
         */
        @POST(APIPath.GET_UPLOAD_TOKEN)
        Call<SimpleResponse> getUploadToken();

        /**
         * publish content.
         */
        @POST(APIPath.PUBLISH_CONTENT)
        Call<SimpleResponse> publishContent(@Body RequestBody content);

        /**
         * refresh recommend content.
         */
        @GET(APIPath.GET_RECOMMEND)
        Call<ArrayResponse<BriefContent>> getRecommendContent(@Path("action") String action,
                                                              @Path("id") String id);

        /**
         * refresh recommend content.
         */
        @POST(APIPath.SEARCH_CONTENT)
        Call<ArrayResponse<BriefContent>> searchContent(@Body RequestBody content);

        /**
         * get publish content by id.
         */
        @GET(APIPath.GET_CONTENT)
        Call<ContentResponse> getContentById(@Path("id") String id);

        /**
         * change the content access level.
         */
        @POST(APIPath.SET_CONTENT_PRIVATE)
        Call<SimpleResponse> setPrivate(@Body RequestBody content);

        /**
         * complete publish content info.
         */
        @POST(APIPath.COMPLETE_INFO)
        Call<SimpleResponse> completeInfo(@Body RequestBody content);

        /**
         * complete publish content info.
         */
        @GET(APIPath.GET_MY_CONTENT)
        Call<ArrayResponse<BriefContent>> getMyContentList(@Path("action") String action,
                                                           @Path("id") String id);

        /**
         * match user contact friends.
         */
        @POST(APIPath.MATCH_CONTACTS)
        Call<ArrayResponse<Contact>> matchContacts(@Body RequestBody contacts);

        /**
         * delete user publish content.
         */
        @GET(APIPath.DELETE_CONTENT)
        Call<SimpleResponse> deleteContent(@Path("id") String id);

        /**
         * modify user publish content.
         */
        @GET(APIPath.MODIFY_MY_CONTENT)
        Call<ContentResponse> modifyMyContent(@Path("id") String id);

        /**
         * cookie test.
         */
        @POST("article_api/ceshi")
        Call<SimpleResponse> test();
    }

    private class APIPath {
        private static final String LOGIN = "login_api";

        private static final String GET_AUTH_CODE = "login_api/authcode_api";

        private static final String REGISTER = "login_api/register";

        private static final String MODIFY_PASSWORD = "login_api/edituser_api";

        private static final String LOGOUT = "login_api/login_out_api";

        private static final String GET_UPLOAD_TOKEN = "private_api/upload_api";

        private static final String PUBLISH_CONTENT = "private_api/create_article_api";

        private static final String GET_RECOMMEND = "index_api/index/{action}/{id}";

        private static final String SEARCH_CONTENT = "index_api/search_api";

        private static final String GET_CONTENT = "index_api/see_article_api/{id}";

        private static final String SET_CONTENT_PRIVATE = "private_api/is_private_api";

        private static final String COMPLETE_INFO = "private_api/article_field_api";

        private static final String GET_MY_CONTENT = "private_api/index/{action}/{id}";

        private static final String MATCH_CONTACTS = "private_api/myfriend_api";

        private static final String DELETE_CONTENT = "private_api/delete_article_api/{id}";

        private static final String MODIFY_MY_CONTENT = "private_api/the_article_api/{id}";
    }

    public static Call<SimpleResponse> login(int loginType, Map<String, String> data) {
        JSONObject json = new JSONObject();
        try {
            if(loginType == UserStatus.PHONE_LOGIN){
                json.put("username", data.get("username"));
                json.put("password", data.get("password"));
            }else if(loginType == UserStatus.WECHAT_LOGIN){
                json.put("openid", data.get("openid"));
                json.put("name", data.get("name"));
                json.put("gender", data.get("gender"));
                json.put("iconurl", data.get("iconurl"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().login(request);
    }

    public static Call<SimpleResponse> getVerifiedCode(String phoneNumber, String action) {
        JSONObject json = new JSONObject();
        try {
            json.put("tel_number", phoneNumber);
            json.put("action", action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().getVerifiedCode(request);
    }

    public static Call<SimpleResponse> modifyUser(String phoneNumber, String authCode,
                                                  String password, int action) {
        JSONObject json = new JSONObject();
        try {
            json.put("tel_number", phoneNumber);
            json.put("authcode", authCode);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        if (action == 0)
            return create().register(request);
        else
            return create().modifyPassword(request);
    }

    @Deprecated
    public static Call<SimpleResponse> logout(String phoneNumber) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().logout(request);
    }

    public static Call<SimpleResponse> publishContent(String id, String title, String type, String content) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("type", type);
            json.put("content", content);
            if(id !=null) {
                json.put("article_id", id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().publishContent(request);
    }

    public static Call<ArrayResponse<BriefContent>> getRecommendContent(String action, String id) {
        return create().getRecommendContent(action, id);
    }

    public static Call<ArrayResponse<BriefContent>> searchContent(String key){
        JSONObject json = new JSONObject();
        try {
            json.put("keywords", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().searchContent(request);
    }

    public static Call<ContentResponse> getContentById(String id){
        return create().getContentById(id);
    }

    public static Call<SimpleResponse> setPrivate(String id, int isPrivate) {
        JSONObject json = new JSONObject();
        try {
            json.put("article_id", id);
            json.put("is_private", isPrivate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().setPrivate(request);
    }

    public static Call<SimpleResponse>
    completeInfo(String id, PublishType type, String field1, String field2, String field3,
                 boolean needApply, int extra1, int extra2, List<String> voteItems, boolean editMode) {
        JSONObject json = new JSONObject();
        try {
            json.put("article_id", id);
            json.put("type", type.toString());
            if(editMode && type != PublishType.VOTE){
                json.put("action", "edit");
            }
            if (needApply) {
                json.put("is_sign_up", 1);
                json.put("extra1", extra1);
                json.put("extra2", extra2);
            }
            switch (type) {
                case RESERVE:
                case SALE:
                    json.put("field3", field3);
                case ACTIVITY:
                case RECRUIT:
                    json.put("field1", field1);
                    json.put("field2", field2);
                    break;
                case VOTE:
                    JSONObject items = new JSONObject();
                    for (int i = 0; i < voteItems.size(); i++) {
                        items.put(String.valueOf(i), voteItems.get(i));
                    }
                    json.put("data", items);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
//        System.out.println(json.toString());
        return create().completeInfo(request);
    }

    public static Call<ArrayResponse<BriefContent>> getMyContentList(String action, String id){
        return create().getMyContentList(action, id);
    }

    public static Call<ArrayResponse<Contact>> matchContacts(List<String> signatureCode){
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            for(String code : signatureCode){
                array.put(code);
            }
            json.put("data", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
//        System.out.println(json.toString());
        return create().matchContacts(request);
    }

    public static Call<SimpleResponse> deleteContent(String id){
        return create().deleteContent(id);
    }

    public static Call<ContentResponse> modifyMyContent(String id){
        return create().modifyMyContent(id);
    }

    public static Call<SimpleResponse> test() {
        return create().test();
    }

    /**
     * upload images to qiniu server
     *
     * @param context  context
     * @param paths    image file path
     * @param listener {@link UploadCallback}
     */
    public static void upLoadImages(final Context context, final List<String> paths,
                                    final UploadCallback<String> listener) {
        final OnValidTokenListener onValidTokenListener = new OnValidTokenListener() {
            @Override
            public void onValidToken(String token) {
                doUploadFiles(paths, token, listener);
            }

            @Override
            public void onInvalidToken() {
                ToastUtil.show(context, "图片上传失败");
                Log.e("upload", "invalid token");
            }
        };
        create().getUploadToken().enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null) {
                    if(response.body().isOk()){
                        String token = response.body().getMessage();
                        onValidTokenListener.onValidToken(token);
                    }
                } else {
                    onValidTokenListener.onInvalidToken();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                onValidTokenListener.onInvalidToken();
                t.printStackTrace();
            }
        });

    }

    private static void doUploadFiles(final List<String> paths, String token,
                                      final UploadCallback<String> listener) {
        fileKeys = Collections.synchronizedList(new ArrayList<String>());
        final boolean[] fail = {false};
        for (String path : paths) {
            if(fail[0])
                return;
            getUploadManager().put(path, null, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()) {
                                try {
                                    // store the file key.
                                    fileKeys.add(response.getString("key"));
                                    Log.d("qiniu_upload", response.getString("key"));
                                    uploadCount.addAndGet(1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (uploadCount.get() == paths.size()) {
                                    listener.uploadAll(fileKeys);
                                    // reset uploadCount.
                                    uploadCount.getAndSet(0);
                                }
                            } else {
                                Log.e("qiniu_upload", info.error);
                                fail[0] = true;// multiple thread may cause problem
                                listener.uploadFail(info.error);
                            }
                        }

                    }, new UploadOptions(null, null, false, new UpProgressHandler() {
                        @Override
                        public void progress(String key, double percent) {

                        }
                    }, null));
        }
    }

    private interface OnValidTokenListener {

        /**
         * get the valid token or exist valid token.
         */
        void onValidToken(String token);

        /**
         * can't get the valid token or doesn't exist valid token.
         */
        void onInvalidToken();
    }
}
