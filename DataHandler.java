import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;

public class DataHandler {

    // Gets the average pollutant trend over years
    public static ArrayList<Double> getPollutantTrends(String pollutant) {
        List<Double> trend = new ArrayList<>();

        for (int year_index = 2018; year_index <= 2023; year_index++) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (filename.isEmpty()) {
                continue;
            }

            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            trend.add(getAveragePollutantLevel(dataSet));
        }

        return (ArrayList<Double>) trend;
    }

    // Gets the highest pollutant trend over years
    public static ArrayList<Double> getHighestPollutantTrends(String pollutant) {
        List<Double> trend = new ArrayList<>();

        for (int year_index = 2018; year_index <= 2023; year_index++) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (filename.isEmpty()) {
                continue;
            }

            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            ArrayList<Double> highestPollutantData = getHighestPollutantLevel(dataSet);

            if (!highestPollutantData.isEmpty()) {
                trend.add(highestPollutantData.get(0)); // Store the highest value
            }
        }

        return (ArrayList<Double>) trend;
    }

    // Generates the correct filename for a given pollutant and year
    public static String generateFilename(String pollutant, String year) {
        return switch (pollutant) {
            case "NO2" -> String.format("UKAirPollutionData/%s/mapno2%s.csv", pollutant, year);
            case "PM2.5" -> String.format("UKAirPollutionData/%s/mappm25%sg.csv", pollutant, year);
            case "PM10" -> String.format("UKAirPollutionData/%s/mappm10%sg.csv", pollutant, year);
            default -> {
                System.out.println("Error occurred");
                yield "";
            }
        };
    }

    // Calculates the average pollutant level from dataset
    public static double getAveragePollutantLevel(DataSet data) {
        double total = 0.0;

        for (DataPoint point : data.getData()) {
            total += point.value();
        }

        return data.getData().isEmpty() ? 0 : total / data.getData().size();
    }

    /**
     * Get the ten highest pollutant values
     * @return A list of the 10 highest pollutant values
     */
    public static ArrayList<Double> getHighestPollutantLevel(DataSet dataset) {
        ArrayList<Double> highestPLs = new ArrayList<>();

        List<DataPoint> data = dataset.getData();
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());

        int maxElements = Math.min(10, data.size());
        for (int i = 0; i < maxElements; i++) {
            highestPLs.add(data.get(i).value());
        }

        return highestPLs;
    }

    // Gets the average pollutant level for a specific area over years
    public static double getAveragePollutantLevelForArea(String pollutant, int UGC) {
        double total = 0.0;
        int numberOfYears = 0;

        for (int year = 2018; year <= 2023; year++) {
            String filename = generateFilename(pollutant, String.valueOf(year));
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            List<DataPoint> data = dataSet.getData();

            Optional<DataPoint> result = data.stream().filter(dataPoint -> dataPoint.gridCode() == UGC).findFirst();
            if (result.isPresent()) {
                total += result.get().value();
                numberOfYears++;
            }
        }

        return numberOfYears == 0 ? 0.0 : total / numberOfYears;
    }
}
