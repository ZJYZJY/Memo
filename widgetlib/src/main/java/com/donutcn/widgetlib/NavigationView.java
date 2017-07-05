package com.donutcn.widgetlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * com.donutcn.widgetlib
 * Created by 73958 on 2017/7/4.
 */

public class NavigationView extends LinearLayout {

    private int image_width;
    private int image_height;
    private float text_size;
    private Context context;
    /**
     * 选中图片数组
     */
    private int[] selectedImage;
    /**
     * 未选中图片数组
     */
    private int[] unSelectedImage;

    private ArrayList<TextView> textViews = new ArrayList<TextView>();

    private ArrayList<ImageView> imageViews = new ArrayList<ImageView>();

    public OnItemClickListener onItemClickListener;

    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationView);
        image_height = typedArray.getInteger(R.styleable.NavigationView_image_height, 20);
        image_width = typedArray.getInteger(R.styleable.NavigationView_image_width, 20);
        text_size = typedArray.getDimension(R.styleable.NavigationView_text_size, 12);
        typedArray.recycle();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 动态添加布局
     *
     * @param titles
     *            导航标题
     * @param selectedImage
     *            选中时的图片
     * @param unSelectedImage
     *            未选中时的图片
     * @param screenWidth
     *            屏幕的宽度
     * @param mHeight
     *            控件自身高度
     * @param context
     */
    @SuppressWarnings("deprecation")
    public void setLayout(String[] titles, int[] selectedImage, int[] unSelectedImage, int screenWidth, int mHeight,
                          Context context) {
        this.context = context;
        this.selectedImage = selectedImage;
        this.unSelectedImage = unSelectedImage;
        setOrientation(LinearLayout.HORIZONTAL);
        if (titles != null && titles.length != 0) {
            int widthScale = screenWidth / titles.length;
            for (int i = 0; i < titles.length; i++) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams layoutLp = new LinearLayout.LayoutParams(widthScale,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutLp.gravity = Gravity.CENTER;
                layout.setLayoutParams(layoutLp);

                ImageView image = new ImageView(context);
                LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(image_width, image_height);
                imageLp.topMargin = 5;
                image.setImageDrawable(context.getResources().getDrawable(unSelectedImage[i]));
                image.setLayoutParams(imageLp);

                TextView tv_title = new TextView(context);
                LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                tv_title.setTextSize(text_size);
                tv_title.setText(titles[i]);
                tv_title.setLayoutParams(textLp);

                layout.addView(image);
                layout.addView(tv_title);

                layout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        setColorLing(position);
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                });

                layout.setTag(i);
                addView(layout, widthScale, mHeight);
                imageViews.add(image);
                textViews.add(tv_title);
            }
        }
    }

    /**
     * 底部导航点击接口回调
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * 设置文本和图片为亮色
     *
     * @param position
     */
    public void setColorLing(int position) {
        setColorDark();
        for (int i = 0; i < textViews.size(); i++) {
            if (position == i) {
                textViews.get(i).setTextColor(Color.parseColor("#000000"));
                imageViews.get(i).setImageDrawable(context.getResources().getDrawable(selectedImage[i]));
            }
        }
    }

    /**
     * 设置文本和图片为暗色
     */
    public void setColorDark() {
        for (int i = 0; i < textViews.size(); i++) {
            textViews.get(i).setTextColor(Color.parseColor("#999999"));
            imageViews.get(i).setImageDrawable(context.getResources().getDrawable(unSelectedImage[i]));
        }
    }
}