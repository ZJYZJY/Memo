package com.donutcn.memo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.ContentResponse;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.event.FinishCompressEvent;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.listener.UploadCallback;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.PermissionCheck;
import com.donutcn.memo.utils.RecognizerResultParser;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.StringUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;
import com.google.gson.internal.LinkedTreeMap;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import me.shihao.library.XRadioGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.zibin.luban.Luban;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mPublishType;
    private EditText mTitle;
    private RichEditor mContent;
    private Button mPublishBtn;
    private LinearLayout mAddPic, mTypeSet, mTemplate, mSpeech;
    private View mTools;
    private ImageView mKeyboard, mSpeechClose;
    private ProgressDialog mPublishDialog;
    private ProgressDialog mSpeechDialog;

    private SpeechRecognizer mIat;
    // use HashMap to store the result.
    private Map<String, String> mIatResults = new LinkedHashMap<>();
    private List<String> selectedPhotos = new ArrayList<>();
    private List<File> photoFiles = new ArrayList<>();

    private final String[] mContentTypes = PublishType.toStringArray();
    private LinkedTreeMap mExtraInfo;
    private String mSelectedType;
    private String mTitleStr = "";
    private String mContentStr = "";
    private String mContentId;
    private boolean mEditMode;
    private static final String HOST = "http://otu6v4c72.bkt.clouddn.com/";
    private static final String strategy = "?imageMogr2/auto-orient/thumbnail/!60p/format/jpg" +
            "/interlace/1/blur/1x0/quality/50|imageslim";
    private Context mContext;
    // gridView adapter.
    private String[] iconName;
    private int[] icon = { R.drawable.icon_type_article, R.drawable.icon_type_album,
            R.drawable.icon_type_activity, R.drawable.icon_type_vote, R.drawable.icon_type_recruit,
            R.drawable.icon_type_qa, R.drawable.icon_type_reserve, R.drawable.icon_type_sale};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        WindowUtils.setToolBarButton(this, R.string.btn_publish_finish);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        mSelectedType = mContentTypes[0];
        initView();
        setUpRichTextEditor();
        mContext = this;
        onNewIntent(getIntent());

        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);

        mPublishDialog = new ProgressDialog(this);
        mPublishDialog.setMessage("正在上传中...");

        mSpeechDialog = new ProgressDialog(this);

