package com.fitlife.ui;

import com.fitlife.db.DatabaseHelper;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ViewProgramsForm extends JFrame {
    private JTextField searchField;
    private JTable programsTable;
    private DefaultTableModel tableModel;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(65, 105, 225);
    private static final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color HEADER_BG = new Color(52, 73, 94);

    public ViewProgramsForm() {
        setTitle("FitLife Gym - Browse Programs");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Search panel
        JPanel searchPanel = createSearchPanel();
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Load all programs initially
        loadPrograms("");
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, w, 0, SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        header.setPreferredSize(new Dimension(1000, 100));
        header.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Available Programs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        header.add(titleLabel, BorderLayout.WEST);

        return header;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        panel.add(searchLabel);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        panel.add(searchField);

        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.addActionListener(e -> searchPrograms());
        panel.add(searchButton);

        JButton clearButton = createStyledButton("Clear", new Color(149, 165, 166));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadPrograms("");
        });
        panel.add(clearButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Table columns
        String[] columns = {"ID", "Name", "Cost per Session", "Description", "Trainer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        programsTable = new JTable(tableModel);
        programsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        programsTable.setRowHeight(35);
        programsTable.setShowVerticalLines(false);
        programsTable.setGridColor(new Color(240, 240, 240));
        programsTable.setSelectionBackground(new Color(232, 240, 254));
        programsTable.setSelectionForeground(TEXT_COLOR);

    // Style table header (light background, black text)
    JTableHeader header = programsTable.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    header.setBackground(Color.WHITE);
    header.setForeground(Color.BLACK);
    header.setPreferredSize(new Dimension(header.getWidth(), 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        programsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        programsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Set column widths
        programsTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        programsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        programsTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        programsTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        programsTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(programsTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);

        JButton closeButton = createStyledButton("Close", new Color(231, 76, 60));
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.BLACK);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = bgColor;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(original);
            }
        });

        return button;
    }

    private void searchPrograms() {
        String searchTerm = searchField.getText().trim();
        loadPrograms(searchTerm);
    }

    private void loadPrograms(String searchTerm) {
        tableModel.setRowCount(0);
        
        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = """
                SELECT * FROM Program 
                WHERE name LIKE ? 
                   OR description LIKE ? 
                   OR trainer LIKE ?
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                String searchPattern = "%" + searchTerm + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
                
                ResultSet rs = pstmt.executeQuery();
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

            if (tableModel.getRowCount() == 0 && !searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No programs found matching your search.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading programs: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}