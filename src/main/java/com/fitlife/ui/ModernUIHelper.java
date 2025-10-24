package com.fitlife.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Helper class for creating consistent modern UI components
 */
public class ModernUIHelper {
    // Modern color scheme
    public static final Color PRIMARY_COLOR = new Color(65, 105, 225);
    public static final Color SECONDARY_COLOR = new Color(100, 149, 237);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color DANGER_COLOR = new Color(231, 76, 60);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color INFO_COLOR = new Color(52, 152, 219);
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    public static final Color HEADER_BG = new Color(52, 73, 94);
    public static final Color BORDER_COLOR = new Color(220, 220, 220);
    
    /**
     * Creates a styled button with hover effect
     */
    public static JButton createStyledButton(String text, Color bgColor) {
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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(original);
            }
        });

        return button;
    }
    
    /**
     * Creates a styled text field with modern borders
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    /**
     * Creates a styled combo box
     */
    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(new EmptyBorder(4, 8, 4, 8));
        combo.setBackground(Color.WHITE);
        return combo;
    }
    
    /**
     * Creates a styled label
     */
    public static JLabel createLabel(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    /**
     * Creates a header panel with gradient background
     */
    public static JPanel createGradientHeader(String title, int width, int height) {
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
        header.setPreferredSize(new Dimension(width, height));
        header.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        return header;
    }
    
    /**
     * Styles a table with modern look
     */
    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_COLOR);
        
    // Style table header - light background with black text for visibility
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    table.getTableHeader().setBackground(Color.WHITE);
    table.getTableHeader().setForeground(Color.BLACK);
    table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 38));
    }
}
