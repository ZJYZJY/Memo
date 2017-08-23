package com.donutcn.memo.editor;

import android.os.Build;

import com.donutcn.memo.utils.LogUtil;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

/**
 * com.donutcn.memo.editor
 * Created by 73958 on 2017/8/23.
 */

public class EditorActionImpl implements EditorAction {

    private WebView mWebView;

    public EditorActionImpl(WebView webView){
        this.mWebView = webView;
    }

    @Override
    public void undo() {
        load("javascript:undo()");
    }

    @Override
    public void redo() {
        load("javascript:redo()");
    }

    @Override
    public void focus() {
        load("javascript:focus()");
    }

    @Override
    public void disable() {
        load("javascript:disable()");
    }

    @Override
    public void enable() {
        load("javascript:enable()");
    }

    /******************** Font ********************/
    @Override
    public void bold() {
        load("javascript:bold()");
    }

    @Override
    public void italic() {
        load("javascript:italic()");
    }

    @Override
    public void underline() {
        load("javascript:underline()");
    }

    @Override
    public void strikethrough() {
        load("javascript:strikethrough()");
    }

    @Override
    public void superscript() {
        load("javascript:superscript()");
    }

    @Override
    public void subscript() {
        load("javascript:subscript()");
    }

    @Override
    public void backColor(String color) {
        load("javascript:backColor('" + color + "')");
    }

    @Override
    public void foreColor(String color) {
        load("javascript:foreColor('" + color + "')");
    }

    @Override
    public void fontName(String fontName) {
        load("javascript:fontName('" + fontName + "')");
    }

    @Override
    public void fontSize(double foreSize) {
        load("javascript:fontSize(" + foreSize + ")");
    }

    /******************** Paragraph ********************/
    public void justifyLeft() {
        load("javascript:justifyLeft()");
    }

    @Override
    public void justifyRight() {
        load("javascript:justifyRight()");
    }

    @Override
    public void justifyCenter() {
        load("javascript:justifyCenter()");
    }

    @Override
    public void justifyFull() {
        load("javascript:justifyFull()");
    }

    @Override
    public void insertOrderedList() {
        load("javascript:insertOrderedList()");
    }

    @Override
    public void insertUnorderedList() {
        load("javascript:insertUnorderedList()");
    }

    @Override
    public void indent() {
        load("javascript:indent()");
    }

    @Override
    public void outdent() {
        load("javascript:outdent()");
    }

    @Override
    public void formatPara() {
        load("javascript:formatPara()");
    }

    @Override
    public void formatH1() {
        load("javascript:formatH1()");
    }

    @Override
    public void formatH2() {
        load("javascript:formatH2()");
    }

    @Override
    public void formatH3() {
        load("javascript:formatH3()");
    }

    @Override
    public void formatH4() {
        load("javascript:formatH4()");
    }

    @Override
    public void formatH5() {
        load("javascript:formatH5()");
    }

    @Override
    public void formatH6() {
        load("javascript:formatH6()");
    }

    @Override
    public void lineHeight(double lineHeight) {
        load("javascript:lineHeight(" + lineHeight + ")");
    }

    @Override
    public void insertImage(String imageUrl) {
        load("javascript:insertImage('" + imageUrl + "')");
    }

    @Override
    public void insertText(String text) {
        load("javascript:insertText('" + text + "')");
    }

    @Override
    public void createLink(String linkText, String linkUrl) {
        load("javascript:createLink('" + linkText + "','" + linkUrl + "')");
    }

    @Override
    public void unlink() {
        load("javascript:unlink()");
    }

    @Override
    public void codeReview() {
        load("javascript:codeReview()");
    }

    @Override
    public void insertTable(int colCount, int rowCount) {
        load("javascript:insertTable('" + colCount + "x" + rowCount + "')");
    }

    @Override
    public void insertHorizontalRule() {
        load("javascript:insertHorizontalRule()");
    }

    @Override
    public void formatBlockquote() {
        load("javascript:formatBlock('blockquote')");
    }

    @Override
    public void formatBlockCode() {
        load("javascript:formatBlock('pre')");
    }

    @Override
    public void insertHtml(String html) {
        load("javascript:pasteHTML('" + html + "')");
    }

    @Override
    public void refreshHtml() {
        load("javascript:refreshHTML()");
    }

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.mWebView.evaluateJavascript(trigger, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    LogUtil.d(s);
                }
            });
        } else {
            this.mWebView.loadUrl(trigger);
        }
    }
}
