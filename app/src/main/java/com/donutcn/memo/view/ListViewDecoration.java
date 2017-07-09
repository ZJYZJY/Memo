package com.donutcn.memo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.donutcn.memo.R;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.utils.DensityUtils;
import com.yanzhenjie.recyclerview.swipe.ResCompat;

public class ListViewDecoration extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final int mSize;
    private final int mOrientation;
    private Context mContext;
    private int layoutType = ItemLayoutType.TYPE_IMG;

    public ListViewDecoration(Context context, @DimenRes int size, int orientation) {
        this.mContext = context;
        mDivider = ResCompat.getDrawable(context, R.drawable.divider_recycler);
        mSize = context.getResources().getDimensionPixelSize(size);
        mOrientation = orientation;
    }

    public ListViewDecoration(Context context, @ItemLayoutType int layoutType, @DimenRes int size, int orientation) {
        this.mContext = context;
        this.layoutType = layoutType;
        mDivider = ResCompat.getDrawable(context, R.drawable.divider_recycler);
        mSize = context.getResources().getDimensionPixelSize(size);
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left;
        int right;
        int top;
        int bottom;
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params =
                        (RecyclerView.LayoutParams) child.getLayoutParams();
                left = child.getRight() + params.rightMargin;
                right = left + mSize;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        } else {
            if(layoutType == ItemLayoutType.NO_IMG){
                left = parent.getPaddingLeft() + DensityUtils.dp2px(mContext, 8);
                right = parent.getWidth() - parent.getPaddingRight() - DensityUtils.dp2px(mContext, 8);
            }else {
                left = parent.getPaddingLeft() + DensityUtils.dp2px(mContext, 84);
                right = parent.getWidth() - parent.getPaddingRight() - DensityUtils.dp2px(mContext, 8);
            }
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params =
                        (RecyclerView.LayoutParams) child.getLayoutParams();
                top = child.getBottom() + params.bottomMargin;
                bottom = top + mSize;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, mSize, 0);
        } else {
            outRect.set(0, 0, 0, mSize);
        }
    }
}