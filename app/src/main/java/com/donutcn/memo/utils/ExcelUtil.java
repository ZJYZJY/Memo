package com.donutcn.memo.utils;

import android.content.Context;
import android.os.StatFs;

import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.interfaces.OnWriteExcelListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * com.donutcn.memo
 * Created by 73958 on 2017/9/7.
 */

public class ExcelUtil {

    // 内存地址
    public static String root = getExternalStorageDirectory().getPath();

    private Context mContext;
    private String[] mLabels;
    private String mSheetName;
    private String mFileName;
    private List<MessageItem> mData;
    private OnWriteExcelListener mListener;

    public static ExcelUtil with(Context context){
        return new ExcelUtil(context);
    }

    public ExcelUtil(Context context){
        this.mContext = context;
    }

    public ExcelUtil setSheetName(String sheetName){
        this.mSheetName = sheetName;
        return this;
    }

    public ExcelUtil setLabels(String[] labels){
        this.mLabels = labels;
        return this;
    }

    public ExcelUtil setFileName(String fileName){
        this.mFileName = fileName;
        return this;
    }

    public ExcelUtil setData(List<MessageItem> data){
        this.mData = data;
        return this;
    }

    public ExcelUtil setOnWriteExcelListener(OnWriteExcelListener listener){
        this.mListener = listener;
        return this;
    }

    public ExcelUtil build() throws Exception {
//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
//                && getAvailableStorage() > 1000000) {
//            ToastUtil.show(mContext, "SD卡不可用");
//            return null;
//        }
        File file;
        File dir = new File(getExternalStorageDirectory() + "/com.donutcn.memo/export/");
        file = new File(dir, mFileName + ".xls");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet(mSheetName, 0);
        Label label;
        for (int i = 0; i < mLabels.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, mLabels[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }
        mListener.onWriteExcel(sheet, mData);

        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
        return this;
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    /** 获取SD可用容量 */
    private static long getAvailableStorage() {

        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }
}
