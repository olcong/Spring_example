package app.database.history;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeeklyHistory {
    // field
    LocalDateTime start;    // started day
    LocalDateTime end;      // ended dat
    Long totalTime; // minute
    List<SessionHistory> sessionHistory;
    SessionHistory Latest;

    // Constructor
    public WeeklyHistory() {
        // for start new week (call changeWeek() )
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plusDays(7).with(DayOfWeek.MONDAY);
        start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 5, 0, 0);
        end = LocalDateTime.of(next.getYear(), next.getMonth(), next.getDayOfMonth(), 4, 0, 0);
        totalTime = 0L;
        sessionHistory = new ArrayList<>();
    }

//    public WeeklyHistory() {
//        // for start new week (call changeWeek() )
//        LocalDateTime now = LocalDateTime.now();
//        start = now.with(DayOfWeek.MONDAY).withHour(5).withMinute(0).withSecond(0);
//        end = start.plusWeeks(1).withHour(4).withMinute(0).withSecond(0);
//        totalTime = 0L;
//        sessionHistory = new ArrayList<>();
//    }

    public WeeklyHistory(LocalDateTime end) {
        // create on runtime
        start = LocalDateTime.now();
        this.end = end;
        totalTime = 0L;
        sessionHistory = new ArrayList<>();
    }

    public WeeklyHistory(LocalDateTime start, LocalDateTime end, long totalTime){
        // for read file
        this.start = start;
        this.end = end;
        this.totalTime = totalTime;
        this.sessionHistory = new ArrayList<>();
    }


    // Method
    public void addSessionData(SessionHistory sessionHistory){
        //for load record
        this.sessionHistory.add(sessionHistory);
    }

    public String toStringForSave() {
        // for save recored
        StringBuilder sb = new StringBuilder();
        sb.append(start.toLocalDate()).append(",") // startDay
                .append(end.toLocalDate()).append(",") // endDay
                .append(totalTime).append(",") // totalTime
                .append(sessionHistory.size())
                .append(",\t****** Weekly History ******")
                .append("\n"); // numOfSession

        for (SessionHistory session : sessionHistory) {
            sb.append(session.toStringForSave());
        }
        return sb.toString();
    }

    public String toStringForView(){
        StringBuilder sb = new StringBuilder();
        sb.append("[주간 출결기록] ")
                .append(start.toLocalDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                .append(" ~ ")
                .append(end.toLocalDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                .append(" - 총")
                .append(sessionHistory.size()).append("건, 합계 ")
                .append(totalTime).append("분\n\n");

        for(SessionHistory session : sessionHistory){
            sb.append(session.toStringForView());
        }
        sb.append("----------------------------------------------------------------------\n");

        return sb.toString();
    }




    // from SessionHistory.java
    public void closeSession(Long time){
        Latest.duration = time;
        totalTime += Latest.duration;

        Latest.closeSession();
        sessionHistory.add(Latest);

        Latest = null;
        System.gc();
    }


    // Getter & Setter
    public long getTotalTime() {
        return totalTime + (Latest != null ? Latest.duration : 0);
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
}
