package com.donutcn.memo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.listener.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

public class HaoYeAdapter extends SwipeMenuAdapter<HaoYeAdapter.DefaultViewHolder> {

    private List<String> titles;

    private OnItemClickListener mOnItemClickListener;

    public HaoYeAdapter(List<String> titles) {
        this.titles = titles;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_haoye, parent, false);
    }

    @Override
    public HaoYeAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HaoYeAdapter.DefaultViewHolder holder, int position) {
        holder.setData();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mType;
        ImageView mContentPic1;
        ImageView mContentPic2;
        TextView mTitle;
        TextView mContent;
        TextView mTime;
        TextView mUpvote;
        TextView mComment;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mType = (ImageView) itemView.findViewById(R.id.iv_content_type);
            mContentPic1 = (ImageView) itemView.findViewById(R.id.iv_one);
            mContentPic2 = (ImageView) itemView.findViewById(R.id.iv_two);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mTime = (TextView) itemView.findViewById(R.id.tv_publish_time);
            mUpvote = (TextView) itemView.findViewById(R.id.tv_upvote);
            mComment = (TextView) itemView.findViewById(R.id.tv_comment);
            mType.setBackgroundResource(R.drawable.bg_green);
        }

        public void setData() {

        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
