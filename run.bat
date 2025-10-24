@echo off
echo Starting FitLife Gym Management System...
echo.

:: Check if compiled classes exist
if not exist "target\classes" (
    echo Error: Application not compiled!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

:: Check if SQLite JDBC driver exists
if not exist "lib\sqlite-jdbc-3.43.0.0.jar" (
    echo Error: SQLite JDBC driver not found!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

:: Run the application
java -cp "target/classes;lib/*" com.fitlife.ui.LoginForm

if %errorlevel% neq 0 (
    echo.
    echo Application exited with an error.
    pause
)