package com.fitlife.ui;

import com.fitlife.db.DatabaseHelper;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ManageProgramsFormModern extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField costField;
    private JTextArea descriptionArea;
    private JTextField trainerField;
    private JTable programsTable;
    private DefaultTableModel tableModel;

    public ManageProgramsFormModern() {
        setTitle("FitLife Gym - Manage Programs");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ModernUIHelper.BACKGROUND);

        // Header
        JPanel header = ModernUIHelper.createGradientHeader("Manage Programs", 1100, 80);
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

        // Load programs
        loadPrograms();
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
        costField = ModernUIHelper.createStyledTextField(10);
        trainerField = ModernUIHelper.createStyledTextField(20);
        
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIHelper.BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(descriptionArea.getBorder());

        // Add fields to form (Column 1)
        int row = 0;
        addFormField(formGrid, "Program ID:", idField, gbc, row++, 0);
        addFormField(formGrid, "Name:", nameField, gbc, row++, 0);
        addFormField(formGrid, "Cost per Session:", costField, gbc, row++, 0);

        // Add fields to form (Column 2)
        row = 0;
        addFormField(formGrid, "Trainer:", trainerField, gbc, row++, 2);
        
        // Description spans both columns
        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formGrid.add(ModernUIHelper.createLabel("Description:", true), gbc);

        gbc.gridx = 3;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        gbc.gridheight = 2;
        formGrid.add(descScrollPane, gbc);
        gbc.gridheight = 1;

        card.add(formGrid, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(ModernUIHelper.CARD_COLOR);

        JButton addBtn = ModernUIHelper.createStyledButton("Add Program", ModernUIHelper.SUCCESS_COLOR);
        JButton editBtn = ModernUIHelper.createStyledButton("Edit", ModernUIHelper.INFO_COLOR);
        JButton deleteBtn = ModernUIHelper.createStyledButton("Delete", ModernUIHelper.DANGER_COLOR);
        JButton clearBtn = ModernUIHelper.createStyledButton("Clear", ModernUIHelper.WARNING_COLOR);
        JButton closeBtn = ModernUIHelper.createStyledButton("Close", new Color(149, 165, 166));

        addBtn.addActionListener(e -> handleAdd());
        editBtn.addActionListener(e -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIHelper.CARD_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(ModernUIHelper.BORDER_COLOR, 1));

        String[] columns = {"ID", "Name", "Cost/Session", "Description", "Trainer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        programsTable = new JTable(tableModel);
        ModernUIHelper.styleTable(programsTable);
        
        // Set column widths
        programsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        programsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        programsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        programsTable.getColumnModel().getColumn(3).setPreferredWidth(350);
        programsTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        programsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = programsTable.getSelectedRow();
                if (row != -1) {
                    loadProgramData(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(programsTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadPrograms() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = "SELECT * FROM Program ORDER BY id";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Object[] row = {
                        rs.getString("id"),
                        rs.getString("name"),
                        "Rs. " + rs.getInt("costPerSession"),
                        rs.getString("description"),
                        rs.getString("trainer")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            showError("Error loading programs: " + e.getMessage());
        }
    }

    private void loadProgramData(int row) {
        idField.setText((String) tableModel.getValueAt(row, 0));
        nameField.setText((String) tableModel.getValueAt(row, 1));
        String costStr = String.valueOf(tableModel.getValueAt(row, 2));
        costField.setText(costStr.replace("Rs. ", ""));
        descriptionArea.setText((String) tableModel.getValueAt(row, 3));
        trainerField.setText((String) tableModel.getValueAt(row, 4));
    }

    private boolean validateInput() {
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            costField.getText().trim().isEmpty()) {

            showError("ID, Name, and Cost are required fields!");
            return false;
        }

        try {
            int cost = Integer.parseInt(costField.getText());
            if (cost <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showError("Cost must be a positive number!");
            return false;
        }

        return true;
    }

    private void handleAdd() {
        if (!validateInput()) return;

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                INSERT INTO Program (id, name, costPerSession, description, trainer)
                VALUES (?, ?, ?, ?, ?)
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                setProgramParameters(pstmt);
                pstmt.executeUpdate();

                showSuccess("Program added successfully!");
                loadPrograms();
                clearForm();
            }
        } catch (SQLException e) {
            showError("Error adding program: " + e.getMessage());
        }
    }

    private void handleEdit() {
        if (!validateInput()) return;

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                UPDATE Program 
                SET name = ?, costPerSession = ?, description = ?, trainer = ?
                WHERE id = ?
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, nameField.getText());
                pstmt.setInt(2, Integer.parseInt(costField.getText()));
                pstmt.setString(3, descriptionArea.getText());
                pstmt.setString(4, trainerField.getText());
                pstmt.setString(5, idField.getText());

                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    showSuccess("Program updated successfully!");
                    loadPrograms();
                    clearForm();
                } else {
                    showError("Program not found!");
                }
            }
        } catch (SQLException e) {
            showError("Error updating program: " + e.getMessage());
        }
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showError("Please select a program to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete program " + nameField.getText() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseHelper.getInstance().getConnection();
                String query = "DELETE FROM Program WHERE id = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, id);

                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showSuccess("Program deleted successfully!");
                        loadPrograms();
                        clearForm();
                    } else {
                        showError("Program not found!");
                    }
                }
            } catch (SQLException e) {
                showError("Error deleting program: " + e.getMessage());
            }
        }
    }

    private void setProgramParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, idField.getText());
        pstmt.setString(2, nameField.getText());
        pstmt.setInt(3, Integer.parseInt(costField.getText()));
        pstmt.setString(4, descriptionArea.getText());
        pstmt.setString(5, trainerField.getText());
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        costField.setText("");
        descriptionArea.setText("");
        trainerField.setText("");
        programsTable.clearSelection();
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
