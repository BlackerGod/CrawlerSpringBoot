package ZMQ.crawlerUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String toDay() {
        String format = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }
}
