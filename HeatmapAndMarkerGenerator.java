import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.List;


public class HeatmapAndMarkerGenerator     {

    private static final double MAX_EASTING = 553297;
    private static final double MIN_EASTING = 510394;
    private static final double MAX_NORTHING = 193305;
    private static final double MIN_NORTHING = 168504;
    private static final int MAP_WIDTH = 1781;
    private static final int MAP_HEIGHT = 1100;





    // Load pollution data and return a list of Circle markers
    public static List<Node> generateMarkers(Boolean heatMap, DataSet data) {

        List<Node> markers = new ArrayList<>();


        for (DataPoint point : data.getData()) {
            double easting = point.x();
            double northing = point.y();
            double pollution = point.value();
            double gridCode = point.gridCode();

            // Check if the point is inside the valid london range
            if (easting < MIN_EASTING || easting > MAX_EASTING || northing < MIN_NORTHING || northing > MAX_NORTHING) {
                continue;
            }

            // Convert adjusted coordinates to pixels
            double xPixel = convertEastingToPixel(easting);
            double yPixel = convertNorthingToPixel(northing);

            if (heatMap) {
                generateRectangle(xPixel, yPixel, pollution,markers, point, data, heatMap);
            }
            else {
                displayCircle(xPixel, yPixel, pollution,markers, point, data, heatMap);
            }



        }


        return markers;

    }

    public static void generateRectangle(double x, double y, double pollution, List <Node> markers, DataPoint point, DataSet data, boolean heatMap)
    {
        // Create pollution marker
        Rectangle marker = new Rectangle();
        marker.setWidth(83);
        marker.setHeight(89);
        marker.setFill(getPollutionColor(pollution, heatMap));
        marker.setCursor(Cursor.HAND);
        marker.toFront();

        // Set correct X and Y positions
        marker.setLayoutX(x);
        marker.setLayoutY(y);
        displayToolPoint(point, marker, data, pollution);
        markers.add(marker);




    }
    public static void displayToolPoint(DataPoint point, Node marker, DataSet data, double pollution) {
        // Tooltip for pollution data
        Tooltip tooltip = new Tooltip(
                String.format("%s (%s) \nPollution: %.2f %s \nX: %d\nY: %d\n GridCode: %d",
                        data.getPollutant(), data.getYear(), pollution, data.getUnits(), point.x(), point.y(), point.gridCode())
        );
        Tooltip.install(marker, tooltip);


        marker.setOnMouseClicked((MouseEvent event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pollution Data");
            alert.setHeaderText("Pollution Details for Selected Location");
            alert.setContentText(
                    String.format("Pollutant: %s (%s)\nPollution Level: %.2f %s\nX: %d\nY: %d\nGridCode: %d",
                            data.getPollutant(), data.getYear(), pollution, data.getUnits(), point.x(), point.y(), point.gridCode())
            );
            alert.showAndWait();
        });
    }
    public static void displayCircle(double x, double y, double pollution, List <Node> markers, DataPoint point, DataSet data, boolean heatMap)
    {
        // Create pollution marker
        Circle marker = new Circle(5);
        marker.setFill(getPollutionColor(pollution, heatMap));
        marker.setStroke(Color.BLACK);
        marker.setStrokeWidth(1);
        marker.setCursor(Cursor.HAND);
        marker.toFront();

        // Set correct X and Y positions
        marker.setLayoutX(x);
        marker.setLayoutY(y);

        displayToolPoint(point, marker, data, pollution);

        markers.add(marker);


    }

    public static List<Circle> generateApiPoints(double longitude, double latitude, String pollutant, double value, String measurementTime, String locationName)
    {
        List<Circle> markers = new ArrayList<>();
        displayApiPoints(longitude, latitude, pollutant, value, measurementTime, locationName, markers);
        return markers;

    }
    public static void displayApiPoints(double longitude, double latitude,String pollutant, double value, String measurementTime, String locationName, List<Circle> markers)
    {
        Double xPixel = convertLonToPixel(longitude);
        Double yPixel = convertLatToPixel(latitude);
        Circle marker = new Circle(7.5);
        marker.setFill(getPollutionColor(value, false));
        marker.setStroke(Color.BLACK);
        marker.setStrokeWidth(1.0);
        marker.setLayoutX(xPixel);
        marker.setLayoutY(yPixel);
        marker.setOnMouseClicked((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Location Details");
            alert.setHeaderText(locationName);
            alert.setContentText(String.format("Latitude: %.4f\nLongitude: %.4f\n%s Level: %.2f µg/m³\nMeasured at: %s", latitude, longitude, pollutant, value, measurementTime));
            alert.showAndWait();
        });
        markers.add(marker);
    }


    // converts easting to an x coordinate
    private static double convertEastingToPixel(double easting) {
        double normalized = (easting - MIN_EASTING) / (MAX_EASTING - MIN_EASTING);
        return normalized * MAP_WIDTH;
    }

    // converts the northing to a y coordinate
    private static double convertNorthingToPixel(double northing) {
        double normalized = (MAX_NORTHING - northing) / (MAX_NORTHING - MIN_NORTHING);
        return normalized * MAP_HEIGHT;
    }

    private static double convertLonToPixel(double lon) {
        lon += 1.0E-4;
        double normalized = (lon + 0.40653443) / 0.60858813;
        return normalized * 1781.0;
    }

    private static double convertLatToPixel(double lat) {
        lat += 1.0E-4;
        double normalized = (51.627741 - lat) / 0.23249500000000012;
        return normalized * 1100.0;
    }

    private static Color getPollutionColor(double pollution, boolean heatMap) {
        double alpha = heatMap ? 0.05 : 1; // heatmap is a more transparent color

        if (pollution < 10) return Color.rgb(0, 191, 0, alpha);       // Green
        else if (pollution < 20) return Color.rgb(255, 215, 0, alpha); // Yellow
        else if (pollution < 30) return Color.rgb(255, 140, 0, alpha); // Orange
        else if (pollution < 40) return Color.rgb(220, 20, 60, alpha); // Red
        else if (pollution < 50) return Color.rgb(139, 0, 0, alpha);   // Crimson
        else return Color.rgb(128, 0, 128, alpha);                     // Purple
    }


}

