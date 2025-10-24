package com.fitlife.ui;

import com.fitlife.db.DatabaseHelper;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(65, 105, 225);
    private static final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BUTTON_HOVER = new Color(55, 95, 215);

    public RegisterForm() {
        setTitle("FitLife Gym - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, h, SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Create card panel for registration form
        JPanel cardPanel = createCardPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(cardPanel, gbc);

        // Add the main panel to the frame
        add(mainPanel);

        // Add action listeners
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            new LoginForm().setVisible(true);
            this.dispose();
        });
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(40, 50, 40, 50)
        ));

        // Logo/Icon Panel
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel logoLabel = new JLabel("🏋️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        logoPanel.add(logoLabel);
        card.add(logoPanel);

        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Title Label
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Join FitLife Gym Today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);

        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Username Field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(TEXT_COLOR);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(usernameLabel);

        card.add(Box.createRigidArea(new Dimension(0, 5)));

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        usernameField.setMaximumSize(new Dimension(300, 40));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(usernameField);

        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(passwordLabel);

        card.add(Box.createRigidArea(new Dimension(0, 5)));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(passwordField);

        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirm Password Field
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmLabel.setForeground(TEXT_COLOR);
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(confirmLabel);

        card.add(Box.createRigidArea(new Dimension(0, 5)));

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        confirmPasswordField.setMaximumSize(new Dimension(300, 40));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(confirmPasswordField);

        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Register Button
        registerButton = createStyledButton("Register", PRIMARY_COLOR, Color.BLACK);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(registerButton);

        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Back Button
        backButton = createStyledButton("Back to Login", Color.WHITE, Color.BLACK);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(12, 30, 12, 30)
        ));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(backButton);

        return card;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(300, 45));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (bgColor.equals(PRIMARY_COLOR)) {
                    button.setBackground(BUTTON_HOVER);
                } else if (!bgColor.equals(Color.WHITE)) {
                    button.setBackground(bgColor.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate input
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username and password cannot be empty!",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match!",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = "INSERT INTO Login (username, password, isAdmin) VALUES (?, ?, 0)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                new LoginForm().setVisible(true);
                this.dispose();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("UNIQUE")) {
                JOptionPane.showMessageDialog(this,
                    "Username already exists!",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Database error occurred!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}