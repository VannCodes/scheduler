package com.scheduler;

import java.util.*;

public class AnimatedPriorityScheduler extends AnimatedScheduler {
    private boolean preempt;

    public AnimatedPriorityScheduler(List<Job> jobs, boolean preempt) {
        super(jobs);
        this.preempt = preempt;
    }

    public AnimatedPriorityScheduler(List<Job> jobs) {
        this(jobs, false);
    }

    @Override
    public void start() {
        addStep("", "Starting Priority " + (preempt ? "Preemptive" : "Non-Preemptive") + " Simulation");
        
        while (completed.size() != nJobs) {
            lookupReady();
            addStep("", "Checking for ready jobs at time " + timer);

            if (ready.isEmpty()) {
                timer++;
                addStep("", "No jobs ready, advancing time to " + timer);
                continue;
            }

            Job job = priorityJob();
            ready.remove(job);
            addStep(job.getId(), "Selected highest priority job " + job.getId() + " (priority: " + job.getPriority() + ")");
            int start = timer;

            if (preempt) {
                // Preemptive
                while (!job.isCompleted()) {
                    processJob(job);
                    addStep(job.getId(), "Processing job " + job.getId() + " (remaining: " + job.getRemainingTime() + ")");
                    lookupReady();

                    if (!ready.isEmpty()) {
                        Job highest = priorityJob();
                        if (highest.getPriority() > job.getPriority()) {
                            ready.add(0, job);
                            addStep(highest.getId(), "Preempting job " + job.getId() + " for higher priority job " + highest.getId());
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

    private Job priorityJob() {
        return ready.stream()
                .max(Comparator.comparingInt(Job::getPriority))
                .orElse(null);
    }

    @Override
    protected String getAlgorithmName() {
        return "Priority " + (preempt ? "Preemptive" : "Non-Preemptive");
    }
}
