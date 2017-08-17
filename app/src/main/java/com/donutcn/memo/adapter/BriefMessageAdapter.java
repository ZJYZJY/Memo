package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.BriefMessage;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.PublishType;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class BriefMessageAdapter extends SwipeMenuAdapter<BriefMessageAdapter.ViewHolder> {

    private List<BriefMessage> list;
    private Context mContext;
    private RequestManager glide;

    private OnItemClickListener mOnItemClickListener;

    public BriefMessageAdapter(Context context, List<BriefMessage> list) {
        this.mContext = context;
        this.list = list;
        glide = Glide.with(context);
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
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_brief_message, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        ViewHolder viewHolder = new ViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBadge
                .setShowShadow(false)
                .setBadgeGravity(Gravity.CENTER | Gravity.END)
                .setGravityOffset(32, true)
                .setBadgeNumber(list.get(position).getNewMsgCount());
        holder.mTitle.setText(list.get(position).getTitle());
        String subTitle = list.get(position).getSubTitle();
        PublishType type = PublishType.getType(list.get(position).getType());
        if(type == null){
            // private letter
            glide.load(list.get(position).getType()).centerCrop().into(holder.mImage);
        } else {
            int count = list.get(position).getNewMsgCount();
            if(count > 0){
                holder.mSubTitle.setText(mContext.getString(
                        R.string.placeholder_new_reply, count, type.getReply()));
            } else {
                holder.mSubTitle.setText(mContext.getString(
                        R.string.placeholder_no_new_reply, type.getReply()));
            }
            switch (type){
                case ARTICLE:
                    holder.mImage.setImageResource(R.drawable.icon_message_article);
                    break;
                case ALBUM:
                    holder.mImage.setImageResource(R.drawable.icon_message_album);
                    break;
                case ACTIVITY:
                    holder.mImage.setImageResource(R.drawable.icon_message_activity);
                    break;
                case VOTE:
                    holder.mImage.setImageResource(R.drawable.icon_message_vote);
                    break;
                case RECRUIT:
                    holder.mImage.setImageResource(R.drawable.icon_message_recruit);
                    break;
                case QA:
                    holder.mImage.setImageResource(R.drawable.icon_message_qa);
                    break;
                case RESERVE:
                    holder.mImage.setImageResource(R.drawable.icon_message_reserve);
                    break;
                case SALE:
                    holder.mImage.setImageResource(R.drawable.icon_message_sale);
                    break;
            }
        }
        if(subTitle != null && !subTitle.equals("")){
            holder.mSubTitle.setText(subTitle);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mTitle;
        TextView mSubTitle;
        Badge mBadge;
        ImageView mArrow;
        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImage = (ImageView) itemView.findViewById(R.id.message_item_icon);
            mTitle = (TextView) itemView.findViewById(R.id.message_item_title);
            mSubTitle = (TextView) itemView.findViewById(R.id.message_item_subtitle);
            mBadge = new QBadgeView(mContext).bindTarget(itemView.findViewById(R.id.message_item_container));
            mArrow = (ImageView) itemView.findViewById(R.id.message_item_arrow);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
                // hide the red dot when click the message item.
                mBadge.hide(false);
            }
        }
    }

}
