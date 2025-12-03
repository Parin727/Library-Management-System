package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private int cornerRadius = 15;

    public RoundedButton(String text) {
        this(text, UIStyles.PRIMARY_COLOR);
    }

    public RoundedButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        
        setFont(UIStyles.BUTTON_FONT);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add padding
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }
        });
        
        setBackground(normalColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius));

        // Draw button
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        // Draw text
        super.paintComponent(g2);
        g2.dispose();
    }
}
