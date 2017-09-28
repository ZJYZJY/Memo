package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.Contact;
import com.donutcn.memo.entity.Template;
import com.donutcn.memo.interfaces.OnItemClickListener;

import java.util.List;

/**
 * com.donutcn.memo.adapter
 * Created by 73958 on 2017/9/28.
 */

public class TemplateListAdapter extends RecyclerView.Adapter<TemplateListAdapter.ViewHolder> {

    private Context mContext;
    private List<Template> list;
    private RequestManager glide;

    private OnItemClickListener mOnItemClickListener;

    public TemplateListAdapter(Context context, List<Template> list) {
        this.mContext = context;
        this.list = list;
        glide = Glide.with(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_template, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        glide.load(list.get(position).getShortCut()).into(holder.shortCut);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView shortCut;
        TextView title;
        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            shortCut = (ImageView) itemView.findViewById(R.id.template_short_cut);
            title = (TextView) itemView.findViewById(R.id.template_title);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
