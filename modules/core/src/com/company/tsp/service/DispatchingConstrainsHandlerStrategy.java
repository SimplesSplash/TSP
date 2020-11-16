package com.company.tsp.service;

import com.company.tsp.core.*;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component(DispatchingConstrainsHandlerStrategy.NAME)
public class DispatchingConstrainsHandlerStrategy implements ConstrainsHandlerStrategy {
    public static final String NAME = "tsp_DispatchingConstrainsHandlerStrategy";

    @Override
    public boolean isNodeAvailable(Node node, BasicInputDTO inputDTO, double distance, Ant ant) {
        DispatchingInputDTO dispDTO;
        if (inputDTO instanceof DispatchingInputDTO)
            dispDTO = (DispatchingInputDTO) inputDTO;
        else
            throw new RuntimeException("Incorrect type of inputDTO");
        double avgSpeed = dispDTO.getAvgSpeed() / 60 *1000;
        double minsOnTheWay = Math.ceil(distance / avgSpeed);
        int newTracker=ant.getTimeTrackerMins() + (int) minsOnTheWay;
        Map<String, String> constrains = node.getConstrains();
        if (!constrains.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                String time = constrains.get("availabilityTimeBeginHour");
                Date timeBegin =dateFormat.parse(time);
                time =constrains.get("availabilityTimeEndHour");
                Date timeEnd = dateFormat.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateFormat.parse(dispDTO.getStartTime()).getTime());
                calendar.add(Calendar.MINUTE, newTracker);
                if ((calendar.get(Calendar.HOUR_OF_DAY) > timeBegin.getHours() ||
                    calendar.get(Calendar.HOUR_OF_DAY) == timeBegin.getHours() && calendar.get(Calendar.MINUTE) >= timeBegin.getMinutes()) &&
                        ((calendar.get(Calendar.HOUR_OF_DAY) < timeEnd.getHours()))){
                    return true;
                }else
                    return false;


            } catch (ClassCastException | ParseException e) {
                throw new RuntimeException("unable to get availabilityTimeBeginHour or availabilityTimeEndHour field of node with id: " + node.getId());
            }
        }
        return true;
    }

    @Override
    public DispatchingInputDTO parseIncomingJSON(String json) {
       Gson gson = new Gson();
        DispatchingInputDTO inputDTO = gson.fromJson(json, DispatchingInputDTO.class);
        return inputDTO;

    }

    @Override
    public RequestSenderName getRequestSenderName() {
        return RequestSenderName.DISPATCHING;
    }

    @Override
    public void updateTimeTracker(BasicInputDTO inputDTO, Node node, double distance, Ant ant) {
        DispatchingInputDTO dispDTO;
        if (inputDTO instanceof DispatchingInputDTO)
            dispDTO = (DispatchingInputDTO) inputDTO;
        else
            throw new RuntimeException("Incorrect type of inputDTO");
       Map<String , String> constrains = node.getConstrains();
        double collectionDurationHours = 0.0;
        if (!constrains.isEmpty()){
            try {
                collectionDurationHours = Double.parseDouble(constrains.get("collectionDurationHours"));

            }catch (ClassCastException e){
                throw new RuntimeException("unable to get collectionDurationHours field of node with id: " + node.getId());
            }
        }
        double avgSpeed = dispDTO.getAvgSpeed() / 60 *1000;
        double minsOnTheWay = Math.ceil(distance / avgSpeed);
        ant.setTimeTrackerMins(ant.getTimeTrackerMins() + (int) minsOnTheWay + (int) Math.ceil(collectionDurationHours * 60));

    }
}