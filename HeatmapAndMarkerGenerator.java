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
    private static final Color GREEN = new Color(0,0.75,0.1,0.1);
    private static final Color YELLOW = new Color(0.25,0.75,0.1,0.1);
    private static final Color ORANGE = new Color(0.5,0.75,0.1,0.1);
    private static final Color RED = new Color(0.75,0.25,0.1,0.1);
    private static final Color CRIMSON = new Color(0.5,0,0.1,0.1);
    private static final Color PURPLE = new Color(0.25,0,0.5,0.1);



    // Load pollution data and return a list of Circle markers
    public static List<Node> loadData(Boolean heatMap, DataSet data) {

        List<Node> markers = new ArrayList<Node>();

        System.out.println("Checking dataset: " + data.getPollutant() + " " + data.getYear());
        System.out.println("Total Data Points: " + data.getData().size());

        int validPoints = 0;

        for (DataPoint point : data.getData()) {
            double easting = point.x();
            double northing = point.y();
            double pollution = point.value();

            // Check if the point is inside the valid london range
            if (easting < MIN_EASTING || easting > MAX_EASTING || northing < MIN_NORTHING || northing > MAX_NORTHING) {
                continue;
            }

            // Convert adjusted coordinates to pixels
            double xPixel = convertEastingToPixel(easting);
            double yPixel = convertNorthingToPixel(northing);

            if (heatMap) {
                generateRectangle(xPixel, yPixel, pollution, data, markers);
            }
            else {
                generateCircle(xPixel, yPixel, pollution, data, markers);
            }



        }

        System.out.println("Valid Data Points Used: " + validPoints);

        return markers;

    }

    private static void generateRectangle(double x, double y, double pollution, DataSet data, List <Node> markers)
    {
        // Create pollution marker
        Rectangle marker = new Rectangle();
        marker.setWidth(83);
        marker.setHeight(89);
        marker.setFill(getPollutionColor(pollution));
        marker.setCursor(Cursor.HAND);
        marker.toFront();

        // Set correct X and Y positions
        marker.setLayoutX(x);
        marker.setLayoutY(y);


        // Tooltip for pollution data
        Tooltip tooltip = new Tooltip(
                String.format("%s (%s) \nPollution: %.2f %s",
                        data.getPollutant(), data.getYear(), pollution, data.getUnits()));
        Tooltip.install(marker, tooltip);
        markers.add(marker);



    }
    private static void generateCircle(double x, double y, double pollution, DataSet data, List <Node> markers)
    {
        // Create pollution marker
        Circle marker = new Circle(5);
        marker.setFill(getPollutionColor(pollution));
        marker.setStroke(Color.BLACK);
        marker.setStrokeWidth(1);
        marker.setCursor(Cursor.HAND);
        marker.toFront();

        // Set correct X and Y positions
        marker.setLayoutX(x);
        marker.setLayoutY(y);


        // Tooltip for pollution data
        Tooltip tooltip = new Tooltip(
                String.format("%s (%s) \nPollution: %.2f %s",
                        data.getPollutant(), data.getYear(), pollution, data.getUnits()));
        Tooltip.install(marker, tooltip);
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


    // This gives a colour to the points (can be configured later)
    private static Color getPollutionColor(double pollution) {
        if (pollution < 10) return GREEN;
        else if (pollution < 20) return YELLOW;
        else if (pollution < 30) return ORANGE;
        else if (pollution < 40) return RED;
        else if (pollution < 50) return CRIMSON;
        else return PURPLE;
    }

}

