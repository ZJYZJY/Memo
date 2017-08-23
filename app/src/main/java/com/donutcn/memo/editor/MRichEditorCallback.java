package com.donutcn.memo.editor;

import android.support.v4.app.Fragment;

import com.even.mricheditor.ActionType;
import com.even.mricheditor.RichEditorCallback;

/**
 * com.donutcn.memo.editor.interfaces
 * Created by 73958 on 2017/8/23.
 */

public class MRichEditorCallback extends RichEditorCallback {

    private EditorMenuFragment mEditorMenuFragment;

    public MRichEditorCallback(Fragment fragment){
        this.mEditorMenuFragment = (EditorMenuFragment) fragment;
    }

    @Override
    public void returnHtml(String html) {
        super.returnHtml(html);
    }

    @Override
    public String getHtml() {
        return super.getHtml();
    }

    @Override
    public void notifyFontStyleChange(ActionType type, final String value) {
        switch (type) {
            case FAMILY:
                mEditorMenuFragment.updateFontFamilyStates(value);
                break;
            case SIZE:
                mEditorMenuFragment.updateFontStates(ActionType.SIZE, Double.valueOf(value));
                break;
            case FORE_COLOR:
            case BACK_COLOR:
                mEditorMenuFragment.updateFontColorStates(type, value);
                break;
            case LINE_HEIGHT:
                mEditorMenuFragment.updateFontStates(ActionType.LINE_HEIGHT,
                        Double.valueOf(value));
                break;
            case JUSTIFY_LEFT:
            case JUSTIFY_CENTER:
            case JUSTIFY_RIGHT:
            case JUSTIFY_FULL:
                mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_LEFT,
                        type == ActionType.JUSTIFY_LEFT);
                mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_CENTER,
                        type == ActionType.JUSTIFY_CENTER);
                mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_RIGHT,
                        type == ActionType.JUSTIFY_RIGHT);
                mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_FULL,
                        type == ActionType.JUSTIFY_FULL);
                break;
            case BOLD:
            case ITALIC:
            case UNDERLINE:
            case SUBSCRIPT:
            case SUPERSCRIPT:
            case STRIKETHROUGH:
                mEditorMenuFragment.updateActionStates(type, Boolean.valueOf(value));
                break;
            case NORMAL:
            case H1:
            case H2:
            case H3:
            case H4:
            case H5:
            case H6:
            case STYLE_NONE:
                mEditorMenuFragment.updateStyleStates(type);
                break;
            case ORDERED:
            case UNORDERED:
            case LIST_STYLE_NONE:
                mEditorMenuFragment.updateActionStates(ActionType.UNORDERED, type == ActionType.UNORDERED);
                mEditorMenuFragment.updateActionStates(ActionType.ORDERED, type == ActionType.ORDERED);
                break;
            default:
                break;
        }
    }
}