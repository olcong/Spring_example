package app.database.history;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;

public class UserHistory {
    WeeklyHistory thisWeek;
    LinkedList<WeeklyHistory> prevWeeks;

    // constructor
    public UserHistory() {
        // for runtime
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonday = now.getDayOfWeek() == DayOfWeek.MONDAY
                ? now.plusWeeks(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY)) // if today is monday
                : now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)); // else

        thisWeek = new WeeklyHistory(
                nextMonday.withHour(5).withMinute(0).withSecond(0).withNano(0) // setEndTime = next monday 5 AM
        );

        prevWeeks = new LinkedList<>();
    }

    public UserHistory(WeeklyHistory thisWeek, LinkedList<WeeklyHistory> prevWeeks){
        // for load record
        this.thisWeek = thisWeek;
        this.prevWeeks = prevWeeks;
    }

    //field
    private static int retentionLimit = 7;   // save 7 week


    //method
    public void changeWeek(){
        while(prevWeeks.size()>=retentionLimit) prevWeeks.poll();     // remove old data
        prevWeeks.offer(thisWeek);

        thisWeek = new WeeklyHistory();
    }

    public String toStringForSave(Long userId) {
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(",").append(prevWeeks.size() + 1).append("\n"); // id, numOfDataset

        sb.append(thisWeek.toStringForSave());
        for (WeeklyHistory week : prevWeeks) {
            sb.append(week.toStringForSave());
        }

        return sb.toString();
    }

    public String toStringForView(){
        StringBuilder sb = new StringBuilder();

        sb.append("※ 참고: 현재 세션을 제외한 과거 기록만 조회 가능합니다.\n");
        sb.append("출결 기록 분량: ").append(prevWeeks.size() + 1).append("주\n\n");

        sb.append("이번 주 출결 내역: \n");
        sb.append("============================================\n");
        sb.append(thisWeek.toStringForView());

        sb.append("\n이전 출결 내역 (시간순): \n");
        sb.append("============================================\n");
        if(prevWeeks.isEmpty()) sb.append("\n기록 없음\n");
        for (WeeklyHistory week : prevWeeks) {
            sb.append(week.toStringForView());
        }

        return sb.toString();
    }


    public static void setRetentionLimit(int retentionLimit) {UserHistory.retentionLimit = retentionLimit;}
}
