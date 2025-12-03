package gui;

import logic.*;
import models.*;
import database.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class StaffDashboardGUI extends JFrame {
    private User user;
    private JTabbedPane tabbedPane;

    public StaffDashboardGUI(User user) {
        this.user = user;
        setTitle("Staff Dashboard - " + user.getUsername() + " (STAFF)");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UIStyles.applyGlobalStyles(this);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIStyles.REGULAR_FONT);
        tabbedPane.setBackground(UIStyles.BACKGROUND_COLOR);

        tabbedPane.addTab("Reserve Book", createReservePanel());
        tabbedPane.addTab("My Books / Renew", createMyBooksPanel());
        tabbedPane.addTab("View All Users", createViewUsersPanel());
        tabbedPane.addTab("View Issued Books", createViewIssuedBooksPanel());
        tabbedPane.addTab("View All Fines", createViewAllFinesPanel());
        tabbedPane.addTab("Pay Fine", createFinePanel());
        tabbedPane.addTab("Feedback", createFeedbackPanel());
        tabbedPane.addTab("Logout", createLogoutPanel());

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

        String[] columns = { "ID", "Title", "Author", "Available" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        styleTable(table);

        // Load all books initially
        loadBooksIntoTable(model, "");

        searchButton.addActionListener(e -> {
            loadBooksIntoTable(model, searchField.getText());
        });

        JButton reserveButton = new RoundedButton("Reserve Selected Book", UIStyles.SUCCESS_COLOR);
        reserveButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int bookId = (int) model.getValueAt(row, 0);
                TransactionManager tm = new TransactionManager();
                if (tm.reserveBook(user.getUserId(), bookId)) {
                    JOptionPane.showMessageDialog(this, "Book Reserved Successfully!");
                    // Refresh the book list after reservation
                    loadBooksIntoTable(model, searchField.getText());
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

        String[] columns = { "Transaction ID", "Book", "Due Date", "Status" };
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

    private JPanel createViewUsersPanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = UIStyles.createPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = UIStyles.createTitleLabel("All Registered Users");
        topPanel.add(titleLabel);

        String[] columns = { "User ID", "Username", "Role", "Library Card ID" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshButton = new RoundedButton("Refresh", UIStyles.INFO_COLOR);
        refreshButton.addActionListener(e -> loadAllUsers(model));

        JPanel bottomPanel = UIStyles.createPanel();
        bottomPanel.add(refreshButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadAllUsers(model);
        return panel;
    }

    private JPanel createViewIssuedBooksPanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = UIStyles.createPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = UIStyles.createTitleLabel("All Issued Books");
        topPanel.add(titleLabel);

        String[] columns = { "Transaction ID", "User", "Book Title", "Issue Date", "Due Date", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshButton = new RoundedButton("Refresh", UIStyles.INFO_COLOR);
        refreshButton.addActionListener(e -> loadAllIssuedBooks(model));

        JPanel bottomPanel = UIStyles.createPanel();
        bottomPanel.add(refreshButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadAllIssuedBooks(model);
        return panel;
    }

    private void loadAllUsers(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT user_id, username, role, library_card_id FROM users ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("library_card_id")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createViewAllFinesPanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = UIStyles.createPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = UIStyles.createTitleLabel("All User Fines");
        topPanel.add(titleLabel);

        String[] columns = { "Transaction ID", "User", "Book Title", "Due Date", "Fine Amount", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshButton = new RoundedButton("Refresh", UIStyles.INFO_COLOR);
        refreshButton.addActionListener(e -> loadAllFines(model));

        JPanel bottomPanel = UIStyles.createPanel();
        bottomPanel.add(refreshButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadAllFines(model);
        return panel;
    }

    private void loadAllFines(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.transaction_id, u.username, b.title, t.due_date, t.fine_amount, t.status " +
                "FROM transactions t " +
                "JOIN users u ON t.user_id = u.user_id " +
                "JOIN books b ON t.book_id = b.book_id " +
                "WHERE t.fine_amount > 0 " +
                "ORDER BY t.fine_amount DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("transaction_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getString("due_date"),
                        "$" + String.format("%.2f", rs.getDouble("fine_amount")),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading fines: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllIssuedBooks(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.transaction_id, u.username, b.title, t.issue_date, t.due_date, t.status " +
                "FROM transactions t " +
                "JOIN users u ON t.user_id = u.user_id " +
                "JOIN books b ON t.book_id = b.book_id " +
                "WHERE t.status = 'RESERVED' " +
                "ORDER BY t.transaction_id DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("transaction_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getString("issue_date"),
                        rs.getString("due_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading issued books: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMyBooks(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.transaction_id, b.title, t.due_date, t.status FROM transactions t JOIN books b ON t.book_id = b.book_id WHERE t.user_id = ? AND t.status = 'RESERVED'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
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

    private void loadBooksIntoTable(DefaultTableModel model, String searchQuery) {
        model.setRowCount(0);
        BookRecordManager manager = new BookRecordManager();
        List<Book> books = manager.searchBooks(searchQuery);
        for (Book b : books) {
            model.addRow(new Object[] { b.getBookId(), b.getTitle(), b.getAuthor(), b.getAvailableQuantity() });
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

        gbc.gridx = 0;
        gbc.gridy = 0;
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

    private JPanel createLogoutPanel() {
        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new GridBagLayout());

        JLabel logoutLabel = UIStyles.createTitleLabel("Are you sure you want to logout?");
        JButton logoutButton = new RoundedButton("Logout", UIStyles.DANGER_COLOR);

        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginGUI().setVisible(true);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(logoutLabel, gbc);

        gbc.gridy = 1;
        panel.add(logoutButton, gbc);

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
