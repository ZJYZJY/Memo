package com.donutcn.memo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import io.github.mthli.knife.KnifeText;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mPublishType;
    private EditText mTitle;
    private KnifeText mContent;
    private Button mPublishBtn;

    private LinearLayout mAddPic, mTypeSet, mTemplate, mSpeech;
    private HorizontalScrollView mTools;
    private ImageView mKeyboard;

    private final String[] mContentTypes = {"文章", "相册", "问答", "活动", "投票", "招聘", "预订"};
    private String mSelectedType = "文章";
    private String mTitleStr = "";
    private String mContentStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        initView();
        setUpRichTextEditor();
    }

    public void initView() {
        mPublishBtn = (Button) findViewById(R.id.publish);
        mPublishType = (TextView) findViewById(R.id.publish_spinner);
        mTitle = (EditText) findViewById(R.id.et_publish_title);
        mContent = (KnifeText) findViewById(R.id.et_publish_content);

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

    }

    public void onBack(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish:
                Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();
                Log.e("hdu", mContent.getText().toString());
                Log.e("hdu", mContent.toHtml());
                break;
            case R.id.publish_spinner:
                new AlertDialog.Builder(PublishActivity.this)
                        .setItems(mContentTypes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelectedType = mContentTypes[which];
                                String text = getResources().getText(R.string.placeholder_publish_type) + mSelectedType;
                                mPublishType.setText(text);
                            }
                        }).show();
                break;
            case R.id.pub_add_pic:
                Toast.makeText(this, "图片", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pub_type_setting:
                mTools.setVisibility(mTools.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.pub_template:
                Toast.makeText(this, "模版", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pub_speech_input:
                Toast.makeText(this, "语音输入", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pub_keyboard_toggle:
                toggleKeyboard();
                break;
            // type setting tools onClick listener.
            case R.id.bold:
                mContent.bold(!mContent.contains(KnifeText.FORMAT_BOLD));
                break;
            case R.id.italic:
                mContent.italic(!mContent.contains(KnifeText.FORMAT_ITALIC));
                break;
            case R.id.underline:
                mContent.underline(!mContent.contains(KnifeText.FORMAT_UNDERLINED));
                break;
            case R.id.strikethrough:
                mContent.strikethrough(!mContent.contains(KnifeText.FORMAT_STRIKETHROUGH));
                break;
            case R.id.bullet:
                mContent.bullet(!mContent.contains(KnifeText.FORMAT_BULLET));
                break;
            case R.id.quote:
                mContent.quote(!mContent.contains(KnifeText.FORMAT_QUOTE));
                break;
            case R.id.link:
                showLinkDialog();
                break;
            case R.id.clear:
                mContent.clearFormats();
                break;
        }
    }

    private void showLinkDialog() {
        final int start = mContent.getSelectionStart();
        final int end = mContent.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

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
                        // When KnifeText lose focus, use this method
                        mContent.link(link, start, end);
                    }
                })
                .setNegativeButton(R.string.btn_dialog_cancel, null)
                .create()
                .show();
    }

    private void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
