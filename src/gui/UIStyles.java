package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIStyles {
    // Colors
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219); // Modern Blue
    public static final Color SECONDARY_COLOR = new Color(46, 204, 113); // Emerald Green
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96); // Darker Green
    public static final Color WARNING_COLOR = new Color(243, 156, 18); // Orange
    public static final Color DANGER_COLOR = new Color(231, 76, 60); // Red
    public static final Color INFO_COLOR = new Color(52, 73, 94); // Dark Blue/Grey
    
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Soft Grey
    public static final Color PANEL_BACKGROUND = new Color(255, 255, 255); // White
    public static final Color TEXT_COLOR = new Color(44, 62, 80); // Midnight Blue
    public static final Color ACCENT_COLOR = DANGER_COLOR; // Alias for backward compatibility

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public static void applyGlobalStyles(Component c) {
        if (c instanceof JPanel) {
            c.setBackground(BACKGROUND_COLOR);
        }
        if (c instanceof JFrame) {
            ((JFrame) c).getContentPane().setBackground(BACKGROUND_COLOR);
        }
    }

    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    public static JPanel createBackgroundPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(REGULAR_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(REGULAR_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(5, 5, 5, 5)));
        return field;
    }
    
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(REGULAR_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(5, 5, 5, 5)));
        return field;
    }
}
