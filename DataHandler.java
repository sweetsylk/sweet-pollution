
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DataHandler {
    public DataHandler() {
    }

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

    public static double getAveragePollutantLevel(DataSet data) {
        double total = 0.0;

        DataPoint point;
        for(Iterator<DataPoint> var3 = data.getData().iterator(); var3.hasNext(); total += point.value()) {
            point = var3.next();
        }

        return data.getData().isEmpty() ? 0.0 : total / (double)data.getData().size();
    }

    public static double getAveragePollutantLevelByPeriod(String pollutant, String year) {
        String filename = generateFilename(pollutant, String.valueOf(year));
        DataLoader loader = new DataLoader();
        DataSet dataSet = loader.loadDataFile(filename);
        return getAveragePollutantLevel(dataSet);
    }

    public static ArrayList getHighestPollutantLevel(DataSet dataset) {
        ArrayList highestPLs = new ArrayList();
        List<DataPoint> data = dataset.getData();
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());

        for(int i = 0; i < 10; ++i) {
            highestPLs.add(data.get(i));
        }

        return highestPLs;
    }

    public static double getAveragePollutantLevelForArea(String pollutant, int UGC) {
        double total = 0.0;
        int numberOfYears = 0;

        for(int year = 2018; year <= 2023; ++year) {
            String filename = generateFilename(pollutant, String.valueOf(year));
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            List<DataPoint> data = dataSet.getData();
            Optional<DataPoint> result = data.stream().filter((dataPoint) -> dataPoint.gridCode() == UGC).findFirst();
            if (result.isPresent()) {
                total += result.get().value();
                ++numberOfYears;
            }
        }

        return numberOfYears == 0 ? 0.0 : total / (double)numberOfYears;
    }
}
