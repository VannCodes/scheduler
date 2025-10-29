package com.scheduler;

import java.util.*;

public class FCFS extends Scheduler {
    
    public FCFS(List<Job> jobs) {
        super(jobs);
    }

    @Override
    public void start() {
        while (completed.size() != nJobs) {
            lookupReady();

            if (ready.isEmpty()) {
                timer++;
                continue;
            }

            Job job = ready.remove(0);
            int start = timer;

            while (!job.isCompleted()) {
                processJob(job);
            }
            int end = timer;

            completeJob(job);
            updateGantt(job.getId(), start, end);
        }
    }
}
