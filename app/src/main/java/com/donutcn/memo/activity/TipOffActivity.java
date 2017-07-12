package com.donutcn.memo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.donutcn.memo.R;
import com.donutcn.memo.utils.WindowUtils;

public class TipOffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_off);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_tip_off);
    }

    public void onBack(View view){
        finish();
    }
}
