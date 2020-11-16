package com.company.tsp.service;

import java.io.IOException;
import java.text.ParseException;

public interface TSPSolverService {
    String NAME = "tsp_TSPSolverService";

    String[] resolve(String json) throws IOException, ParseException;

    void resolve() throws IOException;
}