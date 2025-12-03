package models;

public class Staff extends User {
    public Staff(int userId, String username, String password, String libraryCardId) {
        super(userId, username, password, "STAFF", libraryCardId);
    }

    @Override
    public void displayDashboard() {
        System.out.println("Displaying Staff Dashboard...");
    }
}
