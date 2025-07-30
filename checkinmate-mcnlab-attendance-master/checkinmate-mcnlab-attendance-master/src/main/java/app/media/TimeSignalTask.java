package app.media;

import app.ConfigValue;
import app.system.TimeManage;

import java.time.Duration;
import java.time.LocalTime;

// new Thread
public class TimeSignalTask implements Runnable {

    @Override
    public void run() {
        int hour = LocalTime.now().getHour() + 1; // 현재 시각 (0~23)
        if(hour > 12) hour %= 12;

        if(ConfigValue.hourSignalOn) {
            String soundFile = String.format("hour_signal_%02d.wav", hour); // 파일명 자동 생성
            String soundFile2 = String.format("mbctimer.wav", hour); // 파일명 자동 생성

            System.out.println(TimeManage.getTimeByString() + "[TimeSignalTask] " + hour + ":00 play signal: " + soundFile);
            SoundPlayer.playSound(soundFile);
            SoundPlayer.playSound(soundFile2);
        }
    }

    // 현재 시간 기준으로 다음 정각까지 남은 시간 계산 (Main에서 호출)
    public static long getInitialDelay(int targetMinute, int offsetSeconds) {
        LocalTime now = LocalTime.now();
        LocalTime nextRunTime = now.withMinute(targetMinute).withSecond(0);
        nextRunTime = nextRunTime.plusSeconds(offsetSeconds);


        // 현재 시간이 이미 목표 분(targetMinute)을 지났다면 다음 시간으로 이동
        if (now.isAfter(nextRunTime)) {
            nextRunTime = nextRunTime.plusHours(1);
        }

        long delaySeconds = Duration.between(now, nextRunTime).getSeconds();

        System.out.println(TimeManage.getTimeByString() + "[TimeSignalTask] Next Signal Time: " + nextRunTime);
        System.out.println(TimeManage.getTimeByString() + "[TimeSignalTask] Remain Sec for Next Signal: " + delaySeconds);

        return delaySeconds;
    }
}
