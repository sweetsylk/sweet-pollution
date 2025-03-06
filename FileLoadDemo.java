
/**
 * This class is a short demo showing how to use the DataLoader to load the pollution data 
 * csv files from disk and access the data.
 *
 * @author Michael Kölling
 * @version 1.0
 */
public class FileLoadDemo
{
    /**
     * Create and run the demo.
     */
    public FileLoadDemo(String filename)
    {
        showFile(filename);
    }

    /**
     * This method loads one of the pollution data files.
     */
    public void showFile(String filename)
    {
        DataLoader loader = new DataLoader();
        
        DataSet dataSet = loader.loadDataFile(filename);
        System.out.println(dataSet);
    }
}
