package gui;

import logic.*;
import models.*;
import database.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Reserve Book", createReservePanel());
        tabbedPane.addTab("My Books / Renew", createMyBooksPanel());
        tabbedPane.addTab("Pay Fine", createFinePanel());
        tabbedPane.addTab("Feedback", createFeedbackPanel());

        add(tabbedPane);
    }

    private JPanel createReservePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search Title/Author:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        String[] columns = {"ID", "Title", "Author", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        searchButton.addActionListener(e -> {
            model.setRowCount(0);
            BookRecordManager manager = new BookRecordManager();
            List<Book> books = manager.searchBooks(searchField.getText());
            for (Book b : books) {
                model.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getAvailableQuantity()});
            }
        });

        JButton reserveButton = new JButton("Reserve Selected Book");
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

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(reserveButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMyBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Transaction ID", "Book", "Due Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyBooks(model));

        JButton renewButton = new JButton("Renew Selected");
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

        JPanel btnPanel = new JPanel();
        btnPanel.add(refreshButton);
        btnPanel.add(renewButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        loadMyBooks(model); // Initial load
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
        JPanel panel = new JPanel(new FlowLayout());
        JLabel fineLabel = new JLabel("Total Fine: $0.00");
        JButton payButton = new JButton("Pay Fine");
        
        payButton.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Fine Paid! (Simulation)");
        });

        panel.add(fineLabel);
        panel.add(payButton);
        return panel;
    }

    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea feedbackArea = new JTextArea();
        JButton submitButton = new JButton("Submit Feedback");

        submitButton.addActionListener(e -> {
            FeedbackManager fm = new FeedbackManager();
            if (fm.submitFeedback(user.getUserId(), feedbackArea.getText())) {
                JOptionPane.showMessageDialog(this, "Feedback Submitted!");
                feedbackArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error submitting feedback.");
            }
        });

        panel.add(new JLabel("Enter your feedback:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);
        return panel;
    }
}
