import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.collections.*;
import javafx.scene.Node;
/**
 * Write a description of class Graphs here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Graphs
{
    // instance variables - replace the example below with your own
    private int x;
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    LineChart chart = new LineChart(xAxis, yAxis);
    /**
     * Constructor for objects of class Graphs
     */
    public Graphs()
    {
        // initialise instance variables
        
        ObservableList<XYChart.Data> dataList = FXCollections.observableArrayList();
        for(int i = 0; i <10; i++){
            dataList.add(new XYChart.Data(i, 0.5*i*i +3));
        }
        ObservableList<XYChart.Series> seriesList = FXCollections.observableArrayList();
        
        seriesList.add(new XYChart.Series("Trends", dataList));
        chart.setData(seriesList);
        
        
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public Node loadGraph()
    {
        return chart;
    }
    
    public void addToGrah(){
        
    }
    
    public Data loadData(){
        DataLoader loader = new DataLoader();
        DataSet dataSet = loader.loadDataFile(filename);
        
    }
}
