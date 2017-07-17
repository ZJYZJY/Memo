package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.WindowUtils;

public class CompletingPage extends AppCompatActivity {

    private EditText mVote1, mVote2, mVote3;
    private LinearLayout mVoteSelection;

    private PublishType mContentType;
    private int mViewIndex = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        mContentType = (PublishType) getIntent().getSerializableExtra("type");
        switch (mContentType) {
            case ACTIVITY:
                setContentView(R.layout.completing_page_activity);
                initActivityView();
                break;
            case VOTE:
                setContentView(R.layout.completing_page_vote);
                initVoteView();
                break;
            case RECRUIT:
                setContentView(R.layout.completing_page_recruit);
                initRecruitView();
                break;
            case RESERVE:
                setContentView(R.layout.completing_page_reserve);
                initReserveView();
                break;
            case SALE:
                setContentView(R.layout.completing_page_sale);
                initSaleView();
                break;
        }
    }

    public void initActivityView(){

    }

    public void initVoteView() {
        mVoteSelection = (LinearLayout) findViewById(R.id.completing_info_container);
        mVote1 = (EditText) findViewById(R.id.vote_1);
        mVote2 = (EditText) findViewById(R.id.vote_2);
        mVote3 = (EditText) findViewById(R.id.vote_3);

        final TextView vote_add = (TextView) findViewById(R.id.vote_add);
        vote_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.completing_page_vote_item, null);
                if (mViewIndex == 7) {
                    ((EditText) ((ViewGroup) view).getChildAt(0)).setHint(R.string.hint_completing_vote_four);
                    mVoteSelection.addView(view, mViewIndex);
                    mViewIndex++;
                } else if (mViewIndex == 8) {
                    ((EditText) ((ViewGroup) view).getChildAt(0)).setHint(R.string.hint_completing_vote_five);
                    mVoteSelection.addView(view, mViewIndex);
                    mViewIndex++;
                } else if (mViewIndex == 9) {
                    vote_add.setText(getString(R.string.hint_completing_vote_dec));
                } else {
                    mViewIndex = 7;
                    vote_add.setText(getString(R.string.hint_completing_vote_add));
                }
            }
        });
    }

    public void initRecruitView(){

    }

    public void initReserveView(){

    }

    public void initSaleView(){

    }

    public void onFinish(View view) {
        Intent intent = new Intent(this, SocialShareActivity.class);
        startActivity(intent);
    }

    public void onBack(View view) {
        finish();
    }
}
