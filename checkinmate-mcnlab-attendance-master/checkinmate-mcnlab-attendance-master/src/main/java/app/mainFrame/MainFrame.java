package app.mainFrame;

import app.RoundButton;
import app.adminPage.AdminFrame;
import app.database.DatabaseController;
import app.database.UserList;
import app.system.ResponseCode;
import app.system.TimeManage;
import app.userSession.SessionManage;
import app.userStats.StatsFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainFrame extends JFrame {
    // Font Option
    final static String fontName = "NanumSquare";
    final static int keyPadFontSize = 46;
    final static int submitFontSize = 22;
    final static int IDfieldFontSize = 40;

    // Color Option
    final static Color frameBackground = new Color(248, 249, 250);
    final static Color submitFieldColor = new Color(248, 249, 250);
    final static Color donutGraphColor = new Color(0, 91, 187);
    final static Color keyPadColor = new Color(0, 91, 187);
    final static Color keyPadPressed = new Color(0, 45, 93);
    final static Color submitColor = new Color(0, 91, 187);
    final static Color submitPressed = new Color(0, 45, 93);
    final static Color eraseColor = new Color(120, 130, 140);
    final static Color erasePressed = new Color(50, 60, 80);
    final static Color logoColor = new Color(80, 90, 120);
    final static Color clockColor = new Color(20, 20, 20);

    // Other Page
    AdminFrame adminFrame;
    StatsFrame statsFrame;

    // Content Pane
    static Container contentPane;
    static JPanel keyPadPanel;

    // Database Controller (for submit, search previous data)
    static DatabaseController controller;

    // Submit Area (inputID)
    static JPanel submitPanel;
    JPanel textArea;
    JLabel idField;
    String inputValue = "";
    static RoundButton submitButton;
    static RoundButton eraseButton;

    // User Info (after Submit)
    static JPanel userInfoPanel1;
    static JPanel userInfoPanel2;
    static JLabel userNameLabel;
    static JLabel greetingLabel;
    static JLabel startTimeLabel;
    static JLabel endTimeLabel;
    static JLabel sumOfSessionLabel;
    static JLabel totalAttendanceLabel;
    static RoundButton closeButton;
    static RoundButton statusButton;
    static DonutChartPanel donutChart;


    // Clock Area
    JLabel clock1;  // HH:mm
    JLabel clock2;  // ss
    JLabel date;    // yyyy-MM-dd
    JLabel dayOfWeek;    // yyyy-MM-dd
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATTER_DAY = DateTimeFormatter.ofPattern("eeee", Locale.ENGLISH);

    // for Screen Saver
    private JPanel screenSaverPanel;
    private long lastInteractionTime = System.currentTimeMillis();
    private final int idleThreshold = 5 * 60 * 1000; // 5분 (밀리초)
    private boolean screenSaverOn;


    // Temporary Struct
    static RoundButton tmpButton;

    // set JFrame options
    private void initFrame() {
        setTitle("CheckIn Mate");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 480);
        setLocationRelativeTo(null);
        setVisible(true);

        contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.setBackground(frameBackground);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "프로그램을 종료하시겠습니까?\n모든 세션이 저장됩니다.",
                        "종료 확인",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.sessionManage.shutdownSessions(); // 세션 저장 후 종료
                    controller.saveStatus();
                    controller.saveUserInfo();
                    dispose(); // 창 닫기
                    System.exit(0); // 완전 종료
                }
            }
        });
    }

    // make button and return instance
    private void setButton(String name, int x, int y, int w, int h, ActionListener actionListener) {
        JButton button = new JButton(name);
        button.setBounds(x, y, w, h);
        button.addActionListener(actionListener);
        contentPane.add(button);
    }

    private static RoundButton setCircleButton(String name, int x, int y, int w, int h, ActionListener actionListener, Font font) {
        return setCircleButton(name, x, y, w, h, actionListener, font, tmpButton, 80, keyPadColor, keyPadPressed);
    }

    private static RoundButton setCircleButton(String name, int x, int y, int w, int h, ActionListener actionListener, Font font, RoundButton instance, int roundSize, Color defaultColor, Color pressedColor) {
        instance = new RoundButton(name, roundSize, defaultColor, pressedColor);
        instance.setBounds(x, y, w, h);
        instance.addActionListener(actionListener);
        instance.setFont(font);
        //contentPane.add(instance);
        return instance;
    }

    // set layout
    private void setKeyPad() {
        keyPadPanel = new JPanel();
        keyPadPanel.setLayout(null);
        keyPadPanel.setBackground(frameBackground);
        keyPadPanel.setVisible(true);

        // offset value
        int xOffset = 0, yOffset = 0, xGap = 100, yGap = 100, buttonWidth = 85, buttonHeight = 85;
        keyPadPanel.setBounds(480, 20, xGap * 2 + buttonWidth, yGap * 3 + buttonHeight);

        // 1 ~ 9 button
        for (int i = 0; i < 9; i++) {
            String tmpString = Integer.toString(i + 1);
            tmpButton = setCircleButton(tmpString, xOffset + xGap * (i % 3), yOffset + yGap * (i / 3), buttonWidth, buttonHeight, e -> {
                if (inputValue.length() < 15) {
                    inputValue += tmpString;
                    refreshIdField();
                }
            }, new Font(fontName, Font.BOLD, keyPadFontSize));
            keyPadPanel.add(tmpButton);
        }

        // 0 button
        tmpButton = setCircleButton("0", xOffset, yOffset + yGap * 3, buttonWidth + xGap, buttonHeight, e -> {
            if (inputValue.length() < 15) {
                inputValue += "0";
                refreshIdField();
            }
        }, new Font(fontName, Font.BOLD, keyPadFontSize));
        keyPadPanel.add(tmpButton);

        // erase 1-char button
        tmpButton = setCircleButton("←", xOffset + xGap * 2, yOffset + yGap * 3, buttonWidth, buttonWidth, e -> {
            if (!inputValue.isEmpty()) {
                inputValue = inputValue.substring(0, inputValue.length() - 1);
                refreshIdField();
            }
        }, new Font(fontName, Font.BOLD, keyPadFontSize));
        keyPadPanel.add(tmpButton);

        contentPane.add(keyPadPanel);
    }


    private void putLogo() {
        URL imageUrl = getClass().getClassLoader().getResource("MCN Logo.png");
        ImageIcon logo = (imageUrl != null) ? new ImageIcon(imageUrl) : null;
        if (logo != null) {
            JLabel mcnLogo = new JLabel(logo);
            mcnLogo.setBounds(15, 25, 100, 78);
            contentPane.add(mcnLogo);
        } else {
            System.err.println(TimeManage.getTimeByString() + "[Load Image Error] Can't find logo");
        }

        JLabel AppTitle = new JLabel("MCN Lab. Attendance Management System");
        AppTitle.setFont(new Font(fontName, Font.PLAIN, 16));
        AppTitle.setForeground(logoColor);
        AppTitle.setBounds(122, -14, 400, 100);
        contentPane.add(AppTitle);

        JLabel AppSubTitle = new JLabel("CheckInMate");
        AppSubTitle.setFont(new Font(fontName, Font.BOLD, 36));
        AppSubTitle.setForeground(logoColor);
        AppSubTitle.setBounds(120, 20, 400, 100);
        contentPane.add(AppSubTitle);
    }

    private void setSubmitField() {
        submitPanel = new JPanel();
        submitPanel.setBounds(20, 200, 420, 200);
        submitPanel.setLayout(null);
        submitPanel.setBackground(submitFieldColor);

        textArea = new JPanel();
        textArea.setBackground(new Color(0, 91, 187));
        textArea.setBounds(10, 120, 390, 2);
        submitPanel.add(textArea);

        idField = new JLabel("Input Your ID");
        idField.setFont(new Font(fontName, Font.PLAIN, IDfieldFontSize));
        idField.setForeground(new Color(160, 160, 160));
        idField.setBounds(10, 70, 385, 40);
        idField.setHorizontalAlignment(JLabel.RIGHT);
        submitPanel.add(idField);

        submitButton = setCircleButton("Submit", 295, 155, 110, 40, e -> {
            ResponseCode code;
            if(inputValue.isEmpty()) return;

            code = controller.submit(Long.parseLong(inputValue));

            // for test
            System.out.println(TimeManage.getTimeByString() + code.getMessage());

            inputValue = "";
            refreshIdField();
        }, new Font(fontName, Font.BOLD, submitFontSize), submitButton, 40, submitColor, submitPressed);
        submitPanel.add(submitButton);

        eraseButton = setCircleButton("Erase", 170, 155, 110, 40, e -> {
            inputValue = "";
            refreshIdField();
        }, new Font(fontName, Font.BOLD, submitFontSize), eraseButton, 40, eraseColor, erasePressed);
        submitPanel.add(eraseButton);

        contentPane.add(submitPanel);
        submitPanel.setVisible(true);
    }

    private void refreshIdField() {
        if (inputValue.isEmpty()) {
            idField.setText("Input Your ID");
            idField.setForeground(new Color(160, 160, 160));
            idField.setFont(new Font(fontName, Font.PLAIN, IDfieldFontSize));
        } else {
            idField.setText(inputValue);
            idField.setForeground(new Color(20, 20, 20));
            idField.setFont(new Font(fontName, Font.PLAIN, IDfieldFontSize + 4));
        }
    }

    public static void setSignedIn(Long id) {
        /*  JPanel UserInfoPanel;
            JLabel userNameLabel;
            JLabel greetingLabel;
            JLabel startTimeLabel;
            JLabel endTimeLabel;
            JLabel sumOfSessionLabel;
        */

        submitPanel.setVisible(false);
        keyPadPanel.setVisible(false);

        userInfoPanel1 = new JPanel();
        userInfoPanel1.setBounds(20, 200, 420, 200);
        userInfoPanel1.setLayout(null);
        userInfoPanel1.setBackground(submitFieldColor);
        //----------------------------------------------------
        userNameLabel = new JLabel(UserList.getName(id));
        userNameLabel.setFont(new Font(fontName, Font.BOLD, 46));
        userNameLabel.setBounds(5, 5, 180, 50);
        userInfoPanel1.add(userNameLabel);
        //----------------------------------------------------
        greetingLabel = new JLabel("님 안녕하세요!");
        greetingLabel.setFont(new Font(fontName, Font.PLAIN, 24));
        greetingLabel.setBounds(190, 20, 150, 30);
        userInfoPanel1.add(greetingLabel);
        //----------------------------------------------------
        startTimeLabel = new JLabel("최초 인증:   " + SessionManage.getFirstAttend(id));
        startTimeLabel.setFont(new Font(fontName, Font.PLAIN, 16));
        startTimeLabel.setBounds(5, 60, 250, 30);
        userInfoPanel1.add(startTimeLabel);
        //----------------------------------------------------
        endTimeLabel = new JLabel("마지막 인증: " + SessionManage.getLastAttend(id));
        endTimeLabel.setFont(new Font(fontName, Font.PLAIN, 16));
        endTimeLabel.setBounds(5, 80, 250, 30);
        userInfoPanel1.add(endTimeLabel);
        //----------------------------------------------------
        sumOfSessionLabel = new JLabel(
                "이번 세션 인증 시간 합계: " + SessionManage.getThisSessionTime(id) + "분");
        sumOfSessionLabel.setFont(new Font(fontName, Font.PLAIN, 20));
        sumOfSessionLabel.setBounds(5, 110, 350, 45);
        userInfoPanel1.add(sumOfSessionLabel);
        //----------------------------------------------------
        long total = controller.historyManage.getThisWeekTotal(id);
        String time;
        if(total >= 60){
            time = String.valueOf(total / 60) + "시간 " + String.valueOf(total % 60) + "분";
        } else {
            time = String.valueOf(total / 60)+"분";
        }

        totalAttendanceLabel = new JLabel(
                "이번 주 활동 시간 합계  : " + time);
        totalAttendanceLabel.setFont(new Font(fontName, Font.BOLD, 22));
        totalAttendanceLabel.setBounds(5, 150, 350, 45);
        userInfoPanel1.add(totalAttendanceLabel);
        //----------------------------------------------------
        userInfoPanel2 = new JPanel();
        userInfoPanel2.setBounds(480, 35, 290, 385);
        userInfoPanel2.setLayout(null);
        userInfoPanel2.setBackground(submitFieldColor);

        //----------------------------------------------------
        // Donut Graph
        donutChart = new DonutChartPanel(controller.historyManage.getThisWeekTotal(id));
        donutChart.setBounds(10,0,270,270);
        userInfoPanel2.add(donutChart);

        //----------------------------------------------------
        statusButton = setCircleButton("View Log", 20, 280, 250, 40, e -> {
            new StatsFrame(id);
        }, new Font(fontName, Font.BOLD, submitFontSize), statusButton, 40, eraseColor, erasePressed);
        userInfoPanel2.add(statusButton);
        //----------------------------------------------------
        closeButton = setCircleButton("Close", 20, 330, 250, 40, e -> {
            closeSigned();
            refreshDesign();
        }, new Font(fontName, Font.BOLD, submitFontSize), closeButton, 40, submitColor, submitPressed);
        userInfoPanel2.add(closeButton);

        //----------------------------------------------------

        //----------------------------------------------------
        contentPane.add(userInfoPanel1);
        contentPane.add(userInfoPanel2);
        userInfoPanel1.setVisible(true);
        userInfoPanel2.setVisible(true);
        refreshDesign();
    }

    public static void closeSigned() {
        userInfoPanel1.setVisible(false);
        userInfoPanel2.setVisible(false);
        submitPanel.setVisible(true);
        keyPadPanel.setVisible(true);
    }

    private static void refreshDesign() {
        contentPane.revalidate();
        contentPane.repaint();
    }

    // Clock
    private void setClockArea() {
        clock1 = new JLabel("00:00");
        clock1.setFont(new Font(fontName, Font.BOLD, 52));
        clock1.setForeground(clockColor);
        clock1.setBounds(185, 85, 400, 100);
        contentPane.add(clock1);

        clock2 = new JLabel(": 00");
        clock2.setFont(new Font(fontName, Font.PLAIN, 30));
        clock2.setForeground(clockColor);
        clock2.setBounds(350, 90, 400, 100);
        contentPane.add(clock2);

        date = new JLabel("yyyy-MM-dd");
        date.setFont(new Font(fontName, Font.PLAIN, 25));
        date.setForeground(clockColor);
        date.setBounds(20, 77, 300, 100);
        contentPane.add(date);

        dayOfWeek = new JLabel("eeee");
        dayOfWeek.setFont(new Font(fontName, Font.BOLD, 14));
        dayOfWeek.setForeground(clockColor);
        dayOfWeek.setBounds(20, 100, 300, 100);
        contentPane.add(dayOfWeek);

        updateDateTime();
    }

    private void startClockUpdate() { // and activate screen saver
        Timer timer = new Timer(1000, e -> {
            LocalTime now = LocalTime.now();
            clock1.setText(now.format(DateTimeFormatter.ofPattern("HH:mm")));
            clock2.setText(now.format(DateTimeFormatter.ofPattern(": ss")));

            if (now.getMinute() == 0 && now.getHour() == 0) updateDateTime();


            // for screen saver
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastInteractionTime > idleThreshold && !screenSaverOn) {
                showScreenSaver();
                refreshDesign();
            }
        });
        timer.start();
    }

    // Calendar
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        date.setText(now.format(FORMATTER));
        dayOfWeek.setText(now.format(FORMATTER_DAY));
    }

    // Screen Saver
    private void initScreenSaver() {
        screenSaverPanel = (JPanel) getGlassPane();
        screenSaverPanel.setBackground(Color.BLACK);
        screenSaverPanel.setSize(getWidth(), getHeight());
        screenSaverPanel.setLayout(null);
        screenSaverPanel.setOpaque(true);

        screenSaverOn = false;

        screenSaverPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                hideScreenSaver();
            }
        });

        setGlassPane(screenSaverPanel);
        contentPane.add(screenSaverPanel);
        screenSaverPanel.setVisible(false);
    }
    private void showScreenSaver() {
        screenSaverPanel.setVisible(true);
        userInfoPanel1.setVisible(false);
        userInfoPanel2.setVisible(false);
        submitPanel.setVisible(false);
        keyPadPanel.setVisible(false);
        screenSaverOn = true;
    }
    private void hideScreenSaver() {
        screenSaverPanel.setVisible(false);
        lastInteractionTime = System.currentTimeMillis();
        submitPanel.setVisible(true);
        keyPadPanel.setVisible(true);
        refreshDesign();
        screenSaverOn = false;
    }
    private void setupUserActivityTracker() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            lastInteractionTime = System.currentTimeMillis();

            if (screenSaverPanel.isVisible()) {
                hideScreenSaver();
            }

        }, AWTEvent.MOUSE_EVENT_MASK);
    }




    // constructor
    public MainFrame() {
        this(null);
    }

    public MainFrame(DatabaseController databaseController) {
        controller = databaseController;

        initFrame();
        initScreenSaver();
        setKeyPad();
        putLogo();
        setSubmitField();

        setClockArea();
        startClockUpdate();
        setupUserActivityTracker();

        setButton("Exit", 10, 440, 80, 30, e -> System.exit(0));


        // repaint
        refreshDesign();

    }
}





