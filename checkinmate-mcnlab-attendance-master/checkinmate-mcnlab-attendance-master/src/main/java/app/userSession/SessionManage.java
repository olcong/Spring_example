package app.userSession;

import app.database.UserList;
import app.database.history.HistoryManage;
import app.mainFrame.MainFrame;
import app.system.ResponseCode;
import app.system.TimeManage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManage {
    // Data Structure
    static ConcurrentHashMap<Long, SessionEntity> session;

    // config value
    static int expireTime = 150;    //minute
    static long defaultAttendance = 30L;

    // submit request
    static public boolean isContain(Long id){
        return session.containsKey(id);
    }

    public ResponseCode submit(Long id){
        // valid user check
        if(!UserList.isValidUser(id)){
            System.out.println(TimeManage.getTimeByString()+" [Submit] Not Exist User");
            return ResponseCode.NotFound;
        }

        // if valid
        ResponseCode code;
        SessionEntity se;

        // case 1: already exist user - extend session
        if(isContain(id)){
            se = session.get(id);

            // case 1-1: session expired
            if(se.isActiveFired()){
                se = new SessionEntity(id);
                se.Activate();
                session.put(id, se);

                code = ResponseCode.RenewedSubmit;  // 213
            }
            // case 1-2: valid session
            else {
                se.extendActive();
                session.put(id, se);

                code = ResponseCode.ExtendSubmit;   // 212
            }
        }
        // case2: first check - new session
        else{
            se = new SessionEntity(id);
            se.Activate();
            session.put(id, se);

            code = ResponseCode.FirstSubmit;        // 211
        }

        // refresh mainFrame
        MainFrame.setSignedIn(id);
        return code;
    }


    // refresh session ( 10min interval refresh and expire old session )
    public void refreshSession() {
        LocalDateTime now = LocalDateTime.now();
        Iterator<Map.Entry<Long, SessionEntity>> iterator = session.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, SessionEntity> entry = iterator.next();
            Long userId = entry.getKey();
            SessionEntity se = entry.getValue();

            if (se.isActiveFired()) {
                // save at HistoryManage
                HistoryManage.closeSession(userId, getThisSessionTime(userId));

                // remove session
                iterator.remove();
                System.out.println(TimeManage.getTimeByString() +
                        " [refreshSession] Expired session removed for user " +
                        userId + ": " + UserList.getName(userId));
            }
        }
    }

    // before reboot (AM 4:00)
    public void shutdownSessions() {
        System.out.println(TimeManage.getTimeByString() + " [shutdown] Saving active sessions...");

        for (Map.Entry<Long, SessionEntity> entry : session.entrySet()) {
            Long userId = entry.getKey();
            SessionEntity se = entry.getValue();

            // ✅ 세션 기록을 HistoryManage에 저장
            HistoryManage.closeSession(userId, getThisSessionTime(userId));
        }

        // ✅ 모든 세션 제거
        session.clear();
        System.out.println(TimeManage.getTimeByString() + " [shutdown] All sessions terminated before reboot.");
    }


    public void setExpireTime(int t){expireTime = t;}

    // Getter
    public static String getFirstAttend(Long id){return session.get(id).getFirstCheck().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));}
    public static String getLastAttend(Long id){return session.get(id).getLastCheck().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));}
    public static Long getThisSessionTime(Long id){return session.get(id).getTimeOfSession();}


    public SessionManage() {
        session = new ConcurrentHashMap<>();
    }
}

// Entity for Session Manage - Package Protected
class SessionEntity {
    Long id;
    String name;

    LocalDateTime FirstCheck;
    LocalDateTime LastCheck;

    Long timeOfSession;    // attendance time in this session

    public void Activate() {
        FirstCheck = LocalDateTime.now();
        LastCheck = LocalDateTime.now();
        timeOfSession = SessionManage.defaultAttendance;
    }
    public void extendActive() {
        LastCheck = LocalDateTime.now();
        timeOfSession = Duration.between(FirstCheck, LastCheck).toMinutes();
    }

    public boolean isActiveFired() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(LastCheck, now);
        return duration.toMinutes() > SessionManage.expireTime;
    }



    // Constructor
    public SessionEntity() {this(0L);}

    public SessionEntity(Long id) {
        this.id = id;
        timeOfSession = SessionManage.defaultAttendance;
    }


    // Getter & Setter
    public LocalDateTime getFirstCheck() {return FirstCheck;}
    public LocalDateTime getLastCheck() {return LastCheck;}
    public Long getTimeOfSession() {return timeOfSession;}
}
