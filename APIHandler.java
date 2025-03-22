import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIHandler {
    private static String apiKey;

    // Default constructor uses the real key
    public APIHandler() {
        this.apiKey = ApiKeyLoader.getApiKey();
    }

    // Additional constructor used in JUNI test
    public APIHandler(String customKey) {
        this.apiKey = customKey;
    }

    public static JSONArray fetchLocationData() throws Exception {
        URL url = new URL("https://api.openaq.org/v3/locations?bbox=-0.40653443,51.395246,0.20205370,51.627741&limit=100");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("x-api-key", apiKey);
        int responseCode = conn.getResponseCode();
        if (responseCode != 200)
        {
            System.out.println("API Request Failed: HTTP " + responseCode);
            return null;
        }
        else
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();

            String line;
            while((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();
            conn.disconnect();
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("results");
        }
    }

    public static JSONObject fetchLatestMeasurementByLocation(LocationData locationData, String pollutant) throws Exception {
        String pollutantCleaned = pollutant.toLowerCase().replaceAll("[^a-z0-9]", ""); // This just makes the input strictly alphanumeric

        String urlString = String.format("https://api.openaq.org/v3/locations/%d/latest", locationData.getLocationId());
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-API-Key", apiKey);
        conn.setRequestProperty("Accept", "application/json");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.out.println("API call failed with response code " + responseCode + " for location ID: " + locationData.getLocationId());
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();

        String line;
        while((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        conn.disconnect();
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray measurements = jsonResponse.getJSONArray("results");

        for(int i = 0; i < measurements.length(); ++i) {
            JSONObject measurement = measurements.getJSONObject(i);
            int sensorId = measurement.getInt("sensorsId");
            String parameter = locationData.getSensorIdToParameter().get(sensorId);
            if ("no2".equalsIgnoreCase(parameter) && pollutantCleaned.equals("no2")) {
                return measurement;
            }
            else if ("pm25".equalsIgnoreCase(parameter) && pollutantCleaned.equals("pm25")) {
                return measurement;
            }
            else if ("pm10".equalsIgnoreCase(parameter) && pollutantCleaned.equals("pm10")) {
                return measurement;
            }
        }

        return null;
    }

    public static List<LocationData> loadAllLocationData() {
        JSONArray locationsArray;
        try {
            locationsArray = fetchLocationData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<LocationData> locations = new ArrayList<>();
        if (locationsArray != null) {
            for(int i = 0; i < locationsArray.length(); ++i) {
                JSONObject locationJson = locationsArray.getJSONObject(i);
                locations.add(new LocationData(locationJson));
            }
        }

        return locations;
    }



}
