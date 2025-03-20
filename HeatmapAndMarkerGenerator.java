import javafx.scene.Node;
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
    public static List<Node> loadData(Boolean heatMap, DataSet data) {

        List<Node> markers = new ArrayList<>();

        // System.out.println("Checking dataset: " + data.getPollutant() + " " + data.getYear());
        // System.out.println("Total Data Points: " + data.getData().size());

        int validPoints = 0;

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
                generateCircle(xPixel, yPixel, pollution,markers, point, data, heatMap);
            }



        }

        //System.out.println("Valid Data Points Used: " + validPoints);

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
        generateToolPoint(point, marker, data, pollution);
        markers.add(marker);




    }
    public static void generateToolPoint(DataPoint point, Node marker,  DataSet data, double pollution) {
        // Tooltip for pollution data
        Tooltip tooltip = new Tooltip(
                String.format("%s (%s) \nPollution: %.2f %s \nx: %d\ny: %d\n GridCode: %d ",
                        data.getPollutant(), data.getYear(), pollution, data.getUnits(), point.x(),point.y(), point.gridCode()));
        Tooltip.install(marker, tooltip);
    }
    public static void generateCircle(double x, double y, double pollution, List <Node> markers, DataPoint point, DataSet data, boolean heatMap)
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

        generateToolPoint(point, marker, data, pollution);

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


    private static Color getPollutionColor(double pollution, boolean heatMap) {
        double alpha = heatMap ? 0.05 : 1; // heatmap is a more transparent color

        if (pollution < 10) return Color.rgb(0, 191, 0, alpha);       // Green
        else if (pollution < 20) return Color.rgb(255, 215, 0, alpha); // Yellow
        else if (pollution < 30) return Color.rgb(255, 140, 0, alpha); // Orange
        else if (pollution < 40) return Color.rgb(220, 20, 60, alpha); // Red
        else if (pollution < 50) return Color.rgb(139, 0, 0, alpha);   // Crimson
        else return Color.rgb(128, 0, 128, alpha);                     // Purple
    }
    
    // filters pollution points based on selected colors
    public static void filterPollutionPoints(List<Node> markers, boolean green, boolean yellow, boolean orange, boolean red, boolean crimson, boolean purple) {
        for (Node marker : markers) {
            Color color = null;

            // check if marker is a circle
            if (marker instanceof Circle) {
                Circle circle = (Circle) marker;
                color = (Color) circle.getFill();
            }
        
            if (color != null) {
                // check if the color matches a selected filter
                boolean shouldBeVisible =
                        (green && color.equals(Color.rgb(0, 191, 0))) ||        // green
                        (yellow && color.equals(Color.rgb(255, 215, 0))) ||      // yellow
                        (orange && color.equals(Color.rgb(255, 140, 0))) ||      // orange
                        (red && color.equals(Color.rgb(220, 20, 60))) ||         // red
                        (crimson && color.equals(Color.rgb(139, 0, 0))) ||       // crimson
                        (purple && color.equals(Color.rgb(128, 0, 128)));        // purple

                marker.setVisible(shouldBeVisible); // show or hide marker
            }
        }
    }
}

