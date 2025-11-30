package gui;

import database.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class RegisterGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton registerButton;

    public RegisterGUI() {
        setTitle("Register User");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Apply global background
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);
        setLayout(new GridBagLayout());

        JPanel mainPanel = UIStyles.createPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = UIStyles.createTitleLabel("Register New User");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(UIStyles.createLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = UIStyles.createTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(UIStyles.createLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = UIStyles.createPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(passwordField, gbc);

        // Role
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(UIStyles.createLabel("Role:"), gbc);

        gbc.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"STUDENT", "STAFF"});
        roleBox.setFont(UIStyles.REGULAR_FONT);
        roleBox.setBackground(Color.WHITE);
        mainPanel.add(roleBox, gbc);

        // Register Button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        registerButton = new RoundedButton("Register", UIStyles.SUCCESS_COLOR);
        mainPanel.add(registerButton, gbc);

        add(mainPanel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        String libCardId = "LIB-" + new Random().nextInt(100000);

        String sql = "INSERT INTO users (username, password, role, library_card_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setString(4, libCardId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Card ID: " + libCardId);
                this.dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Username might already exist.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
