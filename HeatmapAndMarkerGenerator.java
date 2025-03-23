import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * This class is for drawing on the points/heatmaps onto the map
 * it provides methods for making the points/rectangles for the maps and also some others like picking pollution colours
 * @author Irfan Hussein
 * @version 1.0
 */
public class HeatmapAndMarkerGenerator {
    private static final double MAX_EASTING = 553297.0;
    private static final double MIN_EASTING = 510394.0;
    private static final double MAX_NORTHING = 193305.0;
    private static final double MIN_NORTHING = 168504.0;
    private static final double MAX_LATTITUDE = 51.627741;
    private static final double MIN_LATTITUDE = 51.395246;
    private static final double MIN_LONGITUDE = -0.40653443;
    private static final double MAX_LONGITUDE = 0.229848496;

    private static final int MAP_WIDTH = 1781;
    private static final int MAP_HEIGHT = 1100;


    public HeatmapAndMarkerGenerator() {
    }

    /**
     * this checks for whether the data given fits the dimensions of the northing/easting and then it generates
     * @param heatMap whether the user wants heat map or point
     * @param data a given selected dataset from the CSV files
     * @return a list of shapes
     */
    public static List<Node> generateMarkers(Boolean heatMap, DataSet data) {
        List<Node> markers = new ArrayList<>();

        for (DataPoint point : data.getData()) {
            double easting = point.x();
            double northing = point.y();
            double pollution = point.value();
            if (!(easting < MIN_EASTING) && !(easting > MAX_EASTING) && !(northing < MIN_NORTHING) && !(northing > MAX_NORTHING)) {
                double xPixel = convertEastingToPixel(easting);
                double yPixel = convertNorthingToPixel(northing);
                if (heatMap) {
                    displayRectangle(xPixel, yPixel, pollution, markers, point, data);
                } else {
                    displayCircle(xPixel, yPixel, pollution, markers, point, data);
                }
            }
        }

        return markers;
    }

    /**
     * Actually displays the circles (the points) for the map
     * @param x the x coordinate
     * @param y the y coordinate
     * @param pollution the pollution level
     * @param markers the markers
     * @param point the data point itself
     * @param data the dataset the data comes from
     */
    public static void displayCircle(double x, double y, double pollution, List<Node> markers, DataPoint point, DataSet data) {
        Circle marker = new Circle(5.0);
        marker.setFill(getPollutionColor(pollution, false));
        marker.setStroke(Color.BLACK);
        marker.setStrokeWidth(1.0);
        marker.setCursor(Cursor.HAND);
        marker.toFront();
        marker.setLayoutX(x);
        marker.setLayoutY(y);
        generateToolPoint(point, marker, data, pollution);
        markers.add(marker);
    }


    /**
     * This makes the rectangles that make up the heatmap
     * @param x the x coordinate
     * @param y the y coordinate
     * @param pollution the pollution
     * @param markers the list of the rectangles
     * @param point the data point itself
     * @param data the dataset the data come from
     */
    public static void displayRectangle(double x, double y, double pollution, List<Node> markers, DataPoint point, DataSet data) {
        Rectangle marker = new Rectangle();
        marker.setWidth(83.0);
        marker.setHeight(89.0);
        marker.setFill(getPollutionColor(pollution, true));
        marker.setCursor(Cursor.HAND);
        marker.toFront();
        marker.setLayoutX(x);
        marker.setLayoutY(y);
        generateToolPoint(point, marker, data, pollution);
        markers.add(marker);
    }

    /**
     * Shows tool points for the points on the map
     * @param point the data itself
     * @param marker the marker (shape)
     * @param data the dataset the data it comes from
     * @param pollution the level of pollution
     */
    public static void generateToolPoint(DataPoint point, Node marker, DataSet data, double pollution) {
        Tooltip tooltip = new Tooltip(String.format("%s (%s) \nPollution: %.2f %s \nx: %d\ny: %d\n GridCode: %d ", data.getPollutant(), data.getYear(), pollution, data.getUnits(), point.x(), point.y(), point.gridCode()));
        Tooltip.install(marker, tooltip);
        marker.setOnMouseClicked((e) -> {
            double average = DataHandler.getAveragePollutantLevelForArea(data.getPollutant(), point.gridCode());
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Pollution Data");
            alert.setHeaderText("Pollution Details for Selected Location");
            alert.setContentText(String.format("Pollutant: %s (%s)\nPollution Level: %.2f %s\nX: %d\nY: %d\nGridCode: %d\nAverage Pollution over the years: %.2f", data.getPollutant(), data.getYear(), pollution, data.getUnits(), point.x(), point.y(), point.gridCode(), average));
            alert.showAndWait();
        });
    }



    public static List<Circle> generateApiPoints(double longitude, double latitude, String pollutant, double value, String measurementTime, String locationName) {
        List<Circle> markers = new ArrayList<>();
        displayApiPoints(longitude, latitude, pollutant, value, measurementTime, locationName, markers);
        return markers;
    }

