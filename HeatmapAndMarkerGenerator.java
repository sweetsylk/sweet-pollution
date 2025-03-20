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

public class HeatmapAndMarkerGenerator {
    private static final double MAX_EASTING = 553297.0;
    private static final double MIN_EASTING = 510394.0;
    private static final double MAX_NORTHING = 193305.0;
    private static final double MIN_NORTHING = 168504.0;
    private static final int MAP_WIDTH = 1781;
    private static final int MAP_HEIGHT = 1100;

    public HeatmapAndMarkerGenerator() {
    }

    public static List<Node> generateMarkers(Boolean heatMap, DataSet data) {
        List<Node> markers = new ArrayList<>();

        for (DataPoint point : data.getData()) {
            double easting = point.x();
            double northing = point.y();
            double pollution = point.value();
            if (!(easting < 510394.0) && !(easting > 553297.0) && !(northing < 168504.0) && !(northing > 193305.0)) {
                double xPixel = convertEastingToPixel(easting);
                double yPixel = convertNorthingToPixel(northing);
                if (heatMap) {
                    generateRectangle(xPixel, yPixel, pollution, markers, point, data, heatMap);
                } else {
                    displayCircle(xPixel, yPixel, pollution, markers, point, data, heatMap);
                }
            }
        }

        return markers;
    }

    public static void generateRectangle(double x, double y, double pollution, List<Node> markers, DataPoint point, DataSet data, boolean heatMap) {
        Rectangle marker = new Rectangle();
        marker.setWidth(83.0);
        marker.setHeight(89.0);
        marker.setFill(getPollutionColor(pollution, heatMap));
        marker.setCursor(Cursor.HAND);
        marker.toFront();
        marker.setLayoutX(x);
        marker.setLayoutY(y);
        generateToolPoint(point, marker, data, pollution);
        markers.add(marker);
    }

    public static void generateToolPoint(DataPoint point, Node marker, DataSet data, double pollution) {
        Tooltip tooltip = new Tooltip(String.format("%s (%s) \nPollution: %.2f %s \nx: %d\ny: %d\n GridCode: %d ", data.getPollutant(), data.getYear(), pollution, data.getUnits(), point.x(), point.y(), point.gridCode()));
        Tooltip.install(marker, tooltip);
        marker.setOnMouseClicked((event) -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Pollution Data");
            alert.setHeaderText("Pollution Details for Selected Location");
            alert.setContentText(String.format("Pollutant: %s (%s)\nPollution Level: %.2f %s\nX: %d\nY: %d\nGridCode: %d", data.getPollutant(), data.getYear(), pollution, data.getUnits(), point.x(), point.y(), point.gridCode()));
            alert.showAndWait();
        });
    }

    public static void displayCircle(double x, double y, double pollution, List<Node> markers, DataPoint point, DataSet data, boolean heatMap) {
        Circle marker = new Circle(5.0);
        marker.setFill(getPollutionColor(pollution, heatMap));
        marker.setStroke(Color.BLACK);
        marker.setStrokeWidth(1.0);
        marker.setCursor(Cursor.HAND);
        marker.toFront();
        marker.setLayoutX(x);
        marker.setLayoutY(y);
        generateToolPoint(point, marker, data, pollution);
        markers.add(marker);
    }

    public static List<Circle> generateApiPoints(double longitude, double latitude, String pollutant, double value, String measurementTime, String locationName) {
        List<Circle> markers = new ArrayList<>();
        displayApiPoints(longitude, latitude, pollutant, value, measurementTime, locationName, markers);
        return markers;
    }

    public static void displayApiPoints(double longitude, double latitude, String pollutant, double value, String measurementTime, String locationName, List<Circle> markers) {
        double xPixel = convertLonToPixel(longitude);
        double yPixel = convertLatToPixel(latitude);
        Circle marker = new Circle(7.5);
        marker.setFill(getPollutionColor(value, false));
        marker.setStroke(Color.BLACK);
        marker.setStrokeWidth(1.0);
        marker.setLayoutX(xPixel);
        marker.setLayoutY(yPixel);
        marker.setOnMouseClicked((event) -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Location Details");
            alert.setHeaderText(locationName);
            alert.setContentText(String.format("Latitude: %.4f\nLongitude: %.4f\n%s Level: %.2f µg/m³\nMeasured at: %s", latitude, longitude, pollutant, value, measurementTime));
            alert.showAndWait();
        });
        markers.add(marker);
    }

    private static double convertEastingToPixel(double easting) {
        double normalized = (easting - 510394.0) / 42903.0;
        return normalized * 1781.0;
    }

    private static double convertNorthingToPixel(double northing) {
        double normalized = (193305.0 - northing) / 24801.0;
        return normalized * 1100.0;
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

            boolean shouldBeVisible = green && color.equals(Color.rgb(0, 191, 0)) || yellow && color.equals(Color.rgb(255, 215, 0)) || orange && color.equals(Color.rgb(255, 140, 0)) || red && color.equals(Color.rgb(220, 20, 60)) || crimson && color.equals(Color.rgb(139, 0, 0)) || purple && color.equals(Color.rgb(128, 0, 128));
            marker.setVisible(shouldBeVisible);
        } while (true);
    }
}
