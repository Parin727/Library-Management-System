package gui;

import database.DBConnection;
import models.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginGUI() {
        setTitle("Library Management System - Login");
        setSize(450, 350); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);
        setLayout(new GridBagLayout());

        JPanel mainPanel = UIStyles.createPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = UIStyles.createTitleLabel("Login");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(UIStyles.createLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = UIStyles.createTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(usernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(UIStyles.createLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = UIStyles.createPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        
        JPanel buttonPanel = UIStyles.createPanel();
        loginButton = new RoundedButton("Login", UIStyles.PRIMARY_COLOR);
        registerButton = new RoundedButton("Register", UIStyles.SUCCESS_COLOR);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterGUI().setVisible(true);
            }
        });
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");
                String libCard = rs.getString("library_card_id");

                User user = null;
                if ("STUDENT".equalsIgnoreCase(role)) {
                    user = new Student(userId, username, password, libCard);
                    new UserDashboardGUI(user).setVisible(true);
                } else if ("STAFF".equalsIgnoreCase(role)) {
                    user = new Staff(userId, username, password, libCard);
                    new UserDashboardGUI(user).setVisible(true);
                } else if ("LIBRARIAN".equalsIgnoreCase(role)) {
                    user = new Librarian(userId, username, password, libCard);
                    new LibrarianDashboardGUI(user).setVisible(true);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
