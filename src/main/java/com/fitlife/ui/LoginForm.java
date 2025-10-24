package com.fitlife.ui;

import com.fitlife.db.DatabaseHelper;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(65, 105, 225);
    private static final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private static final Color ACCENT_COLOR = new Color(255, 99, 71);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BUTTON_HOVER = new Color(55, 95, 215);

    public LoginForm() {
        setTitle("FitLife Gym - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
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
        
        // Create card panel for login form
        JPanel cardPanel = createCardPanel();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(cardPanel, gbc);
        
        // Add the main panel to the frame
        add(mainPanel);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterForm());
        exitButton.addActionListener(e -> System.exit(0));
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
        JLabel logoLabel = new JLabel("💪");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        logoPanel.add(logoLabel);
        card.add(logoPanel);
        
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Title Label
        JLabel titleLabel = new JLabel("FitLife Gym");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Your Fitness Journey Starts Here");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);
        
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
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
        
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        
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
        
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Login Button
        loginButton = createStyledButton("Login", PRIMARY_COLOR, Color.BLACK);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginButton);
        
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Register Button
        registerButton = createStyledButton("Create New Account", Color.WHITE, Color.BLACK);
        registerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(12, 30, 12, 30)
        ));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(registerButton);
        
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Exit Button
        exitButton = createStyledButton("Exit", new Color(220, 220, 220), TEXT_COLOR);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(exitButton);
        
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

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            String query = "SELECT isAdmin FROM Login WHERE username = ? AND password = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    boolean isAdmin = rs.getBoolean("isAdmin");
                    this.dispose();
                    new MainMenuForm(isAdmin, username).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Database error occurred!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegisterForm() {
        new RegisterForm().setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}