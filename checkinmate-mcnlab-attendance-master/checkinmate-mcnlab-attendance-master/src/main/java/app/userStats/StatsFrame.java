package app.userStats;

import app.RoundButton;
import app.database.UserList;
import app.database.history.HistoryManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StatsFrame extends JFrame {

    private Container contentPane;

    // record string field
    private JTextArea textArea;
    private JScrollPane scrollPane;

    // title
    private JLabel name;
    private JLabel id;
    private JLabel infoMessage;

    private RoundButton closeButton;
    final static String fontName = "NanumSquare";;

    // Color Option
    final static Color frameBackground = new Color(248, 249, 250);

    private void initFrame(Long userID) {
        setTitle("CheckIn Mate");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 480);
        setLocationRelativeTo(null);

        contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.setBackground(frameBackground);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 70, 760, 350);
        textArea.setFont(new Font(fontName, Font.PLAIN, 13));
        contentPane.add(scrollPane);

        closeButton = new RoundButton("Close", 40,
                new Color(0, 91, 187), new Color(0, 45, 93));
        closeButton.setSize(300, 300);
        closeButton.setBounds(670,20,110,40);
        closeButton.setFont(new Font(fontName, Font.BOLD, 22));
        closeButton.addActionListener(e -> dispose());
        contentPane.add(closeButton);

        name = new JLabel(UserList.getName(userID));
        name.setFont(new Font(fontName, Font.BOLD, 40));
        name.setBounds(25,15,180,50);
        contentPane.add(name);

        id = new JLabel("ID:  " + userID);
        id.setFont(new Font(fontName, Font.BOLD, 12));
        id.setBounds(180,15,180,20);
        contentPane.add(id);

        infoMessage = new JLabel("님의 출결 기록은 아래와 같습니다.");
        infoMessage.setFont(new Font(fontName, Font.PLAIN, 18));
        infoMessage.setBounds(180,36,350,20);
        contentPane.add(infoMessage);

        setVisible(true);
    }

    private void displayUserLogs(Long id) {
        String s = HistoryManage.getHistory(id);

        if (!s.isEmpty()) {
            textArea.setText(s);
            textArea.setCaretPosition(0);
        } else {
            textArea.setText("No logs available for user ID: " + id);
        }
    }

    public StatsFrame(Long id) {
        initFrame(id);
        displayUserLogs(id);
    }

}
