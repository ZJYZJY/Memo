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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.event.ItemActionClickEvent;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.type.PublishType;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * com.donutcn.memo.adapter
 * Created by 73958 on 2017/8/13.
 */

public class MessageDetailAdapter extends SwipeMenuAdapter<MessageDetailAdapter.ViewHolder> {

    private List<MessageItem> list;
    private String type;
    private int mUnreadCount;
    private Context mContext;

    private PublishType pType;
    private RequestManager mGlide;
    private OnItemClickListener mOnItemClickListener;

    public MessageDetailAdapter(Context context, List<MessageItem> list, String type, int count) {
        this.mContext = context;
        this.list = list;
        this.type = type;
        this.mUnreadCount = count;
        mGlide = Glide.with(context);
        pType = PublishType.getType(type);
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
    }

    @Override
    public MessageDetailAdapter.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        ViewHolder viewHolder = new ViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.view_comment.setVisibility(View.GONE);
        holder.view_name.setVisibility(View.GONE);
        holder.view_phone.setVisibility(View.GONE);
        holder.view_vote.setVisibility(View.GONE);
        holder.view_answer.setVisibility(View.GONE);
        holder.view_weChat.setVisibility(View.GONE);
        holder.view_email.setVisibility(View.GONE);
        holder.view_resume.setVisibility(View.GONE);
        holder.download.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(MessageDetailAdapter.ViewHolder holder, int position) {
        if(position < mUnreadCount){
            holder.badge
                    .setShowShadow(false)
                    .setBadgeGravity(Gravity.CENTER | Gravity.END)
                    .setGravityOffset(24, true)
                    .setBadgePadding(8, true)
                    .setBadgeText("新");
        }
        String iconUrl = list.get(position).getIconUrl();
        if(iconUrl != null && !"".equals(iconUrl)){
            mGlide.load(iconUrl).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.userIcon);
        }
        holder.name.setText(list.get(position).getName());
        holder.time.setText(list.get(position).getTime().substring(0, 16) + " 提交");
        if(list.get(position).getCommentId() != null){
            holder.view_comment.setVisibility(View.VISIBLE);
            holder.comment.setText(list.get(position).getComment());
        } else {
            String realName = list.get(position).getRealName();
            if(realName != null){
                holder.view_name.setVisibility(View.VISIBLE);
                holder.realName.setText(realName);
            }
            String phone = list.get(position).getPhone();
            if(phone != null){
                holder.view_phone.setVisibility(View.VISIBLE);
                holder.phone.setText(phone);
            }
            switch (pType){
                case ARTICLE:
                case ALBUM:
                    holder.view_comment.setVisibility(View.VISIBLE);
                    holder.comment.setText(list.get(position).getComment());
                    break;
                case VOTE:
                    holder.view_vote.setVisibility(View.VISIBLE);
                    holder.vote.setText(list.get(position).getVote().replace("-", " "));
                    break;
                case QA:
                    holder.view_answer.setVisibility(View.VISIBLE);
                    holder.answer.setText(list.get(position).getAnswer());
                    break;
                case ACTIVITY:
                case RESERVE:
                    String weChat = list.get(position).getWeChat();
                    if(weChat != null && !"".equals(weChat)){
                        holder.view_weChat.setVisibility(View.VISIBLE);
                        holder.weChat.setText(weChat);
                    }
                    String email = list.get(position).getEmail();
                    if(email != null && !"".equals(email)){
                        holder.view_email.setVisibility(View.VISIBLE);
                        holder.email.setText(email);
                    }
                    break;
                case RECRUIT:
                    String resume = list.get(position).getResume();
                    if(resume != null && !"".equals(resume)){
                        holder.download.setVisibility(View.VISIBLE);
                        holder.view_resume.setVisibility(View.VISIBLE);
                        holder.resume.setText(list.get(position).getName() + "的简历");
                    }
                    String email1 = list.get(position).getEmail();
                    if (email1 != null && !"".equals(email1)){
                        holder.view_email.setVisibility(View.VISIBLE);
                        holder.email.setText(email1);
                    }
                    break;
                case SALE:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
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
        TextView answer;
        Badge badge;
        Button delete, download;
        View view_name, view_phone, view_weChat, view_resume, view_email, view_comment, view_vote, view_answer;
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
            answer = (TextView) itemView.findViewById(R.id.message_item_answer);

            delete = (Button) itemView.findViewById(R.id.message_item_delete);
            download = (Button) itemView.findViewById(R.id.message_item_download);
            delete.setOnClickListener(this);
            download.setOnClickListener(this);

            view_name = itemView.findViewById(R.id.message_name_container);
            view_phone = itemView.findViewById(R.id.message_phone_container);
            view_weChat = itemView.findViewById(R.id.message_we_chat_container);
            view_resume = itemView.findViewById(R.id.message_resume_container);
            view_email = itemView.findViewById(R.id.message_email_container);
            view_comment = itemView.findViewById(R.id.message_comment_container);
            view_vote = itemView.findViewById(R.id.message_vote_container);
            view_answer = itemView.findViewById(R.id.message_answer_container);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener == null) {
                return;
            }
            if (v == delete){
                EventBus.getDefault().post(new ItemActionClickEvent(getAdapterPosition(), 0));
            } else if(v == download){
                EventBus.getDefault().post(new ItemActionClickEvent(getAdapterPosition(), 1));
            } else {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
