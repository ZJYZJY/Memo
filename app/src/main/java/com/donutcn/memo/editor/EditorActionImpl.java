package com.donutcn.memo.editor;

/**
 * com.donutcn.memo.editor
 * Created by 73958 on 2017/8/23.
 */

public class EditorActionImpl implements EditorAction {

    private RichEditor mWebView;

    public EditorActionImpl(RichEditor webView){
        this.mWebView = webView;
    }

    @Override
    public void undo() {
        mWebView.exec("javascript:undo()");
    }

    @Override
    public void redo() {
        mWebView.exec("javascript:redo()");
    }

    @Override
    public void focus() {
        mWebView.exec("javascript:focus()");
    }

    @Override
    public void disable() {
        mWebView.exec("javascript:disable()");
    }

    @Override
    public void enable() {
        mWebView.exec("javascript:enable()");
    }

    /******************** Font ********************/
    @Override
    public void bold() {
        mWebView.exec("javascript:bold()");
    }

    @Override
    public void italic() {
        mWebView.exec("javascript:italic()");
    }

    @Override
    public void underline() {
        mWebView.exec("javascript:underline()");
    }

    @Override
    public void strikethrough() {
        mWebView.exec("javascript:strikethrough()");
    }

    @Override
    public void superscript() {
        mWebView.exec("javascript:superscript()");
    }

    @Override
    public void subscript() {
        mWebView.exec("javascript:subscript()");
    }

    @Override
    public void backColor(String color) {
        mWebView.exec("javascript:backColor('" + color + "')");
    }

    @Override
    public void foreColor(String color) {
        mWebView.exec("javascript:foreColor('" + color + "')");
    }

    @Override
    public void fontName(String fontName) {
        mWebView.exec("javascript:fontName('" + fontName + "')");
    }

    @Override
    public void fontSize(double foreSize) {
        mWebView.exec("javascript:fontSize(" + foreSize + ")");
    }

    /******************** Paragraph ********************/
    public void justifyLeft() {
        mWebView.exec("javascript:justifyLeft()");
    }

    @Override
    public void justifyRight() {
        mWebView.exec("javascript:justifyRight()");
    }

    @Override
    public void justifyCenter() {
        mWebView.exec("javascript:justifyCenter()");
    }

    @Override
    public void justifyFull() {
        mWebView.exec("javascript:justifyFull()");
    }

    @Override
    public void insertOrderedList() {
        mWebView.exec("javascript:insertOrderedList()");
    }

    @Override
    public void insertUnorderedList() {
        mWebView.exec("javascript:insertUnorderedList()");
    }

    @Override
    public void indent() {
        mWebView.exec("javascript:indent()");
    }

    @Override
    public void outdent() {
        mWebView.exec("javascript:outdent()");
    }

    @Override
    public void formatPara() {
        mWebView.exec("javascript:formatPara()");
    }

    @Override
    public void formatH1() {
        mWebView.exec("javascript:formatH1()");
    }

    @Override
    public void formatH2() {
        mWebView.exec("javascript:formatH2()");
    }

    @Override
    public void formatH3() {
        mWebView.exec("javascript:formatH3()");
    }

    @Override
    public void formatH4() {
        mWebView.exec("javascript:formatH4()");
    }

    @Override
    public void formatH5() {
        mWebView.exec("javascript:formatH5()");
    }

    @Override
    public void formatH6() {
        mWebView.exec("javascript:formatH6()");
    }

    @Override
    public void lineHeight(double lineHeight) {
        mWebView.exec("javascript:lineHeight(" + lineHeight + ")");
    }

    @Override
    public void insertImage(String imageUrl) {
        mWebView.exec("javascript:insertImage('" + imageUrl + "')");
        try {
            Thread.sleep(70);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        insertHtml("<div style=\"text-align: center;\">" +
//                "<span style=\"font-size: 12px\">请输入图片描述</span></div>");
        insertText("请输入图片描述");
    }

    @Override
    public void insertText(String text) {
        mWebView.exec("javascript:insertText('" + text + "')");
    }

    @Override
    public void createLink(String linkText, String linkUrl) {
        mWebView.exec("javascript:createLink('" + linkText + "','" + linkUrl + "')");
    }

    @Override
    public void unlink() {
        mWebView.exec("javascript:unlink()");
    }

    @Override
    public void codeReview() {
        mWebView.exec("javascript:codeReview()");
    }

    @Override
    public void insertTable(int colCount, int rowCount) {
        mWebView.exec("javascript:insertTable('" + colCount + "x" + rowCount + "')");
    }

    @Override
    public void insertHorizontalRule() {
        mWebView.exec("javascript:insertHorizontalRule()");
    }

    @Override
    public void formatBlockquote() {
        mWebView.exec("javascript:formatBlock('blockquote')");
    }

    @Override
    public void formatBlockCode() {
        mWebView.exec("javascript:formatBlock('pre')");
    }

    @Override
    public void insertHtml(String html) {
        mWebView.exec("javascript:pasteHTML('" + html + "');");
    }

    @Override
    public void refreshHtml() {
        mWebView.exec("javascript:refreshHTML()");
    }

    @Override
    public void destroy(){
        mWebView.exec("javascript:destroy()");
    }
}