    /**
     * Display the actual points onto the given latitude and longitude of the api responses
     * @param longitude the longitude given
     * @param latitude the latitude given
     * @param pollutant the pollutant given
     * @param value the pollution level
     * @param measurementTime the time the measurement was taken at
     * @param locationName the location name
     * @param markers the markers of the api data
     */
    public static void displayApiPoints(double longitude, double latitude, String pollutant, double value, String measurementTime, String locationName, List<Circle> markers) {
        double xPixel = convertLonToPixel(longitude);
        double yPixel = convertLatToPixel(latitude);
        Circle marker = new Circle(7.5);
        marker.setFill(getPollutionColor(value, false));
        marker.setStroke(Color.BLACK);
        marker.setStrokeWidth(1.0);
        marker.setLayoutX(xPixel);
        marker.setLayoutY(yPixel);
        marker.setOnMouseClicked((e) -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Location Details");
            alert.setHeaderText(locationName);
            alert.setContentText(String.format("Latitude: %.4f\nLongitude: %.4f\n%s Level: %.2f µg/m³\nMeasured at: %s", latitude, longitude, pollutant, value, measurementTime));
            alert.showAndWait();
        });
        markers.add(marker);
    }

    /**
     * converts easting to pixel to be placed onto the screen
     * @param easting the easting given
     * @return Xpixel the x pixel
     */
    private static double convertEastingToPixel(double easting) {
        double normalized = (easting - MIN_EASTING) / 42903.0;
        return normalized * MAP_WIDTH;
    }

    /**
     * converts northing to pixel to be placed onto the screen
     * @param northing the northing given
     * @return ypixel the y pixel
     */
    private static double convertNorthingToPixel(double northing) {
        double normalized = (MAX_NORTHING - northing) / 24801.0;
        return normalized * MAP_HEIGHT;
    }

    /*
     * converts longitude to pixel to be placed onto the screen
     * @param lon the longitude
     * @return Xpixel the x pixel
     */
    private static double convertLonToPixel(double lon) {
        double normalized = (lon - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE);
        return (normalized * MAP_WIDTH) - 25; // slight offset to make points accurate
    }
    /*
     * converts latitude to pixel to be placed onto the screen
     * @param lat the latitude
     * @return ypixel the y pixel
     */
    private static double convertLatToPixel(double lat) {
        double normalized = (MAX_LATTITUDE - lat) / (MAX_LATTITUDE - MIN_LATTITUDE);
        return (normalized * MAP_HEIGHT) - 60; // slight offset to make points accurate
    }

    /*
     * Returns a color based on the pollution level given
     * lowers opacity of colour if the map is displaying a heatmap
     * @param pollution the level of pollution
     * @param heatMap whether it is a heatmap or not
     */
    private static Color getPollutionColor(double pollution, boolean heatMap) {
        double alpha = heatMap ? 0.05 : 1.0;
        if (pollution < 10.0) {
            return Color.rgb(0, 191, 0, alpha);
        } else if (pollution < 20.0) {
            return Color.rgb(255, 215, 0, alpha);
        } else if (pollution < 30.0) {
            return Color.rgb(255, 140, 0, alpha);
        } else if (pollution < 40.0) {
            return Color.rgb(220, 20, 60, alpha);
        } else {
            return pollution < 50.0 ? Color.rgb(139, 0, 0, alpha) : Color.rgb(128, 0, 128, alpha);
        }
    }

    /**
     * This hides/ unhides markers depending on the filters selected
     * @param markers the markers on the screen
     * @param green whether a marker is green
     * @param yellow whether a marker is yellow
     * @param orange whether a marker is orange
     * @param red whether a marker is red
     * @param crimson whether a marker is crimson
     * @param purple whether a marker is purple
     */
    public static void filterPollutionPoints(List<Node> markers, boolean green, boolean yellow, boolean orange, boolean red, boolean crimson, boolean purple) {
        Iterator<Node> iterator = markers.iterator();

        do {
            Node marker;
            Color color;
            do {
                if (!iterator.hasNext()) {
                    return;
                }

                marker = iterator.next();
                color = null;
                if (marker instanceof Circle circle) {
                    color = (Color) circle.getFill();
                }
            } while (color == null);

            // this determines whether the marker should still be displayed on the map
            boolean shouldBeVisible = green && color.equals(Color.rgb(0, 191, 0))
                    || yellow && color.equals(Color.rgb(255, 215, 0))
                    || orange && color.equals(Color.rgb(255, 140, 0))
                    || red && color.equals(Color.rgb(220, 20, 60))
                    || crimson && color.equals(Color.rgb(139, 0, 0))
                    || purple && color.equals(Color.rgb(128, 0, 128));

            marker.setVisible(shouldBeVisible);
        } while (true);
    }
}
