package com.donutcn.memo.editor;

import com.even.mricheditor.RichEditorCallback;

/**
 * com.donutcn.memo.editor
 * Created by 73958 on 2017/8/23.
 */

public interface EditorAction {

    void undo();

    void redo();

    void focus();

    void disable();

    void enable();

    /******************** Font ********************/
    void bold();

    void italic();

    void underline();

    void strikethrough();

    void superscript();

    void subscript();

    void backColor(String color);

    void foreColor(String color);

    void fontName(String fontName);

    void fontSize(double foreSize);

    /******************** Paragraph ********************/
    void justifyLeft();

    void justifyRight();

    void justifyCenter();

    void justifyFull();

    void insertOrderedList();

    void insertUnorderedList();

    void indent();

    void outdent();

    void formatPara();

    void formatH1();

    void formatH2();

    void formatH3();

    void formatH4();

    void formatH5();

    void formatH6();

    void lineHeight(double lineHeight);

    void insertImage(String imageUrl);

    void insertText(String text);

    void createLink(String linkText, String linkUrl);

    void unlink();

    void codeReview();

    void insertTable(int colCount, int rowCount);

    void insertHorizontalRule();

    void formatBlockquote();

    void formatBlockCode();

    void insertHtml(String html);

    void refreshHtml();
}
