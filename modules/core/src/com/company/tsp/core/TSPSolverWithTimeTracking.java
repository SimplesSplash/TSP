package com.company.tsp.core;

import com.company.tsp.service.ConstrainsHandlerStrategyFactory;

import java.util.stream.IntStream;


public class TSPSolverWithTimeTracking extends TSPSolver {
    public TSPSolverWithTimeTracking(ConstrainsHandlerStrategyFactory strategyFactory) {
        super(strategyFactory);
    }

    @Override
    protected void moveAnts() {
        IntStream.range(currentIndex, numberOfCities - 1)
                .forEach(i -> {
                    for (Ant ant : ants) {
                        if (!antsWithBadTrails.contains(ant)) {
                            int nextCity = selectNextCity(ant);
                            if (nextCity >= 0) {
                                ant.visitCity(currentIndex, nextCity);
                                strategy.updateTimeTracker(inputDTO, inputDTO.getNodes().get(nextCity), graph[ant.getTrail()[ant.getTrailSize() - 1]][nextCity], ant);
                            }
                        }
                    }
                    currentIndex++;
                });
    }
}
