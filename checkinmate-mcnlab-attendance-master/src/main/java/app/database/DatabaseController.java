package app.database;

import app.database.history.HistoryManage;
import app.system.ResponseCode;
import app.system.TimeManage;
import app.userSession.SessionManage;

import java.sql.Time;

public class DatabaseController {
    public static SessionManage sessionManage;
    public static HistoryManage historyManage;
    public static FileManage fileManage;
    ResponseCode code;

    /* Responsibilities of this class:
     *
     * This class will be used to write JPA code for each request when RDB is connected in the future.
     * You can remove file-based code and change it to database control code.
     */

    public ResponseCode submit(Long id) {
        // Submit request -> Session Check -> Write Log (file -> DB)

        // Request
        code = sessionManage.submit(id);


        switch (code.getCode()) {
            // TOOD - Record SubmitHistory at User List with ResponseCode
            case 211:
                // First Submit
                HistoryManage.createSession(id);
                break;
            case 212:
                // Extend Submit
                HistoryManage.extendSession(id);
                break;
            case 213:
                // Renewed Submit
                HistoryManage.closeSession(id, SessionManage.getThisSessionTime(id));
                HistoryManage.createSession(id);
                break;
            case 404:
                // Can't Find User (Not Found)
                return ResponseCode.NotFound;
            default:
                //ignore
        }
        return ResponseCode.OK;
    }

    public void sync() {
        sessionManage.refreshSession();
        //TODO - for future (DB Sync)
    }

    public void saveUserInfo(){
        fileManage.exportUsers();
    }

    public void saveStatus(){
        fileManage.saveStatus();
    }

    // Constructor
    public DatabaseController() {
        this(null);
    }

    public DatabaseController(SessionManage sessionManage) {
        DatabaseController.sessionManage = sessionManage;
        historyManage = new HistoryManage();
        fileManage = new FileManage(historyManage);
    }
}
