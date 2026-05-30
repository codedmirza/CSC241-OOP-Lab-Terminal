// AdminDashboardFrame.java
// Admin's main window with tabs for each major operation:
// Airports, Aircrafts, Flights, Crew, Bookings, Refunds, Reports.

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminDashboardFrame extends JFrame {

    private Admin admin; // the logged-in admin user
    private AirlineSystem system;
    private StartFrame startFrame;

    public AdminDashboardFrame(Admin admin, AirlineSystem system, StartFrame startFrame) {
        this.admin      = admin;
        this.system     = system;
        this.startFrame = startFrame;

        setTitle("Admin Dashboard_ " + admin.getName());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                FileHandler.saveSystem(system);
                startFrame.setVisible(true);
            }
        });

        AdminGUI();
    }

    private void AdminGUI() {
        JPanel main = new JPanel(new BorderLayout());

    
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(30, 60, 120));
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel hello = new JLabel("Admin Dashboard_ " + admin.getName());
        hello.setForeground(Color.WHITE);
        hello.setFont(new Font("Arial", Font.BOLD, 18));
        top.add(hello, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton updateBtn = new JButton("Update Profile");
        updateBtn.addActionListener(e -> updateAdminProfile());

        JButton deleteBtn = new JButton("Delete Account");
        deleteBtn.setBackground(new Color(220, 50, 50));
        deleteBtn.addActionListener(e -> deleteAdminAccount());

        JButton logoutBtun = new JButton("Logout");
        logoutBtun.addActionListener(e -> {
            admin.logout();
            FileHandler.saveSystem(system);
            startFrame.setVisible(true);
            dispose();
        });

        rightPanel.add(updateBtn);
        rightPanel.add(deleteBtn);
        rightPanel.add(logoutBtun);
        top.add(rightPanel, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Airports",   AirportsTab());
        tabs.addTab("Manage Aircrafts",  AircraftsTab());
        tabs.addTab("Manage Flights",    FlightsTab());
        tabs.addTab("Manage Crew",       CrewTab());
        tabs.addTab("Manage Bookings",   BookingsTab());
        tabs.addTab("Manage Refunds",    RefundsTab());
        tabs.addTab("Reports",           ReportsTab());

        main.add(tabs, BorderLayout.CENTER);
        setContentPane(main);
    }

    // 1. Managing airports (adding, updating, deleting, listing)
    private JPanel AirportsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Code", "Name", "City", "Country", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { 
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model); 

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Airport a : system.getAirports()) {
                model.addRow(new Object[]{ a.getAirportCode(), a.getName(), a.getCity(),
                                           a.getCountry(), a.getStatus()
                });
            }
        };
        refresh.run();

        JPanel buttons = new JPanel();
        JButton addBtn    = new JButton("Add Airport");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> {
            JTextField code = new JTextField(), name = new JTextField(), city = new JTextField(), country = new JTextField();

            Object[] msg = {"Code:", code, "Name:", name, "City:", city, "Country:", country};
            int r = JOptionPane.showConfirmDialog(this, msg, "Add a new Airport", JOptionPane.OK_CANCEL_OPTION);

            if (r != JOptionPane.OK_OPTION) return;

            String c = code.getText().trim().toUpperCase();
            
            if (c.isEmpty() || system.findAirport(c) != null) {
                err("Airport already exists or invalid code."); return;
            }

            // New airports default safely to "Active"
            system.addAirport(new Airport(c, name.getText().trim(), city.getText().trim(), country.getText().trim(), "Active"));
            FileHandler.saveSystem(system);
            refresh.run();
        });

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select an airport first in the table."); return; }

            String c = (String) model.getValueAt(row, 0);
            Airport a = system.findAirport(c);

            JTextField name = new JTextField(a.getName()),
                       city = new JTextField(a.getCity()),
                       country = new JTextField(a.getCountry());
                       
            // JComboBox restriction layout setup to block loose keyboard entry mutations
            String[] statusOptions = {"Active", "Inactive"};
            JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
            statusCombo.setSelectedItem(a.getStatus());


            Object[] msg = {"Name:", name, "City:", city, "Country:", country, "Status:", statusCombo};

            int r = JOptionPane.showConfirmDialog(this, msg, "Update Airport", JOptionPane.OK_CANCEL_OPTION);
            if (r != JOptionPane.OK_OPTION) return;

            a.setName(name.getText().trim());
            a.setCity(city.getText().trim());
            a.setCountry(country.getText().trim());
            
            // Collect directly from drop-down component references
            a.setStatus((String) statusCombo.getSelectedItem());
            
            FileHandler.saveSystem(system);
            refresh.run();
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select an airport first in the table."); return; }
            
            String c = (String) model.getValueAt(row, 0);
            if (system.isAirportInUse(c)) {
                err("Cannot delete as flights are using this airport.");
                return;
            }

            system.removeAirport(c);
            FileHandler.saveSystem(system);
            refresh.run();
        });

        refreshBtn.addActionListener(e -> refresh.run());

        buttons.add(addBtn); 
        buttons.add(updateBtn);
        buttons.add(deleteBtn); 
        buttons.add(refreshBtn);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    // 2. Managing aircrafts (adding, updating status, listing)
    private JPanel AircraftsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Model", "Capacity", "Airline", "Maintenance", "Availability"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Aircraft a : system.getAircrafts()) {
                model.addRow(new Object[]{ a.getAircraftId(), a.getModel(), a.getCapacity(), a.getAirlineName(), a.getMaintenanceStatus(),
                    a.getAvailabilityStatus()
                });
            }
        };
        refresh.run();

        JButton addBtn = new JButton("Add Aircraft");
        JButton updateBtn = new JButton("Update Status");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> {
            JTextField id = new JTextField(), model2 = new JTextField(),
                       cap = new JTextField(), airline = new JTextField();
            Object[] msg = {"Aircraft ID:", id, "Model:", model2,
                            "Capacity:", cap, "Airline:", airline};
            int r = JOptionPane.showConfirmDialog(this, msg, "Add Aircraft",
                JOptionPane.OK_CANCEL_OPTION);
            if (r != JOptionPane.OK_OPTION) return;
            String aid = id.getText().trim();
            if (aid.isEmpty() || system.findAircraft(aid) != null) {
                err("Empty or duplicate ID."); return;
            }
            int cp;
            try { cp = Integer.parseInt(cap.getText().trim()); }
            catch (NumberFormatException ex) { err("Invalid capacity."); return; }
            if (cp <= 0) { err("Capacity must be positive."); return; }
            system.addAircraft(new Aircraft(aid, model2.getText(), cp,
                                            airline.getText(), "Good", "Available"));
            FileHandler.saveSystem(system);
            refresh.run();
        });
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select an aircraft."); return; }
            String id = (String) model.getValueAt(row, 0);
            Aircraft a = system.findAircraft(id);
            String[] options = {"Available", "Assigned", "Under Maintenance"};
            String s = (String) JOptionPane.showInputDialog(this,
                "New status:", "Update Status", JOptionPane.QUESTION_MESSAGE,
                null, options, a.getAvailabilityStatus());
            if (s != null) {
                a.setAvailabilityStatus(s);
                FileHandler.saveSystem(system);
                refresh.run();
            }
        });
        refreshBtn.addActionListener(e -> refresh.run());

        JPanel buttons = new JPanel();
        buttons.add(addBtn); buttons.add(updateBtn); buttons.add(refreshBtn);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    // 3. Flights Tab
    private JPanel FlightsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Flight ID", "From", "To", "Departure", "Arrival",
                         "Fare", "Capacity", "Available", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Flight f : system.getFlights()) {
                model.addRow(new Object[]{
                    f.getFlightId(),
                    f.getSource().getCity(),
                    f.getDestination().getCity(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    f.getFare(),
                    f.getCapacity(),
                    f.getAvailableSeatsCount(),
                    f.getStatus()
                });
            }
        };
        refresh.run();

        JButton addBtn = new JButton("Add Flight");
        JButton updateBtn = new JButton("Update");
        JButton removeBtn = new JButton("Remove");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> openAddFlightDialog(refresh));
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select a flight."); return; }
            String id = (String) model.getValueAt(row, 0);
            Flight f = system.findFlight(id);
            JTextField fare = new JTextField(String.valueOf(f.getFare()));
            String[] statuses = {"Scheduled", "Delayed", "Cancelled", "Completed"};
            JComboBox<String> statusBox = new JComboBox<>(statuses);
            statusBox.setSelectedItem(f.getStatus());
            Object[] msg = {"New Fare:", fare, "Status:", statusBox};
            int r = JOptionPane.showConfirmDialog(this, msg, "Update Flight",
                JOptionPane.OK_CANCEL_OPTION);
            if (r != JOptionPane.OK_OPTION) return;
            try { f.setFare(Double.parseDouble(fare.getText().trim())); }
            catch (NumberFormatException ex) { /* keep old */ }
            f.updateStatus((String) statusBox.getSelectedItem());
            FileHandler.saveSystem(system);
            refresh.run();
        });
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select a flight."); return; }
            String id = (String) model.getValueAt(row, 0);
            int c = JOptionPane.showConfirmDialog(this,
                "Remove flight " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            Flight f = system.findFlight(id);
            if (f != null && f.getAircraft() != null) {
                f.getAircraft().setAvailabilityStatus("Available");
            }
            system.removeFlight(id);
            FileHandler.saveSystem(system);
            refresh.run();
        });
        refreshBtn.addActionListener(e -> refresh.run());

        JPanel buttons = new JPanel();
        buttons.add(addBtn); buttons.add(updateBtn);
        buttons.add(removeBtn); buttons.add(refreshBtn);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private void openAddFlightDialog(Runnable refresh) {
        if (system.getAirports().size() < 2) {
            err("Need at least 2 airports first."); return;
        }
        if (system.getAircrafts().isEmpty()) {
            err("Need at least 1 aircraft first."); return;
        }

        JTextField id = new JTextField();
        JComboBox<Airport> srcBox = new JComboBox<>(
            system.getAirports().toArray(new Airport[0]));
        JComboBox<Airport> dstBox = new JComboBox<>(
            system.getAirports().toArray(new Airport[0]));
        JComboBox<Aircraft> acBox = new JComboBox<>();
        for (Aircraft a : system.getAircrafts()) {
            if (a.getAvailabilityStatus().equals("Available")) acBox.addItem(a);
        }
        if (acBox.getItemCount() == 0) { err("No available aircrafts."); return; }
        JTextField dep = new JTextField("2026-06-01 09:00");
        JTextField arr = new JTextField("2026-06-01 11:00");
        JTextField fare = new JTextField("15000");

        Object[] msg = {"Flight ID:", id, "Source:", srcBox, "Destination:", dstBox,
                        "Aircraft:", acBox, "Departure:", dep, "Arrival:", arr,
                        "Fare:", fare};
        int r = JOptionPane.showConfirmDialog(this, msg, "Add Flight",
            JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        String fid = id.getText().trim();
        if (fid.isEmpty() || system.findFlight(fid) != null) {
            err("Empty or duplicate flight ID."); return;
        }
        Airport src = (Airport) srcBox.getSelectedItem();
        Airport dst = (Airport) dstBox.getSelectedItem();
        if (src == dst) { err("Source and destination cannot be same."); return; }
        Aircraft ac = (Aircraft) acBox.getSelectedItem();
        double fareVal;
        try { fareVal = Double.parseDouble(fare.getText().trim()); }
        catch (NumberFormatException ex) { err("Invalid fare."); return; }

        Flight f = new Flight(fid, src, dst, dep.getText(), arr.getText(),
                              fareVal, ac);
        system.addFlight(f);
        ac.setAvailabilityStatus("Assigned");
        FileHandler.saveSystem(system);
        refresh.run();
    }

    // 4. Crew Tab
    private JPanel CrewTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Emp ID", "Name", "Role", "Available", "Assigned Flights"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Crew c : system.getCrewMembers()) {
                model.addRow(new Object[]{
                    c.getEmployeeId(), c.getName(), c.getRole(),
                    c.isAvailable() ? "Yes" : "No",
                    c.getAssignedFlights().size()
                });
            }
        };
        refresh.run();

        JButton addBtn    = new JButton("Add Crew");
        JButton assignBtn = new JButton("Assign to Flight");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> openAddCrewDialog(refresh));
        assignBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select a crew member."); return; }
            String eid = (String) model.getValueAt(row, 0);
            Crew chosen = null;
            for (Crew c : system.getCrewMembers()) {
                if (c.getEmployeeId().equals(eid)) { chosen = c; break; }
            }
            if (chosen == null) return;
            if (system.getFlights().isEmpty()) { err("No flights to assign."); return; }
            Flight f = (Flight) JOptionPane.showInputDialog(this, "Choose flight:",
                "Assign", JOptionPane.QUESTION_MESSAGE, null,
                system.getFlights().toArray(), system.getFlights().get(0));
            if (f == null) return;
            f.assignCrew(chosen);
            chosen.assignFlight(f);
            FileHandler.saveSystem(system);
            refresh.run();
        });
        refreshBtn.addActionListener(e -> refresh.run());

        JPanel buttons = new JPanel();
        buttons.add(addBtn); buttons.add(assignBtn); buttons.add(refreshBtn);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private void openAddCrewDialog(Runnable refresh) {
        String[] types = {"Pilot", "CabinCrew", "GroundStaff"};
        String type = (String) JOptionPane.showInputDialog(this, "Crew type:",
            "Add Crew", JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        if (type == null) return;

        // Removed duty field component, and streamlined extra components
        JTextField empId = new JTextField(), name = new JTextField(),
                   phone = new JTextField(), addr = new JTextField();
        JTextField extra1 = new JTextField(), extra2 = new JTextField();

        Object[] msg;
        switch (type) {
            case "Pilot":
                msg = new Object[]{
                    "Employee ID:", empId, "Name:", name, "Phone:", phone,
                    "Address:", addr,
                    "License No:", extra1, "Experience years:", extra2
                };
                break;
            default:
                // Completely removed "Duty" and "Section/Department" text fields for CabinCrew and GroundStaff
                msg = new Object[]{
                    "Employee ID:", empId, "Name:", name, "Phone:", phone,
                    "Address:", addr
                };
        }
        int r = JOptionPane.showConfirmDialog(this, msg, "Add " + type,
            JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        String pid = "P" + (system.getCrewMembers().size() + 1);
        Crew c;
        switch (type) {
            case "Pilot":
                int yrs;
                try { yrs = Integer.parseInt(extra2.getText().trim()); }
                catch (Exception ex) { yrs = 0; }
                // Passes a standard "Standard Flight" value for dutySchedule field compatibility
                c = new Pilot(pid, name.getText().trim(), phone.getText().trim(), addr.getText().trim(),
                              empId.getText().trim(), "Standard Flight", extra1.getText().trim(), yrs);
                break;
            case "CabinCrew":
                // Passes placeholder "General Flight Duty" and "All Sections" to the background models
                c = new CabinCrew(pid, name.getText().trim(), phone.getText().trim(), addr.getText().trim(),
                                  empId.getText().trim(), "General Flight Duty", "All Sections");
                break;
            default:
                // Ground staff defaults
                c = new GroundStaff(pid, name.getText().trim(), phone.getText().trim(), addr.getText().trim(),
                                    empId.getText().trim(), "Ground Duty", "General Logistics");
        }
        system.addCrew(c);
        FileHandler.saveSystem(system);
        refresh.run();
    }
    
    // 5. Bookings Tab
    private JPanel BookingsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Booking ID", "User", "Flight", "Seat", "Date",
                         "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Booking b : system.getBookings()) {
                model.addRow(new Object[]{
                    b.getBookingId(),
                    b.getUser().getName(),
                    b.getFlight().getFlightId(),
                    b.getSeat().getSeatNumber(),
                    b.getBookingDate(),
                    "Rs " + b.getTotalAmount(),
                    b.getStatus()
                });
            }
        };
        refresh.run();

        JButton cancelBtn = new JButton("Cancel This Booking");
        JButton refreshBtn = new JButton("Refresh");

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select a booking."); return; }
            String id = (String) model.getValueAt(row, 0);
            Booking b = system.findBooking(id);
            if (b == null) return;
            if (!b.getStatus().equals("Confirmed")) {
                err("Already cancelled or not active."); return;
            }
            int c = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + id + " and refund the user?",
                "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            b.setStatus("Cancelled by Admin");
            b.getSeat().releaseSeat();
            if (b.getPayment() != null) b.getPayment().refund();
            system.addNotification(new Notification(b.getUser(),
                "Booking " + id + " was cancelled by admin. Refund processed."));
            FileHandler.saveSystem(system);
            refresh.run();
        });
        refreshBtn.addActionListener(e -> refresh.run());

        JPanel buttons = new JPanel();
        buttons.add(cancelBtn); buttons.add(refreshBtn);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    // 6. Refunds Tab
    private JPanel RefundsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Request ID", "Booking", "User", "Reason",
                         "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (RefundRequest r : system.getRefundRequests()) {
                model.addRow(new Object[]{
                    r.getRequestId(),
                    r.getBooking().getBookingId(),
                    r.getBooking().getUser().getName(),
                    r.getReason(),
                    r.getRequestDate(),
                    r.getStatus()
                });
            }
        };
        refresh.run();

        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn  = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select a request."); return; }
            String rid = (String) model.getValueAt(row, 0);
            RefundRequest req = findRefund(rid);
            if (req == null || !req.getStatus().equals("Pending")) {
                err("Request is not pending."); return;
            }
            req.approve();
            Booking b = req.getBooking();
            b.setStatus("Refunded");
            b.getSeat().releaseSeat();
            if (b.getPayment() != null) b.getPayment().refund();
            system.addNotification(new Notification(b.getUser(),
                "Refund APPROVED for booking " + b.getBookingId() + "."));
            FileHandler.saveSystem(system);
            refresh.run();
        });
        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { err("Select a request."); return; }
            String rid = (String) model.getValueAt(row, 0);
            RefundRequest req = findRefund(rid);
            if (req == null || !req.getStatus().equals("Pending")) {
                err("Request is not pending."); return;
            }
            req.reject();
            req.getBooking().setStatus("Confirmed");
            system.addNotification(new Notification(req.getBooking().getUser(),
                "Refund REJECTED for booking " + req.getBooking().getBookingId() + "."));
            FileHandler.saveSystem(system);
            refresh.run();
        });
        refreshBtn.addActionListener(e -> refresh.run());

        JPanel buttons = new JPanel();
        buttons.add(approveBtn); buttons.add(rejectBtn); buttons.add(refreshBtn);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    private RefundRequest findRefund(String rid) {
        for (RefundRequest r : system.getRefundRequests()) {
            if (r.getRequestId().equals(rid)) return r;
        }
        return null;
    }

    // 7. Reports Tab
    private JPanel ReportsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JPanel buttons = new JPanel();
        buttons.add(reportButton("Bookings",  area, () -> bookingReportText()));
        buttons.add(reportButton("Flights",   area, () -> flightReportText()));
        buttons.add(reportButton("Revenue",   area, () -> revenueReportText()));
        buttons.add(reportButton("Refunds",   area, () -> refundReportText()));
        buttons.add(reportButton("Full Report", area, () -> fullReportText()));

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private JButton reportButton(String label, JTextArea area,
                                 java.util.function.Supplier<String> producer) {
        JButton b = new JButton(label);
        b.addActionListener(e -> area.setText(producer.get()));
        return b;
    }

    private String bookingReportText() {
        StringBuilder sb = new StringBuilder("===== BOOKINGS REPORT =====\n\n");
        if (system.getBookings().isEmpty()) return sb.append("No bookings.\n").toString();
        for (Booking b : system.getBookings()) sb.append(b.getBookingDetails()).append("\n");
        sb.append("\nTotal bookings: ").append(system.getBookings().size());
        return sb.toString();
    }

    private String flightReportText() {
        StringBuilder sb = new StringBuilder("===== FLIGHTS REPORT =====\n\n");
        if (system.getFlights().isEmpty()) return sb.append("No flights.\n").toString();
        for (Flight f : system.getFlights()) {
            int booked = f.getCapacity() - f.getAvailableSeatsCount();
            double occ = f.getCapacity() == 0 ? 0
                       : ((double) booked / f.getCapacity()) * 100;
            sb.append(f).append("\n");
            sb.append(String.format("    Booked: %d/%d (%.1f%%)%n",
                                    booked, f.getCapacity(), occ));
        }
        return sb.toString();
    }

    private String revenueReportText() {
        double total = 0, refunded = 0;
        for (Booking b : system.getBookings()) {
            if (b.getStatus().equals("Confirmed")) total += b.getTotalAmount();
            else if (b.getStatus().equals("Refunded")
                  || b.getStatus().equals("Cancelled by Admin"))
                refunded += b.getTotalAmount();
        }
        return "===== REVENUE REPORT =====\n\n"
             + "Confirmed bookings revenue : Rs " + total + "\n"
             + "Refunded amount             : Rs " + refunded + "\n"
             + "Net revenue                 : Rs " + (total - refunded) + "\n";
    }

    private String refundReportText() {
        StringBuilder sb = new StringBuilder("===== REFUNDS REPORT =====\n\n");
        if (system.getRefundRequests().isEmpty())
            return sb.append("No refund requests.\n").toString();
        int p = 0, a = 0, j = 0;
        for (RefundRequest r : system.getRefundRequests()) {
            sb.append(r).append("\n");
            switch (r.getStatus()) {
                case "Pending":  p++; break;
                case "Approved": a++; break;
                case "Rejected": j++; break;
            }
        }
        sb.append(String.format("\nPending: %d | Approved: %d | Rejected: %d", p, a, j));
        return sb.toString();
    }

    private String fullReportText() {
        return "        FULL SYSTEM REPORT        \n\n"
             + "Users      : " + system.getUsers().size() + "\n"
             + "Airports   : " + system.getAirports().size() + "\n"
             + "Aircrafts  : " + system.getAircrafts().size() + "\n"
             + "Flights    : " + system.getFlights().size() + "\n"
             + "Crew       : " + system.getCrewMembers().size() + "\n\n"
             + bookingReportText() + "\n\n"
             + flightReportText()  + "\n\n"
             + revenueReportText() + "\n\n"
             + refundReportText();
    }

    private void updateAdminProfile() {
        JTextField nameField     = new JTextField(admin.getName());
        JTextField phoneField    = new JTextField(admin.getPhoneNumber() != null ? admin.getPhoneNumber() : "");
        JTextField addressField  = new JTextField(admin.getAddress() != null ? admin.getAddress() : "");
        JTextField emailField    = new JTextField(admin.getEmail());
        JPasswordField passField = new JPasswordField(admin.getPassword());

        Object[] msg = {
            "Name:",        nameField,
            "Phone:",       phoneField,
            "Address:",     addressField,
            "Email:",       emailField,
            "New Password:", passField
        };

        int result = JOptionPane.showConfirmDialog(this, msg, "Update Admin Profile", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        String newName  = nameField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newAddr  = addressField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPass  = new String(passField.getPassword()).trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            err("Name and Email cannot be empty.");
            return;
        }
        admin.updateProfile(newName, newPhone, newAddr);

        admin.setEmail(newEmail);
        if (!newPass.isEmpty() && newPass.length() >= 4) {
            admin.setPassword(newPass);
        }

        FileHandler.saveSystem(system);
        
        setTitle("Admin Dashboard - " + admin.getName());
        JOptionPane.showMessageDialog(this, "Profile updated successfully!",  "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAdminAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "You are about to DELETE the only Admin account.\n" +
            "Are you ABSOLUTELY sure you want to delete this admin account?", "Delete Admin Account",
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.ERROR_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        system.setAdmin(null);           
        FileHandler.saveSystem(system);

        JOptionPane.showMessageDialog(this, "Admin has been deleted. \nYou are now logged out.", "Account Deleted", JOptionPane.INFORMATION_MESSAGE);

        admin.logout();
        startFrame.setVisible(true);
        dispose();
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}