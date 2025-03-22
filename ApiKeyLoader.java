import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ApiKeyLoader {
    private static String API_KEY;

    public ApiKeyLoader() {
    }

    public static String getApiKey() {
        return API_KEY;
    }

    static {
        String key;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(".env"));

            try {
                key = reader.readLine();
                if (key == null || key.trim().isEmpty()) {
                    throw new RuntimeException("API key has not been found in .env file");
                }
            } catch (Throwable var5) {
                try {
                    reader.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }

                throw var5;
            }

            reader.close();
        } catch (IOException var6) {
            IOException e = var6;
            throw new RuntimeException("Could not load .env file", e);
        }

        API_KEY = key.trim();
    }

}

