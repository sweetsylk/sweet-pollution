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

    /**
     * Finds the data points with the ten highest pollutant levels through all the years
     * @return a list of the ten data points with the highest pollutant levels
     */
    public static ArrayList<DataPoint> getHighestPollutantTrends(String pollutant) {
        ArrayList<DataPoint> trend = new ArrayList<>();

        for (int year_index = 2018; year_index <= 2023; year_index++) {
            String year = String.valueOf(year_index);
            String filename = generateFilename(pollutant, year);
            if (filename.isEmpty()) {
                continue;
            }

            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            ArrayList<DataPoint> highestPollutantData = getHighestPollutantLevel(dataSet);
            if (!highestPollutantData.isEmpty()) {
                if (trend.size() < 10){
                    for (int i = 0; trend.size() + i < 10; i++){
                        trend.add(highestPollutantData.get(i));
                    }
                }
                else{
                    for(int i = 0; i <= 10; i++){
                        if (trend.get(i).value() < highestPollutantData.get(i).value()){
                            trend.set(i, highestPollutantData.get(i));
                        }
                        else{
                            break;
                        }
                    }
                }                
            }
            trend.sort(Comparator.comparingDouble(DataPoint::value).reversed());
        }

        return trend;
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
     * Gets the average pollutant level of all the locations in a single year
     * @return the average pollutant level of every location for that year
     */
    public static double getAveragePollutantLevelByPeriod(String pollutant, String year){
        String filename = generateFilename(pollutant, String.valueOf(year));
        DataLoader loader = new DataLoader();
        DataSet dataSet = loader.loadDataFile(filename);
        return getAveragePollutantLevel(dataSet);
    }

    /**
     * Gets the data points with the highest 10 pollutant levels 
     * @return A list of the 10 highest pollutant values
     */
    public static ArrayList<DataPoint> getHighestPollutantLevel(DataSet dataset) {
        ArrayList<DataPoint> highestPLs = new ArrayList<>();

        List<DataPoint> data = dataset.getData();
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());
       
        
        for (int i = 0; i < 10; i++) {
            highestPLs.add(data.get(i));
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

        return numberOfYears == 0 ? 0.0 :total / numberOfYears;
    }
}
