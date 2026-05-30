// UserDashboardFrame.java
// Logged-in user's main window. Has buttons for each user action.
// Most actions open dialogs; booking opens a separate frame.

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserDashboardFrame extends JFrame {

    private User user;
    private AirlineSystem system;
    private StartFrame startFrame;
    private JTabbedPane tabs;

    public UserDashboardFrame(User user, AirlineSystem system, StartFrame startFrame) {
        this.user       = user;
        this.system     = system;
        this.startFrame = startFrame;

        setTitle("User Dashboard - " + user.getName());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                FileHandler.saveSystem(system);
                startFrame.setVisible(true);
            }
        });

        UserGUI();
    }
    private void UserGUI() {
        JPanel main = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(30, 60, 120));
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel hello = new JLabel("Welcome! " + user.getName());
        hello.setForeground(Color.WHITE);
        hello.setFont(new Font("Times New Roman", Font.BOLD, 16));
        top.add(hello, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            user.logout();
            FileHandler.saveSystem(system);
            startFrame.setVisible(true);
            dispose();
        });
        //delete and update user buttons to create
        top.add(logoutBtn, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        
        tabs = new JTabbedPane();
        tabs.addTab("Search Flights",     FlightSearchPanel());
        tabs.addTab("Book Ticket",        BookingPanel());
        tabs.addTab("My Bookings",        BookingDisplayPanel());
        tabs.addTab("Cancel & Refund",    CancelBookingPanel());
        tabs.addTab("Notifications",      NotificationsPanel());

        main.add(tabs, BorderLayout.CENTER);
        setContentPane(main);
    }

    // ===================== TAB 1: Search flights =====================
    private JPanel   FlightSearchPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filter = new JPanel();
        filter.add(new JLabel("Source City:"));
        JTextField srcField = new JTextField(10);
        filter.add(srcField);
        filter.add(new JLabel("Destination:"));
        JTextField dstField = new JTextField(10);
        filter.add(dstField);
        JButton search = new JButton("Search");
        filter.add(search);
        JButton showAll = new JButton("Show All");
        filter.add(showAll);

        String[] cols = {"Flight ID", "From", "To", "Departure", "Arrival",
                         "Fare (Rs)", "Available", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        Runnable refresh = () -> {
            model.setRowCount(0);
            String s = srcField.getText().trim();
            String d = dstField.getText().trim();
            List<Flight> flights = (s.isEmpty() || d.isEmpty())
                ? system.searchFlights() : system.searchFlights(s, d);
            for (Flight f : flights) {
                model.addRow(new Object[]{
                    f.getFlightId(),
                    f.getSource().getCity(),
                    f.getDestination().getCity(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    f.getFare(),
                    f.getAvailableSeatsCount() + "/" + f.getCapacity(),
                    f.getStatus()
                });
            }
        };
        search.addActionListener(e -> refresh.run());
        showAll.addActionListener(e -> { srcField.setText(""); dstField.setText(""); refresh.run(); });
        refresh.run();

        p.add(filter, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        // Refresh whenever this tab is shown
        tabsAddRefreshListener(p, refresh);

        return p;
    }

    // ===================== TAB 2: Book ticket =====================
    private JPanel BookingPanel(){
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel info = new JLabel(
            "<html><body style='width:500px'>"
          + "Click the button below to start a new booking. "
          + "You will choose a flight, select a seat, enter luggage weight, "
          + "and pay using your preferred method."
          + "</body></html>");
        info.setFont(new Font("Arial", Font.PLAIN, 13));
        p.add(info, BorderLayout.NORTH);

        JButton start = new JButton("Start New Booking");
        start.setFont(new Font("Arial", Font.BOLD, 14));
        start.addActionListener(e -> new BookingFrame(user, system, this).setVisible(true));

        JPanel center = new JPanel();
        center.add(start);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ===================== TAB 3: My bookings =====================
    private JPanel BookingDisplayPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Booking ID", "Flight", "Seat", "Date", "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        JButton refreshBtn = new JButton("Refresh");
        JButton viewTicketBtn = new JButton("View Ticket");

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Booking b : user.getBookings()) {
                model.addRow(new Object[]{
                    b.getBookingId(),
                    b.getFlight().getFlightId(),
                    b.getSeat().getSeatNumber() + " (" + b.getSeat().getClassType() + ")",
                    b.getBookingDate(),
                    "Rs " + b.getTotalAmount(),
                    b.getStatus()
                });
            }
        };
        refreshBtn.addActionListener(e -> refresh.run());
        viewTicketBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booking first."); return; }
            Booking b = user.getBookings().get(row);
            if (b.getTicket() == null) {
                JOptionPane.showMessageDialog(this, "No ticket available for this booking.");
                return;
            }
            JTextArea area = new JTextArea(b.getTicket().viewTicket());
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Ticket", JOptionPane.PLAIN_MESSAGE);
        });
        refresh.run();

        JPanel bottom = new JPanel();
        bottom.add(refreshBtn);
        bottom.add(viewTicketBtn);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        tabsAddRefreshListener(p, refresh);
        return p;
    }

    // ===================== TAB 4: Cancel / Refund =====================
    private JPanel CancelBookingPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Booking ID", "Flight", "Seat", "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Booking b : user.getBookings()) {
                if (b.getStatus().equals("Confirmed")) {
                    model.addRow(new Object[]{
                        b.getBookingId(),
                        b.getFlight().getFlightId(),
                        b.getSeat().getSeatNumber(),
                        "Rs " + b.getTotalAmount(),
                        b.getStatus()
                    });
                }
            }
        };
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh.run());

        JButton cancelBtn = new JButton("Cancel & Request Refund");
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a confirmed booking first.");
                return;
            }
            // Find matching booking
            String bookingId = (String) model.getValueAt(row, 0);
            Booking b = system.findBooking(bookingId);
            if (b == null) return;

            String reason = JOptionPane.showInputDialog(this, "Reason for cancellation:");
            if (reason == null || reason.trim().isEmpty()) return;

            b.cancelBooking();
            system.addRefundRequest(new RefundRequest(b, reason));
            FileHandler.saveSystem(system);

            JOptionPane.showMessageDialog(this,
                "Refund request submitted. Status: PENDING.\n"
              + "Admin will review your request shortly.",
                "Submitted", JOptionPane.INFORMATION_MESSAGE);
            refresh.run();
        });

        JPanel bottom = new JPanel();
        bottom.add(refreshBtn);
        bottom.add(cancelBtn);

        refresh.run();
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        tabsAddRefreshListener(p, refresh);
        return p;
    }

    // ===================== TAB 5: Notifications =====================
    private JPanel NotificationsPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 13));

        Runnable refresh = () -> {
            listModel.clear();
            List<Notification> mine = system.getNotificationsFor(user);
            if (mine.isEmpty()) {
                listModel.addElement("No notifications.");
            } else {
                for (Notification n : mine) {
                    listModel.addElement(n.toString());
                    n.markAsRead();
                }
            }
        };
        refresh.run();

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh.run());

        p.add(new JScrollPane(list), BorderLayout.CENTER);
        p.add(refreshBtn, BorderLayout.SOUTH);

        tabsAddRefreshListener(p, refresh);
        return p;
    }

    
    
    // Helper: refresh the panel whenever its tab is selected
    private void tabsAddRefreshListener(JPanel panel, Runnable refresh) {
        SwingUtilities.invokeLater(() -> {
            tabs.addChangeListener(e -> {
                if (tabs.getSelectedComponent() == panel) refresh.run();
            });
        });
    }

    // Called by BookingFrame when a booking is completed, to refresh tables
    public void refreshAllTables() {
        // Re-build to reflect latest data
        getContentPane().removeAll();
        UserGUI();
        revalidate();
        repaint();
    }
}
