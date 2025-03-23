import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Unit tests for the APIHandler class.
 * @author Khem-Talah
 * @version 1.0
 */
class APIHandlerTest {


    /*
     * Load JSON data from a file and return it as a string. (utility method for test json files)
     */
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
    /*
    Just tests for if the fetchLocationData() method returns anything at all
     */
    @Test
    public void testFetchLocationData_Success() throws Exception {
        APIHandler handler = new APIHandler();
        JSONArray results = handler.fetchLocationData();
        assertNotNull(results);
        assertFalse(results.isEmpty(), "Expecting a result that is not null");
    }

    /*
     * Tests the fetchLocationData method and compares it against a validated curl response in the json file "everylocation" in the test folder.
     */
    @Test
    public void testFetchLocationData_AgainstExpectedJson() throws Exception {
        APIHandler handler = new APIHandler();
        // 1. Get the actual JSON from the live API call.
        JSONArray actualJsonArray = handler.fetchLocationData();
        assert actualJsonArray != null;
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
    /*
     * Tests the loadAllLocationData method and asserts it returns a non-empty list of LocationData.
     */
    @Test
    public void testLoadAllLocationData_Success() {
        APIHandler handler = new APIHandler();
        List<LocationData> locations = handler.loadAllLocationData();
        assertNotNull(locations);
        assertFalse(locations.isEmpty(), "Expected a list of location data to be returned");
    }

    /*
     * Tests the fetchLatestMeasurementByLocation method and makes sure that the returned values have some corresponding fields with the test measurement
     * The test measurement json file is for Southwark - A2 Old Kent Road location id: 146
     * We are just checking if the sensors match in the API response and the validated curl response
     */
    @Test
    public void testFetchLatestMeasurementByLocation_AgainstExpectedJson() throws Exception {
        APIHandler handler = new APIHandler();
        JSONObject allLocations = new JSONObject(loadJsonFromFile("/test/everylocation.json"));
        JSONArray locationArray = allLocations.getJSONArray("results");
        JSONObject testLocation = locationArray.getJSONObject(2);
        LocationData locationData = new LocationData(testLocation);
        JSONObject actualResultPM10 = handler.fetchLatestMeasurementByLocation(locationData, "pm10");


        String expectedString = loadJsonFromFile("/test/examplemeasurement.json");
        JSONObject expectedJson = new JSONObject(expectedString);
        JSONArray expectedArray = expectedJson.getJSONArray("results");
        JSONObject expected = expectedArray.getJSONObject(0);
        assert actualResultPM10 != null;
        assertEquals(expected.getInt("sensorsId"), actualResultPM10.getInt("sensorsId"));

        }


    /*
     * Tests the fetchLatestMeasurementByLocation method with a non-existent location ID.
     * This test should assert that the method returns null when the server returns a 404 error.
     */
    @Test
    public void testFetchLatestMeasurementByLocation_NonExistentId() throws Exception {
        APIHandler handler = new APIHandler();
        JSONObject locationJson = new JSONObject();
        locationJson.put("id", 999999999);  // Very large, presumably invalid ID
        locationJson.put("name", "somalia");
        JSONObject coords = new JSONObject();
        coords.put("latitude", 51.5);
        coords.put("longitude", -0.1);
        locationJson.put("coordinates", coords);
        // Add "sensors"
        locationJson.put("sensors", new JSONArray());

        LocationData locationData = new LocationData(locationJson);

        // This should lead to a 404 from the server, returning null
        JSONObject result = handler.fetchLatestMeasurementByLocation(locationData, "pm25");

        // Assert: Since responseCode != 200, we expect null
        assertNull(result, "Expected null if server returns a non-200 error like 404");
    }

    /*
     * Tests the fetchLatestMeasurementByLocation method with a sensor ID that does not exist in the location data.
     * This test should assert that the method returns null when the sensor ID is not found in the location data.
     */
    @Test
    public void testFetchLatestMeasurementByLocation_UnknownSensor() throws Exception {
        APIHandler handler = new APIHandler();
        // Suppose you have a real location ID that does exist in OpenAQ
        // but artificially omit the sensor ID from the map so it can’t match.
        JSONObject locationJson = new JSONObject();
        locationJson.put("id", 146);
        locationJson.put("name", "A Real Named Location");
        JSONObject coords = new JSONObject();
        coords.put("latitude", 51.5);
        coords.put("longitude", -0.1);
        locationJson.put("coordinates", coords);


        locationJson.put("sensors", new JSONArray());
        LocationData locationData = new LocationData(locationJson);

        // We know location 146 might return data with sensorId=someNumber,
        // but if that ID isn't in our locationData's map, we skip it.
        JSONObject measurement = handler.fetchLatestMeasurementByLocation(locationData, "pm25");

        // Because the ID in the real data won't match anything in our map, we get null
        assertNull(measurement, "Expected null if the sensor ID isn't in our map");
    }


    /*
     * Tests the fetchLatestMeasurementByLocation method with an invalid API key.
     * This test should assert that the method returns null when the API key is invalid.
     * this test uses a fake key to actually ensure an invalid api key is being used for the testing
     */
    @Test
    public void testFetchLocationData_InvalidKey() throws Exception {
        APIHandler handler = new APIHandler("fake ass key");
        JSONArray result = handler.fetchLocationData();
        // Expect a 401 , so the code returns null
        assertNull(result, "With an invalid key, we expect null or an error response");
    }






}