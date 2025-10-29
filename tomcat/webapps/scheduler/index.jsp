<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CPU Scheduler Simulator</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        select, input, textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        textarea {
            height: 120px;
            resize: vertical;
        }
        button {
            background-color: #007bff;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
        }
        button:hover {
            background-color: #0056b3;
        }
        button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
        .controls {
            text-align: center;
            margin: 20px 0;
        }
        .play-btn { background-color: #28a745; }
        .pause-btn { background-color: #ffc107; color: #000; }
        .stop-btn { background-color: #dc3545; }
        .step-btn { background-color: #17a2b8; }
        
        .simulation-area {
            margin-top: 30px;
            padding: 20px;
            border: 2px solid #ddd;
            border-radius: 10px;
            background-color: #f8f9fa;
        }
        
        .gantt-container {
            margin: 20px 0;
            overflow-x: auto;
        }
        
        .gantt-chart {
            display: flex;
            align-items: center;
            min-height: 100px;
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 10px;
        }
        
        .gantt-block {
            height: 40px;
            margin-right: 2px;
            border-radius: 3px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            font-size: 12px;
            min-width: 30px;
            transition: all 0.3s ease;
            position: relative;
        }
        
        .gantt-block.active {
            animation: pulse 1s infinite;
            box-shadow: 0 0 10px rgba(0,0,0,0.3);
        }
        
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
        }
        
        .gantt-block.job-a { background-color: #007bff; }
        .gantt-block.job-b { background-color: #28a745; }
        .gantt-block.job-c { background-color: #ffc107; color: #000; }
        .gantt-block.job-d { background-color: #dc3545; }
        .gantt-block.job-e { background-color: #6f42c1; }
        .gantt-block.idle { background-color: #6c757d; }
        
        .status-panel {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
            gap: 20px;
            margin: 20px 0;
        }
        
        .status-box {
            background-color: white;
            padding: 15px;
            border-radius: 5px;
            border: 1px solid #ddd;
        }
        
        .status-title {
            font-weight: bold;
            margin-bottom: 10px;
            color: #333;
        }
        
        .queue {
            min-height: 60px;
            border: 2px dashed #ddd;
            border-radius: 5px;
            padding: 10px;
            display: flex;
            flex-wrap: wrap;
            gap: 5px;
        }
        
        .queue-item {
            background-color: #007bff;
            color: white;
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: bold;
        }
        
        .current-job {
            background-color: #28a745 !important;
            animation: pulse 1s infinite;
        }
        
        .completed-job {
            background-color: #6c757d !important;
        }
        
        .time-display {
            text-align: center;
            font-size: 24px;
            font-weight: bold;
            color: #007bff;
            margin: 20px 0;
        }
        
        .action-log {
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
            max-height: 200px;
            overflow-y: auto;
            margin-top: 20px;
        }
        
        .log-entry {
            padding: 5px 0;
            border-bottom: 1px solid #eee;
        }
        
        .log-entry:last-child {
            border-bottom: none;
        }
        
        .log-time {
            font-weight: bold;
            color: #007bff;
        }
        
        .stats {
            display: flex;
            justify-content: space-around;
            margin: 20px 0;
        }
        
        .stat-box {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            text-align: center;
            border: 1px solid #ddd;
        }
        
        .stat-value {
            font-size: 24px;
            font-weight: bold;
            color: #007bff;
        }
        
        .stat-label {
            color: #666;
            margin-top: 5px;
        }
        
        .example {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-top: 10px;
            font-family: monospace;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎬 CPU Scheduler Simulator</h1>
        
        <form id="simulationForm">
            <div class="form-group">
                <label for="algorithm">Scheduling Algorithm:</label>
                <select name="algorithm" id="algorithm" onchange="updateAlgorithmInfo()">
                    <option value="fcfs">First Come First Serve (FCFS)</option>
                    <option value="sjf">Shortest Job First (SJF) - Non-Preemptive</option>
                    <option value="sjf_preemptive">Shortest Job First (SJF) - Preemptive</option>
                    <option value="priority">Priority Scheduling - Non-Preemptive</option>
                    <option value="priority_preemptive">Priority Scheduling - Preemptive</option>
                    <option value="roundrobin">Round Robin</option>
                </select>
            </div>
            
            <div class="form-group" id="timeQuantumGroup" style="display: none;">
                <label for="timeQuantum">Time Quantum (for Round Robin):</label>
                <input type="number" name="timeQuantum" id="timeQuantum" value="2" min="1" max="10">
            </div>
            
            <div class="form-group">
                <label for="jobs">Job Data (one job per line):</label>
                <textarea name="jobs" id="jobs" placeholder="Enter jobs in format: JobID,BurstTime,ArrivalTime,Priority" required>A,2,3,10
B,7,2,4
C,5,4,1
D,3,5,8</textarea>
                <div class="example">
                    Format: JobID,BurstTime,ArrivalTime,Priority<br>
                    Example:<br>
                    A,2,3,10<br>
                    B,7,2,4<br>
                    C,5,4,1<br>
                    D,3,5,8<br><br>
                    Note: Priority is optional (defaults to 0)
                </div>
            </div>
            
            <div class="controls">
                <button type="button" id="startBtn" onclick="startSimulation()">🚀 Start Animation</button>
                <button type="button" id="playBtn" onclick="playAnimation()" disabled>▶️ Play</button>
                <button type="button" id="pauseBtn" onclick="pauseAnimation()" disabled>⏸️ Pause</button>
                <button type="button" id="stopBtn" onclick="stopAnimation()" disabled>⏹️ Stop</button>
                <button type="button" id="stepBtn" onclick="stepAnimation()" disabled>⏭️ Step</button>
            </div>
        </form>
        
        <div id="simulationArea" class="simulation-area" style="display: none;">
            <div class="time-display" id="timeDisplay">Time: 0</div>
            
            <div class="status-panel">
                <div class="status-box">
                    <div class="status-title">Ready Queue</div>
                    <div class="queue" id="readyQueue"></div>
                </div>
                
                <div class="status-box">
                    <div class="status-title">Currently Running</div>
                    <div class="queue" id="currentJob"></div>
                </div>
                
                <div class="status-box">
                    <div class="status-title">Completed Jobs</div>
                    <div class="queue" id="completedJobs"></div>
                </div>
            </div>
            
            <div class="gantt-container">
                <div class="status-title">Gantt Chart</div>
                <div class="gantt-chart" id="ganttChart"></div>
            </div>
            
            <div class="stats" id="statsPanel" style="display: none;">
                <div class="stat-box">
                    <div class="stat-value" id="avgWaitingTime">0.00</div>
                    <div class="stat-label">Average Waiting Time</div>
                </div>
                <div class="stat-box">
                    <div class="stat-value" id="avgTurnAroundTime">0.00</div>
                    <div class="stat-label">Average Turn-Around Time</div>
                </div>
            </div>
            
            <div class="action-log" id="actionLog">
                <div class="log-entry">Ready to start simulation...</div>
            </div>
        </div>
    </div>

    <script>
        let simulationData = null;
        let currentStep = 0;
        let isPlaying = false;
        let animationInterval = null;
        let animationSpeed = 1000; // 1 second per step

        function updateAlgorithmInfo() {
            const algorithm = document.getElementById('algorithm').value;
            const timeQuantumGroup = document.getElementById('timeQuantumGroup');
            
            if (algorithm === 'roundrobin') {
                timeQuantumGroup.style.display = 'block';
            } else {
                timeQuantumGroup.style.display = 'none';
            }
        }

        function startSimulation() {
            const algorithm = document.getElementById('algorithm').value;
            const jobs = document.getElementById('jobs').value;
            const timeQuantum = document.getElementById('timeQuantum').value;
            
            const params = new URLSearchParams();
            params.append('action', 'simulate');
            params.append('algorithm', algorithm);
            params.append('jobs', jobs);
            if (timeQuantum) {
                params.append('timeQuantum', timeQuantum);
            }
            
            fetch('animated', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert('Error: ' + data.error);
                    return;
                }
                
                simulationData = data;
                currentStep = 0;
                document.getElementById('simulationArea').style.display = 'block';
                document.getElementById('startBtn').disabled = true;
                document.getElementById('playBtn').disabled = false;
                document.getElementById('stepBtn').disabled = false;
                
                updateDisplay();
                addLogEntry('Simulation data loaded: ' + data.algorithm);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error starting simulation: ' + error.message);
            });
        }

        function playAnimation() {
            if (!simulationData) return;
            
            isPlaying = true;
            document.getElementById('playBtn').disabled = true;
            document.getElementById('pauseBtn').disabled = false;
            document.getElementById('stopBtn').disabled = false;
            document.getElementById('stepBtn').disabled = true;
            
            animationInterval = setInterval(() => {
                if (currentStep < simulationData.steps.length - 1) {
                    currentStep++;
                    updateDisplay();
                } else {
                    pauseAnimation();
                    addLogEntry('Animation completed!');
                }
            }, animationSpeed);
        }

        function pauseAnimation() {
            isPlaying = false;
            if (animationInterval) {
                clearInterval(animationInterval);
                animationInterval = null;
            }
            
            document.getElementById('playBtn').disabled = false;
            document.getElementById('pauseBtn').disabled = true;
            document.getElementById('stepBtn').disabled = false;
        }

        function stopAnimation() {
            pauseAnimation();
            currentStep = 0;
            updateDisplay();
            document.getElementById('stopBtn').disabled = true;
            addLogEntry('Animation stopped and reset');
        }

        function stepAnimation() {
            if (!simulationData) return;
            
            if (currentStep < simulationData.steps.length - 1) {
                currentStep++;
                updateDisplay();
            }
        }

        function updateDisplay() {
            if (!simulationData || currentStep >= simulationData.steps.length) return;
            
            const step = simulationData.steps[currentStep];
            
            // Update time display
            document.getElementById('timeDisplay').textContent = 'Time: ' + step.time;
            
            // Update ready queue
            updateQueue('readyQueue', step.readyQueue);
            
            // Update current job
            updateCurrentJob(step.currentJob);
            
            // Update completed jobs
            updateQueue('completedJobs', step.completedJobs);
            
            // Update Gantt chart
            updateGanttChart(step);
            
            // Add log entry
            addLogEntry('T' + step.time + ': ' + step.action);
            
            // Show final stats if at the end
            if (currentStep === simulationData.steps.length - 1) {
                showFinalStats();
            }
        }

        function updateQueue(queueId, items) {
            const queue = document.getElementById(queueId);
            queue.innerHTML = '';
            
            items.forEach(item => {
                const div = document.createElement('div');
                div.className = 'queue-item';
                div.textContent = item;
                queue.appendChild(div);
            });
        }

        function updateCurrentJob(jobId) {
            const currentJobDiv = document.getElementById('currentJob');
            currentJobDiv.innerHTML = '';
            
            if (jobId) {
                const div = document.createElement('div');
                div.className = 'queue-item current-job';
                div.textContent = jobId;
                currentJobDiv.appendChild(div);
            }
        }

        function updateGanttChart(step) {
            const ganttChart = document.getElementById('ganttChart');
            
            // Create a block for this time step
            if (step.currentJob) {
                const block = document.createElement('div');
                block.className = 'gantt-block job-' + step.currentJob.toLowerCase() + ' active';
                block.textContent = step.currentJob;
                block.title = step.currentJob + ' at time ' + step.time;
                ganttChart.appendChild(block);
            } else {
                const block = document.createElement('div');
                block.className = 'gantt-block idle';
                block.textContent = 'IDLE';
                block.title = 'Idle at time ' + step.time;
                ganttChart.appendChild(block);
            }
        }

        function addLogEntry(message) {
            const log = document.getElementById('actionLog');
            const entry = document.createElement('div');
            entry.className = 'log-entry';
            entry.innerHTML = '<span class="log-time">T' + (simulationData ? simulationData.steps[currentStep].time : 0) + '</span>: ' + message;
            log.appendChild(entry);
            log.scrollTop = log.scrollHeight;
        }

        function showFinalStats() {
            document.getElementById('statsPanel').style.display = 'flex';
            document.getElementById('avgWaitingTime').textContent = simulationData.avgWaitingTime.toFixed(2);
            document.getElementById('avgTurnAroundTime').textContent = simulationData.avgTurnAroundTime.toFixed(2);
        }

        // Initialize
        updateAlgorithmInfo();
    </script>
</body>
</html>