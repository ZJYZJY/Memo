package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    private OnItemClickListener mOnItemClickListener;

    public BriefMessageAdapter(Context context, List<BriefMessage> list) {
        this.mContext = context;
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
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_brief_message, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        ViewHolder viewHolder = new ViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;

        PublishType p = PublishType.RECRUIT;
        switch (p){
            case ARTICLE:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_blue_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_article);
                break;
            case ALBUM:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_blue_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_album);
                break;
            case ACTIVITY:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_green_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_activity);
                break;
            case VOTE:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_green_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_vote);
                break;
            case RECRUIT:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_red_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_recruit);
                break;
            case QA:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_green_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_article);// lack of resources "type_qa"
                break;
            case RESERVE:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_red_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_reserve);
                break;
            case SALE:
                viewHolder.mImage.setBackgroundResource(R.drawable.type_red_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_sale);
                break;
            default:
                viewHolder.mImage.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBadge
                .setShowShadow(false)
                .setBadgeGravity(Gravity.CENTER | Gravity.END)
                .setGravityOffset(32, true)
                .setBadgeNumber(6);
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
