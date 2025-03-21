import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class APIHandlerTest {
    @Test
    void testFetchAirPollutionData_Success() throws Exception {
        JSONArray results = APIHandler.fetchAirPollutionData();
        assertNotNull(results);
        assertTrue(results.length() > 0, "Expected to have non-empty results array");
    }

    @Test
    void testFetchLatestMeasurementByLocation_Success() throws Exception {
        // 1. Load the JSON file from test resources
        //    getResourceAsStream looks inside src/test/resources by default
        String test_file = "test/TestData.json";
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