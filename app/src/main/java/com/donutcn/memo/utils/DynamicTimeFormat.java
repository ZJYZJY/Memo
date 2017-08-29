package com.donutcn.memo.utils;

import android.support.annotation.NonNull;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/1.
 */

public class DynamicTimeFormat extends SimpleDateFormat {

    private static Locale locale = Locale.CHINA;
    private static String weeks[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private static String moments[] = {"中午", "凌晨", "早上", "下午", "晚上"};

    private String mFormat = "%s";

    public DynamicTimeFormat() {
        this("%s", "yyyy年", "M月d日", "HH:mm");
    }

    public DynamicTimeFormat(String format) {
        this();
        this.mFormat = format;
    }

    public DynamicTimeFormat(String yearFormat, String dateFormat, String timeFormat) {
        super(String.format(locale, "%s %s %s", yearFormat, dateFormat, timeFormat), locale);
    }

    public DynamicTimeFormat(String format, String yearFormat, String dateFormat, String timeFormat) {
        this(yearFormat, dateFormat, timeFormat);
        this.mFormat = format;
    }

    @Override
    public StringBuffer format(@NonNull Date date, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
        toAppendTo = super.format(date, toAppendTo, pos);

        Calendar other = calendar;
        Calendar today = Calendar.getInstance();

        int hour = other.get(Calendar.HOUR_OF_DAY);
        /*
         * time[0] : 2017年
         * time[1] : 8月1日
         * time[2] : 17:13
         */
        String[] times = toAppendTo.toString().split(" ");
        /*
         * 0:00~6:00    凌晨
         * 6:00~12:00   早上
         * 12:00~13:00  中午
         * 13:00~18:00  下午
         * 18:00~24:00  晚上
         */
        String moment = hour == 12 ? moments[0] : moments[hour / 6 + 1];
        String timeFormat = moment + " " + times[2];
        // hour offset in one day.
        String timePassFormat = String.valueOf(today.get(Calendar.HOUR_OF_DAY) - hour);
        String dateFormat = times[1];
        String yearFormat = times[0];
        toAppendTo.delete(0, toAppendTo.length());

        boolean yearTemp = today.get(Calendar.YEAR) == other.get(Calendar.YEAR);
        // same year
        if (yearTemp) {
            int todayMonth = today.get(Calendar.MONTH);
            int otherMonth = other.get(Calendar.MONTH);
            // same month or last week
            if (todayMonth == otherMonth || todayMonth - otherMonth == 1) {
                int temp;
                if(todayMonth == otherMonth){
                    temp = today.get(Calendar.DATE) - other.get(Calendar.DATE);
                }else {
                    temp = today.get(Calendar.DAY_OF_YEAR) - other.get(Calendar.DAY_OF_YEAR);
                }
                switch (temp) {
                    case 0:
                        if(Integer.valueOf(timePassFormat) == 0){
                            int min = today.get(Calendar.MINUTE) - other.get(Calendar.MINUTE);
                            if(min == 0){
                                toAppendTo.append("刚刚");
                            }else {
                                toAppendTo.append(String.valueOf(min));
                                toAppendTo.append("分钟前");
                            }
                        }else {
                            toAppendTo.append(timePassFormat);
                            toAppendTo.append("小时前");
                        }
                        break;
                    case 1:
                        toAppendTo.append("昨天 ");
//                        toAppendTo.append(timeFormat);
                        break;
                    case 2:
                        toAppendTo.append("前天 ");
//                        toAppendTo.append(timeFormat);
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = other.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth = today.get(Calendar.WEEK_OF_MONTH);
                        // same week
                        if (dayOfMonth == todayOfMonth) {
                            int dayOfWeek = other.get(Calendar.DAY_OF_WEEK);
                            if (dayOfWeek != 1) {//判断当前是不是星期日
                                toAppendTo.append(weeks[other.get(Calendar.DAY_OF_WEEK) - 1]);
                                toAppendTo.append(' ');
                                toAppendTo.append(timeFormat);
                            } else {
                                toAppendTo.append(dateFormat);
                            }
                        } else {
                            toAppendTo.append(dateFormat);
                        }
                        break;
                    default:
                        toAppendTo.append(dateFormat);
                        break;
                }
            } else {
                toAppendTo.append(dateFormat);
            }
        } else {
            toAppendTo.append(yearFormat);
        }

        int length = toAppendTo.length();
        toAppendTo.append(String.format(locale, mFormat, toAppendTo.toString()));
        toAppendTo.delete(0, length);
        return toAppendTo;
    }
}
