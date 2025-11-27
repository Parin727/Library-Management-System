import gui.LoginGUI;
import database.DatabaseSetup;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize Database
        DatabaseSetup.initialize();

        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}
