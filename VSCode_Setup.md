# VSCode Setup Instructions for CPU Scheduler Web Application

## Prerequisites
1. **Java Extension Pack** - Install from VSCode Extensions marketplace
2. **Apache Tomcat** - Download and extract to project folder

## Quick Setup Steps

### 1. Install Required Extensions
Open VSCode and install these extensions:
- **Extension Pack for Java** (by Microsoft)
- **Tomcat for Java** (optional, for easier Tomcat management)

### 2. Download Tomcat
1. Download Apache Tomcat 9.x from: https://tomcat.apache.org/download-90.cgi
2. Extract to your project folder: `C:\scheduler\tomcat\`
3. Download servlet-api.jar and place in `lib\` folder

### 3. Download Servlet API
```powershell
# Run this in PowerShell to download servlet-api.jar
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar" -OutFile "lib\servlet-api.jar"
```

## Running the Application

### Method 1: Using VSCode Tasks (Recommended)
1. Press `Ctrl+Shift+P` to open command palette
2. Type "Tasks: Run Task"
3. Select "Run Complete Setup" - this will:
   - Build the Java classes
   - Deploy to Tomcat
   - Start Tomcat server

### Method 2: Using Terminal
1. Open integrated terminal (`Ctrl+``)
2. Run the build script:
   ```cmd
   .\build.bat
   ```
3. Start Tomcat manually:
   ```cmd
   .\tomcat\bin\startup.bat
   ```

### Method 3: Using Debug Configuration
1. Go to Run and Debug view (`Ctrl+Shift+D`)
2. Select "Run Tomcat with Debug" from dropdown
3. Click the play button or press `F5`

## Accessing the Application
Once Tomcat is running:
1. Open your browser
2. Navigate to: `http://localhost:8080/scheduler`
3. Use the web interface to run simulations

## VSCode Features Available

### IntelliSense & Debugging
- Full Java IntelliSense support
- Breakpoint debugging in servlets
- Hot code replacement during debugging

### Tasks Available
- **Build Scheduler App** - Compiles Java classes
- **Deploy to Tomcat** - Copies app to Tomcat webapps
- **Start Tomcat Server** - Starts Tomcat
- **Stop Tomcat Server** - Stops Tomcat
- **Run Complete Setup** - Runs all steps in sequence

### Debug Configurations
- **Debug Scheduler Servlet** - Debug individual servlet
- **Run Tomcat with Debug** - Debug entire Tomcat application

## Troubleshooting

### Common Issues:
1. **"servlet-api.jar not found"**
   - Download servlet-api.jar to `lib\` folder
   - Or update the path in `.vscode/tasks.json`

2. **"Tomcat not found"**
   - Extract Tomcat to `tomcat\` folder in project root
   - Update paths in `.vscode/tasks.json` if needed

3. **"Port 8080 already in use"**
   - Stop other services using port 8080
   - Or change Tomcat port in `tomcat/conf/server.xml`

### Useful Commands:
- `Ctrl+Shift+P` → "Java: Reload Projects" - Refresh Java project
- `Ctrl+Shift+P` → "Tasks: Run Task" - Run build tasks
- `F5` - Start debugging
- `Ctrl+F5` - Run without debugging

## Project Structure
```
scheduler/
├── .vscode/                 # VSCode configuration
│   ├── tasks.json          # Build and run tasks
│   ├── launch.json         # Debug configurations
│   └── settings.json       # Project settings
├── lib/                    # External JAR files
│   └── servlet-api.jar
├── tomcat/                 # Tomcat installation
├── scheduler-webapp/       # Web application
└── build.bat              # Build script
```

## Next Steps
1. Install Java Extension Pack
2. Download and extract Tomcat
3. Download servlet-api.jar
4. Run "Run Complete Setup" task
5. Access `http://localhost:8080/scheduler`
