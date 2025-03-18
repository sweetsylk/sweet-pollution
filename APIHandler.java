import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIHandler {

    // Updated endpoint for OpenAQ measurements for London (v2).
    private static final String API_URL = "https://api.openaq.org/v3/locations?bbox=-0.40653443,51.395246,0.20205370,51.627741&limit=100";
    private static final String API_KEY = "b761039efe38ef19516f67876743392777d4a895c2416e81dd9441cdddacab9b";

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
}
