package app;

import java.time.LocalTime;

public class ConfigValue {
    public static Integer timeToActivate = 150;    // Limit minute for activate

    public static LocalTime inspectionStart = LocalTime.of(4, 0, 0);
    public static LocalTime inspectionEnd = LocalTime.of(5, 0, 0);

    public static boolean hourSignalOn = true;



    public static boolean hourSignalSwitch(){
        if(hourSignalOn){
            hourSignalOn = false;
            return false;
        } else {
            hourSignalOn = true;
            return true;
        }
    }
}
