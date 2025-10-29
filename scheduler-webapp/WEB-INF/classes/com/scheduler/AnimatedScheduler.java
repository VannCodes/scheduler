package com.scheduler;

import java.util.*;

public abstract class AnimatedScheduler {
    protected List<Job> jobs;
    protected List<Job> completed;
    protected List<Job> ready;
    protected List<SimulationStep> steps;
    protected int timer;
    protected int nJobs;

    public AnimatedScheduler(List<Job> jobs) {
        this.jobs = new ArrayList<>(jobs);
        this.jobs.sort(Comparator.comparingInt(Job::getArrivalTime));
        this.completed = new ArrayList<>();
        this.ready = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.timer = 0;
        this.nJobs = jobs.size();
    }

    protected void lookupReady() {
        List<Job> toMove = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getArrivalTime() <= timer) {
                toMove.add(job);
            }
        }
        ready.addAll(toMove);
        jobs.removeAll(toMove);
    }

    protected void processJob(Job job) {
        job.processJob();
        timer++;
    }

    protected void completeJob(Job job) {
        job.setCompletionTime(timer);
        job.calculateMetrics();
        completed.add(job);
    }

    protected void addStep(String currentJob, String action) {
        List<String> readyIds = new ArrayList<>();
        for (Job job : ready) {
            readyIds.add(job.getId());
        }
        
        List<String> completedIds = new ArrayList<>();
        for (Job job : completed) {
            completedIds.add(job.getId());
        }
        
        steps.add(new SimulationStep(timer, currentJob, readyIds, completedIds, action));
    }

    public abstract void start();

    public SimulationData getSimulationData() {
        double avgWT = completed.isEmpty() ? 0 : 
            completed.stream().mapToInt(Job::getWaitingTime).sum() / (double) completed.size();
        double avgTAT = completed.isEmpty() ? 0 : 
            completed.stream().mapToInt(Job::getTurnAroundTime).sum() / (double) completed.size();
            
        return new SimulationData(steps, completed, avgWT, avgTAT, getAlgorithmName());
    }

    protected abstract String getAlgorithmName();

    public static class SimulationStep {
        private int time;
        private String currentJob;
        private List<String> readyQueue;
        private List<String> completedJobs;
        private String action;

        public SimulationStep(int time, String currentJob, List<String> readyQueue, 
                             List<String> completedJobs, String action) {
            this.time = time;
            this.currentJob = currentJob;
            this.readyQueue = new ArrayList<>(readyQueue);
            this.completedJobs = new ArrayList<>(completedJobs);
            this.action = action;
        }

        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"time\":").append(time).append(",");
            json.append("\"currentJob\":\"").append(currentJob != null ? currentJob : "").append("\",");
            json.append("\"action\":\"").append(action).append("\",");
            json.append("\"readyQueue\":[");
            
            for (int i = 0; i < readyQueue.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(readyQueue.get(i)).append("\"");
            }
            
            json.append("],\"completedJobs\":[");
            
            for (int i = 0; i < completedJobs.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(completedJobs.get(i)).append("\"");
            }
            
            json.append("]}");
            return json.toString();
        }
    }

    public static class SimulationData {
        private List<SimulationStep> steps;
        private List<Job> completedJobs;
        private double avgWaitingTime;
        private double avgTurnAroundTime;
        private String algorithm;

        public SimulationData(List<SimulationStep> steps, List<Job> completedJobs, 
                           double avgWaitingTime, double avgTurnAroundTime, String algorithm) {
            this.steps = steps;
            this.completedJobs = completedJobs;
            this.avgWaitingTime = avgWaitingTime;
            this.avgTurnAroundTime = avgTurnAroundTime;
            this.algorithm = algorithm;
        }

        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"algorithm\":\"").append(algorithm).append("\",");
            json.append("\"avgWaitingTime\":").append(avgWaitingTime).append(",");
            json.append("\"avgTurnAroundTime\":").append(avgTurnAroundTime).append(",");
            json.append("\"steps\":[");
            
            for (int i = 0; i < steps.size(); i++) {
                if (i > 0) json.append(",");
                json.append(steps.get(i).toJson());
            }
            
            json.append("],\"completedJobs\":[");
            
            for (int i = 0; i < completedJobs.size(); i++) {
                if (i > 0) json.append(",");
                json.append(completedJobs.get(i).toJson());
            }
            
            json.append("]}");
            return json.toString();
        }
    }
}
