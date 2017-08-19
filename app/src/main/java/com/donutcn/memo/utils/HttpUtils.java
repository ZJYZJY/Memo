package com.donutcn.memo.utils;

import android.content.Context;

import com.donutcn.memo.constant.FieldConfig;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.entity.Contact;
import com.donutcn.memo.entity.ContentResponse;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.helper.RouterHelper;
import com.donutcn.memo.helper.RouterHelper.APIPath;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/27.
 */

public class HttpUtils {

    public static final String PATH = RouterHelper.getApiUri().toString();

    private static Retrofit instance;
    private static OkHttpClient okHttpClient;
    private static ClearableCookieJar cookieJar;
    private static UploadManager uploadManager;

    private static List<String> fileKeys;
    private static AtomicInteger uploadCount = new AtomicInteger(0);

    public static synchronized void create(Context context) {
        if (instance == null) {
            cookieJar = new PersistentCookieJar(
                    new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            okHttpClient = new OkHttpClient.Builder()
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

    private static List<Cookie> getCookies(){
        HttpUrl httpUrl = new HttpUrl.Builder().scheme(RouterHelper.scheme()).host(RouterHelper.host()).build();
        return okHttpClient.cookieJar().loadForRequest(httpUrl);
    }

    public static String cookieHeader() {
        List<Cookie> cookies = getCookies();
        StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }
        return cookieHeader.toString();
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
        @Headers(FieldConfig.REQUEST_HEADER)
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
         * complete publish content info.
         */
        @GET(APIPath.GET_MY_CONTENT)
        Call<ArrayResponse<BriefContent>> getMyContentList(@Path("action") String action,
                                                           @Path("timestamp") long timeStamp);

        /**
         * refresh content.
         */
        @GET(APIPath.GET_RECOMMEND)
        Call<ArrayResponse<BriefContent>> getRecommendContent(@Path("action") String action,
                                                              @Path("timestamp") long timeStamp);

        @GET(APIPath.GET_FOLLOWED_CONTENT)
        Call<ArrayResponse<BriefContent>> getFollowedContent(@Path("action") String action,
                                                             @Path("timestamp") long timeStamp);

        @GET(APIPath.GET_LATEST_CONTENT)
        Call<ArrayResponse<BriefContent>> getLatestContent(@Path("action") String action,
                                                           @Path("timestamp") long timeStamp);

        /**
         * refresh recommend content.
         */
        @POST(APIPath.SEARCH_CONTENT)
        Call<ArrayResponse<BriefContent>> searchContent(@Body RequestBody content);

        /**
         * get publish content by id.
         */
        @Deprecated
        @GET(APIPath.GET_CONTENT)
        Call<ContentResponse> getContentById(@Path("id") String id);

        /**
         * change the content access level.
         */
        @Headers(FieldConfig.REQUEST_HEADER)
        @POST(APIPath.SET_CONTENT_PRIVATE)
        Call<SimpleResponse> setPrivate(@Body RequestBody content);

        /**
         * complete publish content info.
         */
        @POST(APIPath.COMPLETE_INFO)
        Call<SimpleResponse> completeInfo(@Body RequestBody content);

        /**
         * match user contact friends.
         */
        @POST(APIPath.MATCH_CONTACTS)
        Call<ArrayResponse<Contact>> matchContacts(@Body RequestBody contacts);

        /**
         * delete user publish content.
         */
        @Headers(FieldConfig.REQUEST_HEADER)
        @GET(APIPath.DELETE_CONTENT)
        Call<SimpleResponse> deleteContent(@Path("id") String id);

        /**
         * modify user publish content.
         */
        @Headers(FieldConfig.REQUEST_HEADER)
        @GET(APIPath.MODIFY_MY_CONTENT)
        Call<ContentResponse> modifyMyContent(@Path("id") String id);

        @GET(APIPath.SYNC_USER_INFO)
        Call<SimpleResponse> syncUserInfo();

        @Headers(FieldConfig.REQUEST_HEADER)
        @POST(APIPath.MODIFY_USER_INFO)
        Call<SimpleResponse> modifyUserInfo(@Body RequestBody info);

        @Headers(FieldConfig.REQUEST_HEADER)
        @GET(APIPath.FOLLOW_USER)
        Call<SimpleResponse> follow(@Path("userId") String userId, @Path("action") int action);

        @GET(APIPath.GET_USER_INFO)
        Call<SimpleResponse> getUserById(@Path("userId") String userId);

        @GET(APIPath.VERIFY_CONTENT)
        Call<SimpleResponse> verifyContentById(@Path("id") String id);

        /**
         * cookie test.
         */
        @POST("article_api/ceshi")
        Call<SimpleResponse> test();
    }

    public static Call<SimpleResponse> login(int loginType, Map<String, String> data) {
        JSONObject json = new JSONObject();
        try {
            if(loginType == UserStatus.PHONE_LOGIN){
                json.put(FieldConfig.USER_NAME, data.get(FieldConfig.USER_NAME));
                json.put("password", data.get("password"));
            }else if(loginType == UserStatus.WECHAT_LOGIN){
                json.put(FieldConfig.USER_OPEN_ID, data.get(FieldConfig.USER_OPEN_ID));
                json.put(FieldConfig.USER_NICKNAME, data.get(FieldConfig.USER_NICKNAME));
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
            json.put(FieldConfig.USER_PHONE, phoneNumber);
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
            json.put(FieldConfig.USER_PHONE, phoneNumber);
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
            json.put(FieldConfig.USER_NAME, phoneNumber);
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
            json.put(FieldConfig.CONTENT_TITLE, title);
            json.put(FieldConfig.CONTENT_TYPE, type);
            json.put(FieldConfig.CONTENT, content);
            if(id !=null) {
                json.put(FieldConfig.CONTENT_ID, id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().publishContent(request);
    }

    public static Call<ArrayResponse<BriefContent>> getContentList(int type, String action, long timeStamp) {
        switch (type) {
            case 0:
                return create().getMyContentList(action, timeStamp);
            case 1:
                return create().getRecommendContent(action, timeStamp);
            case 2:
                return create().getLatestContent(action, timeStamp);
            case 3:
                return create().getFollowedContent(action, timeStamp);
            default:
                return null;
        }
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

    @Deprecated
    public static Call<ContentResponse> getContentById(String id){
        return create().getContentById(id);
    }

    public static Call<SimpleResponse> setPrivate(String id, int isPrivate) {
        JSONObject json = new JSONObject();
        try {
            json.put(FieldConfig.CONTENT_ID, id);
            json.put(FieldConfig.CONTENT_RIGHTS, isPrivate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().setPrivate(request);
    }

    public static Call<SimpleResponse>
    completeInfo(String id, PublishType type, String field1, String field2, String field3,
                 boolean needApply, int extra1, int extra2, List<String> voteItems,
                 boolean isSingleVote, boolean editMode) {
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
                    json.put("is_single", isSingleVote ? 1 : 0);
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

    public static Call<SimpleResponse> syncUserInfo(){
        return create().syncUserInfo();
    }

    public static Call<SimpleResponse> modifyUserInfo(Map<String, String> info){
        JSONObject json = new JSONObject();
        try {
            json.put(FieldConfig.USER_NICKNAME, info.get(FieldConfig.USER_NICKNAME));
            json.put(FieldConfig.USER_GENDER, info.get(FieldConfig.USER_GENDER));
            json.put(FieldConfig.USER_ICON_URL, info.get(FieldConfig.USER_ICON_URL));
            json.put(FieldConfig.USER_SIGNATURE, info.get(FieldConfig.USER_SIGNATURE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        return create().modifyUserInfo(request);
    }

    public static Call<SimpleResponse> follow(String userId, int action){
        return create().follow(userId, action);
    }

    public static Call<SimpleResponse> getUserById(String userId){
        return create().getUserById(userId);
    }

    public static Call<SimpleResponse> verifyContentById(String id){
        return create().verifyContentById(id);
    }

    public static Call<SimpleResponse> test() {
        return create().test();
    }

    /**
     * upload images to qiniu server
     *
     * @param context  context
     * @param files    image file
     * @param listener {@link UploadCallback}
     */
    public static void upLoadImages(final Context context, final List<File> files,
                                    final UploadCallback<String> listener) {
        final OnValidTokenListener onValidTokenListener = new OnValidTokenListener() {
            @Override
            public void onValidToken(String token) {
                doUploadFiles(files, token, listener);
            }

            @Override
            public void onInvalidToken() {
                ToastUtil.show(context, "图片上传失败");
                LogUtil.e("upload", "invalid token");
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

    private static void doUploadFiles(final List<File> files, String token,
                                      final UploadCallback<String> listener) {
        fileKeys = Collections.synchronizedList(new ArrayList<String>());
        final boolean[] fail = {false};
        for (File file : files) {
            if(fail[0])
                return;
            getUploadManager().put(file, null, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()) {
                                try {
                                    // store the file key.
                                    fileKeys.add(response.getString("key"));
                                    LogUtil.d("qiniu_upload", response.getString("key"));
                                    uploadCount.addAndGet(1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (uploadCount.get() == files.size()) {
                                    listener.uploadAll(fileKeys);
                                    // reset uploadCount.
                                    uploadCount.getAndSet(0);
                                }
                            } else {
                                LogUtil.e("qiniu_upload", info.error);
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
