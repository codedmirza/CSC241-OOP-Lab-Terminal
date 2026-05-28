
/*Functionality: created an airline object Loads saved data, 
then opens first GUI StartFrame. File saving happens when window closes.*/ 
// import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//changes: added comments, i removed demo data thing in main class.

public class Main {

    public static void main(String[] args) {
        // // System look and feel (Windows/Mac/Linux native look)
        // try {
        //     UIManager.setLookAndFeel(
        //             UIManager.getSystemLookAndFeelClassName());
        // }
        // catch (Exception e) {
        //     System.out.println("Look and Feel not applied.");
        // }


        // Create the main system object
        AirlineSystem system = new AirlineSystem();

        // Load previously saved data (users, flights, etc.)
        FileHandler.loadSystem(system);
        // using SwingUtiklities is not necessory. 
        // Launch GUI on Event Dispatch Thread (Swing best practice)
       //previously it was this [ SwingUtilities.invokeLater(() -> new StartFrame(system).setVisible(true));]
       
       //so made it simple,below 
       // Open first window
        StartFrame frame = new StartFrame(system);
        frame.setVisible(true);
    }

}