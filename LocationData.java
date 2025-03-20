import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

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
