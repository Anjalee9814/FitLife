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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fitlife.db.DatabaseHelper;

public class ManageProgramsForm extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField costField;
    private JTextArea descriptionArea;
    private JTextField trainerField;
    private JTable programsTable;
    private DefaultTableModel tableModel;

    public ManageProgramsForm() {
        setTitle("FitLife Gym - Manage Programs");
        setSize(800, 700);
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
        splitPane.setDividerLocation(350);
        
        add(splitPane);
        
        // Load existing programs
        loadPrograms();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize components
        idField = new JTextField(10);
        nameField = new JTextField(20);
        costField = new JTextField(10);
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        trainerField = new JTextField(20);

        // Add components
        int row = 0;
        addFormField(panel, "Program ID:", idField, gbc, row++);
        addFormField(panel, "Name:", nameField, gbc, row++);
        addFormField(panel, "Cost per Session:", costField, gbc, row++);
        addFormField(panel, "Description:", descScrollPane, gbc, row++);
        addFormField(panel, "Trainer:", trainerField, gbc, row++);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Add action listeners
        addButton.addActionListener(e -> handleAdd());
        editButton.addActionListener(e -> handleEdit());
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
        
        // Create table model
        String[] columns = {"ID", "Name", "Cost per Session", "Description", "Trainer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        programsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(programsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add table selection listener
        programsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = programsTable.getSelectedRow();
                if (row != -1) {
                    loadProgramData(row);
                }
            }
        });

        return panel;
    }

    private void loadPrograms() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = "SELECT * FROM Program";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("costPerSession"),
                        rs.getString("description"),
                        rs.getString("trainer")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading programs: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProgramData(int row) {
        idField.setText((String) tableModel.getValueAt(row, 0));
        nameField.setText((String) tableModel.getValueAt(row, 1));
        costField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        descriptionArea.setText((String) tableModel.getValueAt(row, 3));
        trainerField.setText((String) tableModel.getValueAt(row, 4));
    }

    private boolean validateInput() {
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            costField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "ID, Name, and Cost are required fields!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int cost = Integer.parseInt(costField.getText());
            if (cost <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Cost must be a positive number!",
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
                INSERT INTO Program (id, name, costPerSession, description, trainer)
                VALUES (?, ?, ?, ?, ?)
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                setProgramParameters(pstmt);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this,
                    "Program added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadPrograms();
                clearForm();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error adding program: " + e.getMessage(),
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
                UPDATE Program 
                SET name = ?, costPerSession = ?, description = ?, trainer = ?
                WHERE id = ?
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Set parameters in different order for UPDATE
                pstmt.setString(1, nameField.getText());
                pstmt.setInt(2, Integer.parseInt(costField.getText()));
                pstmt.setString(3, descriptionArea.getText());
                pstmt.setString(4, trainerField.getText());
                pstmt.setString(5, idField.getText());
                
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Program updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadPrograms();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Program not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error updating program: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a program to delete!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this program?",
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
                        JOptionPane.showMessageDialog(this,
                            "Program deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        loadPrograms();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Program not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error deleting program: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
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
}