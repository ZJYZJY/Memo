package com.donutcn.memo.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

public abstract class BasePublishAdapter<VH extends RecyclerView.ViewHolder> extends SwipeMenuAdapter<VH> {

    public static final int ITEM_FOOTER = 10000;
    public static final int ITEM_NORMAL = 20000;

    private boolean hasFooter = false;

    public void setFooterEnable(boolean enable){
        hasFooter = enable;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        if(viewType == ITEM_NORMAL){
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
        }else {
            if (hasFooter)
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false);
        }
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
    }

    @Override
    public int getItemViewType(int position) {
        if(position < getItemCount() - 1){
            return ITEM_NORMAL;
        }else {
            if(hasFooter)
                return ITEM_FOOTER;
        }
        return ITEM_NORMAL;
    }
}
