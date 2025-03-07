import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    public static List<Double> getPollutantTrends(String pollutant) {
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
                    System.out.println("Unknown file loaded");
                    continue;
            }

            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);

            trend.add(getAveragePollutantLevel(dataSet)); // Now loads correct dataset
        }

        return trend;
    }



    public static double getAveragePollutantLevel(DataSet data) {
        double total = 0.0;

        for (DataPoint point : data.getData()) {
            total += point.value();

        }
        double average = 0;
        if (!data.getData().isEmpty()) {
            average = total / data.getData().size();
        }


        System.out.println(average);
        return average;


    }
}
