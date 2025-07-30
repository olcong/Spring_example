package app.database;

import app.database.history.HistoryManage;
import app.system.TimeManage;

import java.io.*;
import java.nio.charset.StandardCharsets;

/* Data file format
 *
 * ============================
 * userList.csv
 * ----------------------------
 * id, name, sumOfAttendance - iterative
 *
 * ============================
 * checkInHistory.csv
 * ----------------------------
 * numOfHistorySet (first line)
 * > iterative
 *      id, startDate, endDate, numOfData (one dataset)
 *      -> iterative
 *          checkedDateTime, type
 *
 * ============================
 */ // file format


// temporary class before DB system
public class FileManage {
    BufferedReader reader;
    HistoryManage historyManage;

    // Directory Path
    //String jarDir = new File(System.getProperty("java.class.path")).getAbsoluteFile().getParent(); // for JAR
    String jarDir = System.getProperty("user.dir"); // only for IDE

    public void readUsers(BufferedReader reader) throws IOException {
        String s;
        reader.readLine(); // ignore first line (title)
        while ((s = reader.readLine()) != null) {
            String[] data = s.split(","); // ID, name, totalAttendanceTime
            UserList.addUser(Long.parseLong(data[0]), data[1]);
            System.out.println(Long.parseLong(data[0]) + "," + data[1]);
        }
    }
    public void exportUsers()  {
        File exportFile = new File(jarDir, "UserData.csv");

        // BufferedWriter 생성
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile), StandardCharsets.UTF_8))) {
            // 첫 번째 줄에 헤더 작성
            writer.write("ID,Name\n");

            // UserList에서 모든 유저 정보를 가져와 저장
            for (UserData user : UserList.getAllUsers()) {
                writer.write(user.id + "," + user.name +"\n");
            }

            System.out.println(TimeManage.getTimeByString() + "[FileManage] User data successfully exported to " + exportFile.getAbsolutePath());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readStatus(BufferedReader reader) {
        historyManage.loadHistory(reader);
        if(historyManage.isWeekChanged()) historyManage.changeWeek();
        System.out.println(TimeManage.getTimeByString() + "[FileManage] Finish Read Status");


    }

    public void saveStatus() {
        try {
            File historyFile = new File(jarDir, "UserHistory.csv");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(historyFile), StandardCharsets.UTF_8));
            historyManage.saveHistory(writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileManage(HistoryManage historyManage) {
        this.historyManage = historyManage;
        try {
            // JAR과 같은 폴더에 있는 UserData.csv 읽기
            File userDataFile = new File(jarDir, "UserData.csv");
            if (userDataFile.exists()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(userDataFile), StandardCharsets.UTF_8));
                readUsers(reader);
                reader.close();
            }
            else {
                System.out.println("File not found - UserData.csv");
            }

            // JAR과 같은 폴더에 있는 UserStatus.csv 읽기
            File userStatusFile = new File(jarDir, "UserHistory.csv");
            if (userStatusFile.exists()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(userStatusFile), StandardCharsets.UTF_8));
                readStatus(reader);
                reader.close();
            } else {
                System.out.println("File not found - UserHistory.csv");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
