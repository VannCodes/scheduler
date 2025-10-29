package com.scheduler;

import java.util.*;

public class AnimatedFCFS extends AnimatedScheduler {
    
    public AnimatedFCFS(List<Job> jobs) {
        super(jobs);
    }

    @Override
    public void start() {
        addStep("", "Starting FCFS Simulation");
        
        while (completed.size() != nJobs) {
            lookupReady();
            addStep("", "Checking for ready jobs at time " + timer);

            if (ready.isEmpty()) {
                timer++;
                addStep("", "No jobs ready, advancing time to " + timer);
                continue;
            }

            Job job = ready.remove(0);
            addStep(job.getId(), "Starting job " + job.getId() + " at time " + timer);
            int start = timer;

            while (!job.isCompleted()) {
                processJob(job);
                addStep(job.getId(), "Processing job " + job.getId() + " (remaining: " + job.getRemainingTime() + ")");
            }
            int end = timer;

            completeJob(job);
            addStep("", "Completed job " + job.getId() + " at time " + timer);
        }
        
        addStep("", "Simulation completed");
    }

    @Override
    protected String getAlgorithmName() {
        return "FCFS";
    }
}
