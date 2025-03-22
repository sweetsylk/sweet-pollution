import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class APIHandlerTest {


    private String loadJsonFromFile(String file) throws Exception {
        APIHandler handler = new APIHandler();
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
        APIHandler handler = new APIHandler();
        // 1. Get the actual JSON from the live API call.
        JSONArray actualJsonArray = handler.fetchLocationData();
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
        APIHandler handler = new APIHandler();
        JSONArray results = handler.fetchLocationData();
        assertNotNull(results);
        assertTrue(results.length() > 0, "Expected to have non-empty results array");
    }

    @Test
    void testFetchLatestMeasurementByLocation_AgainstExpectedJson() throws Exception {
        APIHandler handler = new APIHandler();
        JSONObject allLocations = new JSONObject(loadJsonFromFile("/test/everylocation.json"));
        JSONArray locationArray = allLocations.getJSONArray("results");
        JSONObject testLocation = locationArray.getJSONObject(2);
        LocationData locationData = new LocationData(testLocation);
        JSONObject actualResultNO2 = handler.fetchLatestMeasurementByLocation(locationData, "pm10");


        String expectedString = loadJsonFromFile("/test/examplemeasurement.json");
        JSONObject expectedJson = new JSONObject(expectedString);
        JSONArray expectedArray = expectedJson.getJSONArray("results");
        JSONObject expected = expectedArray.getJSONObject(0);
        assertEquals(expected.getInt("sensorsId"), actualResultNO2.getInt("sensorsId"));

        }


    @Test
    void testLoadAllLocationData_Success() throws Exception{
        APIHandler handler = new APIHandler();
        List<LocationData> locations = handler.loadAllLocationData();
        assertNotNull(locations);
        assertFalse(locations.isEmpty(), "Expected a list of location data to be returned");
    }

    @Test
    void testFetchLatestMeasurementByLocation_NonExistentId() throws Exception {
        APIHandler handler = new APIHandler();
        // Setup: Create a bogus location ID that doesn't exist in the API
        JSONObject locationJson = new JSONObject();
        locationJson.put("id", 999999999);  // Very large, presumably invalid ID
        // Add "name"
        locationJson.put("name", "Bogus Location");
        // Add "coordinates" - minimal but valid
        JSONObject coords = new JSONObject();
        coords.put("latitude", 51.5);
        coords.put("longitude", -0.1);
        locationJson.put("coordinates", coords);
        // Add "sensors"
        locationJson.put("sensors", new JSONArray());

        // Now the constructor won't fail on missing fields
        LocationData locationData = new LocationData(locationJson);

        // Act: This should lead to a 404 from the server, so your method returns null
        JSONObject result = handler.fetchLatestMeasurementByLocation(locationData, "pm25");

        // Assert: Since responseCode != 200, we expect null
        assertNull(result, "Expected null if server returns a non-200 error like 404");
    }

    @Test
    void testFetchLatestMeasurementByLocation_UnknownSensor() throws Exception {
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

        // For demonstration, define sensors = empty array -> no sensor ID map
        locationJson.put("sensors", new JSONArray());

        // Build the object
        LocationData locationData = new LocationData(locationJson);

        // Act
        // We know location 146 might return data with sensorId=someNumber,
        // but if that ID isn't in our locationData's map, we skip it.
        JSONObject measurement = handler.fetchLatestMeasurementByLocation(locationData, "pm25");

        // Assert
        // Because the ID in the real data won't match anything in our map, we get null
        assertNull(measurement, "Expected null if the sensor ID isn't in our map");
    }


    @Test
    void testFetchLocationData_InvalidKey() throws Exception {
        APIHandler handler = new APIHandler("fake ass key");
        JSONArray result = handler.fetchLocationData();
        // Expect a 401 or 403, so the code returns null
        assertNull(result, "With an invalid key, we expect null or an error response");
    }






}