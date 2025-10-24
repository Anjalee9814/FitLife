package com.fitlife.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:GymDB.db";
    private static DatabaseHelper instance;
    private Connection connection;

    private DatabaseHelper() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initializeTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private void initializeTables() throws SQLException {
        // Create Login table
        String createLoginTable = """
            CREATE TABLE IF NOT EXISTS Login (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(50) NOT NULL,
                isAdmin BOOLEAN DEFAULT 0
            )
        """;

        // Create Program table
        String createProgramTable = """
            CREATE TABLE IF NOT EXISTS Program (
                id VARCHAR(10) PRIMARY KEY,
                name VARCHAR(30) NOT NULL,
                costPerSession INTEGER NOT NULL,
                description VARCHAR(100),
                trainer VARCHAR(30)
            )
        """;

        // Create Staff table
        String createStaffTable = """
            CREATE TABLE IF NOT EXISTS Staff (
                id VARCHAR(10) PRIMARY KEY,
                name VARCHAR(50) NOT NULL,
                gender VARCHAR(10),
                role VARCHAR(20) CHECK(role IN ('Trainer', 'Reception', 'Cleaner', 'Nutritionist')),
                contact VARCHAR(15),
                email VARCHAR(50),
                salary INTEGER
            )
        """;

        // Create Booking table
        String createBookingTable = """
            CREATE TABLE IF NOT EXISTS Booking (
                bookingId VARCHAR(10) PRIMARY KEY,
                fullName VARCHAR(50) NOT NULL,
                contactNumber VARCHAR(15),
                membershipType VARCHAR(20) CHECK(membershipType IN ('Monthly', 'Quarterly', 'Annual')),
                program VARCHAR(30),
                startDate DATE,
                numberOfSessions INTEGER,
                totalCost DECIMAL(10,2)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createLoginTable);
            stmt.execute(createProgramTable);
            stmt.execute(createStaffTable);
            stmt.execute(createBookingTable);
            
            // Insert default admin account if it doesn't exist
            String insertAdmin = "INSERT OR IGNORE INTO Login (username, password, isAdmin) VALUES ('admin', 'admin123', 1)";
            stmt.execute(insertAdmin);
            
            // Insert sample customer accounts
            String[] sampleUsers = {
                "INSERT OR IGNORE INTO Login (username, password, isAdmin) VALUES ('amal', 'amal123', 0)",
                "INSERT OR IGNORE INTO Login (username, password, isAdmin) VALUES ('nimal', 'nimal123', 0)",
                "INSERT OR IGNORE INTO Login (username, password, isAdmin) VALUES ('bimal', 'bimal123', 0)"
            };
            
            for (String sql : sampleUsers) {
                stmt.execute(sql);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}