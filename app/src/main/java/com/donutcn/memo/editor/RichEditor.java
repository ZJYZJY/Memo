package com.donutcn.memo.editor;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * com.donutcn.memo.editor
 * Created by 73958 on 2017/8/24.
 */

public class RichEditor extends WebView {

    private static final String SETUP_HTML = "file:///android_asset/richEditor.html";

    private String fileUrl;
    private boolean isReady = false;
    private AfterInitialLoadListener mLoadListener;

    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, android.R.attr.webViewStyle);
    }

    public RichEditor(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebViewClient());
        setFileUrl(SETUP_HTML);
        loadUrl(fileUrl);
    }

    public void setFileUrl(String url){
        this.fileUrl = url;
    }

    protected EditorWebViewClient createWebViewClient() {
        return new EditorWebViewClient();
    }

    public void exec(final String trigger) {
        if (isReady) {
            load(trigger);
        } else {
            postDelayed(new Runnable() {
                @Override public void run() {
                    exec(trigger);
                }
            }, 50);
        }
    }

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }

    protected class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(fileUrl);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public interface AfterInitialLoadListener {
        void onAfterInitialLoad(boolean isReady);
    }

    public void setOnInitialLoadListener(AfterInitialLoadListener listener) {
        mLoadListener = listener;
    }
}
