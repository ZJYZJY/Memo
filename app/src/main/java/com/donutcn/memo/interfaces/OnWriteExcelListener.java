package com.donutcn.memo.interfaces;

import com.donutcn.memo.entity.MessageItem;

import java.util.List;

import jxl.write.WritableSheet;
import jxl.write.WriteException;

/**
 * com.donutcn.memo.interfaces
 * Created by 73958 on 2017/9/7.
 */

public interface OnWriteExcelListener {

    void onWriteExcel(WritableSheet sheet, List<MessageItem> list) throws WriteException;
}
