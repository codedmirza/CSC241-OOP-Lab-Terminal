// Authentication.java (GUI version)
// Credentials are passed in as parameters (from text fields) instead of
// being read from Scanner.

public class Authentication {

    private AirlineSystem system;
    public static final String ADMIN_SECRET_KEY = "AERO2025";

    public Authentication(AirlineSystem system) {
        this.system = system;
    }

    public Account login(String username, String password, String role) {
        if (role.equalsIgnoreCase("admin")) {
            Admin a = system.getAdmin();
            if (a != null && a.login(username, password)) return a;
        } else {
            for (User u : system.getUsers()) {
                if (u.login(username, password)) return u;
            }
        }
        return null;
    }

    public String signUpUser(String username, String password, String name,
                             String email, String phone, String address,
                             String cnic, String city) {
        if (usernameTaken(username))      return "Username already exists.";
        if (password == null || password.length() < 4)
                                          return "Password must be at least 4 characters.";
        if (name.isEmpty() || email.isEmpty()) return "Name and email cannot be empty.";

        String id = "U" + (system.getUsers().size() + 1);
        User user = new User( name, phone, address, username, password,
                             email, cnic, city);
        system.addUser(user);
        return null;
    }

    public String signUpAdmin(String secretKey, String username, String password,
                              String name, String email, String phone, String address) {
        if (system.getAdmin() != null)           return "Admin already exists.";
        if (!secretKey.equals(ADMIN_SECRET_KEY)) return "Wrong secret key.";
        if (usernameTaken(username))             return "Username already exists.";

        Admin admin = new Admin( name, phone, address, username,
                                password, email, secretKey);
        system.setAdmin(admin);
        return null;
    }

    private boolean usernameTaken(String u) {
        for (User user : system.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(u)) return true;
        }
        return system.getAdmin() != null
            && system.getAdmin().getUsername().equalsIgnoreCase(u);
    }
}
