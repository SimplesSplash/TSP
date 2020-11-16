package com.company.tsp.service;

import com.company.tsp.core.ConstrainsHandlerStrategy;
import com.company.tsp.core.RequestSenderName;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component(ConstrainsHandlerStrategyFactory.NAME)
public class ConstrainsHandlerStrategyFactory {
    public static final String NAME = "tsp_ConstrainsHandlerStrategyFactory";

    private Map<RequestSenderName, ConstrainsHandlerStrategy> srategies;

    public ConstrainsHandlerStrategyFactory(Set<ConstrainsHandlerStrategy> stratSet) {
        createStrategy(stratSet);
    }

    public ConstrainsHandlerStrategy findStrategy(RequestSenderName requestSenderName){
        return srategies.get(requestSenderName);
    }

    private void createStrategy(Set<ConstrainsHandlerStrategy> stratSet) {
        srategies = new HashMap<>();
        stratSet.forEach(strat -> srategies.put(strat.getRequestSenderName(),strat));
    }
}