// Main.java (V3 - GUI version)
// Loads saved data, then opens StartFrame. File saving happens when window closes.

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Use system look and feel (looks native on Windows/Mac/Linux)
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }

        AirlineSystem system = new AirlineSystem();

        // Load saved data
        FileHandler.loadSystem(system);

        // First-run seed
        if (system.getUsers().isEmpty() && system.getAirports().isEmpty()) {
            seedDemoData(system);
            FileHandler.saveSystem(system);
        }

        // Launch GUI on the Event Dispatch Thread (Swing rule)
        SwingUtilities.invokeLater(() -> new StartFrame(system).setVisible(true));
    }

    private static void seedDemoData(AirlineSystem system) {
        Admin admin = new Admin("A1", "Admin User", "0300-1234567",
                                "Islamabad", "admin", "admin123",
                                "admin@airline.com", "AERO2025");
        system.setAdmin(admin);

        Airport isb = new Airport("ISB", "Islamabad International",
                                  "Islamabad", "Pakistan", "Active");
        Airport khi = new Airport("KHI", "Jinnah International",
                                  "Karachi",   "Pakistan", "Active");
        Airport lhe = new Airport("LHE", "Allama Iqbal International",
                                  "Lahore",    "Pakistan", "Active");
        system.addAirport(isb);
        system.addAirport(khi);
        system.addAirport(lhe);

        Aircraft a1 = new Aircraft("AC100", "Boeing 737", 30,
                                   "PIA", "Good", "Available");
        Aircraft a2 = new Aircraft("AC200", "Airbus A320", 25,
                                   "AirBlue", "Good", "Available");
        system.addAircraft(a1);
        system.addAircraft(a2);

        Flight f1 = new Flight("PK101", isb, khi,
                               "2026-06-01 09:00", "2026-06-01 11:00",
                               18000, a1);
        Flight f2 = new Flight("PA202", lhe, isb,
                               "2026-06-02 14:00", "2026-06-02 15:30",
                               12000, a2);
        a1.setAvailabilityStatus("Assigned");
        a2.setAvailabilityStatus("Assigned");
        system.addFlight(f1);
        system.addFlight(f2);

        User user = new User("U1", "Faizan Ahmad", "0312-1112233", "Lahore",
                             "faizan", "1234", "faizan@mail.com",
                             "3520112345671", "Lahore");
        system.addUser(user);

        Pilot pilot = new Pilot("P1", "Ahmed Khan", "0301-1112233", "Islamabad",
                                "EMP100", "Morning", "LIC-123", 8);
        CabinCrew cc = new CabinCrew("P2", "Sara Ali", "0303-2223344", "Karachi",
                                     "EMP101", "Evening", "Economy");
        system.addCrew(pilot);
        system.addCrew(cc);
    }
}
