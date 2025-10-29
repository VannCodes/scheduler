package com.scheduler;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public class AnimatedSchedulerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/animated.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("simulate".equals(action)) {
            String algorithm = request.getParameter("algorithm");
            String jobsData = request.getParameter("jobs");
            String timeQuantum = request.getParameter("timeQuantum");
            
            try {
                List<Job> jobs = parseJobs(jobsData);
                AnimatedScheduler.SimulationData simulationData = runAnimatedSimulation(algorithm, jobs, timeQuantum);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                PrintWriter out = response.getWriter();
                out.print(simulationData.toJson());
                out.flush();
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().print("{\"error\": \"" + e.getMessage() + "\"}");
            }
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
    
    private AnimatedScheduler.SimulationData runAnimatedSimulation(String algorithm, List<Job> jobs, String timeQuantum) {
        AnimatedScheduler scheduler = createAnimatedScheduler(algorithm, jobs, timeQuantum);
        scheduler.start();
        return scheduler.getSimulationData();
    }
    
    private AnimatedScheduler createAnimatedScheduler(String algorithm, List<Job> jobs, String timeQuantum) {
        switch (algorithm.toLowerCase()) {
            case "fcfs":
                return new AnimatedFCFS(jobs);
            case "sjf":
                return new AnimatedSJF(jobs);
            case "sjf_preemptive":
                return new AnimatedSJF(jobs, true);
            case "priority":
                return new AnimatedPriorityScheduler(jobs);
            case "priority_preemptive":
                return new AnimatedPriorityScheduler(jobs, true);
            case "roundrobin":
                int quantum = timeQuantum != null ? Integer.parseInt(timeQuantum) : 2;
                return new AnimatedRoundRobin(jobs, quantum);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}