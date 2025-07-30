package app.system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeManage {
    public static LocalDateTime getNow(){
        return LocalDateTime.now();
    }

    public static String getTimeByString(){
        return getNow().format(DateTimeFormatter.ofPattern("HH:mm:ss "));
    }
    public static String getTimeByString(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss "));
    }


    public static String getDateTimeByString(){
        return getNow().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss "));
    }
    public static String getDateTimeByString(LocalDateTime date){
        return date.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss "));
    }
}
