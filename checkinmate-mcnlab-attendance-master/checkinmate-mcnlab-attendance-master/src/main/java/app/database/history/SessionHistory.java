package app.database.history;

import app.system.TimeManage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SessionHistory {
    Long id;
    LocalDateTime startTime;
    LocalDateTime endTime;
    List<String> checkHistory;
    long duration;

    // Constructor
    public SessionHistory(Long id) {
        // for runtime
        this(id, LocalDateTime.now(), LocalDateTime.now());
        checkHistory.add(TimeManage.getDateTimeByString()+"New Session");
    }

    //for load record
    public SessionHistory(Long id, LocalDateTime start, LocalDateTime end) {
        // for record file load
        this.id = id;
        this.startTime = start;
        this.endTime = end;
        this.checkHistory = new ArrayList<>();
    }
    public void addCheckData(String s){
        checkHistory.add(s);
    }


    public String toStringForSave() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append(startTime.toLocalDate()).append(",") // startDay
                .append(startTime.toLocalTime().format(timeFormatter)).append(",") // startTime
                .append(endTime.toLocalDate()).append(",") // endDay
                .append(endTime.toLocalTime().format(timeFormatter)).append(",") // endTime
                .append(checkHistory.size()).append("\n"); // numOfCheck

        for (String check : checkHistory) {
            sb.append(check).append("\n");
        }
        return sb.toString();
    }

    public String toStringForView(){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("[세션 기록] ")
                .append(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss").format(startTime))
                .append(" ~ ")
                .append(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss").format(endTime))
                .append(" - 총 ").append(checkHistory.size()).append("건 기록:\n");

        for(String check : checkHistory){
            sb.append("\t").append(check).append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }



    // Method for runtime
    public void addCheckPoint(){
        checkHistory.add(TimeManage.getDateTimeByString() + "Session Extend");
        endTime = LocalDateTime.now();
        duration = Duration.between(startTime, endTime).toMinutes();
    }


    public void closeSession(){
        closeSession(LocalDateTime.now());
    }
    public void closeSession(LocalDateTime time){
        checkHistory.add(TimeManage.getDateTimeByString(time) + "Session Expired");
        duration = Duration.between(startTime, endTime).toMinutes();
    }


}
