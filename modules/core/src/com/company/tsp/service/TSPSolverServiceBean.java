package com.company.tsp.service;

import au.com.bytecode.opencsv.CSVReader;
import com.company.tsp.core.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service(TSPSolverService.NAME)
public class TSPSolverServiceBean implements TSPSolverService {

    @Inject
    private ConstrainsHandlerStrategyFactory constrainsHandlerStrategyFactory;

    ConstrainsHandlerStrategy strategy;

    @Override
    public String[] resolve(String json) throws IOException, ParseException {
        BasicInputDTO basicInputDTO = parseIncomingJSON(json);
        TSPSolver tSPSolver;
        if (Boolean.TRUE.equals(basicInputDTO.isTimeTrackingNeccessary()))
            tSPSolver = new TSPSolverWithTimeTracking(constrainsHandlerStrategyFactory);
        else
            tSPSolver = new TSPSolver(constrainsHandlerStrategyFactory);
        int[] orderedNodesArray = tSPSolver.startAntOptimization(basicInputDTO);
        String[] outputNodesArray = new String[orderedNodesArray.length];
        for (int i = 0; i < orderedNodesArray.length; i++) {
            outputNodesArray[i] = basicInputDTO.getNodes().get(orderedNodesArray[i]).getId();
        }
        return outputNodesArray;
    }

    @Override
    public void resolve() throws IOException {
        String appHome = System.getProperty("app.home");
        List<Node> nodeList = readCSV(appHome+"/test2.csv");
        TSPSolver tSPSolver = new TSPSolver(constrainsHandlerStrategyFactory);
        tSPSolver.startAntOptimization(nodeList);
    }

    private List<Node> readCSV(String path) throws IOException {
        List<Node> result = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(path), ' ', '"',0);
        String[] nextLine;
        while ((nextLine = reader.readNext()) !=null){
            result.add(new Node(Double.parseDouble(nextLine[1]), Double.parseDouble(nextLine[2])));
        }
        return result;
    }

    private BasicInputDTO parseIncomingJSON(String json) throws ParseException {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String requestSender = jsonObject.get("requestSender").getAsString();
        if (requestSender == null)
            throw  new RuntimeException("requestSender field in necessary");

         strategy = constrainsHandlerStrategyFactory.findStrategy(RequestSenderName.fromId(requestSender));

         if (strategy == null)
             throw new RuntimeException("Unknown requestSender");

        return strategy.parseIncomingJSON(json);


    }

}