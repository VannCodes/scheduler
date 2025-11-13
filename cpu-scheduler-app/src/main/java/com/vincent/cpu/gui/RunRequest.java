package com.vincent.cpu.gui;
import java.util.List;

public class RunRequest {
    public String algorithm;
    public List<ProcessIn> processes;
    public Integer quantum; // Optional for RR

    public RunRequest() {}
    public RunRequest(String algorithm, List<ProcessIn> processes, Integer quantum) {
        this.algorithm = algorithm;
        this.processes = processes;
        this.quantum = quantum;
    }
}
