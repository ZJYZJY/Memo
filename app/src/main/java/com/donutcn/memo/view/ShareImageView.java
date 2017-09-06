package com.donutcn.memo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.donutcn.memo.R;
import com.donutcn.memo.editor.RichEditor;
import com.donutcn.memo.utils.DensityUtils;
import com.donutcn.widgetlib.NoTouchScrollView;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * com.donutcn.memo.view
 * Created by 73958 on 2017/9/4.
 */

public class ShareImageView extends RelativeLayout {

    private RichEditor mContent;
    private ImageView mQRCode;
    private NoTouchScrollView mScrollView;

    private Context mContext;
    private static final String SETUP_HTML = "file:///android_asset/editor.html";

    public ShareImageView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ShareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public ShareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void initView(){
        mContent = (RichEditor) findViewById(R.id.share_content);
        mQRCode = (ImageView) findViewById(R.id.share_qr_code);
        mScrollView = (NoTouchScrollView) findViewById(R.id.sv_share_container);

        mContent.loadUrl(SETUP_HTML);
    }

    public void setWebChromeClient(WebChromeClient webChromeClient){
        mContent.setWebChromeClient(webChromeClient);
    }

    public void setContent(String content){
        mContent.setHtml(content);
    }

    public void setQRCode(String url){
        mQRCode.setImageBitmap(EncodingUtils.createQRCode(url,
                DensityUtils.dp2px(mContext, 128),
                DensityUtils.dp2px(mContext, 128),
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
    }

    public Bitmap getBitmap() {
        int h = 0;
        Bitmap bitmap = null;

        for (int i = 0; i < mScrollView.getChildCount(); i++) {
            h += mScrollView.getChildAt(i).getHeight();
            mScrollView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }

        bitmap = Bitmap.createBitmap(mScrollView.getWidth(), h,
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        mScrollView.draw(canvas);
        return bitmap;
    }
}
