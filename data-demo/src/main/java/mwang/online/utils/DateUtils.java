package mwang.online.utils;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author mwangli
 * @date 2022/3/28 10:01
 */
public class DateUtils {

    public static String format(Long timestamp, String pattern) {
        return FastDateFormat.getInstance(pattern).format(timestamp);
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            return FastDateFormat.getInstance(pattern).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前日期加目标天数的日期
     * Example: 昨天(-1)，明天(+1)，上周(-7)，30天内(-30)
     */
    public static Date getNextDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        Date parse = parse("2022-01-01", "yyyy-MM-dd");
        assert parse != null;
        calendar.setTime(parse);
        while (calendar.get(Calendar.YEAR)<=2022){
            StringBuilder stringBuilder = new StringBuilder();
            String date = format(calendar.getTime().getTime(), "yyyy-MM-dd");
            stringBuilder.append(date).append("\t");
            int YEAR = calendar.get(Calendar.YEAR);
            stringBuilder.append(YEAR).append("\t");
            int MONTH = calendar.get(Calendar.MONTH) +1;
            stringBuilder.append(MONTH).append("\t");
            int DATE = calendar.get(Calendar.DATE);
            stringBuilder.append(DATE).append("\t");
            int DAY_OF_WEEK = calendar.get(Calendar.DAY_OF_WEEK)-1;
            DAY_OF_WEEK = DAY_OF_WEEK == 0? 7:DAY_OF_WEEK;
            stringBuilder.append(DAY_OF_WEEK).append("\t");
            int DAY_OF_YEAR = calendar.get(Calendar.DAY_OF_YEAR);
            stringBuilder.append(DAY_OF_YEAR).append("\t");
            int WEEK_OF_YEAR = calendar.get(Calendar.WEEK_OF_YEAR);
            stringBuilder.append(WEEK_OF_YEAR).append("\t");
            stringBuilder.append((MONTH /3)+1).append("\t");
            stringBuilder.append(DAY_OF_WEEK>=6?0:1).append("\t");
            stringBuilder.append(0).append("\t");
            System.out.println(stringBuilder);
            calendar.add(Calendar.DATE,1);
        }
    }
}
