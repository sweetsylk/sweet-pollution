import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class DataHandler {

    public static List<Double> getAveragePollutantTrends(String pollutant) {
        List<Double> trend = new ArrayList<>();

        for (int year = 2018; year <= 2023; year++) {
            String filename;
            switch (pollutant) {
                case "NO2":
                    filename = String.format("UKAirPollutionData/%s/mapno2%s.csv", pollutant, year);
                    break;
                case "PM2.5":
                    filename = String.format("UKAirPollutionData/%s/mappm25%sg.csv", pollutant, year);
                    break;
                case "PM10":
                    filename = String.format("UKAirPollutionData/%s/mappm10%sg.csv", pollutant, year);
                    break;
                default:
                    System.out.println("Unknown pollutant selected.");
                    continue;
            }

            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);

            if (dataSet != null && !dataSet.getData().isEmpty()) {
                trend.add(getAveragePollutantLevel(dataSet));
            } else {
                trend.add(0.0); // Add zero if no data available
            }
        }
        return trend;
    }

    public static List<DataPoint> getHighestPollutantLevels(String pollutant) {
        List<DataPoint> highestPLs = new ArrayList<>();

        for (int year = 2018; year <= 2023; year++) {
            String filename;
            switch (pollutant) {
                case "NO2":
                    filename = String.format("UKAirPollutionData/%s/mapno2%s.csv", pollutant, year);
                    break;
                case "PM2.5":
                    filename = String.format("UKAirPollutionData/%s/mappm25%sg.csv", pollutant, year);
                    break;
                case "PM10":
                    filename = String.format("UKAirPollutionData/%s/mappm10%sg.csv", pollutant, year);
                    break;
                default:
                    System.out.println("Unknown pollutant selected.");
                    continue;
            }

            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);

            if (dataSet != null && !dataSet.getData().isEmpty()) {
                highestPLs.addAll(getHighestPollutantLevel(dataSet));
            }
        }

        return highestPLs;
    }

    public static double getAveragePollutantLevel(DataSet data) {
        double total = 0.0;
        for (DataPoint point : data.getData()) {
            total += point.value();
        }
        return data.getData().isEmpty() ? 0 : total / data.getData().size();
    }

    public static List<DataPoint> getHighestPollutantLevel(DataSet data) {
        List<DataPoint> sortedData = new ArrayList<>(data.getData());
        sortedData.sort(Comparator.comparingDouble(DataPoint::value).reversed());

        return sortedData.subList(0, Math.min(10, sortedData.size())); // Prevents out-of-bounds
    }
}
