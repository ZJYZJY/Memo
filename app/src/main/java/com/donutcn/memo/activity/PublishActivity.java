package com.donutcn.memo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.donutcn.memo.R;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mPublishType;
    private EditText mTitle, mContent;
    private Button mPublishBtn;

    private LinearLayout mAddPic, mTypeSet, mTemplate, mSpeech;
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
    }

    public void initView(){
        mPublishBtn = (Button) findViewById(R.id.publish);
        mPublishType = (TextView) findViewById(R.id.publish_spinner);
        mTitle = (EditText) findViewById(R.id.et_publish_title);
        mContent = (EditText) findViewById(R.id.et_publish_content);

        mAddPic = (LinearLayout) findViewById(R.id.pub_add_pic);
        mTypeSet = (LinearLayout) findViewById(R.id.pub_type_setting);
        mTemplate = (LinearLayout) findViewById(R.id.pub_template);
        mSpeech = (LinearLayout) findViewById(R.id.pub_speech_input);
        mKeyboard = (ImageView) findViewById(R.id.pub_keyboard_toggle);

        mPublishBtn.setOnClickListener(this);
        mPublishType.setOnClickListener(this);

        mAddPic.setOnClickListener(this);
        mTypeSet.setOnClickListener(this);
        mTemplate.setOnClickListener(this);
        mSpeech.setOnClickListener(this);
        mKeyboard.setOnClickListener(this);
    }

    public void onBack(View view){
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.publish:
                Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.publish_spinner:
                new AlertDialog.Builder(PublishActivity.this)
                        .setItems(mContentTypes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelectedType = mContentTypes[which];
                                mPublishType.setText("发布类型：" + mSelectedType);
                            }
                        }).show();
                break;
            case R.id.pub_add_pic:
                Toast.makeText(this, "图片", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pub_type_setting:
                Toast.makeText(this, "排版", Toast.LENGTH_SHORT).show();
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
        }
    }

    private void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
