import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This class contains helper methods to get certain features from data like averages or highest and also allows for dataset filenames to be made
 */
public class DataHandler {
    public DataHandler() {
    }

    /**
     * This returns a list of the average pollution level for a pollutant every year
     * @param pollutant the pollutant
     * @return the list of average pollution level for a given pollutant
     */
    public static ArrayList getPollutantTrends(String pollutant) {
        ArrayList trend = new ArrayList();

        for(int year_index = 2018; year_index <= 2023; ++year_index) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (!filename.isEmpty()) {
                DataLoader loader = new DataLoader();
                DataSet dataSet = loader.loadDataFile(filename);
                trend.add(getAveragePollutantLevel(dataSet));
            }
        }

        return trend;
    }

    /**
     * Returns the highest pollution level for each year for a pollutant
     * @param pollutant the pollutant
     * @return the highest pollution level for each year for a pollutant
     */
    public static ArrayList<DataPoint> getHighestPollutantTrends(String pollutant) {
        ArrayList<DataPoint> trend = new ArrayList<>();

        for(int year_index = 2018; year_index <= 2023; ++year_index) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (!filename.isEmpty()) {
                DataLoader loader = new DataLoader();
                DataSet dataSet = loader.loadDataFile(filename);
                ArrayList<DataPoint> highestPollutantData = getHighestPollutantLevel(dataSet);
                if (!highestPollutantData.isEmpty()) {
                    int i;
                    if (trend.size() < 10) {
                        for(i = 0; trend.size() + i < 10; ++i) {
                            trend.add(highestPollutantData.get(i));
                        }
                    } else {
                        for(i = 0; i <= 10 && trend.get(i).value() < highestPollutantData.get(i).value(); ++i) {
                            trend.set(i, highestPollutantData.get(i));
                        }
                    }
                }
            }
        }

        return trend;
    }

    /**
     * this gives the file name needed to be loaded for a given year and pollutant
     * @param pollutant the pollutant
     * @param year the year
     * @return the file name
     */
    public static String generateFilename(String pollutant, String year) {
        if (pollutant == null || year == null) {
            return "";
        }

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


    /**
     * This gives the average pollution level in each data set
     * @param data the dataset
     * @return the average pollution level
     */
    public static double getAveragePollutantLevel(DataSet data) {
        double total = 0.0;

        DataPoint point;
        for(Iterator<DataPoint> iterator = data.getData().iterator(); iterator.hasNext(); total += point.value()) {
            point = iterator.next();
        }

        return data.getData().isEmpty() ? 0.0 : total / (double)data.getData().size();
    }

    /**
     * Returns the average pollutant level for a given year
     * @param pollutant the pollutant
     * @param year the year
     * @return the average
     */
    public static double getAveragePollutantLevelByPeriod(String pollutant, String year) {
        String filename = generateFilename(pollutant, String.valueOf(year));
        DataLoader loader = new DataLoader();
        DataSet dataSet = loader.loadDataFile(filename);
        return getAveragePollutantLevel(dataSet);
    }

    /**
     * Returns a list of the highest pollution level in a given year for a pollutant
     * @param dataset the dataset
     * @return the 10 highest pollution levels
     */
    public static ArrayList getHighestPollutantLevel(DataSet dataset) {
        ArrayList highestPLs = new ArrayList();
        List<DataPoint> data = dataset.getData();
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());

        for(int i = 0; i < 10; ++i) {
            highestPLs.add(data.get(i));
        }

        return highestPLs;
    }

    /**
     * This gets you the average pollutation level for a given area
     * @param pollutant the pollutant
     * @param gridCode the location
     * @return the average pollution level for a given gridcode
     */
    public static double getAveragePollutantLevelForArea(String pollutant, int gridCode) {
        double total = 0.0;
        int numberOfYears = 0;

        for(int year = 2018; year <= 2023; ++year) {
            String filename = generateFilename(pollutant, String.valueOf(year));
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            List<DataPoint> data = dataSet.getData();
            Optional<DataPoint> result = data.stream().filter((dataPoint) -> dataPoint.gridCode() == gridCode).findFirst();
            if (result.isPresent()) {
                total += result.get().value();
                ++numberOfYears;
            }
        }

        return numberOfYears == 0 ? 0.0 : total / (double)numberOfYears;
    }
}
