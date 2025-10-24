# FitLife Gym Management System

A Java Swing application for managing a gym's operations including staff, programs, and member bookings.

## Prerequisites

1. Java JDK 17 or higher (OpenJDK recommended)
2. Apache Maven
3. SQLite (included via Maven dependency)

## Setup Instructions

1. Install Java:
   - Download and install OpenJDK from [Adoptium](https://adoptium.net/)
   - Add Java to your system PATH

2. Install Maven:
   - Download Apache Maven from [Maven website](https://maven.apache.org/download.cgi)
   - Extract the archive to a directory of your choice
   - Add Maven's bin directory to your system PATH
   - Verify installation with `mvn -v`

3. Build the project:
   ```bash
   mvn clean package
   ```

4. Run the application:
   ```bash
   java -cp target/fitlife-gym-1.0-SNAPSHOT.jar com.fitlife.ui.LoginForm
   ```

## Default Login Credentials

### Admin Account
- Username: admin
- Password: admin123

### Sample Member Accounts
- Username: amal, Password: amal123
- Username: nimal, Password: nimal123
- Username: bimal, Password: bimal123

## Features

1. **User Authentication**
   - Login with admin/member accounts
   - New member registration

2. **Admin Features**
   - Manage staff members (add, update, delete)
   - Manage gym programs (add, edit, delete)

3. **Member Features**
   - View available programs
   - Manage bookings (create, edit, cancel)

4. **Database**
   - SQLite database for persistent storage
   - Automatic database creation and table setup

## Technical Details

- Built with Java Swing for GUI
- Uses SQLite for database management
- Follows MVC pattern for code organization
- Implements input validation and error handling

## Project Structure

```
src/main/java/com/fitlife/
├── db/
│   └── DatabaseHelper.java
└── ui/
    ├── LoginForm.java
    ├── RegisterForm.java
    ├── MainMenuForm.java
    ├── ManageStaffForm.java
    ├── ManageProgramsForm.java
    ├── ManageBookingsForm.java
    └── ViewProgramsForm.java
```