package com.fitlife.ui;

import com.fitlife.db.DatabaseHelper;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ManageStaffFormModern extends JFrame {
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

    public ManageStaffFormModern() {
        setTitle("FitLife Gym - Manage Staff");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ModernUIHelper.BACKGROUND);

        // Header
        JPanel header = ModernUIHelper.createGradientHeader("Manage Staff", 1200, 80);
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

        // Load staff
        loadStaff("");
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
        idField = ModernUIHelper.createStyledTextField(10);
        nameField = ModernUIHelper.createStyledTextField(20);
        genderCombo = ModernUIHelper.createStyledComboBox(new String[]{"Male", "Female"});
        roleCombo = ModernUIHelper.createStyledComboBox(new String[]{"Trainer", "Reception", "Cleaner", "Nutritionist"});
        contactField = ModernUIHelper.createStyledTextField(15);
        emailField = ModernUIHelper.createStyledTextField(20);
        salaryField = ModernUIHelper.createStyledTextField(10);
        salaryField.setEditable(false);
        salaryField.setBackground(new Color(240, 240, 240));

        // Add fields to form
        int row = 0;
        addFormField(formGrid, "Staff ID:", idField, gbc, row++, 0);
        addFormField(formGrid, "Name:", nameField, gbc, row++, 0);
        addFormField(formGrid, "Gender:", genderCombo, gbc, row++, 0);
        addFormField(formGrid, "Contact:", contactField, gbc, row++, 0);

        row = 0;
        addFormField(formGrid, "Role:", roleCombo, gbc, row++, 2);
        addFormField(formGrid, "Email:", emailField, gbc, row++, 2);
        addFormField(formGrid, "Salary:", salaryField, gbc, row++, 2);

        card.add(formGrid, BorderLayout.CENTER);

        // Role change listener to update salary
        roleCombo.addActionListener(e -> updateSalary());
        updateSalary(); // Set initial salary

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ModernUIHelper.CARD_COLOR);

        JButton addBtn = ModernUIHelper.createStyledButton("Add Staff", ModernUIHelper.SUCCESS_COLOR);
        JButton updateBtn = ModernUIHelper.createStyledButton("Update", ModernUIHelper.INFO_COLOR);
        JButton deleteBtn = ModernUIHelper.createStyledButton("Delete", ModernUIHelper.DANGER_COLOR);
        JButton clearBtn = ModernUIHelper.createStyledButton("Clear", ModernUIHelper.WARNING_COLOR);
        JButton closeBtn = ModernUIHelper.createStyledButton("Close", new Color(149, 165, 166));

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);

        card.add(buttonPanel, BorderLayout.SOUTH);

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
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(ModernUIHelper.CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIHelper.BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchPanel.add(searchLabel);

        searchField = ModernUIHelper.createStyledTextField(20);
        searchPanel.add(searchField);

        JButton searchBtn = ModernUIHelper.createStyledButton("Search", ModernUIHelper.PRIMARY_COLOR);
        searchBtn.addActionListener(e -> loadStaff(searchField.getText().trim()));
        searchPanel.add(searchBtn);

        JButton showAllBtn = ModernUIHelper.createStyledButton("Show All", ModernUIHelper.INFO_COLOR);
        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            loadStaff("");
        });
        searchPanel.add(showAllBtn);

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
        ModernUIHelper.styleTable(staffTable);

        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = staffTable.getSelectedRow();
                if (row != -1) {
                    loadStaffData(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

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
            String query = "SELECT * FROM Staff WHERE name LIKE ? ORDER BY id";

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
                        "Rs. " + rs.getInt("salary")
                    };
                    tableModel.addRow(row);
                }
            }

            if (tableModel.getRowCount() == 0 && !searchName.isEmpty()) {
                showInfo("No staff members found matching your search.");
            }
        } catch (SQLException e) {
            showError("Error loading staff data: " + e.getMessage());
        }
    }

    private void loadStaffData(int row) {
        idField.setText((String) tableModel.getValueAt(row, 0));
        nameField.setText((String) tableModel.getValueAt(row, 1));
        genderCombo.setSelectedItem(tableModel.getValueAt(row, 2));
        roleCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        contactField.setText((String) tableModel.getValueAt(row, 4));
        emailField.setText((String) tableModel.getValueAt(row, 5));
        String salaryStr = String.valueOf(tableModel.getValueAt(row, 6));
        salaryField.setText(salaryStr.replace("Rs. ", ""));
    }

    private boolean validateInput() {
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty()) {

            showError("ID, Name, and Contact are required fields!");
            return false;
        }

        // Validate email format
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format!");
            return false;
        }

        return true;
    }

    private void handleAdd() {
        if (!validateInput()) return;

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                INSERT INTO Staff (id, name, gender, role, contact, email, salary)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                setStaffParameters(pstmt);
                pstmt.executeUpdate();

                showSuccess("Staff member added successfully!");
                loadStaff("");
                clearForm();
            }
        } catch (SQLException e) {
            showError("Error adding staff member: " + e.getMessage());
        }
    }

    private void handleUpdate() {
        if (!validateInput()) return;

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                UPDATE Staff 
                SET name = ?, gender = ?, role = ?, contact = ?, email = ?, salary = ?
                WHERE id = ?
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, (String) genderCombo.getSelectedItem());
                pstmt.setString(3, (String) roleCombo.getSelectedItem());
                pstmt.setString(4, contactField.getText());
                pstmt.setString(5, emailField.getText());
                pstmt.setInt(6, Integer.parseInt(salaryField.getText()));
                pstmt.setString(7, idField.getText());

                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    showSuccess("Staff member updated successfully!");
                    loadStaff("");
                    clearForm();
                } else {
                    showError("Staff member not found!");
                }
            }
        } catch (SQLException e) {
            showError("Error updating staff member: " + e.getMessage());
        }
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showError("Please select a staff member to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete staff member " + nameField.getText() + "?",
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
                        showSuccess("Staff member deleted successfully!");
                        loadStaff("");
                        clearForm();
                    } else {
                        showError("Staff member not found!");
                    }
                }
            } catch (SQLException e) {
                showError("Error deleting staff member: " + e.getMessage());
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

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
