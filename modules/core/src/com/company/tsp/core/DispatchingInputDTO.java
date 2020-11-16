package com.company.tsp.core;

import java.util.List;

public class DispatchingInputDTO extends BasicInputDTO {



    protected double avgSpeed;

    protected String endNodeid;

    public DispatchingInputDTO(String requestSender,
                               List<Node> nodes,
                               String startNodeId,
                               boolean isTimeTrackingNeccessary,
                               String startTime,
                               double avgSpeed,
                               String endNodeid) {
        super(requestSender, nodes, startNodeId, isTimeTrackingNeccessary, startTime);
        this.avgSpeed = avgSpeed;
        this.endNodeid = endNodeid;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public String getEndNodeid() {
        return endNodeid;
    }
}
