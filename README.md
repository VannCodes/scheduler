# CPU Scheduler Application

A JavaFX desktop application for visualizing CPU scheduling algorithms with a Python FastAPI backend.

## Prerequisites

### For Backend (Python)
- Python 3.8 or higher
- pip (Python package manager)

### For Frontend (Java)
- Java 17 or higher
- Maven 3.6 or higher

## How to Run

### Step 1: Start the Backend Server

1. Navigate to the backend directory:
   ```bash
   cd CPU_SCHEDULER
   ```

2. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```
   
   **Note for Python 3.13 users**: If you encounter Rust compilation errors, try:
   ```bash
   pip install --only-binary :all: -r requirements.txt
   ```
   
   This forces pip to use pre-built wheels only. If issues persist, consider using Python 3.11 or 3.12 which have better package support.

3. Start the FastAPI server:
   ```bash
   python api.py
   ```
   
   The server will start on `http://127.0.0.1:8001`
   
   (Alternatively, you can use uvicorn directly: `uvicorn api:app --host 127.0.0.1 --port 8001`)

### Step 2: Start the Frontend Application

1. Open a new terminal and navigate to the frontend directory:
   ```bash
   cd cpu-scheduler-app
   ```

2. Build and run the JavaFX application:
   ```bash
   mvn clean javafx:run
   ```
   
   Or if you prefer to use the exec plugin:
   ```bash
   mvn clean compile exec:java
   ```

   The JavaFX application window should open.

## Usage

1. **Add Processes**: Click "Add Process" to add a new process to the table
2. **Edit Process Data**: Double-click on any cell (except ID) to edit:
   - Burst Time: Positive integer
   - Arrival Time: Non-negative integer
   - Priority: Non-negative integer
3. **Select Algorithm**: Choose from the dropdown:
   - FCFS (First Come First Served)
   - SJF (Shortest Job First)
   - SJF Preemptive
   - Priority
   - Priority Preemptive
   - Round Robin (will prompt for quantum value)
4. **Run**: Click the "Run" button to execute the scheduling algorithm
5. **View Results**: 
   - Animated Gantt Chart showing process execution
   - Results table with Completion Time (CT), Turnaround Time (TAT), Waiting Time (WT)
   - Average TAT and WT displayed at the bottom

## Troubleshooting

- **Backend not responding**: Make sure the Python server is running on port 8001
- **JavaFX not launching**: Ensure Java 17+ is installed and Maven can find it
- **Port already in use**: If port 8001 is busy, change it in `ApiHelper.java` (BASE_URL) and restart both services
- **Dependencies not found**: Run `mvn clean install` in the frontend directory to download dependencies

## Building from Source

### Frontend (JAR file)
```bash
cd cpu-scheduler-app
mvn clean package
```
The JAR file will be in `target/cpu-scheduler-app-1.0-SNAPSHOT.jar`

### Running the JAR
```bash
java --module-path <path-to-javafx> --add-modules javafx.controls,javafx.fxml -jar target/cpu-scheduler-app-1.0-SNAPSHOT.jar
```

Note: You may need to include JavaFX modules separately if using Java 11+ without bundled JavaFX.


