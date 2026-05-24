// StartFrame.java
// Main starting window for Airline Management & Booking System
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
public class StartFrame extends JFrame {
    private AirlineSystem system;
    public StartFrame(AirlineSystem system) {
        this.system = system;
        // Window settings
        setTitle("Airline Management & Booking System");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Save system when closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                FileHandler.saveSystem(system);
            }
        });
        buildUI();
    }
    private void buildUI() {
        // Main panel
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 248, 252));
        // ================= TITLE =================
        JLabel title = new JLabel(
                "Airline Management & Booking System",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(20, 50, 120));
        title.setBorder(
                BorderFactory.createEmptyBorder(30, 10, 10, 10)
        );
        main.add(title, BorderLayout.NORTH);
        // ================= CENTER PANEL =================
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(5, 1, 10, 15));
        center.setBorder(
                BorderFactory.createEmptyBorder(20, 70, 30, 70)
        );
        center.setOpaque(false);
        // Subtitle
        JLabel subtitle = new JLabel(
                "Welcome — please choose an option",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        center.add(subtitle);
        // Buttons
        center.add(makeButton(
                "User Login",
                e -> openLogin("user")
        ));
        center.add(makeButton(
                "User Sign Up",
                e -> openSignUp("user")
        ));
        center.add(makeButton(
                "Admin Login",
                e -> openLogin("admin")
        ));
        center.add(makeButton(
                "Admin Sign Up",
                e -> openSignUp("admin")
        ));
        main.add(center, BorderLayout.CENTER);

        // ================= FOOTER =================
        JLabel footer = new JLabel(
                "Faizan Ahmad, Ashar Sheraz, M.Usman Azeem, Zarak Khan ",
                SwingConstants.CENTER
        );
        footer.setFont(new Font("Arial", Font.ITALIC, 13));
        footer.setForeground(Color.GRAY);
        footer.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        );
        main.add(footer, BorderLayout.SOUTH);
        // Set main panel
        setContentPane(main);
    }
    // ================= BUTTON STYLE =================
    private JButton makeButton(String text, ActionListener action) {
        JButton b = new JButton(text);
        // Font
        b.setFont(new Font("Arial", Font.BOLD, 16));
        // Colors
        b.setBackground(new Color(20, 50, 120));
        b.setForeground(Color.WHITE);
        // Button appearance
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        // Size
        b.setPreferredSize(new Dimension(220, 45));
        // Cursor effect
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Action
        b.addActionListener(action);
        return b;
    }
    // ================= OPEN LOGIN =================
    private void openLogin(String role) {
        new LoginFrame(system, role, this).setVisible(true);
        setVisible(false);
    }
    // ================= OPEN SIGNUP =================
    private void openSignUp(String role) {
        new SignUpFrame(system, role, this).setVisible(true);
        setVisible(false);
    }
}