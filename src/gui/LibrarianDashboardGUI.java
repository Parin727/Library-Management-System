package gui;

import logic.BookRecordManager;
import models.Book;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class LibrarianDashboardGUI extends JFrame {
    private User user;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookRecordManager bookManager;

    public LibrarianDashboardGUI(User user) {
        this.user = user;
        this.bookManager = new BookRecordManager();
        setTitle("Librarian Dashboard - " + user.getUsername());
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UIStyles.applyGlobalStyles(this);
        setLayout(new BorderLayout());

        JPanel topPanel = UIStyles.createPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton addButton = new RoundedButton("Add Book", UIStyles.SUCCESS_COLOR);
        JButton updateButton = new RoundedButton("Update Selected", UIStyles.WARNING_COLOR);
        JButton deleteButton = new RoundedButton("Delete Selected", UIStyles.DANGER_COLOR);
        JButton refreshButton = new RoundedButton("Refresh List", UIStyles.INFO_COLOR);
        JButton logoutButton = new RoundedButton("Logout", UIStyles.DANGER_COLOR);

        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);
        topPanel.add(logoutButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = { "ID", "Title", "Author", "ISBN", "Qty", "Available" };
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);
        styleTable(bookTable);

        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> showAddBookDialog());
        updateButton.addActionListener(e -> showUpdateBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        refreshButton.addActionListener(e -> loadBooks());
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginGUI().setVisible(true);
        });

        loadBooks();
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookManager.searchBooks("");
        for (Book b : books) {
            tableModel.addRow(new Object[] {
                    b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getQuantity(), b.getAvailableQuantity()
            });
        }
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Add Book", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField titleField = UIStyles.createTextField();
        JTextField authorField = UIStyles.createTextField();
        JTextField isbnField = UIStyles.createTextField();
        JTextField qtyField = UIStyles.createTextField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(UIStyles.createLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField.setPreferredSize(new Dimension(200, 30));
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIStyles.createLabel("Author:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIStyles.createLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIStyles.createLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        panel.add(qtyField, gbc);

        JButton saveButton = new RoundedButton("Save", UIStyles.SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                Book book = new Book(0, titleField.getText(), authorField.getText(), isbnField.getText(), qty, qty);
                if (bookManager.addBook(book)) {
                    JOptionPane.showMessageDialog(dialog, "Book Added!");
                    dialog.dispose();
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error adding book.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Quantity");
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showUpdateBookDialog() {
        int row = bookTable.getSelectedRow();
        if (row == -1)
            return;

        int bookId = (int) tableModel.getValueAt(row, 0);
        Book book = bookManager.getBookById(bookId);

        JDialog dialog = new JDialog(this, "Update Book", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        JPanel panel = UIStyles.createPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField titleField = UIStyles.createTextField();
        titleField.setText(book.getTitle());
        JTextField authorField = UIStyles.createTextField();
        authorField.setText(book.getAuthor());
        JTextField qtyField = UIStyles.createTextField();
        qtyField.setText(String.valueOf(book.getQuantity()));
        JTextField availField = UIStyles.createTextField();
        availField.setText(String.valueOf(book.getAvailableQuantity()));

        titleField.setPreferredSize(new Dimension(200, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(UIStyles.createLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIStyles.createLabel("Author:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIStyles.createLabel("Total Qty:"), gbc);
        gbc.gridx = 1;
        panel.add(qtyField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIStyles.createLabel("Available Qty:"), gbc);
        gbc.gridx = 1;
        panel.add(availField, gbc);

        JButton updateBtn = new RoundedButton("Update", UIStyles.WARNING_COLOR);
        updateBtn.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                int avail = Integer.parseInt(availField.getText());
                Book newBook = new Book(bookId, titleField.getText(), authorField.getText(), book.getIsbn(), qty,
                        avail);
                if (bookManager.updateBook(newBook)) {
                    JOptionPane.showMessageDialog(dialog, "Book Updated!");
                    dialog.dispose();
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error updating book.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Numbers");
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(updateBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook() {
        int row = bookTable.getSelectedRow();
        if (row != -1) {
            int bookId = (int) tableModel.getValueAt(row, 0);
            if (bookManager.deleteBook(bookId)) {
                JOptionPane.showMessageDialog(this, "Book Deleted!");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting book.");
            }
        }
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
