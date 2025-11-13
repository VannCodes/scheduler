package com.vincent.cpu.gui;

public class ProcessIn {
    public String id;
    public int bt;
    public Integer at;
    public Integer priority;

    public ProcessIn() {}
    public ProcessIn(String id, int bt, Integer at, Integer priority) {
        this.id = id;
        this.bt = bt;
        this.at = at;
        this.priority = priority;
    }
}
