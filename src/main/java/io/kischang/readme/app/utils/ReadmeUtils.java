package io.kischang.readme.app.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class ReadmeUtils {

    public static String dateTime() {
        return dateTime(new Date());
    }

    public static String dateTime(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

}
