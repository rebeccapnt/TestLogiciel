package services;

import exceptions.WeatherException;
import models.enums.ThresholdEnum;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeatherAgent implements IWeatherAgent {
    private String computeEndpoint(ArrayList<ThresholdEnum> thresholdEnums){
        StringBuilder endpointBuilder = new StringBuilder();

        for (ThresholdEnum threshold : thresholdEnums) {
            endpointBuilder.append(threshold.getName());
            endpointBuilder.append(",");
        }
        String endpoint = endpointBuilder.toString();
        return endpoint.substring(0, endpoint.length()-1);
    }

    private JSONObject requestApi(double latitude, double longitude, String endpoint) throws WeatherException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(
                        String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=%s",
                                latitude, longitude, endpoint))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new WeatherException("Unexpected response " + response);
            }
            if (response.body() == null){
                throw new WeatherException("Response body is null");
            }
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new WeatherException("Received an IOException", e);
        }
    }

    private HashMap<ThresholdEnum, Double> computeCurrentValues(JSONObject resultRequest, ArrayList<ThresholdEnum> thresholdEnums){
        HashMap<ThresholdEnum, Double> currentValues = new HashMap<ThresholdEnum, Double>();
        for (ThresholdEnum thresholdEnum : thresholdEnums){
            double value = resultRequest.getJSONObject("current").getDouble(thresholdEnum.getName());
            currentValues.put(thresholdEnum, value);
        }
        return currentValues;
    }

    public HashMap<ThresholdEnum, Double> getValuesFromData(double latitude, double longitude, ArrayList<ThresholdEnum> thresholdEnums) throws WeatherException {
        String endpoint = computeEndpoint(thresholdEnums);
        JSONObject resultRequest = requestApi(latitude, longitude, endpoint);
        return computeCurrentValues(resultRequest, thresholdEnums);
    }

    public static void main(String[] args) {
        WeatherAgent weatherAgent = new WeatherAgent();
        double latitude = 47.75;
        double longitude = -3.36667;
        ArrayList<ThresholdEnum> thresholdEnums = new ArrayList<>(List.of(ThresholdEnum.RAIN, ThresholdEnum.WIND, ThresholdEnum.TEMPERATURE));
        try {
            HashMap<ThresholdEnum, Double> currentValues = weatherAgent.getValuesFromData(latitude, longitude, thresholdEnums);
            for (Map.Entry<ThresholdEnum, Double> entry : currentValues.entrySet()) {
                System.out.println(entry.getKey().getName() + ": " + entry.getValue());
            }
        } catch (WeatherException e) {
            throw new RuntimeException(e);
        }
    }
}
