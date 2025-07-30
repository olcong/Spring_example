package app.system;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PreventSleep {

    public static void preventSleepMode() {
        try {
            Robot robot = new Robot();

            // Shift 키 누르고 떼기 (실제 입력 영향 없음)
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_SHIFT);

            System.out.println(TimeManage.getTimeByString() + "[PreventSleep] Preventing sleep mode with keyboard input");
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
