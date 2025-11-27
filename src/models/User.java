package models;

public abstract class User {
    protected int userId;
    protected String username;
    protected String password;
    protected String role;
    protected String libraryCardId;

    public User(int userId, String username, String password, String role, String libraryCardId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.libraryCardId = libraryCardId;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getLibraryCardId() { return libraryCardId; }
    
    public abstract void displayDashboard();
}
