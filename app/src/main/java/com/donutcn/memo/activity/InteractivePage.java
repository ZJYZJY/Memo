package com.donutcn.memo.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.utils.DensityUtils;
import com.donutcn.memo.utils.WindowUtils;

public class InteractivePage extends AppCompatActivity {

    private BottomSheetBehavior behavior;
    private BottomSheetDialog dialog;
    private Button mInteractive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_page);
//        WindowUtils.setToolBarTitle(this, R.string.title_activity_publish);
//        WindowUtils.setToolBarButton(this, R.string.btn_publish_finish);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary);

        mInteractive = (Button) findViewById(R.id.interactive);
        mInteractive.setText(getResources().getString(R.string.interactive_enroll));
        // open the bottom sheet dialog.
        mInteractive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBSDialog();
            }
        });
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
    private void showBSDialog() {
        dialog = new BottomSheetDialog(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.interactive_bottom_dialog, null);
        dialog.setContentView(view);
        View parent = (View) view.getParent();
        behavior = BottomSheetBehavior.from(parent);
        behavior.setPeekHeight(DensityUtils.dp2px(this, 512));
        ((TextView)parent.findViewById(R.id.interactive_type)).setText(getResources().getString(R.string.interactive_enroll));

        dialog.show();
    }
}
