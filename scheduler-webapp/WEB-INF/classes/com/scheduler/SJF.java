package com.scheduler;

import java.util.*;

public class SJF extends Scheduler {
    private boolean preempt;

    public SJF(List<Job> jobs, boolean preempt) {
        super(jobs);
        this.preempt = preempt;
    }

    public SJF(List<Job> jobs) {
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

            Job job = shortestJob();
            ready.remove(job);
            int start = timer;

            if (preempt) {
                // Preemptive
                while (!job.isCompleted()) {
                    processJob(job);
                    lookupReady();

                    if (!ready.isEmpty()) {
                        Job shortest = shortestJob();
                        if (shortest.getRemainingTime() < job.getRemainingTime()) {
                            ready.add(job);
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

    private Job shortestJob() {
        return ready.stream()
                .min(Comparator.comparingInt(Job::getRemainingTime))
                .orElse(null);
    }
}
