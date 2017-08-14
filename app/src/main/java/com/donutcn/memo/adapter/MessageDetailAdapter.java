package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.listener.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * com.donutcn.memo.adapter
 * Created by 73958 on 2017/8/13.
 */

public class MessageDetailAdapter extends SwipeMenuAdapter<MessageDetailAdapter.ViewHolder> {

    private List<MessageItem> list;
    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public MessageDetailAdapter(Context context, List<MessageItem> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
    }

    @Override
    public MessageDetailAdapter.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        ViewHolder viewHolder = new ViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageDetailAdapter.ViewHolder holder, int position) {
        holder.badge
                .setShowShadow(false)
                .setBadgeGravity(Gravity.CENTER | Gravity.END)
                .setGravityOffset(24, true)
                .setBadgePadding(8, true)
                .setBadgeText("新");
    }

    @Override
    public int getItemCount() {
        return list == null ? 1 : list.size() + 1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userIcon;
        TextView name;
        TextView time;
        TextView realName;
        TextView phone;
        TextView weChat;
        TextView resume;
        TextView email;
        TextView comment;
        TextView vote;
        Badge badge;
        Button delete, download;
        View view_name, view_phone, view_weChat, view_resume, view_email, view_comment, view_vote;
        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            userIcon = (ImageView) itemView.findViewById(R.id.message_item_icon);
            name = (TextView) itemView.findViewById(R.id.message_item_name);
            time = (TextView) itemView.findViewById(R.id.message_item_time);
            badge = new QBadgeView(mContext).bindTarget(itemView.findViewById(R.id.message_user_container));

            realName = (TextView) itemView.findViewById(R.id.message_item_real_name);
            phone = (TextView) itemView.findViewById(R.id.message_item_phone);
            weChat = (TextView) itemView.findViewById(R.id.message_item_we_chat);
            resume = (TextView) itemView.findViewById(R.id.message_item_resume);
            email = (TextView) itemView.findViewById(R.id.message_item_email);
            vote = (TextView) itemView.findViewById(R.id.message_item_vote);
            comment = (TextView) itemView.findViewById(R.id.message_item_comment);

            delete = (Button) itemView.findViewById(R.id.message_item_delete);
            download = (Button) itemView.findViewById(R.id.message_item_download);

            view_name = itemView.findViewById(R.id.message_name_container);
            view_phone = itemView.findViewById(R.id.message_phone_container);
            view_weChat = itemView.findViewById(R.id.message_we_chat_container);
            view_resume = itemView.findViewById(R.id.message_resume_container);
            view_email = itemView.findViewById(R.id.message_email_container);
            view_comment = itemView.findViewById(R.id.message_comment_container);
            view_vote = itemView.findViewById(R.id.message_vote_container);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
