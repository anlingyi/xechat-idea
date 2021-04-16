package cn.xeblog.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author anlingyi
 * @date 2020/6/2
 */
public class DateUtils {

    private DateUtils() {

    }

    public static String getTime() {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(LocalDateTime.now());
    }

}
