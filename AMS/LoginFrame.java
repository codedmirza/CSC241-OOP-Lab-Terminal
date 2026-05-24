// LoginFrame.java
// Login window. Same frame is used for both user and admin (parameterized by role).
// On success, opens the right dashboard frame.

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private AirlineSystem system;
    private String role;
    private StartFrame parent;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(AirlineSystem system, String role, StartFrame parent) {
        this.system = system;
        this.role   = role;
        this.parent = parent;

        setTitle((role.equals("admin") ? "Admin" : "User") + " Login");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Going back to start frame when this closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                parent.setVisible(true);
            }
        });

        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel(
            (role.equals("admin") ? "Admin" : "User") + " Login",
            SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(30, 60, 120));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);
        main.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            parent.setVisible(true);
            dispose();
        });
        buttons.add(loginBtn);
        buttons.add(backBtn);
        main.add(buttons, BorderLayout.SOUTH);

        setContentPane(main);

        // Press Enter in password field = login
        passwordField.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword()).trim();

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.",
                "Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Account acc = null;
        if (role.equals("admin")) {
            Admin a = system.getAdmin();
            if (a != null && a.login(u, p)) acc = a;
        } else {
            for (User user : system.getUsers()) {
                if (user.login(u, p)) { acc = user; break; }
            }
        }

        if (acc == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Success → open dashboard
        if (acc instanceof Admin) {
            new AdminDashboardFrame((Admin) acc, system, parent).setVisible(true);
        } else {
            new UserDashboardFrame((User) acc, system, parent).setVisible(true);
        }
        dispose();
    }
}
