package app.userStats;

import javax.swing.*;
import java.awt.*;

public class StatsFrame_Backup extends JFrame {

    Container contentPane;

    // Color Option
    final static Color frameBackground = new Color(248, 249, 250);

    private void initFrame() {
        setTitle("CheckIn Mate");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 480);
        setLocationRelativeTo(null);
        setVisible(true);

        contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.setBackground(frameBackground);
    }


    public StatsFrame_Backup(Long id){
        initFrame();

    }
}
