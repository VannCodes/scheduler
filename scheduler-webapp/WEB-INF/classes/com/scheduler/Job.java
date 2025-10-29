package com.scheduler;

public class Job {
    private String id;
    private int burstTime;
    private int arrivalTime;
    private int remainingTime;
    private int completionTime;
    private int turnAroundTime;
    private int waitingTime;
    private int priority;

    public Job(String id, int burstTime, int arrivalTime, int priority) {
        this.id = id;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.completionTime = -1;
        this.turnAroundTime = -1;
        this.waitingTime = -1;
    }

    public Job(String id, int burstTime, int arrivalTime) {
        this(id, burstTime, arrivalTime, 0);
    }

    public Job(String id, int burstTime) {
        this(id, burstTime, 0, 0);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getBurstTime() { return burstTime; }
    public void setBurstTime(int burstTime) { this.burstTime = burstTime; }

    public int getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(int arrivalTime) { this.arrivalTime = arrivalTime; }

    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }

    public int getTurnAroundTime() { return turnAroundTime; }
    public void setTurnAroundTime(int turnAroundTime) { this.turnAroundTime = turnAroundTime; }

    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public void processJob() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    public boolean isCompleted() {
        return remainingTime <= 0;
    }

    public void calculateMetrics() {
        if (completionTime > 0) {
            turnAroundTime = completionTime - arrivalTime;
            waitingTime = turnAroundTime - burstTime;
        }
    }

    @Override
    public String toString() {
        return "Job[" + id + "] - BT:" + burstTime + " AT:" + arrivalTime + 
               " CT:" + completionTime + " TAT:" + turnAroundTime + " WT:" + waitingTime + 
               (priority > 0 ? " Priority:" + priority : "");
    }

    public String toJson() {
        return "{\"id\":\"" + id + "\",\"burstTime\":" + burstTime + 
               ",\"arrivalTime\":" + arrivalTime + ",\"priority\":" + priority +
               ",\"completionTime\":" + completionTime + ",\"turnAroundTime\":" + turnAroundTime +
               ",\"waitingTime\":" + waitingTime + "}";
    }
}
