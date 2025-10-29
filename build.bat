@echo off
echo Building CPU Scheduler Web Application...

REM Set Tomcat path - UPDATE THIS PATH TO YOUR TOMCAT INSTALLATION
set TOMCAT_HOME=C:\apache-tomcat-9.0.xx
set SERVLET_JAR=%TOMCAT_HOME%\lib\servlet-api.jar

REM Check if Tomcat path exists
if not exist "%TOMCAT_HOME%" (
    echo ERROR: Tomcat not found at %TOMCAT_HOME%
    echo Please update the TOMCAT_HOME variable in this script to point to your Tomcat installation
    pause
    exit /b 1
)

REM Check if servlet-api.jar exists
if not exist "%SERVLET_JAR%" (
    echo ERROR: servlet-api.jar not found at %SERVLET_JAR%
    echo Please ensure Tomcat is properly installed
    pause
    exit /b 1
)

REM Navigate to classes directory
cd scheduler-webapp\WEB-INF\classes

REM Compile Java files
echo Compiling Java source files...
javac -cp "%SERVLET_JAR%" com\scheduler\*.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo Compilation successful!

REM Go back to root directory
cd ..\..

REM Create WAR file (optional)
echo Creating WAR file...
jar -cf scheduler.war -C scheduler-webapp .

if %ERRORLEVEL% neq 0 (
    echo WARNING: WAR file creation failed, but compilation was successful
    echo You can still deploy the application manually
) else (
    echo WAR file created successfully: scheduler.war
)

echo.
echo Build completed successfully!
echo.
echo Next steps:
echo 1. Copy scheduler-webapp folder to %TOMCAT_HOME%\webapps\
echo 2. Start Tomcat server
echo 3. Access http://localhost:8080/scheduler-webapp
echo.
pause
