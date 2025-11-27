package gui;

import database.DBConnection;
import models.*;
import javax.swing.*;
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
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);

        registerButton = new JButton("Register");
        add(registerButton);

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
