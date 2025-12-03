package models;

public class Librarian extends User {
    public Librarian(int userId, String username, String password, String libraryCardId) {
        super(userId, username, password, "LIBRARIAN", libraryCardId);
    }

    @Override
    public void displayDashboard() {
        System.out.println("Displaying Librarian Dashboard...");
    }
}
