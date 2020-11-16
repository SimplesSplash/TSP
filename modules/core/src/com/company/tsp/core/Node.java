package com.company.tsp.core;

import java.util.Map;

public class Node {
    protected String id;
    protected double longitude;
    protected double lat;
    protected Map<String, String> constrains;

    public Node(String id, double longitude, double y) {
        this.id = id;
        this.longitude = longitude;
        this.lat = y;
    }

    public Node(double longitude, double y) {
        this.longitude = longitude;
        this.lat = y;
    }

    public Node(String id, double longitude, double y, Map constrains) {
        this.id = id;
        this.longitude = longitude;
        this.lat = y;
        this.constrains = constrains;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLat() {
        return lat;
    }

    public Map getConstrains() {
        return constrains;
    }

    public double getDistanceToNode(Node node){
        return Math.sqrt((Math.pow(this.longitude - node.longitude, 2))+(Math.pow(this.lat - node.lat, 2)));
    }
}
