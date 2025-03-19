
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DataHandler {
    public DataHandler() {
    }

    public static ArrayList<Double> getPollutantTrends(String pollutant) {
        List<Double> trend = new ArrayList();

        for(int year_index = 2018; year_index <= 2023; ++year_index) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (!filename.isEmpty()) {
                DataLoader loader = new DataLoader();
                DataSet dataSet = loader.loadDataFile(filename);
                trend.add(getAveragePollutantLevel(dataSet));
            }
        }

        return (ArrayList)trend;
    }

    public static ArrayList<Double> getHighestPollutantTrends(String pollutant) {
        List<Double> trend = new ArrayList();

        for(int year_index = 2018; year_index <= 2023; ++year_index) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (!filename.isEmpty()) {
                DataLoader loader = new DataLoader();
                DataSet dataSet = loader.loadDataFile(filename);
                ArrayList<Double> highestPollutantData = getHighestPollutantLevel(dataSet);
                if (!highestPollutantData.isEmpty()) {
                    trend.add((Double)highestPollutantData.get(0));
                }
            }
        }

        return (ArrayList)trend;
    }

    public static String generateFilename(String pollutant, String year) {
        String var10000;
        switch (pollutant) {
            case "NO2":
                var10000 = String.format("UKAirPollutionData/%s/mapno2%s.csv", pollutant, year);
                break;
            case "PM2.5":
                var10000 = String.format("UKAirPollutionData/%s/mappm25%sg.csv", pollutant, year);
                break;
            case "PM10":
                var10000 = String.format("UKAirPollutionData/%s/mappm10%sg.csv", pollutant, year);
                break;
            default:
                System.out.println("Error occurred");
                var10000 = "";
        }

        return var10000;
    }

    public static double getAveragePollutantLevel(DataSet data) {
        double total = 0.0;

        DataPoint point;
        for(Iterator var3 = data.getData().iterator(); var3.hasNext(); total += point.value()) {
            point = (DataPoint)var3.next();
        }

        return data.getData().isEmpty() ? 0.0 : total / (double)data.getData().size();
    }

    public static ArrayList<Double> getHighestPollutantLevel(DataSet dataset) {
        ArrayList<Double> highestPLs = new ArrayList();
        List<DataPoint> data = dataset.getData();
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());
        int maxElements = Math.min(10, data.size());

        for(int i = 0; i < maxElements; ++i) {
            highestPLs.add(((DataPoint)data.get(i)).value());
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
            Optional<DataPoint> result = data.stream().filter((dataPoint) -> {
                return dataPoint.gridCode() == UGC;
            }).findFirst();
            if (result.isPresent()) {
                total += ((DataPoint)result.get()).value();
                ++numberOfYears;
            }
        }

        return numberOfYears == 0 ? 0.0 : total / (double)numberOfYears;
    }
}
