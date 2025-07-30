package app.media;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

public class SoundPlayer {
    public static void playSound(String fileName) {
        try {
            // 프로젝트 내 resources/signal 폴더에 있는 파일 경로 지정
            InputStream audioStream = SoundPlayer.class.getClassLoader().getResourceAsStream("signal/" + fileName);

            if (audioStream == null) {
                System.err.println("[SoundPlayer] 파일을 찾을 수 없음: " + fileName);
                return;
            }

            BufferedInputStream bufferedInputStream = new BufferedInputStream(audioStream);

            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedInputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();

            // **클립이 제대로 실행되도록 약간의 대기 (안정성 확보)**
            Thread.sleep(200);

            // **음원이 끝날 때까지 정확히 대기**
            clip.drain();
            clip.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[SoundPlayer] 음원 재생 오류: " + fileName);
        }
    }
}
