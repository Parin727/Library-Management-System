package logic;

import database.DBConnection;
import java.sql.*;
import java.time.LocalDate;

public class TransactionManager {

    public boolean reserveBook(int userId, int bookId) {
        BookRecordManager bookManager = new BookRecordManager();
        var book = bookManager.getBookById(bookId);
        if (book == null || book.getAvailableQuantity() <= 0) {
            return false;
        }

        String sql = "INSERT INTO transactions (user_id, book_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, 'RESERVED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.setString(4, LocalDate.now().plusDays(7).toString()); // 1 week reservation
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                book.setAvailableQuantity(book.getAvailableQuantity() - 1);
                bookManager.updateBook(book);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean renewBook(int transactionId) {
        // Check if already overdue
        String checkSql = "SELECT due_date FROM transactions WHERE transaction_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String dueDateStr = rs.getString("due_date");
                LocalDate dueDate = LocalDate.parse(dueDateStr);
                if (LocalDate.now().isAfter(dueDate)) {
                    System.out.println("Error: Cannot renew overdue book.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE transactions SET due_date=? WHERE transaction_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().plusDays(14).toString()); // Extend by 14 days
            pstmt.setInt(2, transactionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean payFine(int transactionId, double amount) {
        String sql = "UPDATE transactions SET fine_amount = fine_amount - ? WHERE transaction_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, transactionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
