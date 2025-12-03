import gui.LoginGUI;
import database.DatabaseSetup;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        DatabaseSetup.initialize();

        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}
