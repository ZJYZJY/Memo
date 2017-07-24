package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.PublishType;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

public class MessageAdapter extends SwipeMenuAdapter<MessageAdapter.ViewHolder> {

    private List<String> titles;
    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public MessageAdapter(Context context, List<String> titles) {
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
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        ViewHolder viewHolder = new ViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;

        PublishType p = PublishType.RECRUIT;
        String btnText;
        int background;
        switch (p){
            case ARTICLE:
                viewHolder.mExtra.setVisibility(View.GONE);
                btnText = mContext.getString(R.string.interactive_view_comment);
                background = R.drawable.selector_blue_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_blue_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_article);
                break;
            case ALBUM:
                viewHolder.mExtra.setVisibility(View.GONE);
                btnText = mContext.getString(R.string.interactive_view_comment);
                background = R.drawable.selector_blue_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_blue_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_album);
                break;
            case ACTIVITY:
                viewHolder.mExtra.setVisibility(View.GONE);
                btnText = mContext.getString(R.string.interactive_view_enroll);
                background = R.drawable.selector_green_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_green_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_activity);
                break;
            case VOTE:
                viewHolder.mExtra.setVisibility(View.GONE);
                btnText = mContext.getString(R.string.interactive_view_vote);
                background = R.drawable.selector_green_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_green_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_vote);
                break;
            case RECRUIT:
                viewHolder.mExtra.setVisibility(View.GONE);
                btnText = mContext.getString(R.string.interactive_view_resume);
                background = R.drawable.selector_red_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_red_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_recruit);
                break;
            case QA:
                viewHolder.mExtra.setVisibility(View.GONE);
                btnText = mContext.getString(R.string.interactive_view_answer);
                background = R.drawable.selector_green_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_green_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_article);// lack of resources "type_qa"
                break;
            case RESERVE:
                viewHolder.mExtra.setVisibility(View.GONE);
                btnText = mContext.getString(R.string.interactive_view_reserve);
                background = R.drawable.selector_red_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_red_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_reserve);
                break;
            case SALE:
                viewHolder.mExtra.setVisibility(View.VISIBLE);
                btnText = mContext.getString(R.string.interactive_view_order);
                background = R.drawable.selector_red_btn;
                viewHolder.mImage.setBackgroundResource(R.drawable.type_red_img_bg);
                viewHolder.mImage.setImageResource(R.drawable.type_sale);
                break;
            default:
                viewHolder.mExtra.setVisibility(View.VISIBLE);
                btnText = mContext.getString(R.string.interactive_view_reply);
                background = mContext.getResources().getColor(R.color.background_black_light);
                viewHolder.mImage.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                break;
        }
        viewHolder.mInteraction.setText(btnText);
        viewHolder.mInteraction.setBackgroundResource(background);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mTitle;
        TextView mTime;
        Button mInteraction;
        TextView mInfo;
        TextView mExtra;
        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
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
