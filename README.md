# Library Management System Walkthrough (SQLite)

## Prerequisites
- Java JDK 8+ installed.
- SQLite JDBC Driver (e.g., `sqlite-jdbc-3.x.x.jar`) added to the classpath.

## Database Setup
- The application automatically creates a `library.db` file in the project root when run for the first time.
- It also inserts a default Librarian account:
  - Username: `admin`
  - Password: `admin123`

## Running the Application

1. Compile the Java files. Ensure the SQLite JDBC driver is in your classpath:
   - On Windows:
     ```
     cd src
     javac -cp ".;path/to/sqlite-jdbc.jar" Main.java database/*.java models/*.java logic/*.java gui/*.java
     ```
   - On Linux/Mac (use `:` instead of `;` for the classpath separator):
     ```
     cd src
     javac -cp ".:path/to/sqlite-jdbc.jar" Main.java database/*.java models/*.java logic/*.java gui/*.java
     ```

2. Run the `Main` class:
  ```
  java -cp ".;path/to/sqlite-jdbc.jar" Main
  ```
(On Linux/Mac, use `:` instead of `;`.)

## Features

### Login
- Use the default librarian credentials (`admin` / `admin123`) to log in as a Librarian.
- Or click “Register” to create a new Student or Staff account.

### User Dashboard (Student/Staff)
- Reserve Book: Search for books and reserve them.
- My Books / Renew: View reserved books and renew them if not overdue.
- Pay Fine: Simulate paying fines.
- Feedback: Submit feedback to the library.

### Librarian Dashboard
- Add Book: Add new book records to the database.
- Update/Delete: Manage existing book records.
- Refresh: Reload the book list.

## Troubleshooting
- “SQLite JDBC Driver not found”:
- Make sure you have downloaded the SQLite JDBC jar file and included it in your classpath when running.
- Database Error:
- Ensure the application has write permissions to the directory to create `library.db`.
