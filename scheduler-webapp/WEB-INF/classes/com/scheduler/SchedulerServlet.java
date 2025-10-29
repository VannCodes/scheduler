package com.scheduler;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public class SchedulerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String algorithm = request.getParameter("algorithm");
        String jobsData = request.getParameter("jobs");
        
        try {
            List<Job> jobs = parseJobs(jobsData);
            Scheduler scheduler = createScheduler(algorithm, jobs, request);
            scheduler.start();
            
            request.setAttribute("algorithm", algorithm);
            request.setAttribute("jobs", jobs);
            request.setAttribute("completedJobs", scheduler.getCompletedJobs());
            request.setAttribute("ganttChart", scheduler.getGanttChart());
            request.setAttribute("avgWaitingTime", scheduler.getAverageWaitingTime());
            request.setAttribute("avgTurnAroundTime", scheduler.getAverageTurnAroundTime());
            
            request.getRequestDispatcher("/results.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private List<Job> parseJobs(String jobsData) {
        List<Job> jobs = new ArrayList<>();
        String[] lines = jobsData.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String id = parts[0].trim();
                int burstTime = Integer.parseInt(parts[1].trim());
                int arrivalTime = Integer.parseInt(parts[2].trim());
                int priority = parts.length > 3 ? Integer.parseInt(parts[3].trim()) : 0;
                
                jobs.add(new Job(id, burstTime, arrivalTime, priority));
            }
        }
        
        return jobs;
    }
    
    private Scheduler createScheduler(String algorithm, List<Job> jobs, HttpServletRequest request) {
        switch (algorithm.toLowerCase()) {
            case "fcfs":
                return new FCFS(jobs);
            case "sjf":
                return new SJF(jobs);
            case "sjf_preemptive":
                return new SJF(jobs, true);
            case "priority":
                return new PriorityScheduler(jobs);
            case "priority_preemptive":
                return new PriorityScheduler(jobs, true);
            case "roundrobin":
                String quantumStr = request.getParameter("timeQuantum");
                int quantum = quantumStr != null ? Integer.parseInt(quantumStr) : 2;
                return new RoundRobin(jobs, quantum);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
