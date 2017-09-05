package com.donutcn.memo.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.donutcn.memo.R;
import com.donutcn.memo.editor.RichEditor;
import com.donutcn.memo.utils.DensityUtils;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * com.donutcn.memo.view
 * Created by 73958 on 2017/9/4.
 */

public class ShareImageView extends RelativeLayout {

    private RichEditor mContent;
    private ImageView mQRCode;

    private Context mContext;
    private String mContentStr;

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
        mContent.loadUrl("file:///android_asset/editor.html");
    }

    public void setContent(String content){
        this.mContentStr = content;
        mContent.setHtml(mContentStr);
    }

    public void setQRCode(String url){
        mQRCode.setImageBitmap(EncodingUtils.createQRCode(url,
                DensityUtils.dp2px(mContext, 128),
                DensityUtils.dp2px(mContext, 128),
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
    }

    /**
     * 创建分享的图片文件
     */
    public String createShareFile() {
        Bitmap bitmap = createBitmap();
        //将生成的Bitmap插入到手机的图片库当中，获取到图片路径
        String filePath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),     bitmap, null, null);
        //及时回收Bitmap对象，防止OOM
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        //转uri之前必须判空，防止保存图片失败
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return getRealPathFromURI(getContext(), Uri.parse(filePath));
    }

    /**
     * 创建分享Bitmap
     */
    public Bitmap createBitmap() {
        //自定义ViewGroup，一定要手动调用测量，布局的方法
        measure(getLayoutParams().width, getLayoutParams().height);
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        //如果图片对透明度无要求，可以设置为RGB_565
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
//        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    private static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null) {
                return "";
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMySize(WindowManager.LayoutParams.MATCH_PARENT, widthMeasureSpec);
        int height = getMySize(WindowManager.LayoutParams.WRAP_CONTENT, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
