package com.fitlife.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fitlife.db.DatabaseHelper;

public class ManageBookingsForm extends JFrame {
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

    public ManageBookingsForm() {
        setTitle("FitLife Gym - Manage Bookings");
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create split pane for form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        splitPane.setTopComponent(formPanel);
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        splitPane.setBottomComponent(tablePanel);
        
        // Set the divider location
        splitPane.setDividerLocation(300);
        
        add(splitPane);
        
        // Load existing bookings
        loadBookings();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize components
        bookingIdLabel = new JLabel();
        fullNameField = new JTextField(20);
        contactField = new JTextField(15);
        membershipTypeCombo = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Annual"});
        programCombo = new JComboBox<>(new String[]{"Yoga", "Weight Training", "Cardio", "CrossFit"});
        startDateField = new JTextField(10);
        sessionsField = new JTextField(5);
        totalCostField = new JTextField(10);
        totalCostField.setEditable(false);

        // Set initial booking ID
        currentBookingId = generateNewBookingId();
        bookingIdLabel.setText(currentBookingId);

        // Add components
        int row = 0;
        addFormField(panel, "Booking ID:", bookingIdLabel, gbc, row++);
        addFormField(panel, "Full Name:", fullNameField, gbc, row++);
        addFormField(panel, "Contact:", contactField, gbc, row++);
        addFormField(panel, "Membership Type:", membershipTypeCombo, gbc, row++);
        addFormField(panel, "Program:", programCombo, gbc, row++);
        addFormField(panel, "Start Date (YYYY-MM-DD):", startDateField, gbc, row++);
        addFormField(panel, "Number of Sessions:", sessionsField, gbc, row++);
        addFormField(panel, "Total Cost:", totalCostField, gbc, row++);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createButton = new JButton("Create");
        JButton editButton = new JButton("Edit");
        JButton cancelButton = new JButton("Cancel");
        JButton resetButton = new JButton("Reset");

        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(resetButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Add action listeners
        createButton.addActionListener(e -> handleCreate());
        editButton.addActionListener(e -> handleEdit());
        cancelButton.addActionListener(e -> dispose());
        resetButton.addActionListener(e -> clearForm());

        // Add sessions field listener to calculate total cost
        sessionsField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateTotalCost(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateTotalCost(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateTotalCost(); }
        });

        return panel;
    }

    private void addFormField(JPanel panel, String label, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columns = {"Booking ID", "Full Name", "Contact", "Membership", "Program", "Start Date", "Sessions", "Cost"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add table selection listener
        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = bookingsTable.getSelectedRow();
                if (row != -1) {
                    loadBookingData(row);
                }
            }
        });

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
                        String.format("%.2f", rs.getDouble("totalCost"))
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading bookings: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBookingData(int row) {
        currentBookingId = (String) tableModel.getValueAt(row, 0);
        bookingIdLabel.setText(currentBookingId);
        fullNameField.setText((String) tableModel.getValueAt(row, 1));
        contactField.setText((String) tableModel.getValueAt(row, 2));
        membershipTypeCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        programCombo.setSelectedItem(tableModel.getValueAt(row, 4));
        startDateField.setText((String) tableModel.getValueAt(row, 5));
        sessionsField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        totalCostField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
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
            JOptionPane.showMessageDialog(this,
                "Error generating booking ID: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
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
            if (membershipType.equals("Quarterly")) {
                totalCost *= 0.9; // 10% discount
            } else if (membershipType.equals("Annual")) {
                totalCost *= 0.8; // 20% discount
            }
            
            totalCostField.setText(String.format("%.2f", totalCost));
        } catch (NumberFormatException e) {
            totalCostField.setText("");
        }
    }

    private void handleCreate() {
        if (!validateInput()) {
            return;
        }

        // Calculate total cost before saving
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
                
                JOptionPane.showMessageDialog(this,
                    "Booking created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadBookings();
                clearForm();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error creating booking: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        if (!validateInput()) {
            return;
        }

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                UPDATE Booking 
                SET fullName = ?, contactNumber = ?, membershipType = ?,
                    program = ?, startDate = ?, numberOfSessions = ?, totalCost = ?
                WHERE bookingId = ?
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Set parameters in different order for UPDATE
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
                    JOptionPane.showMessageDialog(this,
                        "Booking updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadBookings();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Booking not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating booking: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        if (fullNameField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty() ||
            startDateField.getText().trim().isEmpty() ||
            sessionsField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "All fields are required!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate date format
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(startDateField.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format! Use YYYY-MM-DD",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate sessions is a number
        try {
            int sessions = Integer.parseInt(sessionsField.getText());
            if (sessions <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Number of sessions must be a positive number!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
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
        fullNameField.setText("");
        contactField.setText("");
        membershipTypeCombo.setSelectedIndex(0);
        programCombo.setSelectedIndex(0);
        startDateField.setText("");
        sessionsField.setText("");
        totalCostField.setText("");
        bookingsTable.clearSelection();
    }
}