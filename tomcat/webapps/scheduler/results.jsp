<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.scheduler.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Simulation Results - CPU Scheduler</title>
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
        .back-button {
            background-color: #6c757d;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
            display: inline-block;
            margin-bottom: 20px;
        }
        .back-button:hover {
            background-color: #545b62;
        }
        .results-section {
            margin-bottom: 30px;
        }
        .section-title {
            background-color: #007bff;
            color: white;
            padding: 10px 15px;
            margin: 0 0 15px 0;
            border-radius: 5px 5px 0 0;
            font-weight: bold;
        }
        .section-content {
            border: 1px solid #ddd;
            border-top: none;
            padding: 15px;
            border-radius: 0 0 5px 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .gantt-chart {
            display: flex;
            align-items: center;
            margin: 20px 0;
            overflow-x: auto;
        }
        .gantt-entry {
            background-color: #007bff;
            color: white;
            padding: 10px 5px;
            margin-right: 2px;
            border-radius: 3px;
            min-width: 60px;
            text-align: center;
            font-size: 12px;
        }
        .gantt-entry:nth-child(odd) {
            background-color: #28a745;
        }
        .gantt-entry:nth-child(3n) {
            background-color: #ffc107;
            color: #000;
        }
        .gantt-entry:nth-child(4n) {
            background-color: #dc3545;
        }
        .gantt-entry:nth-child(5n) {
            background-color: #6f42c1;
        }
        .stats {
            display: flex;
            justify-content: space-around;
            margin: 20px 0;
        }
        .stat-box {
            background-color: #f8f9fa;
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
    </style>
</head>
<body>
    <div class="container">
        <h1>Simulation Results</h1>
        
        <a href="index.jsp" class="back-button">← Back to Simulator</a>
        
        <div class="results-section">
            <div class="section-title">Algorithm Used</div>
            <div class="section-content">
                <strong><%= request.getAttribute("algorithm") %></strong>
            </div>
        </div>
        
        <div class="results-section">
            <div class="section-title">Performance Statistics</div>
            <div class="section-content">
                <div class="stats">
                    <div class="stat-box">
                        <div class="stat-value"><%= String.format("%.2f", request.getAttribute("avgWaitingTime")) %></div>
                        <div class="stat-label">Average Waiting Time</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-value"><%= String.format("%.2f", request.getAttribute("avgTurnAroundTime")) %></div>
                        <div class="stat-label">Average Turn-Around Time</div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="results-section">
            <div class="section-title">Job Details</div>
            <div class="section-content">
                <table>
                    <thead>
                        <tr>
                            <th>Job ID</th>
                            <th>Burst Time</th>
                            <th>Arrival Time</th>
                            <th>Priority</th>
                            <th>Completion Time</th>
                            <th>Turn-Around Time</th>
                            <th>Waiting Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        List<Job> completedJobs = (List<Job>) request.getAttribute("completedJobs");
                        for (Job job : completedJobs) {
                        %>
                        <tr>
                            <td><%= job.getId() %></td>
                            <td><%= job.getBurstTime() %></td>
                            <td><%= job.getArrivalTime() %></td>
                            <td><%= job.getPriority() %></td>
                            <td><%= job.getCompletionTime() %></td>
                            <td><%= job.getTurnAroundTime() %></td>
                            <td><%= job.getWaitingTime() %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div class="results-section">
            <div class="section-title">Gantt Chart</div>
            <div class="section-content">
                <div class="gantt-chart">
                    <% 
                    List<Scheduler.GanttEntry> ganttChart = (List<Scheduler.GanttEntry>) request.getAttribute("ganttChart");
                    for (Scheduler.GanttEntry entry : ganttChart) {
                    %>
                    <div class="gantt-entry" title="Job <%= entry.getJobId() %>: <%= entry.getStartTime() %> - <%= entry.getEndTime() %>">
                        <%= entry.getJobId() %><br>
                        <%= entry.getStartTime() %>-<%= entry.getEndTime() %>
                    </div>
                    <% } %>
                </div>
                <p><em>Hover over each block to see detailed timing information.</em></p>
            </div>
        </div>
        
        <div class="results-section">
            <div class="section-title">Original Job Data</div>
            <div class="section-content">
                <table>
                    <thead>
                        <tr>
                            <th>Job ID</th>
                            <th>Burst Time</th>
                            <th>Arrival Time</th>
                            <th>Priority</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        List<Job> originalJobs = (List<Job>) request.getAttribute("jobs");
                        for (Job job : originalJobs) {
                        %>
                        <tr>
                            <td><%= job.getId() %></td>
                            <td><%= job.getBurstTime() %></td>
                            <td><%= job.getArrivalTime() %></td>
                            <td><%= job.getPriority() %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
