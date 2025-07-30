package app.database.history;

import app.system.ResponseCode;
import app.system.TimeManage;
import app.userSession.SessionManage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HistoryManage {
    private static Map<Long, UserHistory> historyMap;        // id, UserHistory
    private LocalDateTime lastSavedTime;

    public HistoryManage(){
        historyMap = new HashMap<>();
    }


    // method for load record
    public ResponseCode loadHistory(BufferedReader reader){
        Integer numOfUser, numOfDataset, numOfSession, numOfCheck;
        Long totalTime;
        LocalDateTime start, end;
        Long id;

        String tmp;
        String[] historyData, userData, weekData, sessionData, time, day;

        UserHistory userHistory;
        WeeklyHistory tmpWeek, thisWeek;
        LinkedList<WeeklyHistory> previous;
        SessionHistory sessionHistory;

        try {
            tmp = reader.readLine(); // num Of User, LastSavedDate, LastSavedTime
            if(tmp!=null){
                historyData = tmp.split(",");
                numOfUser = Integer.parseInt(historyData[0]);
                day = historyData[1].split("-");
                time = historyData[2].split(":");
                lastSavedTime = LocalDateTime.of(
                        Integer.parseInt(day[0]), Integer.parseInt(day[1]), Integer.parseInt(day[2]),
                        Integer.parseInt(time[0]), Integer.parseInt(time[1]));
            }else{
                lastSavedTime = LocalDateTime.of(1990,1,1,0,0);
                return ResponseCode.NotFound;
            }
            for(int u=0; u<numOfUser; u++){
                tmp = reader.readLine();    // id, numOfDataset
                userData = tmp.split(",");

                id = Long.parseLong(userData[0]);
                numOfDataset = Integer.parseInt(userData[1]);

                // Create Weekly History Instance
                thisWeek = null;
                previous = new LinkedList<>();

                for(int d=0; d<numOfDataset; d++){
                    tmp = reader.readLine();    // startDay, endDay, totalTime, numOfSession
                    weekData = tmp.split(",");

                    time = weekData[0].split("-");
                    start = LocalDateTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]), 5, 0, 0);

                    time = weekData[1].split("-");
                    end = LocalDateTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]), 4, 0, 0);

                    totalTime = Long.parseLong(weekData[2]);
                    numOfSession = Integer.parseInt(weekData[3]);

                    // Insert WeeklyHistory Data
                    tmpWeek = new WeeklyHistory(start, end, totalTime);

                    for(int s=0; s<numOfSession; s++){
                        tmp = reader.readLine();    // startDay, startTime, endDay, endTime, numOfCheck
                        sessionData = tmp.split(",");

                        day = sessionData[0].split("-");
                        time = sessionData[1].split(":");
                        start = LocalDateTime.of(Integer.parseInt(day[0]), Integer.parseInt(day[1]), Integer.parseInt(day[2]),
                                                Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

                        day = sessionData[2].split("-");
                        time = sessionData[3].split(":");
                        end = LocalDateTime.of(Integer.parseInt(day[0]), Integer.parseInt(day[1]), Integer.parseInt(day[2]),
                                Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

                        numOfCheck = Integer.parseInt(sessionData[4]);

                        // Create SessionHistory Instance
                        sessionHistory = new SessionHistory(id, start, end);

                        for(int c=0; c<numOfCheck; c++){
                            tmp = reader.readLine();    // check data
                            sessionHistory.addCheckData(tmp);
                        }

                        tmpWeek.addSessionData(sessionHistory);

                    } // end of loop - numOfSession

                    if(d==0) thisWeek = tmpWeek;
                    else previous.add(tmpWeek);
                } // end of loop - numOfDataset

                userHistory = new UserHistory(thisWeek, previous);
                historyMap.put(id, userHistory);

            } // end of loop - numOfUser

            System.out.println(TimeManage.getTimeByString()+"Load Submit Record Successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseCode.OK;
    }

    public void saveHistory(BufferedWriter writer) {
        try {
            writer.write(historyMap.size()+","); // numOfUser
            writer.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm"))+"\n"); //lastSaved

            for (Map.Entry<Long, UserHistory> entry : historyMap.entrySet()) {
                Long userId = entry.getKey();
                UserHistory userHistory = entry.getValue();
                writer.write(userHistory.toStringForSave(userId));
            }

            System.out.println(TimeManage.getTimeByString() + " Save Submit Record Successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isWeekChanged() {       // created by Chat-GPT
        // 기준 시각 계산: 현재 시간(now)을 기준으로 이번 주 월요일 4시를 구함
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisWeekStart = now
                .with(java.time.DayOfWeek.MONDAY)
                .withHour(4).withMinute(0).withSecond(10).withNano(0);

        // 만약 현재 시간이 월요일 0시~3시 59분이라면, 기준 시각은 지난 주 월요일로 이동
        if (now.getDayOfWeek() == java.time.DayOfWeek.MONDAY && now.getHour() < 4) {
            thisWeekStart = thisWeekStart.minusWeeks(1);
        }

        // lastSavedTime 이 기준 시각보다 이전이면, 주가 바뀐 것
        return lastSavedTime.isBefore(thisWeekStart);
    }


    // method for view status
    public static String getHistory(Long id){
        UserHistory tmp = historyMap.get(id);
        return tmp.toStringForView();
    }


    // method for runtime
    public static ResponseCode createSession(Long id){
        if(historyMap.containsKey(id)) {
            UserHistory tmp = historyMap.get(id);
            tmp.thisWeek.Latest = new SessionHistory(id);
            historyMap.put(id, tmp);

        } else {
            UserHistory tmpUser = new UserHistory();
            tmpUser.thisWeek.Latest = new SessionHistory(id);
            historyMap.put(id, tmpUser);
        }
        return ResponseCode.Created;
    }
    public static ResponseCode extendSession(Long id){
        historyMap.get(id).thisWeek.Latest.addCheckPoint();

        return ResponseCode.ExtendSubmit;
    }
    public static ResponseCode closeSession(Long id, Long time){
        historyMap.get(id).thisWeek.closeSession(time);
        return ResponseCode.OK;
    }




    // from UserHistory
    public void changeWeek(){
        historyMap.forEach((k,v)->v.changeWeek());
        lastSavedTime = LocalDateTime.now();
    }

    public Long getThisWeekTotal(Long id){
        if(historyMap.containsKey(id))
            return historyMap.get(id).thisWeek.getTotalTime() + SessionManage.getThisSessionTime(id);
        else
            return 0L;
    }
}
