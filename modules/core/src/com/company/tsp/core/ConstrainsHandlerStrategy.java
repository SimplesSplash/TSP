package com.company.tsp.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface ConstrainsHandlerStrategy {
     OkHttpClient httpClient = new OkHttpClient();

    boolean isNodeAvailable(Node node, BasicInputDTO inputDTO, double distance, Ant ant);

    BasicInputDTO parseIncomingJSON(String json) throws ParseException;

    RequestSenderName getRequestSenderName();

    void updateTimeTracker(BasicInputDTO inputDTO, Node node, double distance, Ant ant);

    default int[][] getDistanceMatrix(List<Node> nodes) throws IOException {
        int[][] result = new int[nodes.size()][nodes.size()];
        String uriPrefix = "http://router.project-osrm.org/table/v1/driving/";
        String uriPostfix = "?annotations=distance";
        StringBuilder sb = new StringBuilder(uriPrefix);
        nodes.forEach(node -> {
            sb.append(node.getLongitude());
            sb.append(",");
            sb.append(node.getLat());
            sb.append(";");
        });
        sb.deleteCharAt(sb.length()-1);
        sb.append(uriPostfix);
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            String responceBody = response.body().string();
            JsonElement jsonBody = JsonParser.parseString(responceBody);
            JsonArray distancesArray = jsonBody.getAsJsonObject().get("distances").getAsJsonArray();
            for (int i = 0; i <distancesArray.size() ; i++) {
                JsonArray rowElement = distancesArray.get(i).getAsJsonArray();
                for (int j = 0; j < rowElement.size(); j++) {
                    double dist = rowElement.get(j).getAsDouble();
                    if (dist > 0.0)
                        result[i][j] = (int) dist;
                    else
                        result[i][j] = -1;
                }
            }
            return result;
        }
    }
}


