package com.ppdai.das.console.common.utils;


import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangliang on 2018/7/12.
 */
public class DateUtil {

    public static String DATE_FORMAT_INT = "yyyyMMdd";
    public static String DATE_FORMAT_BASE = "yyyy-MM-dd";
    public static String TIME_FORMAT_DETAIL = "yyyy-MM-dd HH:mm:ss";

    final private static ConcurrentHashMap<String, SimpleDateFormat> simpleDateFormatMap = new ConcurrentHashMap<String, SimpleDateFormat>() {{
        put(DATE_FORMAT_INT, new SimpleDateFormat(DATE_FORMAT_INT));
        put(DATE_FORMAT_BASE, new SimpleDateFormat(DATE_FORMAT_BASE));
        put(TIME_FORMAT_DETAIL, new SimpleDateFormat(TIME_FORMAT_DETAIL));
    }};

    /**
     * 返回一个SimpleDateFormat
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        SimpleDateFormat sdf = simpleDateFormatMap.get(pattern);
        if (null == sdf) {
            sdf = new SimpleDateFormat(pattern);
            simpleDateFormatMap.putIfAbsent(pattern, sdf);
        }
        return sdf;
    }

    final public static int str2Int(String fromat, String time) {
        SimpleDateFormat df = DateUtil.getSdf(fromat);//yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = DateUtil.getSdf(DATE_FORMAT_INT);
        Calendar cal = Calendar.getInstance();
        try {
            if (null != time && time.length() > 5) {
                Date date = df.parse(time);
                cal.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Integer.valueOf(sdf.format(cal.getTime()));
    }

    /**
     * 将日期格式的字符串转换为长整型
     *
     * @param date
     * @param format
     * @return
     */
    final public static long convert2long(String date, String format) {
        try {
            if (StringUtils.isNotBlank(date)) {
                if (StringUtils.isBlank(format)){
                    format = TIME_FORMAT_DETAIL;
                }
                SimpleDateFormat sf = DateUtil.getSdf(format);
                return sf.parse(date).getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    /**
     * 将长整型数字转换为日期格式的字符串
     *
     * @param time
     * @param format
     * @return
     */
    final public static String convert2String(long time, String format) {
        if (time > 0L) {
            if (StringUtils.isBlank(format)){
                format = TIME_FORMAT_DETAIL;
            }
            SimpleDateFormat sf = DateUtil.getSdf(format);
            return sf.format(new Date(time));
        }
        return "";
    }

    /**
     * 获取当前系统的日期
     *
     * @return
     */
    final public static long curTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * String转换成Calendar
     *
     * @param time
     * @return
     */
    final public static Calendar str2Calendar(String time, String format) {
        Calendar cal = Calendar.getInstance();
        if (time != null) {
            try {
                cal.setTime(DateUtil.getSdf(format).parse(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cal;
    }

    /**
     * Calendar转换成String , 格式化输出日期
     *
     * @param a
     * @return
     */
    final public static String calendar2Str(Calendar a, String format) {
        return DateUtil.getSdf(format).format(a.getTime());
    }

    /**
     * 获取时间戳
     */
    final public static String getTimeStemp() {
        return convert2String(curTimeMillis(), TIME_FORMAT_DETAIL);
    }

    /**
     * 获取时间戳
     */
    final public static String getCurrentTime() {
        return convert2String(curTimeMillis(), "yyyyMMddHHmmss");
    }

    /**
     * 示例函数
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /*System.out.println(SimpleDateUtil.convert2long("2000-01-01 01:01:01", SimpleDateUtil.DATE_FORMAT));
        System.out.println(SimpleDateUtil.convert2String(SimpleDateUtil.curTimeMillis(), SimpleDateUtil.TIME_FORMAT));*/

        long time = 1503469593673L;

        System.out.println(convert2String(time, "yyyy-MM-dd HH:mm:ss"));

        System.out.println(convert2String(curTimeMillis(), "yyyyMM-ddHHmmss"));
        /*
        Calendar a = str2Calendar("20170630", DATE_FORMAT_INT);
        for (int i = 0; i < 50; i++) {
            a.replace(Calendar.DAY_OF_YEAR, -1);
            System.out.println(calendar2Str(a, DATE_FORMAT_BASE));
        }
        System.out.println(str2Int("yyyy-MM-dd", "2017-1-24"));*/
    }
}