//        getWindow().getDecorView().postDelayed(showGreeting, 200);
    }

    private Runnable showGreeting = new Runnable() {
        @Override
        public void run() {
            if(hasWindowFocus()){
                showGreetingMenu();
                getWindow().getDecorView().removeCallbacks(this);
            } else {
                getWindow().getDecorView().postDelayed(this, 200);
            }
        }
    };

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                ToastUtil.show(mContext, "初始化失败，错误码：" + code);
            }
        }
    };

    public void initView() {
        mPublishBtn = (Button) findViewById(R.id.toolbar_with_btn);
        mPublishType = (TextView) findViewById(R.id.publish_spinner);
        mTitle = (EditText) findViewById(R.id.et_publish_title);
        mContent = (RichEditor) findViewById(R.id.et_publish_content);

        mContent.setOnTextChangeListener(mContentTextChangeListener);
        mTitle.addTextChangedListener(mTextWatcher);

        mAddPic = (LinearLayout) findViewById(R.id.pub_add_pic);
        mTypeSet = (LinearLayout) findViewById(R.id.pub_type_setting);
        mTemplate = (LinearLayout) findViewById(R.id.pub_template);
        mSpeech = (LinearLayout) findViewById(R.id.pub_speech_input);
        mKeyboard = (ImageView) findViewById(R.id.pub_keyboard_toggle);
        mSpeechClose = (ImageView) findViewById(R.id.pub_speech_close);
        mTools = findViewById(R.id.type_setting_tools);

        View view = findViewById(R.id.publish_spinner_container);
        view.setOnClickListener(this);
        mPublishBtn.setOnClickListener(this);

        mAddPic.setOnClickListener(this);
        mTypeSet.setOnClickListener(this);
        mTemplate.setOnClickListener(this);
        mSpeech.setOnClickListener(this);
        mKeyboard.setOnClickListener(this);

        // type setting tools.
        findViewById(R.id.bold).setOnClickListener(this);
        findViewById(R.id.italic).setOnClickListener(this);
        findViewById(R.id.underline).setOnClickListener(this);
        findViewById(R.id.strikethrough).setOnClickListener(this);
        findViewById(R.id.bullet).setOnClickListener(this);
        findViewById(R.id.quote).setOnClickListener(this);
        findViewById(R.id.link).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
    }

    public void setUpRichTextEditor() {
        mContent.setPlaceholder(getString(R.string.hint_publish_content));
        mContent.setPadding(8, 8, 8, 8);
        mContent.setEditorFontSize(16);
        mContent.setEditorFontColor(getResources().getColor(R.color.textPrimaryDark));
    }

    public void pullContentInfo(String id) {
        HttpUtils.modifyMyContent(id).enqueue(new Callback<ContentResponse>() {
            @Override
            public void onResponse(Call<ContentResponse> call, Response<ContentResponse> response) {
                if (response.body() != null) {
                    if(response.body().isOk()){
                        mSelectedType = response.body().getType().toString();
                        mPublishType.setText(mSelectedType);
                        mTitleStr = response.body().getTitle();
                        mTitle.setText(mTitleStr);
                        mContentStr = response.body().getContentStr();
                        // unEscape the \" to "
                        mTitleStr = mTitleStr.replace("\\\"", "\"");
                        mContentStr = mContentStr.replace("\\\"", "\"");
                        mContent.setHtml(mContentStr);
                        if (!mSelectedType.equals(mContentTypes[0])
                                && !mSelectedType.equals(mContentTypes[1])
                                && !mSelectedType.equals(mContentTypes[5])) {
                            mExtraInfo = response.body().getExtraInfo();
                        }
                    }
                } else {
                    ToastUtil.show(mContext, "获取信息失败");
                }
            }

            @Override
            public void onFailure(Call<ContentResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(mContext, "拉取信息连接失败");
            }
        });
    }

    public void publishContent(List<String> keys){
        if(keys == null && selectedPhotos.size() > 0){
            mPublishDialog.cancel();
            ToastUtil.show(this, "找不到图片，请重新选择");
            return;
        }
        if(keys != null && keys.size() == selectedPhotos.size()){
            for(int i = 0; i < selectedPhotos.size(); i++){
                mContentStr = mContentStr.replace(selectedPhotos.get(i),
                HOST + keys.get(i) + strategy);
            }
        }
        // Escape the " to \"
//        mTitleStr = mTitleStr.replace("\"", "\\\"");
//        mContentStr = mContentStr.replace("\"", "\\\"");
        HttpUtils.publishContent(mContentId, mTitleStr, mSelectedType, mContentStr)
                .enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        ToastUtil.show(mContext, "发布成功");
                        LogUtil.d("publish", response.body().toString());
                        openSharePage(String.valueOf(response.body().getField("article_id")),
                                (String) response.body().getField("url"),
                                (String) response.body().getField("title"),
                                (String) response.body().getField("content"),
                                (String) response.body().getField("picurl"),
                                Integer.valueOf((String) response.body().getField("is_private")));
                    } else if(response.body().unAuthorized()){
                        LoginHelper.ifRequestLogin(mContext, "请先登录");
                    } else {
                        ToastUtil.show(mContext, "发布失败，服务器未知错误");
                    }
                }else {
                    ToastUtil.show(mContext, "发布失败");
                }
                mPublishDialog.cancel();
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(mContext, "发布连接失败");
                mPublishDialog.cancel();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_with_btn:
                if (TextUtils.isEmpty(mTitleStr)) {
                    ToastUtil.show(this, "标题不能为空");
                    return;
                } else if (TextUtils.isEmpty(mContentStr)) {
                    ToastUtil.show(this, "内容不能为空");
                    return;
                }
                WindowUtils.toggleKeyboard(this, v, false);
                mPublishDialog.show();
                // if in edit mode, same image will not be reuploaded.
                selectedPhotos = StringUtil.getImgSrcList(mContentStr);
                if(mSelectedType.equals(mContentTypes[0])
                        || mSelectedType.equals(mContentTypes[1])
                        || mSelectedType.equals(mContentTypes[5])){
                    if(selectedPhotos.size() == 0){
                        publishContent(null);
                    }else {
                        final int count = selectedPhotos.size();
                        // compress the image files.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i < selectedPhotos.size(); i++){
                                    try {
                                        File file = Luban.with(PublishActivity.this)
                                                .load(new File(selectedPhotos.get(i)))
                                                .get();
                                        if(file != null){
                                            photoFiles.add(file);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(count == photoFiles.size()){
                                    EventBus.getDefault().post(new FinishCompressEvent(true));
                                } else {
                                    EventBus.getDefault().post(new FinishCompressEvent(false));
                                }
                            }
                        }).start();
                    }
                }else {
                    startCompletePage();
                    mPublishDialog.cancel();
                }
                break;
            case R.id.publish_spinner_container:
                showPopupMenu(v);
                break;
            case R.id.pub_add_pic:
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setPreviewEnabled(true)
                        .start(this);
                break;
            case R.id.pub_type_setting:
                mTools.setVisibility(mTools.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE);
                break;
            case R.id.pub_template:
                break;
            case R.id.pub_speech_input:
                PermissionCheck permissionCheck = new PermissionCheck(this);
                if (permissionCheck.checkRecordPermission()) {
                    startSpeech();
                }
                break;
            case R.id.pub_keyboard_toggle:
                WindowUtils.toggleKeyboard(this);
                break;
            // type setting tools onClick listener.
            case R.id.bold:
                mContent.setBold();
                break;
            case R.id.italic:
                mContent.setItalic();
                break;
            case R.id.underline:
                mContent.setUnderline();
                break;
            case R.id.strikethrough:
                mContent.setStrikeThrough();
                break;
            case R.id.bullet:
                mContent.setBullets();
                break;
            case R.id.quote:
                mContent.setBlockquote();
                break;
            case R.id.link:
                showLinkDialog();
                break;
            case R.id.clear:
                mContent.removeFormat();
                break;
        }
    }

    @Subscribe
    public void onFinishCompressEvent(FinishCompressEvent event){
        if(event.isSuccess()){
            HttpUtils.upLoadImages(PublishActivity.this, photoFiles, new UploadCallback<String>() {
                @Override
                public void uploadProgress(int progress, int total) {
                }
                @Override
                public void uploadAll(List<String> keys) {
                    publishContent(keys);
                }

                @Override
                public void uploadFail(String error) {
                    mPublishDialog.cancel();
                    ToastUtil.show(mContext, "找不到图片，请重新选择1");
                }
            });
        } else {
            ToastUtil.show(this, "图片上传失败, 压缩错误");
        }
    }

    private void openSharePage(String contentId, String contentUrl, String title,
                               String content, String picUrl, int isPrivate) {
        Intent intent = new Intent(this, SocialShareActivity.class);
        intent.putExtra("contentId", contentId);
        intent.putExtra("contentUrl", contentUrl);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("picUrl", picUrl);
        intent.putExtra("isPrivate", isPrivate);
        startActivity(intent);
    }

    private void startCompletePage() {
        Intent intent = new Intent(this, CompletingPage.class);
        if(mContentId != null){
            intent.putExtra("extraInfo", mExtraInfo);
            intent.putExtra("contentId", mContentId);
        }
        intent.putExtra("title", mTitleStr);
        intent.putExtra("type", PublishType.getType(mSelectedType));
        intent.putExtra("content", mContentStr);
        startActivity(intent);
    }

    private void showPopupMenu(View view) {
        View popWindowView = getLayoutInflater().inflate(R.layout.popup_publish_type, null);
        XRadioGroup radioGroup = (XRadioGroup) popWindowView.findViewById(R.id.publish_type_container);
        final ViewGroup v1 = (ViewGroup) radioGroup.getChildAt(0);
        final ViewGroup v2 = (ViewGroup) radioGroup.getChildAt(1);
        final View img = findViewById(R.id.ic_drop_arrow);
        img.setRotation(180f);
        final PopupWindow popupWindow = new PopupWindow(popWindowView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // select publish type.
        radioGroup.setOnCheckedChangeListener(new XRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(XRadioGroup xRadioGroup, @IdRes int i) {
                for (int j = 0; j < v1.getChildCount(); j++) {
                    ((RadioButton) v1.getChildAt(j))
                            .setTextColor(getResources().getColor(R.color.textPrimaryDark));
                }
                for (int j = 0; j < v2.getChildCount(); j++) {
                    ((RadioButton) v2.getChildAt(j))
                            .setTextColor(getResources().getColor(R.color.textPrimaryDark));
                }
                RadioButton btn = (RadioButton) xRadioGroup.findViewById(i);
                mSelectedType = btn.getText().toString();
                mPublishType.setText(mSelectedType);
                btn.setTextColor(getResources().getColor(R.color.white));
                popupWindow.dismiss();
            }
        });
        int position = PublishType.getType(mSelectedType).ordinal();
        if (position <= 3) {
            ((RadioButton) v1.getChildAt(position)).setChecked(true);
        } else {
            ((RadioButton) v2.getChildAt(position - 4)).setChecked(true);
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                img.setRotation(0f);
                if(mSelectedType.equals(mContentTypes[1])){
                    mAddPic.performClick();
                }
            }
        });
        popupWindow.showAsDropDown(view);
    }

    private void showGreetingMenu(){
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final View popWindowView = getLayoutInflater().inflate(R.layout.popup_publish_greeting, null);
        final PopupWindow popupWindow = new PopupWindow(popWindowView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.AnimPopup);
        popupWindow.setClippingEnabled(false);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(mSelectedType.equals(mContentTypes[1])){
                    mAddPic.performClick();
                } else {
                    mTitle.requestFocus();
                    WindowUtils.toggleKeyboard(mContext, rootView, true);
                }
            }
        });
        // header
        TextView name = (TextView) popWindowView.findViewById(R.id.greeting_name);
        GridView gridView = (GridView) popWindowView.findViewById(R.id.greeting_grid);
        name.setText(getString(R.string.placeholder_publish_greeting_name,
                UserStatus.getCurrentUser().getName(), StringUtil.getMoment()));
        // body
        String[] from ={"image", "text"};
        int[] to = {R.id.image, R.id.text};
        iconName = getResources().getStringArray(R.array.publish_content);
        SimpleAdapter adapter = new SimpleAdapter(mContext, getData(), R.layout.item_publish_greeting, from, to);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedType = mContentTypes[position];
                mPublishType.setText(mSelectedType);
                popupWindow.dismiss();
            }
        });
        // close popup window
        popWindowView.findViewById(R.id.greeting_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popWindowView.findViewById(R.id.greeting_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popWindowView.findViewById(R.id.greeting_body).setOnClickListener(null);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            list.add(map);
        }
        return list;
    }

    private void showLinkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.dialog_publish_link, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.publish_link_dialog);
        builder.setView(view)
                .setTitle(R.string.dialog_publish_title)
                .setPositiveButton(R.string.btn_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String link = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(link)) {
                            return;
                        }
                        link = getResources().getText(R.string.hint_publish_link_dialog) + link;
                        mContent.insertLink(link, null);
                    }
                })
                .setNegativeButton(R.string.btn_dialog_cancel, null)
                .create()
                .show();
    }

    private boolean isContentEmpty() {
        return mTitleStr.equals("") && (mContentStr.equals("") || mContentStr.equals("<br>"));
    }

    public void startSpeech() {
        // set up SpeechRecognizer parameter.
        setParam();
        mIat.startListening(mRecognizerListener);
        // show the listening dialog.
//        mIatDialog.setListener(mRecognizerDialogListener);
//        mIatDialog.show();
    }

    private RichEditor.OnTextChangeListener mContentTextChangeListener = new RichEditor.OnTextChangeListener() {
        @Override
        public void onTextChange(String text) {
            mContentStr = text;
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mTitleStr = s.toString();
        }
    };

    private void printResult(RecognizerResult results) {
        String text = RecognizerResultParser.parseIatResult(results.getResultString());
        String sn = null;
        // read the 'sn' part of the json string.
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuilder resultBuilder = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuilder.append(mIatResults.get(key));
        }

        if (mContent.isFocused()) {
            StringBuilder builder = new StringBuilder(mContentStr);
            builder.append(resultBuilder.toString());
            mContent.setHtml(builder.toString());
        }
