import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This stores a given location in london from the JSONResponse made by the fetchLocationData() method in the APIHandler class
 * This means that it stores the locations from endpoint: https://api.openaq.org/v3/locations?bbox=-0.40653443,51.395246,0.20205370,51.627741&limit=100
 * This then allows for easy access for locationID, locationName, latitude, longitude
 * also maps the pollutant type to the sensorId which allows for pollutant checks in the APIHandler
 * @author Ridwan Adam
 * @version 1.0
 */
public class LocationData {
    private int locationId;
    private String locationName;
    private double latitude;
    private double longitude;
    private Map<Integer, String> sensorIdToParameter;

    public LocationData(JSONObject locationJson) {
        this.locationId = locationJson.getInt("id");
        this.locationName = locationJson.getString("name");
        JSONObject coords = locationJson.getJSONObject("coordinates");
        this.latitude = coords.getDouble("latitude");
        this.longitude = coords.getDouble("longitude");
        this.sensorIdToParameter = new HashMap<>();
        JSONArray sensors = locationJson.getJSONArray("sensors");

        for(int i = 0; i < sensors.length(); ++i) {
            JSONObject sensor = sensors.getJSONObject(i);
            int sensorId = sensor.getInt("id");
            String parameterName = sensor.getJSONObject("parameter").getString("name");
            this.sensorIdToParameter.put(sensorId, parameterName);
        }

    }

    public int getLocationId() {
        return this.locationId;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public Map<Integer, String> getSensorIdToParameter() {
        return this.sensorIdToParameter;
    }
}
