package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class IGeoCodingAgent implements GeoCodingAgent {
    private OkHttpClient client;
    private static final String GEOCODE_API_URL = "https://geocode.maps.co/search?q=";

    @Override
    public Double[] convertAddressToLocation(String address) throws IOException {
        Request request = new Request.Builder()
                .url(GEOCODE_API_URL + address)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                Double lat = jsonObject.getDouble("lat");
                Double lon = jsonObject.getDouble("lon");

                return new Double[]{lat, lon};
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
