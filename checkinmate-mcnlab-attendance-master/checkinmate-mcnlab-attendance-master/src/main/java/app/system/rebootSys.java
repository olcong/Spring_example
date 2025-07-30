package app.system;

import java.io.IOException;

public class rebootSys {



    public static void rebootRequest(){
        try{
            Process process = Runtime.getRuntime().exec("sudo reboot");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
