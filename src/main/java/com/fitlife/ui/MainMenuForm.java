package com.fitlife.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainMenuForm extends JFrame {
    private final boolean isAdmin;
    private final String username;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(65, 105, 225);
    private static final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BACKGROUND = new Color(245, 247, 250);

    public MainMenuForm(boolean isAdmin, String username) {
        this.isAdmin = isAdmin;
        this.username = username;
        
        setTitle("FitLife Gym - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with background color
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Create buttons based on user type
        if (isAdmin) {
            createAdminDashboard(contentPanel);
        } else {
            createMemberDashboard(contentPanel);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);
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
        header.setPreferredSize(new Dimension(900, 100));
        header.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Left side - Welcome text
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome Back, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel);

        header.add(leftPanel, BorderLayout.WEST);

        // Right side - Logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutButton.setForeground(PRIMARY_COLOR);
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(new EmptyBorder(10, 25, 10, 25));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton.addActionListener(e -> {
            new LoginForm().setVisible(true);
            this.dispose();
        });

        rightPanel.add(logoutButton);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private void createAdminDashboard(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;

        // Manage Staff Card
        JPanel staffCard = createDashboardCard(
            "👥", 
            "Manage Staff", 
            "Add, edit, or remove staff members",
            new Color(52, 152, 219),
            e -> new ManageStaffFormModern().setVisible(true)
        );
        panel.add(staffCard, gbc);

        // Manage Programs Card
        gbc.gridx = 1;
        JPanel programsCard = createDashboardCard(
            "🏋️", 
            "Manage Programs", 
            "Create and update fitness programs",
            new Color(155, 89, 182),
            e -> new ManageProgramsFormModern().setVisible(true)
        );
        panel.add(programsCard, gbc);
    }

    private void createMemberDashboard(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        JLabel titleLabel = new JLabel("Member Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;

        // Manage Bookings Card
        JPanel bookingsCard = createDashboardCard(
            "📅", 
            "My Bookings", 
            "View and manage your session bookings",
            new Color(46, 204, 113),
            e -> new ManageBookingsFormModern().setVisible(true)
        );
        panel.add(bookingsCard, gbc);

        // View Programs Card
        gbc.gridx = 1;
        JPanel viewProgramsCard = createDashboardCard(
            "🎯", 
            "Browse Programs", 
            "Explore available fitness programs",
            new Color(230, 126, 34),
            e -> new ViewProgramsForm().setVisible(true)
        );
        panel.add(viewProgramsCard, gbc);
    }

    private JPanel createDashboardCard(String emoji, String title, String description, 
                                      Color accentColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setPreferredSize(new Dimension(360, 280));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    new EmptyBorder(29, 29, 29, 29)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(CARD_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(30, 30, 30, 30)
                ));
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (action != null) {
                    action.actionPerformed(null);
                }
            }
        });

        // Icon
        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(iconLabel);

        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Description
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(120, 120, 120));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(descLabel);

        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Action button
        JButton actionButton = new JButton("Open");
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionButton.setForeground(Color.BLACK);
        actionButton.setBackground(accentColor);
        actionButton.setFocusPainted(false);
        actionButton.setBorder(new EmptyBorder(10, 30, 10, 30));
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.addActionListener(action);

        card.add(actionButton);

        return card;
    }
}