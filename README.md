````markdown
# FitLife Gym Management System

Project description

FitLife is a lightweight desktop Gym Management System built with Java Swing and an embedded SQLite database. It helps small gyms and fitness studios manage staff, create and maintain programs, and handle member bookings and registrations. The user interface is intentionally simple and desktop-first so it can be run locally without a server.

Primary goals:

- Provide an easy-to-use admin interface for staff and program management.
- Let members view programs and manage bookings.
- Keep the system self-contained (single-file SQLite DB) so it can run offline.

Intended audience:

- Small gym owners or managers who need an offline desktop tool.
- Students and developers learning Java Swing, desktop app packaging, and SQLite integration.

## Quick overview

- Language: Java (Swing)
- Database: SQLite (local DB file `GymDB.db` included)
- Build: Plain Java source (no Maven/Gradle). Project includes `compile.bat` and `run.bat` for Windows.

## Prerequisites

1. Java JDK 17 or higher (OpenJDK or Oracle JDK)
2. (Optional) An IDE like IntelliJ IDEA or Eclipse

## Build & run (Windows)

The repository includes helper scripts for Windows:

1. Open PowerShell and change to the project directory:

```powershell
cd "C:\Users\Anjalee Himalki\Desktop\Fitlife"
```

2. To compile (uses the bundled SQLite JDBC JAR in `lib`):

```powershell
# compile with provided script
.\compile.bat
```

3. To run the application:

```powershell
# run with provided script
.\run.bat
```

If you prefer to compile manually, here's an example (Windows):

```powershell
# create build output dir
mkdir -Force build\classes
# compile all Java sources and include sqlite-jdbc on the classpath
javac -d build\classes -cp "lib\sqlite-jdbc-3.43.0.0.jar" @(Get-ChildItem -Recurse -Filter *.java).FullName
# run the LoginForm
java -cp "build\classes;lib\sqlite-jdbc-3.43.0.0.jar" com.fitlife.ui.LoginForm
```

On macOS/Linux the equivalent commands use `:` for classpath separators and `sh` scripts if available.

## Notes about the database

- The project includes `GymDB.db` in the repository for convenience. The app will use this SQLite file in the project root.
- If you want a fresh database, remove or rename `GymDB.db` and the application will recreate tables as needed.

## Default credentials (for demo)

- Admin: `admin` / `admin123`
- Sample members: `amal` / `amal123`, `nimal` / `nimal123`, `bimal` / `bimal123`

## Project structure

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

## License

This repository is not licensed yet. See `LICENSE` if present.

## Contributing

If you'd like to contribute, please open an issue or a pull request. See `CONTRIBUTING.md` for guidelines when available.
````