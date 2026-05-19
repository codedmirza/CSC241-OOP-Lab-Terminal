// StartFrame.java
// The very first window. User chooses User Login, User Sign Up, Admin Login,
// or Admin Sign Up. Each button opens the appropriate frame.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartFrame extends JFrame {

    private AirlineSystem system;

    public StartFrame(AirlineSystem system) {
        this.system = system;

        setTitle("Airline Management & Booking System");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Save data when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                FileHandler.saveSystem(system);
            }
        });

        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 248, 252));

        // Title at top
        JLabel title = new JLabel("Airline Management & Booking System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(30, 60, 120));
        title.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
        main.add(title, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Welcome — please choose an option", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));

        // Buttons in center
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(5, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));
        center.setOpaque(false);

        center.add(subtitle);
        center.add(makeButton("User Login",       e -> openLogin("user")));
        center.add(makeButton("User Sign Up",     e -> openSignUp("user")));
        center.add(makeButton("Admin Login",      e -> openLogin("admin")));
        center.add(makeButton("Admin Sign Up",    e -> openSignUp("admin")));

        main.add(center, BorderLayout.CENTER);

        JLabel footer = new JLabel("CSC241 OOP Project", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.ITALIC, 11));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        main.add(footer, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JButton makeButton(String text, ActionListener action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 14));
        b.setFocusPainted(false);
        b.setBackground(new Color(100, 140, 200));
        b.setForeground(Color.WHITE);
        b.addActionListener(action);
        return b;
    }

    private void openLogin(String role) {
        new LoginFrame(system, role, this).setVisible(true);
        setVisible(false);
    }

    private void openSignUp(String role) {
        new SignUpFrame(system, role, this).setVisible(true);
        setVisible(false);
    }
}
