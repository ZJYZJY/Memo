package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.event.FinishEditVoteItemsEvent;
import com.donutcn.memo.interfaces.OnTextChangerListener;
import com.donutcn.memo.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * com.donutcn.memo.adapter
 * Created by 73958 on 2017/7/21.
 */

public class VoteItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnTextChangerListener {

    private static final int ITEM_HEADER = 10000;
    private static final int ITEM_NORMAL = 20000;
    private static final int ITEM_FOOTER = 30000;

    private Context mContext;
    private ArrayList<Integer> views;
    private ArrayList<String> mVoteItemStr;
    private final int mUnEditable;

    private int maxVoteSelection = 50;
    private OnTextChangerListener listener;

    public VoteItemAdapter(Context context, List<String> items) {
        this.mContext = context;
        this.mVoteItemStr = new ArrayList<>();
        this.views = new ArrayList<>();
        listener = this;
        if (items.size() > 0) {
            mUnEditable = items.size();
            for (int i = 0; i < items.size(); i++) {
                this.views.add(i);
                this.mVoteItemStr.add(items.get(i));
            }
        } else {
            mUnEditable = 0;
            this.views.add(1);
            this.views.add(2);
            this.views.add(3);
            this.mVoteItemStr.add("");
            this.mVoteItemStr.add("");
            this.mVoteItemStr.add("");
        }
    }

    public int getVoteItemCount() {
        return views.size();
    }

//    public ArrayList<String> getTextArray() {
//        return mVoteItemStr;
//    }

    public void setMaxVoteSelection(int max) {
        this.maxVoteSelection = max;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_HEADER:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.completing_page_vote_header, parent, false);
                return new VoteItemAdapter.HeaderViewHolder(view);
            case ITEM_NORMAL:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.completing_page_vote_item, parent, false);
                return new VoteItemAdapter.ViewHolder(view);
            case ITEM_FOOTER:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.completing_page_vote_footer, parent, false);
                return new FooterViewHolder(view);
            default:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.completing_page_vote_item, parent, false);
                return new VoteItemAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            if (maxVoteSelection == getVoteItemCount()) {
                // hide the add button while vote item reach the max.
                ((FooterViewHolder) holder).addItem.setVisibility(View.GONE);
            } else {
                // show the add button while vote item and set hint.
                ((FooterViewHolder) holder).addItem.setVisibility(View.VISIBLE);
                ((FooterViewHolder) holder).addItem.setText(mContext.getString(R.string.hint_completing_vote_add));
                ((FooterViewHolder) holder).addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        views.add(getVoteItemCount());
                        mVoteItemStr.add("");
                        notifyItemInserted(holder.getAdapterPosition());
                        notifyItemRangeChanged(0, views.size() + 2);
                    }
                });
            }
            ((FooterViewHolder) holder).finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("res", mVoteItemStr.toString());
                    int id = ((FooterViewHolder) holder).choiceMode.getCheckedRadioButtonId();
                    EventBus.getDefault().post(new FinishEditVoteItemsEvent(
                            mVoteItemStr.subList(mUnEditable, mVoteItemStr.size()), id == R.id.single_choice));
                }
            });
        } else if (position == 0) {
            // header view will do nothing.
        } else {
//            if (((ViewHolder) holder).vote_et.getTag() instanceof TextWatcher) {
//                ((ViewHolder) holder).vote_et.removeTextChangedListener((TextWatcher) ((ViewHolder) holder).vote_et.getTag());
//            }
            ((ViewHolder) holder).vote_et.setEnabled(true);
            ((ViewHolder) holder).delItem.setVisibility(View.VISIBLE);
            if(position <= mUnEditable){
                ((ViewHolder) holder).vote_et.setEnabled(false);
                ((ViewHolder) holder).delItem.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).vote_et.setText(mVoteItemStr.get(holder.getAdapterPosition() - 1));
            ((ViewHolder) holder).vote_et.setHint(mContext.getString(R.string.hint_completing_vote_count, position));
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    listener.afterTextChanged(s.toString(), holder.getAdapterPosition() - 1);
                }
            };
            ((ViewHolder) holder).vote_et.addTextChangedListener(textWatcher);
//            ((ViewHolder) holder).vote_et.setTag(textWatcher);
            // disable the uneditable item delete button.
            if(position > mUnEditable){
                ((ViewHolder) holder).delItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (views.size() == 2) {
                            ToastUtil.show(mContext, mContext.getString(R.string.toast_min_vote_items));
                            return;
                        }
                        views.remove(holder.getAdapterPosition() - 1);
                        mVoteItemStr.remove(holder.getAdapterPosition() - 1);
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(0, views.size() + 2);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return views == null ? 0 : views.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ITEM_FOOTER;
        } else if (position == 0) {
            return ITEM_HEADER;
        } else {
            return ITEM_NORMAL;
        }
    }

    /**
     * get the vote item string.
     *
     * @param s
     * @param position item position
     */
    @Override
    public void afterTextChanged(String s, int position) {
        mVoteItemStr.set(position, s);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        EditText vote_et;
        ImageView delItem;

        public ViewHolder(View itemView) {
            super(itemView);
            vote_et = (EditText) itemView.findViewById(R.id.completing_vote_et);
            delItem = (ImageView) itemView.findViewById(R.id.completing_vote_del);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        TextView addItem;
        RadioGroup choiceMode;
        RadioButton single, multiple;
        Button finish;

        public FooterViewHolder(View itemView) {
            super(itemView);
            addItem = (TextView) itemView.findViewById(R.id.vote_add);
            choiceMode = (RadioGroup) itemView.findViewById(R.id.choice_mode);
            single = (RadioButton) itemView.findViewById(R.id.single_choice);
            multiple = (RadioButton) itemView.findViewById(R.id.multiple_choice);
            finish = (Button) itemView.findViewById(R.id.completing_finish);
        }
    }
}
