package app.database;

import app.system.TimeManage;

// for Thread
public class DatabaseSyncTask implements Runnable {
    private final DatabaseController controller;

    public DatabaseSyncTask(DatabaseController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        System.out.println(TimeManage.getTimeByString() + "[DatabaseSyncTask] start sync database...");
        controller.sync();  // request sync to controller
        System.out.println(TimeManage.getTimeByString() + "[DatabaseSyncTask] database sync complete.");
    }

    public static long calculateInitialDelayForSync() {
        long minutes = java.time.LocalTime.now().getMinute();
        long nextSyncInMinutes = (minutes % 10 == 5) ? 0 : (55 - minutes) % 10;
        System.out.println(TimeManage.getTimeByString() + "[DatabaseSyncTask] Remain Min for Next Database Sync: " + nextSyncInMinutes);
        return nextSyncInMinutes;
    }
}
