package com.company.tsp.core;

import java.util.List;

public class BasicInputDTO {

    protected String requestSender;

    protected List<Node> nodes;

    protected String startNodeId;

    protected boolean isTimeTrackingNeccessary;

    protected String startTime;

    public BasicInputDTO(String requestSender, List<Node> nodes, String startNodeId, boolean isTimeTrackingNeccessary, String startTime) {
        this.requestSender = requestSender;
        this.nodes = nodes;
        this.startNodeId = startNodeId;
        this.isTimeTrackingNeccessary = isTimeTrackingNeccessary;
        this.startTime = startTime;
    }

    public String getRequestSender() {
        return requestSender;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getStartNodeId() {
        return startNodeId;
    }

    public boolean isTimeTrackingNeccessary() {
        return isTimeTrackingNeccessary;
    }

    public String getStartTime() {
        return startTime;
    }
}
