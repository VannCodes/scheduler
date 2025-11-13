package com.vincent.cpu.gui;

import java.util.List;

public class RunResponse {
    public List<GanttEntry> gantt;
    public List<JobResult> jobs;
    public double avg_tat;
    public double avg_wt;
}
