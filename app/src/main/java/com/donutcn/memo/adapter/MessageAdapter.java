package com.donutcn.memo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

public class MessageAdapter extends SwipeMenuAdapter<MessageAdapter.DefaultViewHolder> {

    private List<String> titles;
    private int mLayoutType;

    private OnItemClickListener mOnItemClickListener;

    public MessageAdapter(List<String> titles, @ItemLayoutType int layoutType) {
        this.titles = titles;
        this.mLayoutType = layoutType;
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
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
    }

    @Override
    public MessageAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.DefaultViewHolder holder, int position) {
        holder.setData();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mTitle;
        TextView mTime;
        Button mInteraction;
        TextView mInfo;
        TextView mExtra;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImage = (ImageView) itemView.findViewById(R.id.message_item_icon);
            mTitle = (TextView) itemView.findViewById(R.id.message_item_title);
            mTime = (TextView) itemView.findViewById(R.id.message_item_time);
            mInteraction = (Button) itemView.findViewById(R.id.message_item_btn);
            mInfo = (TextView) itemView.findViewById(R.id.message_item_info);
            mExtra = (TextView) itemView.findViewById(R.id.message_item_extra_title);
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
