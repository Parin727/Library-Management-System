package models;

public class Student extends User {
    public Student(int userId, String username, String password, String libraryCardId) {
        super(userId, username, password, "STUDENT", libraryCardId);
    }

    @Override
    public void displayDashboard() {
        System.out.println("Displaying Student Dashboard...");
    }
}