//        else if(mTitle.isFocused()){
//            String str = mTitleStr + resultBuilder.toString();
//            mTitle.setText(str);
//        }
    }

    /**
     * RecognizerListener without UI.
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(final int i, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("当前音量" + i);
                }
            });
        }

        @Override
        public void onBeginOfSpeech() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSpeechDialog.setMessage("正在聆听...");
                    mSpeechDialog.show();
                }
            });
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            mSpeechDialog.setMessage("正在识别...");
            printResult(recognizerResult);
            if(isLast){
                mContentTextChangeListener.onTextChange(mContent.getHtml());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSpeechDialog.dismiss();
                    }
                });
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            LogUtil.e(speechError.getPlainDescription(true));
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void setParam() {
        // clear parameter.
        mIat.setParameter(SpeechConstant.PARAMS, null);

        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        // set how long the user does not speak as a timeout.
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // set how long does the user stop talking and say that they are no longer entered.
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // set Punctuation, "0" for false, "1" for true.
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
    }


    /**
     * select photo callback.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                for(String s : photos){
                    LogUtil.d("select_photo", s);
                }
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
                for(int i = 0; i < selectedPhotos.size(); i++){
                    mContent.insertImage(selectedPhotos.get(i), "image");
                }
            }
        }
    }

    /**
     * store the draft of publish content before exit this activity.
     */
    private void storeDraft() {
        if (!isContentEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.dialog_publish_exit_msg))
                    .setPositiveButton(getString(R.string.dialog_publish_pos), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SpfsUtils.write(mContext, SpfsUtils.CACHE, "publishType", mSelectedType);
                            SpfsUtils.write(mContext, SpfsUtils.CACHE, "publishTitle", mTitleStr);
                            SpfsUtils.write(mContext, SpfsUtils.CACHE, "publishContent", mContentStr);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_publish_neg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SpfsUtils.clear(mContext, SpfsUtils.CACHE);
                            finish();
                        }
                    })
                    .setCancelable(true)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCheck.PERMISSION_RECORD_AUDIO) {

        }
    }

    @Override
    public void onBackPressed() {
        storeDraft();
    }

    public void onBack(View view) {
        storeDraft();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(LoginHelper.ifRequestLogin(this, "请先登录")){
            return;
        }
        String action = intent.getAction();
        if(action != null && action.equals(Intent.ACTION_VIEW)){
            // start from web page.
            String data = intent.getDataString();
            int index = Integer.valueOf(data.substring(data.lastIndexOf("/") + 1));
            mSelectedType = PublishType.values()[index].toString();
            mPublishType.setText(mSelectedType);
        } else {
            mEditMode = intent.getBooleanExtra("editMode", false);
            if(mEditMode){
                mContentId = intent.getStringExtra("contentId");
                pullContentInfo(mContentId);
            } else {
                PublishType type = (PublishType) intent.getSerializableExtra("type");
                if (type != null) {
                    mSelectedType = type.toString();
                    mPublishType.setText(mSelectedType);
                } else {
                    mSelectedType = SpfsUtils.readString(this, SpfsUtils.CACHE, "publishType", mSelectedType);
                    mTitleStr = SpfsUtils.readString(this, SpfsUtils.CACHE, "publishTitle", "");
                    mContentStr = SpfsUtils.readString(this, SpfsUtils.CACHE, "publishContent", "");
                    if (!isContentEmpty()) {
                        mPublishType.setText(mSelectedType);
                        mTitle.setText(mTitleStr);
                        mContent.setHtml(mContentStr);
                    } else {
                        getWindow().getDecorView().postDelayed(showGreeting, 200);
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContent.clearCache(true);
        mContent.clearHistory();
        mContent.destroy();

        if (null != mIat) {
            // release connection when exit.
            mIat.cancel();
            mIat.destroy();
        }
    }
}
