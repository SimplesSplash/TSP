package com.company.tsp.core;

import com.company.tsp.service.ConstrainsHandlerStrategyFactory;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class TSPSolver {

    protected double c = 1.0;
    protected double alpha = 1;
    protected double beta = 20;
    protected double evaporation = 0.5;
    protected double Q = 500;
    protected int numberOfAnts = 500;
    protected double randomFactor = 0.03;

    protected int maxIterations = 1000;

    protected int numberOfCities;
    protected int graph[][];
    protected double trails[][];
    protected List<Ant> ants = new ArrayList<>();
    protected Random random = new Random();
    protected double probabilities[];

    protected int currentIndex;

    protected int[] bestTourOrder;
    protected int bestTourLength;

    protected ConstrainsHandlerStrategyFactory strategyFactory;
    protected ConstrainsHandlerStrategy strategy;
    protected BasicInputDTO inputDTO;
    protected List<Ant> antsWithBadTrails = new ArrayList<>();

    public TSPSolver(ConstrainsHandlerStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    protected int[][] generateMatrixFromNodesList(List<Node> nodes) {
        int[][] matrix = new int[nodes.size()][nodes.size()];
        IntStream.range(0, nodes.size())
                .forEach(i -> IntStream.range(0, nodes.size())
                        .forEach(j -> {
                            if (i != j)
                                matrix[i][j] = (int) nodes.get(i).getDistanceToNode(nodes.get(j));
                            else
                                matrix[i][j] = Integer.MAX_VALUE;
                        }));
        return matrix;
    }

    protected int[][] generateMatrixOnRoadsFromNodesList(List<Node> nodes) throws IOException {
        return strategy.getDistanceMatrix(nodes);

    }

    public void startAntOptimization(List<Node> nodes){
        graph = generateMatrixFromNodesList(nodes);
        numberOfCities = graph.length;
        trails = new double[numberOfCities][numberOfCities];
        probabilities = new double[numberOfCities];
        ants.clear();
        for (int i = 0; i < numberOfAnts; i++) {
            ants.add(new Ant(numberOfCities));
        }
        solve();
    }


    public int[] startAntOptimization(BasicInputDTO inputDTO) throws IOException {
        this.inputDTO = inputDTO;
        strategy = strategyFactory.findStrategy(RequestSenderName.fromId(inputDTO.getRequestSender()));
        graph = generateMatrixOnRoadsFromNodesList(inputDTO.getNodes());
        numberOfCities = graph.length;

        trails = new double[numberOfCities][numberOfCities];
        probabilities = new double[numberOfCities];
        ants.clear();
        if (Boolean.TRUE.equals(inputDTO.isTimeTrackingNeccessary)){
            for (int i = 0; i < numberOfAnts; i++) {
                ants.add(new Ant(numberOfCities));
            }
        }else
            for (int i = 0; i < numberOfAnts; i++) {
                ants.add(new Ant(numberOfCities));
            }


        return solve();
    }


    protected int[] solve() {
        setupAnts();
        clearTrails();
        for (int i = 0; i < maxIterations; i++) {
            antsWithBadTrails.clear();
            setupAnts();
            moveAnts();
            updateTrails();
            updateBest();
        }
        if (bestTourOrder == null)
            throw new RuntimeException("Unable to find path with given constraints");
        System.out.println("Best tour length: " + bestTourLength);
        System.out.println("Best tour order: " + Arrays.toString(bestTourOrder));
        return bestTourOrder.clone();
    }


    protected void setupAnts() {
        Node firstNode;
        if (inputDTO == null){
            firstNode = inputDTO.getNodes().stream()
                    .filter(node -> node.getId().equals(inputDTO.getStartNodeId()))
                    .findFirst().orElse(null);
        }else
            firstNode = null;

        IntStream.range(0, numberOfAnts)
                .forEach(i -> {
                    ants.forEach(ant -> {
                        if (firstNode !=null)
                            ant.visitCity(-1, inputDTO.getNodes().indexOf(firstNode));
                        else
                            ant.visitCity(-1, 0);
                        ant.setTimeTrackerMins(0);
                    });
                });
        currentIndex = 0;
    }


    protected void moveAnts() {
        IntStream.range(currentIndex, numberOfCities - 1)
                .forEach(i -> {
                    for (Ant ant : ants) {
                        if (!antsWithBadTrails.contains(ant)) {
                            int nextCity = selectNextCity(ant);
                            if (nextCity >= 0)
                                ant.visitCity(currentIndex, nextCity);
                        }
                    }
                    currentIndex++;
                });
    }


    protected int selectNextCity(Ant ant) {
        if (random.nextDouble() < randomFactor) {
            int t = random.nextInt(numberOfCities - currentIndex);
            OptionalInt cityIndex = IntStream.range(0, numberOfCities)
                    .filter(i -> i == t && !ant.visited(i))
                    .findFirst();
            if (cityIndex.isPresent()) {
                return cityIndex.getAsInt();
            }

        }
        calculateProbabilities(ant);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfCities; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }
        antsWithBadTrails.add(ant);
        return -1;
    }


    protected void calculateProbabilities(Ant ant) {
        int i = ant.getTrail()[currentIndex];
        double pheromone = 0.0;
        for (int l = 0; l < numberOfCities; l++) {
            if (!ant.visited(l)
                && graph[i][l] > 0
                ) {
                pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta);
            }
        }
        for (int j = 0; j < numberOfCities; j++) {
            if (ant.visited(j)
                      || graph[i][j] < 0
                    || !strategy.isNodeAvailable(inputDTO.getNodes().get(j),inputDTO,graph[i][j],ant)

            ) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta);
                probabilities[j] = numerator / pheromone;
            }
        }
    }


    protected void updateTrails() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                trails[i][j] *= evaporation;
            }
        }
        for (Ant ant : ants) {
            if (!antsWithBadTrails.contains(ant)) {
                double contribution = Q / ant.trailLength(graph);
                for (int i = 0; i < numberOfCities - 1; i++) {
                    trails[ant.getTrail()[i]][ant.getTrail()[i + 1]] += contribution;
                }
                trails[ant.getTrail()[numberOfCities - 1]][ant.getTrail()[0]] += contribution;
            }
        }

    }


    protected void updateBest() {
        if (bestTourOrder == null) {
            Ant antWithFullPath = ants.stream().filter(ant -> !antsWithBadTrails.contains(ant))
                    .findFirst().orElse(null);

            if (antWithFullPath == null)
                return;
            bestTourOrder = antWithFullPath.getTrail();
            bestTourLength = antWithFullPath.trailLength(graph);
        }
        for (Ant ant : ants) {
            if (!antsWithBadTrails.contains(ant)) {
                if (ant.trailLength(graph) < bestTourLength) {
                    bestTourLength = ant.trailLength(graph);
                    bestTourOrder = ant.getTrail().clone();
                }
            }
        }
    }


    protected void clearTrails() {
        for (int idx = 0; idx < numberOfCities; idx++) {
            int i = idx;
            for (int j = 0; j < numberOfCities; j++) {
                trails[i][j] = c;
            }
        }
    }
}