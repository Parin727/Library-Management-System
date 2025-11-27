package gui;

import logic.BookRecordManager;
import models.Book;
import models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel - Actions
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Book");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh List");
        
        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Qty", "Available"};
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> showAddBookDialog());
        updateButton.addActionListener(e -> showUpdateBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        refreshButton.addActionListener(e -> loadBooks());

        loadBooks();
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookManager.searchBooks(""); 
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getQuantity(), b.getAvailableQuantity()
            });
        }
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Add Book", true);
        dialog.setLayout(new GridLayout(6, 2));
        dialog.setSize(400, 300);

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField qtyField = new JTextField();

        dialog.add(new JLabel("Title:")); dialog.add(titleField);
        dialog.add(new JLabel("Author:")); dialog.add(authorField);
        dialog.add(new JLabel("ISBN:")); dialog.add(isbnField);
        dialog.add(new JLabel("Quantity:")); dialog.add(qtyField);

        JButton saveButton = new JButton("Save");
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
        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private void showUpdateBookDialog() {
        int row = bookTable.getSelectedRow();
        if (row == -1) return;

        int bookId = (int) tableModel.getValueAt(row, 0);
        Book book = bookManager.getBookById(bookId);

        JDialog dialog = new JDialog(this, "Update Book", true);
        dialog.setLayout(new GridLayout(6, 2));
        dialog.setSize(400, 300);

        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());
        JTextField qtyField = new JTextField(String.valueOf(book.getQuantity()));
        JTextField availField = new JTextField(String.valueOf(book.getAvailableQuantity()));

        dialog.add(new JLabel("Title:")); dialog.add(titleField);
        dialog.add(new JLabel("Author:")); dialog.add(authorField);
        dialog.add(new JLabel("Total Qty:")); dialog.add(qtyField);
        dialog.add(new JLabel("Available Qty:")); dialog.add(availField);

        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                int avail = Integer.parseInt(availField.getText());
                Book newBook = new Book(bookId, titleField.getText(), authorField.getText(), book.getIsbn(), qty, avail);
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
        dialog.add(updateBtn);
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
}
