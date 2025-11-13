package com.vincent.cpu.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.*;

public class GanttChartView extends Pane {
    private final Canvas canvas = new Canvas(800, 85);
    private List<GanttEntry> entries;
    private final Map<String, Color> colorMap = new HashMap<>();
    private int currentIndex = 0;
    private final int rowY = 28, rowHeight = 28, margin = 42;

    public GanttChartView() {
        getChildren().add(canvas);
        setPrefWidth(820);
        setPrefHeight(120);
    }

    public void setEntries(List<GanttEntry> newEntries) {
        this.entries = newEntries == null ? Collections.emptyList() : newEntries;
        colorMap.clear();
        currentIndex = 0;
        drawFrame(-1); // Clear + draw axis only
        if (entries.size() > 0) animate();
    }

    private void animate() {
        Timeline tl = new Timeline();
        for (int i=0; i<entries.size(); i++) {
            final int idx = i;
            double segmentTime = (entries.get(idx).end - entries.get(idx).start) * 0.4;
            double delay = segmentTime + idx * 0.1;
            KeyFrame kf = new KeyFrame(Duration.seconds(delay), e -> drawFrame(idx));
            tl.getKeyFrames().add(kf);
        }
        tl.play();
    }

    private void drawFrame(int upTo) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setLineWidth(1.5);
        int x = margin, y = rowY;
        // Compute scale
        int minTime = entries.stream().mapToInt(e -> e.start).min().orElse(0);
        int maxTime = entries.stream().mapToInt(e -> e.end).max().orElse(10);
        double scale = 35.0; // px per time unit
        // Axis
        gc.setStroke(Color.GRAY);
        gc.strokeLine(x, y+rowHeight, x+(maxTime-minTime)*scale, y+rowHeight); // time axis
        gc.setFill(Color.BLACK);
        gc.fillText("Time", x-34, y+rowHeight+15);
        // Draw executed up to 'upTo'
        for (int i=0; i<=upTo && i<entries.size(); i++) {
            GanttEntry e = entries.get(i);
            Color c = colorFor(e.id);
            gc.setFill(c);
            double bx = x + (e.start - minTime)*scale;
            double bw = Math.max((e.end-e.start)*scale, 2);
            gc.fillRect(bx, y, bw, rowHeight);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(bx, y, bw, rowHeight);
            gc.setFill(Color.WHITE);
            gc.fillText(e.id, bx + bw/2 - 10, y + rowHeight/2 + 5);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(e.start), bx-3, y+rowHeight+19);
            if (i==entries.size()-1 || (i+1 < entries.size() && entries.get(i+1).start != e.end))
                gc.fillText(String.valueOf(e.end), bx+bw-8, y+rowHeight+19);
        }
    }

    private Color colorFor(String pid) {
        if (!colorMap.containsKey(pid)) {
            colorMap.put(pid, Color.hsb((colorMap.size()*67)%360, 0.7, 0.9));
        }
        return colorMap.get(pid);
    }
}
