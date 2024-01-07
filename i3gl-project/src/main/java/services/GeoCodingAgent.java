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
    private OkHttpClient client;
    private static final String GEOCODE_API_URL = "https://geocode.maps.co/search?q=";

    @Override
    public Location convertAddressToLocation(String address) throws GeoCodingException {
        Request request = new Request.Builder()
                .url(GEOCODE_API_URL + address)
                .build();
        try (Response response = client.newCall(request).execute()){
            if (response.body() == null) {
                throw new GeoCodingException("Response body is null");
            }else{
                JSONArray jsonArray = new JSONArray(response.body().string());
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                return new Location(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"));
            }
        } catch (IOException e) {
            throw new GeoCodingException("Received an IOException", e);
        }
    }
}
