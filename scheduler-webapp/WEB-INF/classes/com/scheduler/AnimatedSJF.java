package com.scheduler;

import java.util.*;

public class AnimatedSJF extends AnimatedScheduler {
    private boolean preempt;

    public AnimatedSJF(List<Job> jobs, boolean preempt) {
        super(jobs);
        this.preempt = preempt;
    }

    public AnimatedSJF(List<Job> jobs) {
        this(jobs, false);
    }

    @Override
    public void start() {
        addStep("", "Starting SJF " + (preempt ? "Preemptive" : "Non-Preemptive") + " Simulation");
        
        while (completed.size() != nJobs) {
            lookupReady();
            addStep("", "Checking for ready jobs at time " + timer);

            if (ready.isEmpty()) {
                timer++;
                addStep("", "No jobs ready, advancing time to " + timer);
                continue;
            }

            Job job = shortestJob();
            ready.remove(job);
            addStep(job.getId(), "Selected shortest job " + job.getId() + " (burst: " + job.getBurstTime() + ")");
            int start = timer;

            if (preempt) {
                // Preemptive
                while (!job.isCompleted()) {
                    processJob(job);
                    addStep(job.getId(), "Processing job " + job.getId() + " (remaining: " + job.getRemainingTime() + ")");
                    lookupReady();

                    if (!ready.isEmpty()) {
                        Job shortest = shortestJob();
                        if (shortest.getRemainingTime() < job.getRemainingTime()) {
                            ready.add(job);
                            addStep(shortest.getId(), "Preempting job " + job.getId() + " for shorter job " + shortest.getId());
                            break;
                        }
                    }
                }
            } else {
                // Non-Preemptive
                while (!job.isCompleted()) {
                    processJob(job);
                    addStep(job.getId(), "Processing job " + job.getId() + " (remaining: " + job.getRemainingTime() + ")");
                }
            }
            int end = timer;

            if (job.isCompleted()) {
                completeJob(job);
                addStep("", "Completed job " + job.getId() + " at time " + timer);
            }
        }
        
        addStep("", "Simulation completed");
    }

    private Job shortestJob() {
        return ready.stream()
                .min(Comparator.comparingInt(Job::getRemainingTime))
                .orElse(null);
    }

    @Override
    protected String getAlgorithmName() {
        return "SJF " + (preempt ? "Preemptive" : "Non-Preemptive");
    }
}
