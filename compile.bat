@echo off
echo Compiling FitLife Gym Management System...

:: Create necessary directories if they don't exist
if not exist "target" mkdir target
if not exist "target\classes" mkdir target\classes

:: Compile all Java files
javac -d target/classes -cp "lib/*" src/main/java/com/fitlife/db/*.java src/main/java/com/fitlife/ui/*.java

if %errorlevel% equ 0 (
    echo Compilation successful!
    echo Creating lib directory and downloading SQLite JDBC if not exists...
    if not exist "lib" mkdir lib
    if not exist "lib\sqlite-jdbc-3.43.0.0.jar" (
        powershell -Command "& { Invoke-WebRequest -Uri 'https://github.com/xerial/sqlite-jdbc/releases/download/3.43.0.0/sqlite-jdbc-3.43.0.0.jar' -OutFile 'lib\sqlite-jdbc-3.43.0.0.jar' }"
    )
) else (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo You can now run the application using run.bat
pause