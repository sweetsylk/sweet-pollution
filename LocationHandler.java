public class LocationHandler {
    private final String fileName;

    /**
     * Create and run the demo.
     */
    public LocationHandler(String fileName) {
        this.fileName = fileName;
        showFile();
    }

    /**
     * This method loads one of the pollution data files.
     */
    public void showFile() {
        DataLoader loader = new DataLoader();

        DataSet dataSet = loader.loadDataFile(fileName);
        System.out.println(dataSet.getData());
    }
}
