package com.donutcn.widgetlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.donutcn.widgetlib.R;

/**
 * com.donutcn.widgetlib
 * Created by 73958 on 2017/7/5.
 */

public class CheckableImageButton extends android.support.v7.widget.AppCompatImageButton implements Checkable {

    private boolean isChecked = false;

    private static final int[] CHECKED_STATE_SET = { R.attr.isChecked };

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public CheckableImageButton(Context context) {
        super(context);
    }

    public CheckableImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性的值
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CheckableImageButton);
        isChecked = a.getBoolean(R.styleable.CheckableImageButton_isChecked, false);
        setChecked(isChecked);

        a.recycle();
    }

    public CheckableImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        if(this.isChecked != checked){
            this.isChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        this.isChecked = !this.isChecked;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(states, CHECKED_STATE_SET);
        }
        return states;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
         invalidate();
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(CheckableImageButton button, boolean isChecked);
    }

//    /**
//     * 保存状态
//     */
//    static class SaveState extends BaseSavedState {
//        boolean checked;
//
//        public SaveState(Parcel in) {
//            super(in);
//            checked = (Boolean) in.readValue(null);
//        }
//
//        public SaveState(Parcelable superState) {
//            super(superState);
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest, flags);
//            dest.writeValue(checked);
//        }
//
//        public static final Parcelable.Creator<SaveState> CREATOR = new Creator<CheckableImageButton.SaveState>() {
//
//            @Override
//            public SaveState[] newArray(int size) {
//                return new SaveState[size];
//            }
//
//            @Override
//            public SaveState createFromParcel(Parcel source) {
//                return createFromParcel(source);
//            }
//        };
//    }
//
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superParcelable = super.onSaveInstanceState();
//        SaveState ss = new SaveState(superParcelable);
//        ss.checked = isChecked();
//        return ss;
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        SaveState ss = (SaveState) state;
//        super.onRestoreInstanceState(ss.getSuperState());
//        setChecked(ss.checked);
//    }

    public OnCheckedChangeListener getmOnCheckedChangeListener() {
        return mOnCheckedChangeListener;
    }

    public void setmOnCheckedChangeListener(
            OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }
}
