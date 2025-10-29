package com.scheduler;

import java.util.*;

public class AnimatedRoundRobin extends AnimatedScheduler {
    private int timeQuantum;

    public AnimatedRoundRobin(List<Job> jobs, int timeQuantum) {
        super(jobs);
        this.timeQuantum = timeQuantum;
    }

    public AnimatedRoundRobin(List<Job> jobs) {
        this(jobs, 2);
    }

    @Override
    public void start() {
        addStep("", "Starting Round Robin Simulation (Time Quantum: " + timeQuantum + ")");
        
        while (completed.size() != nJobs) {
            int beforeSwap = timeQuantum;
            lookupReady();
            addStep("", "Checking for ready jobs at time " + timer);

            if (ready.isEmpty()) {
                timer++;
                addStep("", "No jobs ready, advancing time to " + timer);
                continue;
            }

            Job job = ready.remove(0);
            addStep(job.getId(), "Starting time slice for job " + job.getId() + " (quantum: " + timeQuantum + ")");
            int start = timer;

            while (!job.isCompleted() && beforeSwap > 0) {
                processJob(job);
                addStep(job.getId(), "Processing job " + job.getId() + " (remaining: " + job.getRemainingTime() + ", quantum left: " + beforeSwap + ")");
                beforeSwap--;
            }
            int end = timer;

            if (!job.isCompleted()) {
                ready.add(job);
                addStep("", "Time slice expired for job " + job.getId() + ", moving to end of queue");
            } else {
                completeJob(job);
                addStep("", "Completed job " + job.getId() + " at time " + timer);
            }
        }
        
        addStep("", "Simulation completed");
    }

    @Override
    protected String getAlgorithmName() {
        return "Round Robin (TQ: " + timeQuantum + ")";
    }
}
