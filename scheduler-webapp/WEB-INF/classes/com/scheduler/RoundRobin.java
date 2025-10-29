package com.scheduler;

import java.util.*;

public class RoundRobin extends Scheduler {
    private int timeQuantum;

    public RoundRobin(List<Job> jobs, int timeQuantum) {
        super(jobs);
        this.timeQuantum = timeQuantum;
    }

    public RoundRobin(List<Job> jobs) {
        this(jobs, 2);
    }

    @Override
    public void start() {
        while (completed.size() != nJobs) {
            int beforeSwap = timeQuantum;
            lookupReady();

            if (ready.isEmpty()) {
                timer++;
                continue;
            }

            Job job = ready.remove(0);
            int start = timer;

            while (!job.isCompleted() && beforeSwap > 0) {
                processJob(job);
                beforeSwap--;
            }
            int end = timer;

            if (!job.isCompleted()) {
                ready.add(job);
            } else {
                completeJob(job);
            }
            updateGantt(job.getId(), start, end);
        }
    }
}
