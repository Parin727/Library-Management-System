# Library Management System Walkthrough (SQLite)

## Prerequisites
- Java JDK 8+ installed.
- SQLite JDBC Driver (e.g., `sqlite-jdbc-3.x.x.jar`) added to the classpath.

---

## Database Setup
- The application automatically creates a `library.db` file in the project root when run for the first time.
- It also inserts a default Librarian account:
  - **Username:** `admin`
  - **Password:** `admin123`

---

## Running the Application

### 1. Compile the Java files
Ensure the SQLite JDBC driver is in your classpath.

```cmd
cd src
javac -cp ".;path/to/sqlite-jdbc.jar" Main.java database/*.java models/*.java logic/*.java gui/*.java
