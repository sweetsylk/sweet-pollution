import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class APIHandlerTest {


    private String loadJsonFromFile(String file) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(file)) {
            if (is == null) {
                throw new IllegalArgumentException("Could not find the file whoops: " + file);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
    @Test
    void testFetchLocationData_AgainstExpectedJson() throws Exception {
        // 1. Get the actual JSON from the live API call.
        JSONArray actualJsonArray = APIHandler.fetchLocationData();
        JSONObject actual = actualJsonArray.getJSONObject(0);



        String expectedString = loadJsonFromFile("/test/everylocation.json");
        JSONObject expectedJson = new JSONObject(expectedString);
        JSONArray array = expectedJson.getJSONArray("results");
        JSONObject expected = array.getJSONObject(0);

        assertEquals(expected.getInt("id"), actual.getInt("id"));
        JSONArray expectedArray = expected.getJSONArray("sensors");
        JSONArray actualArray = actual.getJSONArray("sensors");
        assertEquals(expectedArray.toString(), actualArray.toString());




    }



    @Test
    void testFetchAirPollutionData_Success() throws Exception {
        JSONArray results = APIHandler.fetchLocationData();
        assertNotNull(results);
        assertTrue(results.length() > 0, "Expected to have non-empty results array");
    }

    @Test
    void testFetchLatestMeasurementByLocation_AgainstExpectedJson() throws Exception {
        JSONObject allLocations = new JSONObject(loadJsonFromFile("/test/everylocation.json"));
        JSONArray locationArray = allLocations.getJSONArray("results");
        JSONObject testLocation = locationArray.getJSONObject(2);
        LocationData locationData = new LocationData(testLocation);
        JSONObject actualResultNO2 = APIHandler.fetchLatestMeasurementByLocation(locationData, "pm10");


        String expectedString = loadJsonFromFile("/test/examplemeasurement.json");
        JSONObject expectedJson = new JSONObject(expectedString);
        JSONArray expectedArray = expectedJson.getJSONArray("results");
        JSONObject expected = expectedArray.getJSONObject(0);
        assertEquals(expected.getInt("sensorsId"), actualResultNO2.getInt("sensorsId"));

        }


    @Test
    void testLoadAllLocationData_Success() throws Exception{
        List<LocationData> locations = APIHandler.loadAllLocationData();
        assertNotNull(locations);
        assertFalse(locations.isEmpty(), "Expected a list of location data to be returned");
    }

}