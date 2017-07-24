package com.donutcn.memo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.PermissionCheck;
import com.donutcn.memo.utils.RecognizerResultParser;
import com.donutcn.memo.utils.SpfsUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_publish);
        WindowUtils.setToolBarButton(this, R.string.btn_publish_finish);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        mSelectedType = mContentTypes[0];
        initView();
        setUpRichTextEditor();
        PublishType type = (PublishType) getIntent().getSerializableExtra("type");
        if (type != null) {
            mSelectedType = type.toString();
            mPublishType.setText(getString(R.string.placeholder_publish_type, mSelectedType));
        } else {
            mSelectedType = SpfsUtils.readString(this, SpfsUtils.CACHE, "publishType", mSelectedType);
            mTitleStr = SpfsUtils.readString(this, SpfsUtils.CACHE, "publishTitle", "");
            mContentStr = SpfsUtils.readString(this, SpfsUtils.CACHE, "publishContent", "");
            if (!isContentEmpty()) {
                mPublishType.setText(getString(R.string.placeholder_publish_type, mSelectedType));
                mTitle.setText(mTitleStr);
                mContent.setHtml(mContentStr);
            }
        }

        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mIatDialog = new RecognizerDialog(this, mInitListener);
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(PublishActivity.this, "初始化失败，错误码：" + code,
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

        mPublishBtn.setOnClickListener(this);
        mPublishType.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_with_btn:
                // according to the selected type, pass the different parameters to activity.
                if (mSelectedType == mContentTypes[0] || mSelectedType == mContentTypes[1]
                        || mSelectedType == mContentTypes[5]) {
                    startActivity(new Intent(this, SocialShareActivity.class));
                } else if (mSelectedType == mContentTypes[2] || mSelectedType == mContentTypes[3]
                        || mSelectedType == mContentTypes[4] || mSelectedType == mContentTypes[6]
                        || mSelectedType == mContentTypes[7]) {
                    Intent intent = new Intent(this, CompletingPage.class);
                    intent.putExtra("type", PublishType.getType(mSelectedType));
                    startActivity(intent);
                }
                Toast.makeText(PublishActivity.this, "type:" + mSelectedType + "\n"
                        + "title:" + mTitleStr + "\n"
                        + "content:" + mContentStr, Toast.LENGTH_SHORT).show();
                break;
            case R.id.publish_spinner:
                new AlertDialog.Builder(this)
                        .setItems(mContentTypes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelectedType = mContentTypes[which];
                                mPublishType.setText(getString(R.string.placeholder_publish_type, mSelectedType));
                            }
                        }).show();
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
                toggleKeyboard();
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

    private boolean isContentEmpty(){
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
            Toast.makeText(PublishActivity.this, error.getPlainDescription(true),
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
                    Toast.makeText(PublishActivity.this, "当前音量" + i, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onBeginOfSpeech() {
            Toast.makeText(PublishActivity.this, "开始了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEndOfSpeech() {
            Toast.makeText(PublishActivity.this, "结束了", Toast.LENGTH_SHORT).show();
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
                mContent.insertImage(selectedPhotos.get(0), "twitter");
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
                            SpfsUtils.write(PublishActivity.this,
                                    SpfsUtils.CACHE, "publishType", mSelectedType);
                            SpfsUtils.write(PublishActivity.this,
                                    SpfsUtils.CACHE, "publishTitle", mTitleStr);
                            SpfsUtils.write(PublishActivity.this,
                                    SpfsUtils.CACHE, "publishContent", mContentStr);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_publish_neg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SpfsUtils.remove(PublishActivity.this,
                                    SpfsUtils.CACHE, "publishType");
                            SpfsUtils.remove(PublishActivity.this,
                                    SpfsUtils.CACHE, "publishTitle");
                            SpfsUtils.remove(PublishActivity.this,
                                    SpfsUtils.CACHE, "publishContent");
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

    private void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
