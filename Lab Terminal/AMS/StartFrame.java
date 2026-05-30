// StartFrame.java
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class StartFrame extends JFrame {

    private AirlineSystem system;

    public StartFrame(AirlineSystem system) {
        this.system = system;

        setTitle("Airline Management & Booking System");
        setSize(580, 560);                   
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                FileHandler.saveSystem(system);
            }
        });

        startUI(); 
    }

    private void startUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(245, 248, 252));   

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
  
        titlePanel.add(Box.createVerticalStrut(35));

        JLabel title = new JLabel("Airline Management & Booking System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));     
        title.setForeground(new Color(12, 54, 124));     
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(title);

        titlePanel.add(Box.createVerticalStrut(30));

        JLabel subtitle = new JLabel("Welcome — please choose an option", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(Color.BLACK);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(subtitle);

        main.add(titlePanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(4, 1, 15, 15));
        center.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));
        center.setOpaque(false);

        JButton userLoginButton = makeButton("User Login");
        userLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                openLogin("user");
            }
        });
      
        JButton userSignupButton = makeButton("User Sign Up");
        userSignupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                openSignUp("user");
            }
        });
            
        JButton adminLoginButton = makeButton("Admin Login");
        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                openLogin("admin");
            }
        });

        JButton adminSignupButton = makeButton("Admin Sign Up");
        adminSignupButton.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(java.awt.event.ActionEvent e) {
                openSignUp("admin");
            }
        });

        center.add(userLoginButton);
        center.add(userSignupButton);
        center.add(adminLoginButton);
        center.add(adminSignupButton);
        main.add(center, BorderLayout.CENTER);

        JLabel footer = new JLabel("Faizan Ahmad, Ashar Sheraz, M.Usman Azeem, Zarak Khan", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.ITALIC, 14));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0)); 
        main.add(footer, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JButton makeButton(String text) {
        // Using an anonymous inner class override to completely bypass OS look-and-feel painting bugs
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw solid background color
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                // Let Swing handle text placement on top of our solid background
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setFocusable(false);
        
        // This is critical: tells Swing not to overlay default native Windows themes over our colors
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        button.setBackground(new Color(18, 54, 126));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        return button;
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