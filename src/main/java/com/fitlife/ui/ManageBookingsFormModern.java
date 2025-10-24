package com.fitlife.ui;

import com.fitlife.db.DatabaseHelper;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ManageBookingsFormModern extends JFrame {
    private JLabel bookingIdLabel;
    private JTextField fullNameField;
    private JTextField contactField;
    private JComboBox<String> membershipTypeCombo;
    private JComboBox<String> programCombo;
    private JTextField startDateField;
    private JTextField sessionsField;
    private JTextField totalCostField;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private String currentBookingId;

    public ManageBookingsFormModern() {
        setTitle("FitLife Gym - Manage Bookings");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ModernUIHelper.BACKGROUND);

        // Header
        JPanel header = ModernUIHelper.createGradientHeader("Manage Bookings", 1200, 80);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(ModernUIHelper.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Form Panel
        JPanel formCard = createFormPanel();
        contentPanel.add(formCard, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Load bookings
        loadBookings();
    }

    private JPanel createFormPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ModernUIHelper.CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIHelper.BORDER_COLOR, 1),
            new EmptyBorder(25, 30, 25, 30)
        ));

        // Form grid
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(ModernUIHelper.CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize components
        currentBookingId = generateNewBookingId();
        bookingIdLabel = ModernUIHelper.createLabel(currentBookingId, true);
        bookingIdLabel.setForeground(ModernUIHelper.PRIMARY_COLOR);
        fullNameField = ModernUIHelper.createStyledTextField(15);
        contactField = ModernUIHelper.createStyledTextField(15);
        membershipTypeCombo = ModernUIHelper.createStyledComboBox(new String[]{"Monthly", "Quarterly", "Annual"});
        programCombo = ModernUIHelper.createStyledComboBox(new String[]{"Yoga", "Weight Training", "Cardio", "CrossFit"});
        startDateField = ModernUIHelper.createStyledTextField(10);
        startDateField.setToolTipText("Format: YYYY-MM-DD");
        sessionsField = ModernUIHelper.createStyledTextField(8);
        totalCostField = ModernUIHelper.createStyledTextField(10);
        totalCostField.setEditable(false);
        totalCostField.setBackground(new Color(240, 240, 240));

        // Add fields to form
        int row = 0;
        addFormField(formGrid, "Booking ID:", bookingIdLabel, gbc, row++, 0);
        addFormField(formGrid, "Full Name:", fullNameField, gbc, row++, 0);
        addFormField(formGrid, "Contact Number:", contactField, gbc, row++, 0);
        addFormField(formGrid, "Membership Type:", membershipTypeCombo, gbc, row++, 0);

        row = 0;
        addFormField(formGrid, "Program:", programCombo, gbc, row++, 2);
        addFormField(formGrid, "Start Date:", startDateField, gbc, row++, 2);
        addFormField(formGrid, "Number of Sessions:", sessionsField, gbc, row++, 2);
        addFormField(formGrid, "Total Cost:", totalCostField, gbc, row++, 2);

        card.add(formGrid, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ModernUIHelper.CARD_COLOR);

        JButton createBtn = ModernUIHelper.createStyledButton("Create", ModernUIHelper.SUCCESS_COLOR);
        JButton editBtn = ModernUIHelper.createStyledButton("Edit", ModernUIHelper.INFO_COLOR);
        JButton deleteBtn = ModernUIHelper.createStyledButton("Delete", ModernUIHelper.DANGER_COLOR);
        JButton clearBtn = ModernUIHelper.createStyledButton("Clear", ModernUIHelper.WARNING_COLOR);
        JButton closeBtn = ModernUIHelper.createStyledButton("Close", new Color(149, 165, 166));

        createBtn.addActionListener(e -> handleCreate());
        editBtn.addActionListener(e -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);

        card.add(buttonPanel, BorderLayout.SOUTH);

        // Add listener for automatic cost calculation
        sessionsField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateTotalCost(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateTotalCost(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateTotalCost(); }
        });
        membershipTypeCombo.addActionListener(e -> calculateTotalCost());

        return card;
    }

    private void addFormField(JPanel panel, String label, JComponent component, 
                             GridBagConstraints gbc, int row, int colOffset) {
        gbc.gridx = colOffset;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(ModernUIHelper.createLabel(label, true), gbc);

        gbc.gridx = colOffset + 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIHelper.CARD_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(ModernUIHelper.BORDER_COLOR, 1));

        String[] columns = {"Booking ID", "Full Name", "Contact", "Membership", "Program", "Start Date", "Sessions", "Cost"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        ModernUIHelper.styleTable(bookingsTable);

        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = bookingsTable.getSelectedRow();
                if (row != -1) {
                    loadBookingData(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = "SELECT * FROM Booking ORDER BY bookingId";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Object[] row = {
                        rs.getString("bookingId"),
                        rs.getString("fullName"),
                        rs.getString("contactNumber"),
                        rs.getString("membershipType"),
                        rs.getString("program"),
                        rs.getString("startDate"),
                        rs.getInt("numberOfSessions"),
                        String.format("Rs. %.2f", rs.getDouble("totalCost"))
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            showError("Error loading bookings: " + e.getMessage());
        }
    }

    private void loadBookingData(int row) {
        currentBookingId = (String) tableModel.getValueAt(row, 0);
        bookingIdLabel.setText(currentBookingId);
        bookingIdLabel.setForeground(ModernUIHelper.INFO_COLOR);
        fullNameField.setText((String) tableModel.getValueAt(row, 1));
        contactField.setText((String) tableModel.getValueAt(row, 2));
        membershipTypeCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        programCombo.setSelectedItem(tableModel.getValueAt(row, 4));
        startDateField.setText((String) tableModel.getValueAt(row, 5));
        sessionsField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        String costStr = String.valueOf(tableModel.getValueAt(row, 7));
        totalCostField.setText(costStr.replace("Rs. ", ""));
    }

    private String generateNewBookingId() {
        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = "SELECT MAX(CAST(SUBSTR(bookingId, 2) AS INTEGER)) as maxId FROM Booking WHERE bookingId LIKE 'B%'";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                int nextId = 1;
                if (rs.next()) {
                    nextId = rs.getInt("maxId") + 1;
                }
                return String.format("B%03d", nextId);
            }
        } catch (SQLException e) {
            showError("Error generating booking ID: " + e.getMessage());
            return "B001";
        }
    }

    private void calculateTotalCost() {
        try {
            String sessionsText = sessionsField.getText().trim();
            if (sessionsText.isEmpty()) {
                totalCostField.setText("");
                return;
            }

            int sessions = Integer.parseInt(sessionsText);
            if (sessions <= 0) {
                totalCostField.setText("");
                return;
            }

            double ratePerSession;
            if (sessions <= 10) {
                ratePerSession = 1000;
            } else if (sessions <= 30) {
                ratePerSession = 800;
            } else if (sessions <= 50) {
                ratePerSession = 600;
            } else {
                ratePerSession = 500;
            }

            double totalCost = sessions * ratePerSession;

            // Apply membership discount
            String membershipType = (String) membershipTypeCombo.getSelectedItem();
            if ("Quarterly".equals(membershipType)) {
                totalCost *= 0.9; // 10% discount
            } else if ("Annual".equals(membershipType)) {
                totalCost *= 0.8; // 20% discount
            }

            totalCostField.setText(String.format("%.2f", totalCost));
        } catch (NumberFormatException e) {
            totalCostField.setText("");
        }
    }

    private void handleCreate() {
        if (!validateInput()) return;

        calculateTotalCost();

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                INSERT INTO Booking (bookingId, fullName, contactNumber, membershipType, 
                                   program, startDate, numberOfSessions, totalCost)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                setBookingParameters(pstmt);
                pstmt.executeUpdate();

                showSuccess("Booking created successfully!");
                loadBookings();
                clearForm();
            }
        } catch (SQLException e) {
            showError("Error creating booking: " + e.getMessage());
        }
    }

    private void handleEdit() {
        if (!validateInput()) return;

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                UPDATE Booking 
                SET fullName = ?, contactNumber = ?, membershipType = ?,
                    program = ?, startDate = ?, numberOfSessions = ?, totalCost = ?
                WHERE bookingId = ?
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fullNameField.getText());
                pstmt.setString(2, contactField.getText());
                pstmt.setString(3, (String) membershipTypeCombo.getSelectedItem());
                pstmt.setString(4, (String) programCombo.getSelectedItem());
                pstmt.setString(5, startDateField.getText());
                pstmt.setInt(6, Integer.parseInt(sessionsField.getText()));
                pstmt.setDouble(7, Double.parseDouble(totalCostField.getText()));
                pstmt.setString(8, currentBookingId);

                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    showSuccess("Booking updated successfully!");
                    loadBookings();
                    clearForm();
                } else {
                    showError("Booking not found!");
                }
            }
        } catch (SQLException e) {
            showError("Error updating booking: " + e.getMessage());
        }
    }

    private void handleDelete() {
        if (currentBookingId == null || currentBookingId.isEmpty()) {
            showError("Please select a booking to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete booking " + currentBookingId + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseHelper.getInstance().getConnection();
                String query = "DELETE FROM Booking WHERE bookingId = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, currentBookingId);

                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showSuccess("Booking deleted successfully!");
                        loadBookings();
                        clearForm();
                    } else {
                        showError("Booking not found!");
                    }
                }
            } catch (SQLException e) {
                showError("Error deleting booking: " + e.getMessage());
            }
        }
    }

    private boolean validateInput() {
        if (fullNameField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty() ||
            startDateField.getText().trim().isEmpty() ||
            sessionsField.getText().trim().isEmpty()) {

            showError("All fields are required!");
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(startDateField.getText());
        } catch (ParseException e) {
            showError("Invalid date format! Use YYYY-MM-DD");
            return false;
        }

        try {
            int sessions = Integer.parseInt(sessionsField.getText());
            if (sessions <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showError("Number of sessions must be a positive number!");
            return false;
        }

        return true;
    }

    private void setBookingParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, currentBookingId);
        pstmt.setString(2, fullNameField.getText());
        pstmt.setString(3, contactField.getText());
        pstmt.setString(4, (String) membershipTypeCombo.getSelectedItem());
        pstmt.setString(5, (String) programCombo.getSelectedItem());
        pstmt.setString(6, startDateField.getText());
        pstmt.setInt(7, Integer.parseInt(sessionsField.getText()));
        pstmt.setDouble(8, Double.parseDouble(totalCostField.getText()));
    }

    private void clearForm() {
        currentBookingId = generateNewBookingId();
        bookingIdLabel.setText(currentBookingId);
        bookingIdLabel.setForeground(ModernUIHelper.PRIMARY_COLOR);
        fullNameField.setText("");
        contactField.setText("");
        membershipTypeCombo.setSelectedIndex(0);
        programCombo.setSelectedIndex(0);
        startDateField.setText("");
        sessionsField.setText("");
        totalCostField.setText("");
        bookingsTable.clearSelection();
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
