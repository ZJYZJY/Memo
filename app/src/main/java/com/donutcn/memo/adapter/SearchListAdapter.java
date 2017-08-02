package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;

public class SearchListAdapter extends SwipeMenuAdapter<SearchListAdapter.ViewHolder> {

    private ArrayList<BriefContent> list;
    private Context mContext;
    private String keyword;
    private OnItemClickListener mOnItemClickListener;

    public SearchListAdapter(Context context, ArrayList<BriefContent> list) {
        this.mContext = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setKeyword(String key){
        this.keyword = key;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_list, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        ViewHolder viewHolder = new ViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = list.get(position).getTitle();
        title = title.replace(keyword, "<font color=\"#DB7A65\">" + keyword + "</font>");
        String content = list.get(position).getContent();
        content = content.replace(keyword, "<font color=\"#DB7A65\">" + keyword + "</font>");
        holder.mTitle.setText(title);
        RichText.fromHtml(title).into(holder.mTitle);
        holder.mAuthorDate.setText(mContext.getString(R.string.placeholder_search_item_author,
                list.get(position).getName(), list.get(position).getTime().substring(2, 10)));
        RichText.fromHtml(content).noImage(true).into(holder.mContent);
        holder.mInfo.setText(mContext.getString(R.string.placeholder_search_item_info,
                list.get(position).getReadCount(), list.get(position).getComment(),
                list.get(position).getUpVote()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mContentPic1;
        ImageView mContentPic2;
        TextView mTitle;
        TextView mAuthorDate;
        TextView mContent;
        TextView mInfo;
        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContentPic1 = (ImageView) itemView.findViewById(R.id.iv_one);
            mContentPic2 = (ImageView) itemView.findViewById(R.id.iv_two);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mAuthorDate = (TextView) itemView.findViewById(R.id.tv_author_date);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mInfo = (TextView) itemView.findViewById(R.id.tv_content_info);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
