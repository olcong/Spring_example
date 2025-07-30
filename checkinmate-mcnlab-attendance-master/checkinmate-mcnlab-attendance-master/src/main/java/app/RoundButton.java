package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundButton extends JButton {
    private int round;

    public RoundButton(String text, int roundSize, Color defaultColor, Color pressedColor) {
        super(text);
        setContentAreaFilled(false);  // remove default button style
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        round = roundSize;

        // change color by mouse event
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(defaultColor);
            }
        });

        setBackground(defaultColor);
        setForeground(Color.WHITE); // set font color
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // set Background color
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), round, round); // set round style

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // remove default border
    }
}