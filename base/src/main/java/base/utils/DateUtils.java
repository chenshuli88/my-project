package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 14:44
 * @description
 * @since 2.8.1
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    protected static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    public static final String FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_MONTH = "yyyy-MM";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_SHORT_DATE_TIME = "MM-dd HH:mm";
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_NO_SECOND = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_JAPAN = "MM.dd(EEE) HH";
    public static final String FORMAT_CHINESE_NO_SECOND = "yyyy年MM月dd日 HH:mm";
    public static final String FORMAT_CHINESE_NO_SECOND_1 = "yyyy年MM月dd日HH:mm";
    public static final String FORMAT_CHINESE = "yyyy年MM月dd日 HH点mm分";
    public static final int TYPE_HTML_SPACE = 2;
    public static final int TYPE_DECREASE_SYMBOL = 3;
    public static final int TYPE_SPACE = 4;
    public static final int TYPE_NULL = 5;
    private static Map<String, SimpleDateFormat> formaters = new HashMap();

    public DateUtils() {
    }

    public static Map<String, SimpleDateFormat> getFormaters() {
        return formaters;
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat dateFormat;
        if (formaters.containsKey(pattern)) {
            dateFormat = (SimpleDateFormat)formaters.get(pattern);
        } else {
            dateFormat = new SimpleDateFormat(pattern);
        }

        return dateFormat.format(date);
    }

    public static String format(Date date) {
        return ((SimpleDateFormat)formaters.get("yyyy-MM-dd HH:mm:ss")).format(date);
    }

    public static String format(Date date, String format, int type) {
        if (date != null) {
            try {
                return (new SimpleDateFormat(format)).format(date);
            } catch (Exception var4) {
                return "";
            }
        } else {
            switch(type) {
                case 2:
                    return "&nbsp;";
                case 3:
                    return "-";
                case 4:
                    return " ";
                case 5:
                    return null;
                default:
                    return "";
            }
        }
    }

    public static Date parse(String str) {
        Date date = null;
        Iterator var2 = formaters.keySet().iterator();

        while(var2.hasNext()) {
            String _pattern = (String)var2.next();
            if (_pattern.getBytes().length == str.getBytes().length) {
                try {
                    date = ((SimpleDateFormat)formaters.get(_pattern)).parse(str);
                    break;
                } catch (ParseException var5) {
                    logger.debug("尝试将日期:" + str + "以“" + _pattern + "”格式化--失败=.=!");
                }
            } else if (_pattern.equals("MM.dd(EEE) HH")) {
                try {
                    date = ((SimpleDateFormat)formaters.get(_pattern)).parse(str);
                    break;
                } catch (ParseException var6) {
                    ;
                }
            }
        }

        return date;
    }

    public static Date parse(String str, String pattern) {
        SimpleDateFormat dateFormat;
        if (formaters.containsKey(pattern)) {
            dateFormat = (SimpleDateFormat)formaters.get(pattern);
        } else {
            dateFormat = new SimpleDateFormat(pattern);
        }

        Date date = null;

        try {
            date = dateFormat.parse(str);
        } catch (ParseException var5) {
            logger.debug("尝试将日期:" + str + "以“" + pattern + "”格式化--失败=.=!");
        }

        return date;
    }

    public static boolean isAfter(Date date1, Date date2) {
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar2.setTime(date2);
        calendar1.setTime(date1);
        return calendar2.after(calendar1);
    }

    public static void main(String[] args) {
        String sdate = "2010-08-19 08:40:04";
        Date d = parse(sdate);
        System.out.println("DateUtils.format(d) = " + format(d));
    }

    public static String getNowTime() {
        return format(new Date());
    }

    static {
        SimpleDateFormat defaultFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        formaters.put("yyyy-MM-dd HH:mm:ss", defaultFormater);
        formaters.put("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        formaters.put("yyyy-MM", new SimpleDateFormat("yyyy-MM", Locale.CHINA));
        formaters.put("HH:mm:ss", new SimpleDateFormat("HH:mm:ss", Locale.CHINA));
        formaters.put("MM-dd HH:mm", new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA));
        formaters.put("yyyy年MM月dd日 HH:mm", new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA));
        formaters.put("yyyy年MM月dd日 HH点mm分", new SimpleDateFormat("yyyy年MM月dd日 HH点mm分", Locale.CHINA));
        formaters.put("yyyy-MM-dd HH:mm:ss", defaultFormater);
        formaters.put("yyyy-MM-dd HH:mm", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA));
        formaters.put("MM.dd(EEE) HH", new SimpleDateFormat("MM.dd(EEE) HH", Locale.JAPAN));
        formaters.put("yyyy年MM月dd日HH:mm", new SimpleDateFormat("yyyy年MM月dd日HH:mm", Locale.CHINA));
    }
}
