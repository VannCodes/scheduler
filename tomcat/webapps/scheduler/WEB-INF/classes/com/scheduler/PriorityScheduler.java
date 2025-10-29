package com.scheduler;

import java.util.*;

public class PriorityScheduler extends Scheduler {
    private boolean preempt;

    public PriorityScheduler(List<Job> jobs, boolean preempt) {
        super(jobs);
        this.preempt = preempt;
    }

    public PriorityScheduler(List<Job> jobs) {
        this(jobs, false);
    }

    @Override
    public void start() {
        while (completed.size() != nJobs) {
            lookupReady();

            if (ready.isEmpty()) {
                timer++;
                continue;
            }

            Job job = priorityJob();
            ready.remove(job);
            int start = timer;

            if (preempt) {
                // Preemptive
                while (!job.isCompleted()) {
                    processJob(job);
                    lookupReady();

                    if (!ready.isEmpty()) {
                        Job highest = priorityJob();
                        if (highest.getPriority() > job.getPriority()) {
                            ready.add(0, job);
                            break;
                        }
                    }
                }
            } else {
                // Non-Preemptive
                while (!job.isCompleted()) {
                    processJob(job);
                }
            }
            int end = timer;

            updateGantt(job.getId(), start, end);
            if (job.isCompleted()) {
                completeJob(job);
            }
        }
    }

    private Job priorityJob() {
        return ready.stream()
                .max(Comparator.comparingInt(Job::getPriority))
                .orElse(null);
    }
}
