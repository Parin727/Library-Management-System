package gui;

import logic.*;
import models.*;
import database.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;

public class UserDashboardGUI extends JFrame {
    private User user;
    private JTabbedPane tabbedPane;

    public UserDashboardGUI(User user) {
        this.user = user;
        setTitle("User Dashboard - " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        UIStyles.applyGlobalStyles(this);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIStyles.REGULAR_FONT);
        tabbedPane.setBackground(UIStyles.BACKGROUND_COLOR);
        
        tabbedPane.addTab("Reserve Book", createReservePanel());
        tabbedPane.addTab("My Books / Renew", createMyBooksPanel());
        tabbedPane.addTab("Pay Fine", createFinePanel());
        tabbedPane.addTab("Feedback", createFeedbackPanel());

        add(tabbedPane);
    }

    private JPanel createReservePanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = UIStyles.createPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JTextField searchField = UIStyles.createTextField();
        searchField.setPreferredSize(new Dimension(250, 30));
        
        JButton searchButton = new RoundedButton("Search", UIStyles.PRIMARY_COLOR);
        
        searchPanel.add(UIStyles.createLabel("Search Title/Author:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        String[] columns = {"ID", "Title", "Author", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        styleTable(table);
        
        searchButton.addActionListener(e -> {
            model.setRowCount(0);
            BookRecordManager manager = new BookRecordManager();
            List<Book> books = manager.searchBooks(searchField.getText());
            for (Book b : books) {
                model.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getAvailableQuantity()});
            }
        });

        JButton reserveButton = new RoundedButton("Reserve Selected Book", UIStyles.SUCCESS_COLOR);
        reserveButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int bookId = (int) model.getValueAt(row, 0);
                TransactionManager tm = new TransactionManager();
                if (tm.reserveBook(user.getUserId(), bookId)) {
                    JOptionPane.showMessageDialog(this, "Book Reserved Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to reserve book (Maybe out of stock).");
                }
            }
        });
        
        JPanel bottomPanel = UIStyles.createPanel();
        bottomPanel.add(reserveButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMyBooksPanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columns = {"Transaction ID", "Book", "Due Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshButton = new RoundedButton("Refresh", UIStyles.INFO_COLOR);
        refreshButton.addActionListener(e -> loadMyBooks(model));

        JButton renewButton = new RoundedButton("Renew Selected", UIStyles.WARNING_COLOR);
        renewButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int transId = (int) model.getValueAt(row, 0);
                TransactionManager tm = new TransactionManager();
                if (tm.renewBook(transId)) {
                    JOptionPane.showMessageDialog(this, "Book Renewed Successfully!");
                    loadMyBooks(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Renewal Failed (Overdue or Error).");
                }
            }
        });

        JPanel btnPanel = UIStyles.createPanel();
        btnPanel.add(refreshButton);
        btnPanel.add(renewButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        loadMyBooks(model); 
        return panel;
    }

    private void loadMyBooks(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.transaction_id, b.title, t.due_date, t.status FROM transactions t JOIN books b ON t.book_id = b.book_id WHERE t.user_id = ? AND t.status = 'RESERVED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("title"),
                    rs.getString("due_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createFinePanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new GridBagLayout());
        
        JLabel fineLabel = UIStyles.createTitleLabel("Total Fine: $0.00");
        JButton payButton = new RoundedButton("Pay Fine", UIStyles.DANGER_COLOR);
        
        payButton.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Fine Paid! (Simulation)");
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(fineLabel, gbc);
        
        gbc.gridy = 1;
        panel.add(payButton, gbc);
        
        return panel;
    }

    private JPanel createFeedbackPanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextArea feedbackArea = new JTextArea();
        feedbackArea.setFont(UIStyles.REGULAR_FONT);
        feedbackArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JButton submitButton = new RoundedButton("Submit Feedback", UIStyles.PRIMARY_COLOR);

        submitButton.addActionListener(e -> {
            FeedbackManager fm = new FeedbackManager();
            if (fm.submitFeedback(user.getUserId(), feedbackArea.getText())) {
                JOptionPane.showMessageDialog(this, "Feedback Submitted!");
                feedbackArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error submitting feedback.");
            }
        });

        panel.add(UIStyles.createLabel("Enter your feedback:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        
        JPanel btnPanel = UIStyles.createPanel();
        btnPanel.add(submitButton);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void styleTable(JTable table) {
        table.setFont(UIStyles.REGULAR_FONT);
        table.setRowHeight(25);
        table.setSelectionBackground(UIStyles.PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setFont(UIStyles.BUTTON_FONT);
        header.setBackground(Color.LIGHT_GRAY);
    }
}
