import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APIHandler {

    // Updated endpoint for OpenAQ measurements for London (v2).
    private static final String API_URL = "https://api.openaq.org/v3/locations?bbox=-0.40653443,51.395246,0.20205370,51.627741&limit=100";
    private static final String API_KEY = "b761039efe38ef19516f67876743392777d4a895c2416e81dd9441cdddacab9b";

    /**
     * This fetches location data from the OPENAQ api for london
     * @return JSONArray it returns the json response
     */
    public static JSONArray fetchAirPollutionData() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("x-api-key", API_KEY);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("⚠ API Request Failed: HTTP " + responseCode);
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("results");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
     * Fetches the latest measurement for a specific sensor ID from the OPENAQ API.
     * @param sensorId the unique identifier of a specific measurement
     * @return the latest measurement
     */
    public static JSONObject fetchLatestMeasurement(int sensorId) {
        String apiKey = "b761039efe38ef19516f67876743392777d4a895c2416e81dd9441cdddacab9b";
        String urlString = String.format("https://api.openaq.org/v3/sensors/%d/measurements?limit=1&sort=desc", sensorId);

        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-API-Key", apiKey);
            conn.setRequestProperty("Accept", "application/json");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray results = jsonResponse.getJSONArray("results");
                if (results.length() > 0) {
                    return results.getJSONObject(0);
                } else {
                    System.out.println("No measurements found for sensor ID: " + sensorId);
                    return null;
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
