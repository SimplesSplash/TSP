package com.company.tsp.core;

public class Ant {

    protected int trailSize;
    protected int trail[];
    protected boolean visited[];
    protected int timeTrackerMins;

    public Ant(int tourSize) {
        this.trailSize = tourSize;
        this.trail = new int[tourSize];
        this.visited = new boolean[tourSize];
        this.timeTrackerMins = 0;
    }

    public void visitCity(int currentIndex, int city) {
        trail[currentIndex + 1] = city;
        visited[city] = true;
    }

    public boolean visited(int i) {
        return visited[i];
    }

    public int trailLength(int graph[][]) {
        int length = graph[trail[trailSize - 1]][trail[0]];
        for (int i = 0; i < trailSize - 1; i++) {
            length += graph[trail[i]][trail[i + 1]];
        }
        return length;
    }

    public void clear() {
        for (int i = 0; i < trailSize; i++)
            visited[i] = false;
    }

    public int getTrailSize() {
        return trailSize;
    }

    public int[] getTrail() {
        return trail;
    }

    public int getTimeTrackerMins() {
        return timeTrackerMins;
    }

    public void setTimeTrackerMins(int timeTrackerMins) {
        this.timeTrackerMins = timeTrackerMins;
    }
}
