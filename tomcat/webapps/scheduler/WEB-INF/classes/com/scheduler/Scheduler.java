package com.scheduler;

import java.util.*;

public abstract class Scheduler {
    protected List<Job> jobs;
    protected List<Job> completed;
    protected List<Job> ready;
    protected List<GanttEntry> gantt;
    protected int timer;
    protected int nJobs;

    public Scheduler(List<Job> jobs) {
        this.jobs = new ArrayList<>(jobs);
        this.jobs.sort(Comparator.comparingInt(Job::getArrivalTime));
        this.completed = new ArrayList<>();
        this.ready = new ArrayList<>();
        this.gantt = new ArrayList<>();
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

    protected void updateGantt(String id, int start, int end) {
        gantt.add(new GanttEntry(id, start, end));
    }

    public abstract void start();

    public List<Job> getCompletedJobs() {
        return new ArrayList<>(completed);
    }

    public List<GanttEntry> getGanttChart() {
        return new ArrayList<>(gantt);
    }

    public double getAverageWaitingTime() {
        if (completed.isEmpty()) return 0;
        int total = completed.stream().mapToInt(Job::getWaitingTime).sum();
        return (double) total / completed.size();
    }

    public double getAverageTurnAroundTime() {
        if (completed.isEmpty()) return 0;
        int total = completed.stream().mapToInt(Job::getTurnAroundTime).sum();
        return (double) total / completed.size();
    }

    public static class GanttEntry {
        private String jobId;
        private int startTime;
        private int endTime;

        public GanttEntry(String jobId, int startTime, int endTime) {
            this.jobId = jobId;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getJobId() { return jobId; }
        public int getStartTime() { return startTime; }
        public int getEndTime() { return endTime; }
    }
}
