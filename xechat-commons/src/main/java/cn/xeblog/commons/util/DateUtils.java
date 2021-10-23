package cn.xeblog.commons.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author anlingyi
 * @date 2020/6/2
 */
public class DateUtils {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    private DateUtils() {

    }

    public static String getTime() {
        return FORMATTER.format(LocalDateTime.now());
    }

}
