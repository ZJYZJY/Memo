package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.base.BasePublishAdapter;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.DynamicTimeFormat;
import com.donutcn.memo.utils.StringUtil;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MemoAdapter extends BasePublishAdapter<MemoAdapter.DefaultViewHolder> {

    private List<BriefContent> list;
    private int mLayoutType;
    private Context mContext;
    private RequestManager glide;

    private OnItemClickListener mOnItemClickListener;

    public MemoAdapter(Context context, List<BriefContent> list, @ItemLayoutType int layoutType) {
        this.mContext = context;
        this.list = list;
        this.mLayoutType = layoutType;
        glide = Glide.with(mContext);
    }

    public void setDataSet(List<BriefContent> list){
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        if(viewType == ITEM_NORMAL){
            switch (mLayoutType) {
                case ItemLayoutType.TYPE_TAG:
                    return super.onCreateContentView(parent, viewType);
                case ItemLayoutType.AVATAR_IMG:
                    View view = super.onCreateContentView(parent, viewType);
                    view.findViewById(R.id.iv_content_icon).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_publish_author).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_content_type).setVisibility(View.GONE);
                    return view;
            }
        }
        return super.onCreateContentView(parent, viewType);
    }

    @Override
    public void onViewRecycled(DefaultViewHolder holder) {
        super.onViewRecycled(holder);
        holder.mContainer.setVisibility(View.GONE);
        Glide.clear(holder.mContentPic1);
        Glide.clear(holder.mContentPic2);
        holder.mContentPic1.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        holder.mContentPic2.setBackgroundColor(mContext.getResources().getColor(R.color.white));
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        String typeStr = list.get(position).getType();
        PublishType type = PublishType.getType(typeStr);
        holder.mTitle.setText(list.get(position).getTitle());
        holder.mContentType.setText(typeStr);
        holder.mContent.setText(list.get(position).getContent());

        // reset image view and url string.
        holder.mContainer.setVisibility(View.GONE);
        String url0 = "";
        String url1 = "";

        url0 = list.get(position).getImage0();
        url1 = list.get(position).getImage1();
        if(!url0.equals("")){
            holder.mContainer.setVisibility(View.VISIBLE);
            glide.load(url0).into(holder.mContentPic1);
        }
        if(!url1.equals("")){
            holder.mContainer.setVisibility(View.VISIBLE);
            glide.load(url1).into(holder.mContentPic2);
        }
        String iconUrl = list.get(position).getUserIcon();
        if(mLayoutType == ItemLayoutType.AVATAR_IMG){
            if(iconUrl != null && !iconUrl.equals("")){
                glide.load(iconUrl).centerCrop().into(holder.mImage);
            } else {
                holder.mImage.setImageResource(R.mipmap.user_default_icon);
            }
        }

        holder.mAuthor.setText(mContext.getString(R.string.placeholder_publish_author_type,
                list.get(position).getName(), typeStr));
        Date date = StringUtil.string2Date(list.get(position).getTime());
        DateFormat dateFormat = new DynamicTimeFormat("%s");
        if(list.get(position).isPrivate()){
            holder.mTime.setText(mContext.getString(R.string.placeholder_content_private, dateFormat.format(date)));
        } else {
            holder.mTime.setText(dateFormat.format(date));
        }
//        holder.mUpvote.setText(list.get(position).getUpVote());
//        holder.mComment.setText(list.get(position).getComment());

        if(mLayoutType == ItemLayoutType.TYPE_TAG && type != null){
            switch (type){
                case ARTICLE:
                case ALBUM:
                    holder.mContentType.setBackgroundResource(R.drawable.type_blue_bg);
                    break;
                case ACTIVITY:
                case VOTE:
                case QA:
                    holder.mContentType.setBackgroundResource(R.drawable.type_green_bg);
                    break;
                case RECRUIT:
                case RESERVE:
                case SALE:
                    holder.mContentType.setBackgroundResource(R.drawable.type_red_bg);
                    break;
            }
        }
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        View mContainer;
        ImageView mContentPic1;
        ImageView mContentPic2;
        TextView mTitle;
        TextView mContent;
        TextView mContentType;
        TextView mAuthor;
        TextView mTime;
        TextView mUpvote;
        TextView mComment;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImage = (ImageView) itemView.findViewById(R.id.iv_content_icon);
            mContainer = itemView.findViewById(R.id.iv_container);
            mContentPic1 = (ImageView) itemView.findViewById(R.id.iv_one);
            mContentPic2 = (ImageView) itemView.findViewById(R.id.iv_two);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mContentType = (TextView) itemView.findViewById(R.id.tv_content_type);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_publish_author);
            mTime = (TextView) itemView.findViewById(R.id.tv_publish_time);
//            mUpvote = (TextView) itemView.findViewById(R.id.tv_upvote);
//            mComment = (TextView) itemView.findViewById(R.id.tv_comment);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
