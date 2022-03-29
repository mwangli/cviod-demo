package mwang.online.utils;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author mwangli
 * @date 2022/3/28 10:01
 */
public class DateUtils {
    public static String format(Long timestamp, String pattern) {
        return FastDateFormat.getInstance(pattern).format(timestamp);
    }
}
