import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * This class contains helper methods to get certain features from data like averages or highest and also allows for dataset filenames to be made
 *  * @author Ayesha Stevens
 *  * @version 1.0
 */
public class DataHandler {
    public DataHandler() {
    }

    /**
     * Generates the filename for a given year and pollutant
     * @param pollutant the pollutant
     * @param year the year
     * @return filename string
     */
    public static String generateFilename(String pollutant, String year) {
        if (pollutant == null || year == null) {
            return "";
        }

        String cleanedPollutant = pollutant.toUpperCase();
        return switch (cleanedPollutant) {
            case "NO2" -> String.format("UKAirPollutionData/%s/mapno2%s.csv", cleanedPollutant, year);
            case "PM2.5" -> String.format("UKAirPollutionData/%s/mappm25%sg.csv", cleanedPollutant, year);
            case "PM10" -> String.format("UKAirPollutionData/%s/mappm10%sg.csv", cleanedPollutant, year);
            default -> {
                System.out.println("Unknown pollutant: " + pollutant);
                yield "";
            }
        };
    }
    /**
     * Returns a list of the average pollution level for a pollutant every year
     * @param pollutant the pollutant
     * @return list of average pollution levels for each year
     */
    public static ArrayList<Double> getPollutantTrends(String pollutant) {
        ArrayList<Double> trend = new ArrayList<>();

        for (int year_index = 2018; year_index <= 2023; ++year_index) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (!filename.isEmpty()) {
                DataLoader loader = new DataLoader();
                DataSet dataSet = loader.loadDataFile(filename);
                if (dataSet != null && !dataSet.getData().isEmpty()) {
                    trend.add(getAveragePollutantLevel(dataSet));
                }
            }
        }

        return trend;
    }

    /**
     * Returns the highest pollution level for each year for a pollutant
     * @param pollutant the pollutant
     * @return the top 10 highest pollutant data points across the years
     */
    public static ArrayList<DataPoint> getHighestPollutantTrends(String pollutant) {
        ArrayList<DataPoint> trend = new ArrayList<>();

        for (int year_index = 2018; year_index <= 2023; ++year_index) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (!filename.isEmpty()) {
                DataLoader loader = new DataLoader();
                DataSet dataSet = loader.loadDataFile(filename);
                if (dataSet != null && !dataSet.getData().isEmpty()) {
                    ArrayList<DataPoint> highestPollutantData = getHighestPollutantLevel(dataSet);
                    trend.addAll(highestPollutantData);
                }
            }
        }

        // Sort all collected points and returns only the top 10
        trend.sort(Comparator.comparingDouble(DataPoint::value).reversed());
        if (trend.size() > 10) {
            return new ArrayList<>(trend.subList(0, 10));
        }
        return trend;
    }


    /**
     * Calculates average pollution level in a dataset
     * @param data the dataset
     * @return average pollution level
     */
    public static double getAveragePollutantLevel(DataSet data) {
        if (data == null || data.getData().isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (DataPoint point : data.getData()) {
            total += point.value();
        }

        return total / data.getData().size();
    }

    /**
     * Returns the average pollutant level for a pollutant for a given year
     * @param pollutant pollutant name
     * @param year year
     * @return average pollutant level
     */
    public static double getAveragePollutantLevelByPeriod(String pollutant, String year) {
        String filename = generateFilename(pollutant, year);
        DataLoader loader = new DataLoader();
        DataSet dataSet = loader.loadDataFile(filename);
        return getAveragePollutantLevel(dataSet);
    }

    /**
     * Gets the top 10 highest pollutant levels in a dataset
     * @param dataset dataset to search
     * @return array list of up to 10 highest points
     */
    public static ArrayList<DataPoint> getHighestPollutantLevel(DataSet dataset) {
        ArrayList<DataPoint> highestPLs = new ArrayList<>();
        if (dataset == null || dataset.getData().isEmpty()) {
            return highestPLs;
        }

        List<DataPoint> data = new ArrayList<>(dataset.getData());
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());

        int limit = Math.min(10, data.size());
        for (int i = 0; i < limit; i++) {
            highestPLs.add(data.get(i));
        }

        return highestPLs;
    }

    /**
     * Returns the average pollutant level for a given grid area over all years
     * @param pollutant pollutant name
     * @param gridCode grid location code
     * @return average pollutant level at this location
     */
    public static double getAveragePollutantLevelForArea(String pollutant, int gridCode) {
        double total = 0.0;
        int count = 0;

        for (int year = 2018; year <= 2023; ++year) {
            String filename = generateFilename(pollutant, String.valueOf(year));
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            if (dataSet != null && !dataSet.getData().isEmpty()) {
                Optional<DataPoint> result = dataSet.getData().stream()
                        .filter(dp -> dp.gridCode() == gridCode).findFirst();
                if (result.isPresent()) {
                    total += result.get().value();
                    count++;
                }
            }
        }

        return count == 0 ? 0.0 : total / count;
    }
}
