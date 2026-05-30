// BookingFrame.java
// Dedicated booking window. Step-by-step: pick flight, pick seat, enter
// luggage, choose payment, confirm.

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingFrame extends JFrame {

    private User user;
    private AirlineSystem system;
    private UserDashboardFrame parent;

    private JTable flightTable;
    private DefaultTableModel flightModel;
    private JComboBox<String> seatCombo;
    private JTextField luggageField;
    private JComboBox<String> paymentCombo;
    private JLabel totalLabel;

    private Flight selectedFlight;

    public BookingFrame(User user, AirlineSystem system, UserDashboardFrame parent) {
        this.user   = user;
        this.system = system;
        this.parent = parent;

        setTitle("Book Ticket - " + user.getName());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---------- Top: flight selection table ----------
        JPanel topWrap = new JPanel(new BorderLayout(5, 5));
        topWrap.setBorder(BorderFactory.createTitledBorder("Step 1 — Select a flight"));

        flightModel = new DefaultTableModel(
            new String[] {"Flight ID", "From", "To", "Departure", "Fare", "Available"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        flightTable = new JTable(flightModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadFlights();

        // When a flight row is clicked, refresh the seat combo
        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onFlightPicked();
        });

        topWrap.add(new JScrollPane(flightTable), BorderLayout.CENTER);
        main.add(topWrap, BorderLayout.NORTH);

        // ---------- Middle: seat / luggage / payment ----------
        JPanel mid = new JPanel(new GridBagLayout());
        mid.setBorder(BorderFactory.createTitledBorder("Step 2 — Booking details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        g.gridx = 0; g.gridy = 0; mid.add(new JLabel("Available seats:"), g);
        seatCombo = new JComboBox<>();
        g.gridx = 1; mid.add(seatCombo, g);

        g.gridx = 0; g.gridy = 1; mid.add(new JLabel("Luggage (kg):"), g);
        luggageField = new JTextField("0");
        g.gridx = 1; mid.add(luggageField, g);

        g.gridx = 0; g.gridy = 2; mid.add(new JLabel("Payment method:"), g);
        paymentCombo = new JComboBox<>(new String[] 
            { "Card", "Cash", "Bank Transfer"});
        g.gridx = 1; mid.add(paymentCombo, g);

        JButton calc = new JButton("Calculate Total");
        g.gridx = 0; g.gridy = 3; mid.add(calc, g);
        totalLabel = new JLabel("Total: ---");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        g.gridx = 1; mid.add(totalLabel, g);
        calc.addActionListener(e -> updateTotal());

        main.add(mid, BorderLayout.CENTER);

        // ---------- Bottom: actions ----------
        JPanel bottom = new JPanel();
        JButton confirm = new JButton("Confirm Booking");
        confirm.setFont(new Font("Arial", Font.BOLD, 13));
        confirm.setBackground(new Color(46, 125, 50));
        confirm.setForeground(Color.WHITE);
        JButton cancel = new JButton("Cancel");

        confirm.addActionListener(e -> doConfirm());
        cancel.addActionListener(e -> dispose());

        bottom.add(confirm);
        bottom.add(cancel);
        main.add(bottom, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private void loadFlights() {
        flightModel.setRowCount(0);
        for (Flight f : system.searchFlights()) {
            if (f.getAvailableSeatsCount() == 0) continue;
            flightModel.addRow(new Object[] {
                f.getFlightId(),
                f.getSource().getCity(),
                f.getDestination().getCity(),
                f.getDepartureTime(),
                "Rs " + f.getFare(),
                f.getAvailableSeatsCount() + "/" + f.getCapacity()
            });
        }
    }

    private void onFlightPicked() {
        int row = flightTable.getSelectedRow();
        if (row < 0) { selectedFlight = null; return; }
        String id = (String) flightModel.getValueAt(row, 0);
        selectedFlight = system.findFlight(id);
        seatCombo.removeAllItems();
        if (selectedFlight != null) {
            for (Seat s : selectedFlight.getAvailableSeats()) {
                seatCombo.addItem("Seat " + s.getSeatNumber()
                                  + " (" + s.getClassType() + ")");
            }
        }
    }

    private double computeTotal() {
        if (selectedFlight == null) return 0;
        double luggage = parseDouble(luggageField.getText(), 0);
        return user.calculateFare(selectedFlight, luggage);
    }

    private void updateTotal() {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, "Pick a flight first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Baggage input structural format check
        String luggageInput = luggageField.getText().trim();
        double luggage;
        try {
            luggage = Double.parseDouble(luggageInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for baggage weight.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (luggage < 0 || luggage > 20) {
            JOptionPane.showMessageDialog(this, "Baggage weight must be between 0 kg and 20 kg.", "Baggage Limit Exceeded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        totalLabel.setText("Total: Rs " + computeTotal());
    }

    private void doConfirm() {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, "Pick a flight first.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        if (seatCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No seats available.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        
        // Comprehensive baggage validation confirmation gate
        String luggageInput = luggageField.getText().trim();
        double luggage;
        try {
            luggage = Double.parseDouble(luggageInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for baggage weight.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (luggage < 0 || luggage > 20) {
            JOptionPane.showMessageDialog(this, "Booking rejected! Baggage weight must be between 0 kg and 20 kg.", "Baggage Validation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Seat seat = pickedSeat();
        if (seat == null) return;

        double total = user.calculateFare(selectedFlight, luggage);

        PaymentMethod method = pickPaymentMethod();
        if (method == null) return;     // user cancelled

        Booking booking = new Booking(user, selectedFlight, seat, luggage);
        booking.setTotalAmount(total);
        Payment pay = new Payment(total, method);

        if (!pay.processPayment()) {
            JOptionPane.showMessageDialog(this, "Payment failed. Booking aborted.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        booking.setPayment(pay);
        booking.confirmBooking();

        // Save to BOTH lists - same object reference
        user.addBooking(booking);
        system.addBooking(booking);

        system.addNotification(new Notification(user,
                "Booking " + booking.getBookingId() + " confirmed."));

        FileHandler.saveSystem(system);

        JOptionPane.showMessageDialog(this,
                "Booking confirmed!\n\n" + booking.getTicket().viewTicket(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        if (parent != null) parent.refreshAllTables();
        dispose();
    }

    private Seat pickedSeat() {
        String s = (String) seatCombo.getSelectedItem();
        if (s == null) return null;
        try {
            int num = Integer.parseInt(s.split(" ")[1]);
            for (Seat seat : selectedFlight.getSeats()) {
                if (seat.getSeatNumber() == num) return seat;
            }
        } catch (Exception ignored) { }
        return null;
    }

    private PaymentMethod pickPaymentMethod() {
        String choice = (String) paymentCombo.getSelectedItem();
        switch (choice) {
            case "Card": {
                String num = JOptionPane.showInputDialog(this, "Card Number (16 Digits):");
                if (num == null || num.trim().isEmpty()) return null;
                
                String holder = JOptionPane.showInputDialog(this, "Card Holder Name:");
                if (holder == null || holder.trim().isEmpty()) return null;
                
                String exp = JOptionPane.showInputDialog(this, "Expiry (MM/YY):");
                if (exp == null || exp.trim().isEmpty()) return null;
                
                String cvv = JOptionPane.showInputDialog(this, "CVV (3-4 Digits):");
                if (cvv == null || cvv.trim().isEmpty()) return null;
                
                return new CardPayment(num.trim(), holder.trim(), exp.trim(), cvv.trim());
            }
            case "Cash":
                return new CashPayment();
            case "Bank Transfer": {
                String acc = JOptionPane.showInputDialog(this, "Account Number:");
                if (acc == null || acc.trim().isEmpty()) return null;
                
                String bk  = JOptionPane.showInputDialog(this, "Bank Name:");
                if (bk == null || bk.trim().isEmpty()) return null;
                
                return new BankTransferPayment(acc.trim(), bk.trim());
            }
        }
        return null;
    }

    private double parseDouble(String s, double dflt) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return dflt; }
    }
}