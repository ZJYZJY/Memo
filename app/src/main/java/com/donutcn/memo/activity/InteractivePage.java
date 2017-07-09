package com.donutcn.memo.activity;

import android.support.annotation.LayoutRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.utils.DensityUtils;
import com.donutcn.memo.utils.WindowUtils;

public class InteractivePage extends AppCompatActivity implements View.OnClickListener {

    private BottomSheetBehavior behavior;
    private BottomSheetDialog dialog;
    private Button mInteractive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_page);

//        WindowUtils.setToolBarTitle(this, R.string.title_activity_publish);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary);

        mInteractive = (Button) findViewById(R.id.interactive);
        mInteractive.setText(getResources().getString(R.string.interactive_enroll));
        // open the bottom sheet dialog.
        mInteractive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBSDialog(R.layout.bottom_dialog_info);
            }
        });

        findViewById(R.id.interactive_bottom_comment).setOnClickListener(this);
    }

    public void onCloseDialog(View view){
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void onBack(View view){
        finish();
    }

    /**
     * create a bottom sheet dialog.
     */
    private void showBSDialog(@LayoutRes int layout) {
        dialog = new BottomSheetDialog(this);
        final View view = LayoutInflater.from(this).inflate(layout, null);
        dialog.setContentView(view);
        View parent = (View) view.getParent();
        behavior = BottomSheetBehavior.from(parent);
        behavior.setPeekHeight(DensityUtils.dp2px(this, 512));
        if(layout == R.layout.bottom_dialog_info){
            ((TextView)parent.findViewById(R.id.interactive_type)).setText(getResources().getString(R.string.interactive_enroll));
        }

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.interactive_bottom_comment:
                showBSDialog(R.layout.bottom_dialog_reply);
                break;
        }
    }
}