class DonutChartPanel extends JPanel {
    private long activityMinutes; // 활동 시간 (분)
    private final int totalMinutes = 900; // 15시간을 분 단위로 설정 (고정)

    public DonutChartPanel(long activityMinutes) {
        this.activityMinutes = activityMinutes;
        setBackground(MainFrame.submitFieldColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 🔹 안티앨리어싱 적용 (그래픽 부드럽게)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 🔹 패널 크기 가져오기 (사이즈 조절 필요)
        int width = getWidth();   // (사용자가 지정)
        int height = getHeight(); // (사용자가 지정)

        // 🔹 도넛의 외곽 원 크기 계산 (패널 크기에서 여백 고려)
        int diameter = Math.min(width, height) - 20;
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;

        // 🔹 도넛 배경 원 그리기 (회색 배경)
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillOval(x, y, diameter, diameter);

        // 🔹 활동 시간에 따른 부채꼴 그리기
        double angle = (double) activityMinutes / totalMinutes * 360; // 15시간 기준 비율
        if (angle > 360) angle = 360;

        g2.setColor(MainFrame.donutGraphColor); // 부채꼴 색상 (원하는 색으로 변경 가능)
        g2.fillArc(x, y, diameter, diameter, 90, -(int) angle); // 시계 방향 그리기

        // 🔹 중앙의 뚫린 부분 (작은 원) 그리기
        int holeDiameter = diameter / 2;
        int holeX = x + (diameter - holeDiameter) / 2;
        int holeY = y + (diameter - holeDiameter) / 2;
        g2.setColor(getBackground()); // 배경색과 동일한 색으로 채우기
        g2.fillOval(holeX, holeY, holeDiameter, holeDiameter);

        // 🔹 중앙 텍스트 추가 (달성률)
        int percentage = (int) ((double) activityMinutes / totalMinutes * 100);
        String text = percentage + "%";
        Font font = new Font(MainFrame.fontName, Font.BOLD, holeDiameter / 5);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics(font);
        int textX = holeX + (holeDiameter - metrics.stringWidth(text)) / 2;
        int textY = holeY + (holeDiameter / 2) + (metrics.getAscent() / 2) - 2;
        g2.setColor(Color.BLACK);
        g2.drawString(text, textX, textY);

    }
}