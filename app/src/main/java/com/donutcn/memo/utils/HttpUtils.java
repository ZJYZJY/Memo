package com.donutcn.memo.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.listener.OnUploadAllListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/27.
 */

public class HttpUtils {

    /**
     * server address.
     */
    private static final String SERVER_HOST = "ascexz.320.io/GoodPage/API";
    private static final String PATH = "http://" + SERVER_HOST + "/";

    /**
     * Successful request.
     */
    public static final int SUCCESS = 200;

    /**
     * Bad Request.
     */
    public static final int FAIL = 400;
    /**
     * Unauthorized request.
     */
    public static final int UNAUTHORIZED = 401;

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

    public static void clearCookies(String phoneNumber) {
        cookieJar.clear();
//        logout(phoneNumber).enqueue(null);
    }

    private static UploadManager getUploadManager() {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }
        return uploadManager;
    }

    /**
     * get the response state code.
     *
     * @param str response string
     */
    public static int stateCode(String str) {
        try {
            JSONObject json = new JSONObject(str);
            return json.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return FAIL;
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
        @POST(APIPath.UPLOAD_CONTENT)
        Call<SimpleResponse> uploadContent(@Body RequestBody content);

        /**
         * refresh recommend content.
         */
        @POST(APIPath.REFRESH_RECOMMEND)
        Call<ArrayResponse> getRecommendContent();


        /**
         * cookie test.
         */
        @POST("article_api/ceshi")
        Call<ResponseBody> test();
    }

    private class APIPath {
        private static final String LOGIN = "login_api";

        private static final String GET_AUTH_CODE = "login_api/authcode_api";

        private static final String REGISTER = "login_api/register";

        private static final String MODIFY_PASSWORD = "login_api/edituser_api";

        private static final String LOGOUT = "login_api/login_out_api";

        private static final String GET_UPLOAD_TOKEN = "upload/upload.php";

        private static final String UPLOAD_CONTENT = "private_api/create_article_api";

        private static final String REFRESH_RECOMMEND = "index_api/index/10";
    }

    public static Call<SimpleResponse> login(String username, String password) {
        String str = "{\"username\":\"" + username + "\"," +
                "\"password\":\"" + password + "\"}";
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), str);
        return create().login(request);
    }

    public static Call<SimpleResponse> getVerifiedCode(String phoneNumber, String action) {
        String str = "{\"tel_number\":\"" + phoneNumber + "\"," +
                "\"action\":\"" + action + "\"}";
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), str);
        return create().getVerifiedCode(request);
    }

    public static Call<SimpleResponse> modifyUser(String phoneNumber, String authCode,
                                                  String password, int action) {
        String str = "{\"tel_number\":\"" + phoneNumber + "\"," +
                "\"authcode\":\"" + authCode + "\"," +
                "\"password\":\"" + password + "\"}";
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), str);
        if (action == 0)
            return create().register(request);
        else
            return create().modifyPassword(request);
    }

    public static Call<SimpleResponse> logout(String phoneNumber) {
        String str = "{\"username\":\"" + phoneNumber + "\"}";
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), str);
        return create().logout(request);
    }

    public static Call<SimpleResponse> uploadContent(String title, String type, String content) {
        String str = "{\"title\":\"" + title + "\"," +
                "\"type\":\"" + type + "\"," +
                "\"content\":\"" + content + "\"}";
        RequestBody request = RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), str);
        return create().uploadContent(request);
    }

    public static Call<ArrayResponse> getRecommendContent() {
        return create().getRecommendContent();
    }

    public static Call<ResponseBody> test() {
        return create().test();
    }

    /**
     * upload images to qiniu server
     *
     * @param context  context
     * @param paths    image file path
     * @param listener {@link OnUploadAllListener}
     */
    public static void upLoadImages(final Context context, final List<String> paths,
                                    final OnUploadAllListener listener) {
        final OnValidTokenListener onValidTokenListener = new OnValidTokenListener() {
            @Override
            public void onValidToken(String token) {
                doUploadFiles(paths, token, listener);
            }

            @Override
            public void onInvalidToken() {
                Toast.makeText(context, "图片上传失败", Toast.LENGTH_SHORT).show();
                Log.e("upload", "invalid token");
            }
        };
        create().getUploadToken().enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body().isOk()) {
                    String token = response.body().getMessage();
                    onValidTokenListener.onValidToken(token);
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
                                      final OnUploadAllListener listener) {
        fileKeys = Collections.synchronizedList(new ArrayList<String>());
        for (String path : paths) {
            getUploadManager().put(path, null, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()) {
                                try {
                                    // store the file key.
                                    fileKeys.add(response.getString("key"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                uploadCount.addAndGet(1);
                                if (uploadCount.get() == paths.size()) {
                                    listener.uploadAll(fileKeys);
                                    // reset uploadCount.
                                    uploadCount.getAndSet(0);
                                }
                            } else {
                                Log.e("qiniu_upload", info.error);
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
