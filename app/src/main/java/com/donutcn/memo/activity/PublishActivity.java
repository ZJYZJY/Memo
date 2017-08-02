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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.listener.OnUploadAllListener;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.PermissionCheck;
import com.donutcn.memo.utils.RecognizerResultParser;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.StringUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import me.shihao.library.XRadioGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mPublishType;
    private EditText mTitle;
    private RichEditor mContent;
    private Button mPublishBtn;
    private LinearLayout mAddPic, mTypeSet, mTemplate, mSpeech;
    private HorizontalScrollView mTools;
    private ImageView mKeyboard;

    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    // use HashMap to store the result.
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private ArrayList<String> selectedPhotos = new ArrayList<>();

    private final String[] mContentTypes = PublishType.toStringArray();
    private String mSelectedType;
    private String mTitleStr = "";
    private String mContentStr = "";
    private static final String HOST = "http://otu6v4c72.bkt.clouddn.com/";
    private Context mContext;
    private ProgressDialog mPublishDialog;

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
        PublishType type = (PublishType) getIntent().getSerializableExtra("type");
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
            }
        }

        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mIatDialog = new RecognizerDialog(this, mInitListener);

        mPublishDialog = new ProgressDialog(this);
        mPublishDialog.setMessage("正在上传中...");
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, "初始化失败，错误码：" + code,
                        Toast.LENGTH_SHORT).show();
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
        mTools = (HorizontalScrollView) findViewById(R.id.type_setting_tools);

        findViewById(R.id.publish_spinner_container).setOnClickListener(this);
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

    public void publishContent(List<String> keys){
        if(keys != null && keys.size() == selectedPhotos.size()){
            for(int i = 0; i < selectedPhotos.size(); i++){
                mContentStr = mContentStr.replace(selectedPhotos.get(i), HOST + keys.get(i));
            }
        }
        // Escape the " to \"
        mTitleStr = mTitleStr.replace("\"", "\\\"");
        mContentStr = mContentStr.replace("\"", "\\\"");
        HttpUtils.publishContent(mTitleStr, mSelectedType, mContentStr)
                .enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body().isOk()){
                    Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
//                    Log.e("id", response.body().getField("article_id"));
                    openSharePage(String.valueOf(response.body().getField("article_id")));
                }else {
                    Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                }
                mPublishDialog.cancel();
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mContext, "发布连接失败", Toast.LENGTH_SHORT).show();
                mPublishDialog.cancel();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_with_btn:
                if (TextUtils.isEmpty(mTitleStr)) {
                    Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(mContentStr)) {
                    Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPublishDialog.show();
                selectedPhotos = (ArrayList<String>) StringUtil.getImgSrcList(mContentStr);
                if(mSelectedType.equals(mContentTypes[0])
                        || mSelectedType.equals(mContentTypes[1])
                        || mSelectedType.equals(mContentTypes[5])){
                    if(selectedPhotos.size() == 0){
                        publishContent(null);
                    }else {
                        HttpUtils.upLoadImages(this, selectedPhotos, new OnUploadAllListener() {
                            @Override
                            public void uploadAll(List<String> keys) {
                                publishContent(keys);
                            }
                        });
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
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(this);
                break;
            case R.id.pub_type_setting:
                mTools.setVisibility(mTools.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE);
                break;
            case R.id.pub_template:
                Toast.makeText(this, "模版", Toast.LENGTH_SHORT).show();
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
                mContent.insertLink("http://www.baidu.com", null);
                showLinkDialog();
                break;
            case R.id.clear:
                mContent.removeFormat();
                break;
        }
    }

    private void openSharePage(String contentId) {
        Intent intent = new Intent(this, SocialShareActivity.class);
        intent.putExtra("contentId", contentId);
        startActivity(intent);
    }

    private void startCompletePage() {
        Intent intent = new Intent(this, CompletingPage.class);
        intent.putExtra("title", mTitleStr);
        intent.putExtra("type", PublishType.getType(mSelectedType));
        intent.putExtra("content", mContentStr);
        startActivity(intent);
    }

    private void showPopupMenu(View view) {
        View popWindowView = getLayoutInflater().inflate(R.layout.publish_type_popup, null);
        XRadioGroup radioGroup = (XRadioGroup) popWindowView.findViewById(R.id.publish_type_container);
        final ViewGroup v1 = (ViewGroup) radioGroup.getChildAt(0);
        final ViewGroup v2 = (ViewGroup) radioGroup.getChildAt(1);
        final View img = findViewById(R.id.ic_drop_arrow);
        img.setRotation(180f);
        PopupWindow popupWindow = new PopupWindow(popWindowView,
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
            }
        });
        popupWindow.showAsDropDown(view);
    }

    private void showLinkDialog() {
//        final int start = mContent.getSelectionStart();
//        final int end = mContent.getSelectionEnd();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//
//        View view = getLayoutInflater().inflate(R.layout.dialog_publish_link, null, false);
//        final EditText editText = (EditText) view.findViewById(R.id.publish_link_dialog);
//        builder.setView(view)
//                .setTitle(R.string.dialog_publish_title)
//                .setPositiveButton(R.string.btn_dialog_ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String link = editText.getText().toString().trim();
//                        if (TextUtils.isEmpty(link)) {
//                            return;
//                        }
//                        link = getResources().getText(R.string.hint_publish_link_dialog) + link;
//                        // When KnifeText lose focus, use this method
//                        mContent.link(link, start, end);
//                    }
//                })
//                .setNegativeButton(R.string.btn_dialog_cancel, null)
//                .create()
//                .show();
    }

    private boolean isContentEmpty() {
        return mTitleStr == "" && (mContentStr == "" || mContentStr == "<br>");
    }

    public void startSpeech() {
        // set up SpeechRecognizer parameter.
        setParam();
        // show the listening dialog.
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
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

    /**
     * RecognizerDialogListener with UI.
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
            if (isLast) {
                mContentTextChangeListener.onTextChange(mContent.getHtml());
            }
        }

        public void onError(SpeechError error) {
            Toast.makeText(mContext, error.getPlainDescription(true),
                    Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mContext, "当前音量" + i, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onBeginOfSpeech() {
            Toast.makeText(mContext, "开始了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEndOfSpeech() {
            Toast.makeText(mContext, "结束了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {

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
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
                mContent.insertImage(selectedPhotos.get(0), "image");
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
                            SpfsUtils.remove(mContext, SpfsUtils.CACHE, "publishType");
                            SpfsUtils.remove(mContext, SpfsUtils.CACHE, "publishTitle");
                            SpfsUtils.remove(mContext, SpfsUtils.CACHE, "publishContent");
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
        if (requestCode == PermissionCheck.PERMISSION_RECORD) {

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
