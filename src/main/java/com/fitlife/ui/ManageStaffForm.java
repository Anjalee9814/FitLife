package com.fitlife.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.fitlife.db.DatabaseHelper;

public class ManageStaffForm extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JComboBox<String> genderCombo;
    private JComboBox<String> roleCombo;
    private JTextField contactField;
    private JTextField emailField;
    private JTextField salaryField;
    private JTextField searchField;
    private JTable staffTable;
    private DefaultTableModel tableModel;

    public ManageStaffForm() {
        setTitle("FitLife Gym - Manage Staff");
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Create split pane
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
        
        // Load existing staff
        loadStaff("");
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize components
        idField = new JTextField(10);
        nameField = new JTextField(20);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        roleCombo = new JComboBox<>(new String[]{"Trainer", "Reception", "Cleaner", "Nutritionist"});
        contactField = new JTextField(15);
        emailField = new JTextField(20);
        salaryField = new JTextField(10);
        salaryField.setEditable(false);

        // Add components
        int row = 0;
        addFormField(panel, "Staff ID:", idField, gbc, row++);
        addFormField(panel, "Name:", nameField, gbc, row++);
        addFormField(panel, "Gender:", genderCombo, gbc, row++);
        addFormField(panel, "Role:", roleCombo, gbc, row++);
        addFormField(panel, "Contact:", contactField, gbc, row++);
        addFormField(panel, "Email:", emailField, gbc, row++);
        addFormField(panel, "Salary:", salaryField, gbc, row++);

        // Role change listener to update salary
        roleCombo.addActionListener(e -> updateSalary());

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Add action listeners
        addButton.addActionListener(e -> handleAdd());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e -> clearForm());

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

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by Name:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> loadStaff(searchField.getText().trim()));
        searchPanel.add(searchButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "Gender", "Role", "Contact", "Email", "Salary"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        staffTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add table selection listener
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = staffTable.getSelectedRow();
                if (row != -1) {
                    loadStaffData(row);
                }
            }
        });

        return panel;
    }

    private void updateSalary() {
        String role = (String) roleCombo.getSelectedItem();
        int salary = switch (role) {
            case "Trainer" -> 70000;
            case "Reception" -> 40000;
            case "Cleaner" -> 30000;
            case "Nutritionist" -> 60000;
            default -> 0;
        };
        salaryField.setText(String.valueOf(salary));
    }

    private void loadStaff(String searchName) {
        tableModel.setRowCount(0);
        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = "SELECT * FROM Staff WHERE name LIKE ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, "%" + searchName + "%");
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getString("role"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getInt("salary")
                    };
                    tableModel.addRow(row);
                }
            }

            if (tableModel.getRowCount() == 0 && !searchName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No staff members found matching your search.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading staff data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStaffData(int row) {
        idField.setText((String) tableModel.getValueAt(row, 0));
        nameField.setText((String) tableModel.getValueAt(row, 1));
        genderCombo.setSelectedItem(tableModel.getValueAt(row, 2));
        roleCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        contactField.setText((String) tableModel.getValueAt(row, 4));
        emailField.setText((String) tableModel.getValueAt(row, 5));
        salaryField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
    }

    private boolean validateInput() {
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "ID, Name, and Contact are required fields!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Invalid email format!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                INSERT INTO Staff (id, name, gender, role, contact, email, salary)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                setStaffParameters(pstmt);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this,
                    "Staff member added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadStaff("");
                clearForm();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error adding staff member: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (!validateInput()) {
            return;
        }

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                UPDATE Staff 
                SET name = ?, gender = ?, role = ?, contact = ?, email = ?, salary = ?
                WHERE id = ?
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Set parameters in different order for UPDATE
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, (String) genderCombo.getSelectedItem());
                pstmt.setString(3, (String) roleCombo.getSelectedItem());
                pstmt.setString(4, contactField.getText());
                pstmt.setString(5, emailField.getText());
                pstmt.setInt(6, Integer.parseInt(salaryField.getText()));
                pstmt.setString(7, idField.getText());
                
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Staff member updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadStaff("");
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Staff member not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error updating staff member: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a staff member to delete!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this staff member?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseHelper.getInstance().getConnection();
                String query = "DELETE FROM Staff WHERE id = ?";
                
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, id);
                    
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Staff member deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        loadStaff("");
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Staff member not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error deleting staff member: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setStaffParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, idField.getText());
        pstmt.setString(2, nameField.getText());
        pstmt.setString(3, (String) genderCombo.getSelectedItem());
        pstmt.setString(4, (String) roleCombo.getSelectedItem());
        pstmt.setString(5, contactField.getText());
        pstmt.setString(6, emailField.getText());
        pstmt.setInt(7, Integer.parseInt(salaryField.getText()));
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        genderCombo.setSelectedIndex(0);
        roleCombo.setSelectedIndex(0);
        contactField.setText("");
        emailField.setText("");
        updateSalary();
        searchField.setText("");
        staffTable.clearSelection();
    }
}