
/*Functionality: created an airline object Loads saved data, 
then opens first GUI StartFrame. File saving happens when window closes.*/ 
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//changes: added comments, i removed demo data thing in main class.

public class Main {

    public static void main(String[] args) {
               // System look and feel (Windows/Mac/Linux native look)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Create the main system object
        AirlineSystem system = new AirlineSystem();

        // Load previously saved data (users, flights, etc.)
        FileHandler.loadSystem(system);

        // Launch GUI on Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> new StartFrame(system).setVisible(true));
    }
}