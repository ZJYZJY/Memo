package com.donutcn.memo.adapter;

import android.content.Context;
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

public class SearchListAdapter extends SwipeMenuAdapter<SearchListAdapter.DefaultViewHolder> {

    private List<String> titles;

    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public SearchListAdapter(Context context, List<String> titles) {
        this.mContext = context;
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
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_list, parent, false);
    }

    @Override
    public SearchListAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchListAdapter.DefaultViewHolder holder, int position) {
        holder.setData();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mContentPic1;
        ImageView mContentPic2;
        TextView mTitle;
        TextView mAuthorDate;
        TextView mContent;
        TextView mInfo;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContentPic1 = (ImageView) itemView.findViewById(R.id.iv_one);
            mContentPic2 = (ImageView) itemView.findViewById(R.id.iv_two);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mAuthorDate = (TextView) itemView.findViewById(R.id.tv_author_date);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mInfo = (TextView) itemView.findViewById(R.id.tv_content_info);
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
