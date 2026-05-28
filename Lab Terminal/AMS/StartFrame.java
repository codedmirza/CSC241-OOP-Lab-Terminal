// StartFrame.java (Updated - Better Colors + Black Text)
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class StartFrame extends JFrame {

    private AirlineSystem system;

    public StartFrame(AirlineSystem system) {
        this.system = system;

        setTitle("Airline Management & Booking System");
        setSize(580, 520);                   
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Save file/system data when window is closing
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
        main.setBackground(new Color(245, 248, 252));   // Light background

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        // below not necessory
        // titlePanel.setOpaque(false);

        JLabel welcome = new JLabel( "WELCOME TO", SwingConstants.CENTER);

        welcome.setFont(new Font("Serif", Font.BOLD, 22)   );
        welcome.setForeground(new Color(30, 60, 120));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Title
        JLabel title = new JLabel("______Airline Management & Booking System______", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        title.setForeground(new Color(0, 51, 102));     
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
  
        titlePanel.add(Box.createVerticalStrut(20));
        titlePanel.add(welcome);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(title);

        // Add to main panel
        main.add(titlePanel, BorderLayout.NORTH);

        // Subtitle
        JLabel subtitle = new JLabel("Choose an option", SwingConstants.CENTER);
        subtitle.setFont(new Font("Times New Roman", Font.PLAIN, 19));

        // Buttons panel 
        JPanel center = new JPanel(new GridLayout(5, 1, 12, 12));
        center.setBorder(BorderFactory.createEmptyBorder(10, 60, 40, 60));
        // below not necessory 
        // center.setOpaque(false);

        center.add(subtitle);
        
      // USER LOGIN BUTTON
       JButton userLoginButton = makeButton("USER Login");

        userLoginButton.addActionListener(new ActionListener() {

       public void actionPerformed(java.awt.event.ActionEvent e) {

        openLogin("user");
      }
       });

       center.add(userLoginButton);


       // USER SIGNUP BUTTON
       JButton userSignupButton = makeButton("USER SignUp");

      userSignupButton.addActionListener(new ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent e) {

        openSignUp("user");
        }
        });

        center.add(userSignupButton);


       // ADMIN LOGIN BUTTON
      JButton adminLoginButton = makeButton("ADMIN Login");

      adminLoginButton.addActionListener(new ActionListener() {

     public void actionPerformed(java.awt.event.ActionEvent e) {

        openLogin("admin");
        }
        });

       center.add(adminLoginButton);

       // ADMIN SIGNUP BUTTON
       JButton adminSignupButton = makeButton("ADMIN SignUp");
       
       adminSignupButton.addActionListener(new ActionListener() {
       
           public void actionPerformed(java.awt.event.ActionEvent e) {
       
               openSignUp("admin");
           }
       });
       
       center.add(adminSignupButton);

        // made the above changes instead of the below for simplification also makeButton() is changed
        
        // center.add(makeButton("USER Login",     e -> openLogin("user")));
        // center.add(makeButton("USER SignUp",   e -> openSignUp("user")));
        // center.add(makeButton("ADMIN Login",    e -> openLogin("admin")));
        // center.add(makeButton("ADMIN SignUp",  e -> openSignUp("admin"))); 

        main.add(center, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("BY     Ashar    Usman     Zarak     Faizan", SwingConstants.CENTER);
        footer.setFont(new Font("Times New Roman", Font.ITALIC, 17));
        footer.setForeground(Color.GRAY);
        main.add(footer, BorderLayout.SOUTH);

        setContentPane(main);
    }

    // private JButton makeButton(String text, ActionListener action) {
    //     JButton b = new JButton(text);
    //     b.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    //     b.setFocusPainted(false);
        
    //     // IMPROVED COLORS (as per your request)
    //     b.setBackground(new Color(230, 240, 255));   // Light blue background
    //     b.setForeground(Color.BLACK);                // Black text (exactly as you wanted)
    //     b.setBorder(BorderFactory.createCompoundBorder(
    //         BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
    //         BorderFactory.createEmptyBorder(12, 0, 12, 0)
    //     ));
        
    //     b.addActionListener(action);
    //     return b;
    // }

    // instead of the above made it simple 
   private JButton makeButton(String text) {

    JButton button = new JButton(text);

    button.setFont(new Font("Times New Roman", Font.PLAIN, 15));

    // Remove focus line
    button.setFocusPainted(false);

    // Prevent button focus
    button.setFocusable(false);

    button.setBackground(new Color(230, 240, 255));

    button.setForeground(Color.BLACK);

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