package app;
import app.database.DatabaseController;
import app.database.DatabaseSyncTask;
import app.mainFrame.MainFrame;
import app.media.SoundPlayer;
import app.media.TimeSignalTask;
import app.system.PreventSleep;
import app.userSession.SessionManage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    static MainFrame mainFrame;
    static DatabaseController controller;
    static SessionManage sessionManage;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    public static void main(String[] args) {
        sessionManage = new SessionManage();
        controller = new DatabaseController(sessionManage);

        // Start Main Frame
        mainFrame = new MainFrame(controller);

        // Database Sync Thread
        scheduler.scheduleAtFixedRate(new DatabaseSyncTask(controller), DatabaseSyncTask.calculateInitialDelayForSync(), 10, TimeUnit.MINUTES);

        // Hour Signal Sound Thread
        scheduler.scheduleAtFixedRate(new TimeSignalTask(), TimeSignalTask.getInitialDelay(0, -12), 3600, TimeUnit.SECONDS);

        // Prevent Sleep Mode
        scheduler.scheduleAtFixedRate(PreventSleep::preventSleepMode, 0, 1, TimeUnit.MINUTES);
    }

    public static SessionManage getSessionManage() {return sessionManage;}

}
