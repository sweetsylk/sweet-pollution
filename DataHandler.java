import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

public class DataHandler {

    public static List<Double> getPollutantTrends(String pollutant) {
        List<Double> trend = new ArrayList<>();

        for (int year = 2018; year <= 2023; year++)
        {
            String filename;
            switch (pollutant)
            {
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
     * 
     * 
     * @return An array list of the 10 dataPoints with the highest pollutant values
     */
    public static ArrayList<DataPoint> getHighestPollutantLevel(String year, String pollutant) {
        ArrayList<DataPoint> highestPLs = new ArrayList<>();
        String filename = "";
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
        }
        
        DataLoader loader = new DataLoader();
        DataSet dataSet = loader.loadDataFile(filename);
        List<DataPoint> data = dataSet.getData();
        
        data.sort(Comparator.comparingDouble(DataPoint::value).reversed());
        
        for(int i=0; i<=10; i++){
            highestPLs.add(data.get(i));
        }
        
        return highestPLs;
    }
}
