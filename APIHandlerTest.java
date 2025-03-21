import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class APIHandlerTest {


    @test
    void testJsonFromFile() throws Exception {
        // 1. Load the JSON file (located in src/test/resources)
        String test_file = "test/everylocation.json";
        URL url = getClass().getResource(test_file);
        assert url != null;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI()).getAbsolutePath()))) {
            // 2. Read it into a String
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // 3. Convert the string into a JSONObject
            JSONObject json = new JSONObject(sb.toString());
            JSONArray results = json.getJSONArray("results");
            assertTrue(results.length() > 0, "Expected at least one measurement in JSON");

            // 4. Run your assertions on the JSON data
            JSONObject firstResult = results.getJSONObject(0);

            // Example field-by-field checks
            assertEquals(123, firstResult.getInt("sensorsId"), "Expected sensorsId=123");
            assertEquals("pm25", firstResult.getString("parameter"), "Expected parameter=pm25");
            assertEquals(12.34, firstResult.getDouble("value"), 0.0001, "Expected value=12.34");
        }
    }

    @Test
    void testFetchAirPollutionData_Success() throws Exception {
        JSONArray results = APIHandler.fetchAirPollutionData();
        assertNotNull(results);
        assertTrue(results.length() > 0, "Expected to have non-empty results array");
    }

    @Test
    void testFetchLatestMeasurementByLocation_Success() throws Exception {
        // 1. Load the a json file from test resources
        String test_file = "test/everylocation.json";
        URL url = getClass().getResource(test_file);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI()).getAbsolutePath()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            JSONObject jsonResponse = new JSONObject(response.toString());


            JSONArray results = jsonResponse.getJSONArray("results");
            assertTrue(results.length() > 0, "Expected at least one measurement in JSON");
            JSONObject measurement = results.getJSONObject(0);
            assertTrue(measurement.has("value"), "Measurement should contain a 'value' field");
            assertEquals("pm25", measurement.getString("parameter"), "Expected parameter to be 'pm25'");
        }
    }

    @Test
    void testLoadAllLocationData_Success() {
        List<LocationData> locations = APIHandler.loadAllLocationData();
        assertNotNull(locations);
        assertFalse(locations.isEmpty(), "Expected a list of location data to be returned");
    }

}