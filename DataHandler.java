import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.Optional;

public class DataHandler {
    public static List<Double> getPollutantTrends(String pollutant) {
        List<Double> trend = new ArrayList<>();


        for (int year_index = 2018; year_index <= 2023; year_index++) {
            // convert year into string 
            String year = String.valueOf(year_index);
            
            // get the filename for the given pollutant and year
            String filename = generateFilename(pollutant, year);
            if (filename.isEmpty()) {
                continue; // skip if no file to use

            }

            // load the dataset from the file
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);

            // calculate the average pollutant level and add it to the trend list
            trend.add(getAveragePollutantLevel(dataSet));
        }

        return trend;
    }   

    // generates the correct filename for a given pollutant and year
    public static String generateFilename(String pollutant, String year) {
        //chooses specefic file(path) based on pollutant and year within folders
        switch (pollutant) {   
            case "NO2":
                return String.format("UKAirPollutionData/%s/mapno2%s.csv", pollutant, year);
            case "PM2.5":
                return String.format("UKAirPollutionData/%s/mappm25%sg.csv", pollutant, year);
            case "PM10":
                return String.format("UKAirPollutionData/%s/mappm10%sg.csv", pollutant, year);
            default:
                System.out.println("Error occured");
                return ""; // return empty string if pollutant is not recognized
        }
    }

    public static double getAveragePollutantLevel(DataSet data) {
        double total = 0.0;

        for (DataPoint point : data.getData())
        {
            total += point.value();
        }
        double average = 0;
        if (!data.getData().isEmpty())
        {
            average = total / data.getData().size();
        }

        return average;
    }
    
    /** 
     * Get the ten highest pollutant values
     * @return An array list of the 10 dataPoints with the highest pollutant values
     */
    public static ArrayList<DataPoint> getHighestPollutantLevel(String filename) {
        ArrayList<DataPoint> highestPLs = new ArrayList<>();
        
        DataLoader loader = new DataLoader();
        DataSet dataSet = loader.loadDataFile(filename);
        List<DataPoint> data = dataSet.getData();
        
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());
        
        for(int i=0; i<=10; i++){
            highestPLs.add(data.get(i));
        }
        
        return highestPLs;
    }
    
    public static double getAveragePollutantLevelForArea(String pollutant, int UGC){    
        double total = 0.0;
        int numberOfYears = 0;
        
        for (int year = 2018; year <= 2023; year++)
        {
            String filename;
            switch (pollutant)
            {   case "NO2":
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
            List<DataPoint> data = dataSet.getData();
            
            Optional<DataPoint> result = data.stream().filter(dataPoint -> dataPoint.gridCode() == UGC).findFirst();
            if (result.isPresent()){
                total += result.get().value();
                numberOfYears += 1;
            }
        }
        double average = total / numberOfYears;
        return average;
    }
}
