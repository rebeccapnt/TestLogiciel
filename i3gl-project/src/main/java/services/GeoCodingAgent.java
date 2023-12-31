package services;

import exceptions.GeoCodingException;
import models.Location;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class GeoCodingAgent implements IGeoCodingAgent {

    private final OkHttpClient client = new OkHttpClient();
    private static final String GEOCODE_API_URL = "https://geocode.maps.co/search?q=";

    @Override
    public Location convertAddressToLocation(String address) throws GeoCodingException, InterruptedException {

        Thread.sleep(1000);

        Request request = new Request.Builder()
                .url(GEOCODE_API_URL + address + "&api_key=659c46915941e989781825tgy96efcf")
                .build();

        try (Response response = client.newCall(request).execute()){
            if (!response.isSuccessful()) {
                throw new GeoCodingException("Unexpected response " + response);
            }
            if (response.body() != null) {
                String responseBody = response.body().string();
                JSONArray jsonArray = new JSONArray(responseBody);
                if (jsonArray.isEmpty())  throw new GeoCodingException("Position not found for : " + address);
                JSONObject firstObject = jsonArray.getJSONObject(0);

                double latitude = firstObject.getDouble("lat");
                double longitude = firstObject.getDouble("lon");

                return new Location(latitude, longitude);
            }else{
                throw new GeoCodingException("Response body is null");
            }
        } catch (IOException e) {
            throw new GeoCodingException("Received an IOException", e);
        }
    }
}
