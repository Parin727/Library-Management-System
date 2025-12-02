package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            if (conn != null) {
                String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT UNIQUE NOT NULL, " +
                        "password TEXT NOT NULL, " +
                        "role TEXT NOT NULL, " +
                        "library_card_id TEXT UNIQUE" +
                        ");";
                stmt.execute(createUsers);

                String createBooks = "CREATE TABLE IF NOT EXISTS books (" +
                        "book_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT NOT NULL, " +
                        "author TEXT NOT NULL, " +
                        "isbn TEXT UNIQUE, " +
                        "quantity INTEGER DEFAULT 1, " +
                        "available_quantity INTEGER DEFAULT 1" +
                        ");";
                stmt.execute(createBooks);

                String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                        "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "book_id INTEGER, " +
                        "issue_date TEXT, " +
                        "due_date TEXT, " +
                        "return_date TEXT, " +
                        "fine_amount REAL DEFAULT 0.00, " +
                        "status TEXT DEFAULT 'ISSUED', " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                        "FOREIGN KEY (book_id) REFERENCES books(book_id)" +
                        ");";
                stmt.execute(createTransactions);

                String createFeedback = "CREATE TABLE IF NOT EXISTS feedback (" +
                        "feedback_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "message TEXT, " +
                        "submission_date TEXT DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                        ");";
                stmt.execute(createFeedback);

                String insertAdmin = "INSERT OR IGNORE INTO users (username, password, role, library_card_id) " +
                        "VALUES ('admin', 'admin123', 'LIBRARIAN', 'LIB-ADMIN-001');";
                stmt.execute(insertAdmin);
                
                System.out.println("Database initialized successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
