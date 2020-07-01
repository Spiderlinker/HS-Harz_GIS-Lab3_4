package de.hsharz.gis.ui;

import java.awt.image.BufferedImage;
import java.util.Objects;

import de.hsharz.gis.IDW;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class IDWView {

    private static final int   TOOLTIP_OFFSET = 10;
    public static final double IMAGE_OFFSET   = 100.0;

    private BorderPane         root;
    private ImageView          imageView;
    private Tooltip            tooltipValue;

    private IDW                idw;
    private BufferedImage      calculatedImage;

    /*
     * Werte zur Darstellung des Bildes
     */
    private double[][]         allValues;
    private long               minXCoordinate = Long.MAX_VALUE;     // initialized with default value
    private long               minYCoordinate = Long.MAX_VALUE;     // initialized with default value
    private long               maxXCoordinate = Long.MIN_VALUE;     // initialized with default value
    private long               maxYCoordinate = Long.MIN_VALUE;     // initialized with default value

    private double             minValue       = Double.MAX_VALUE;
    private double             maxValue       = Double.MIN_VALUE;

    private int                imageWidth;
    private int                imageHeight;

    /** Enthält RGB Farbwerte zur Darstellung der unterschiedlichen Werte im Bild */
    private int[]              rgbValues      = {                   //
            (255 << 24) + (255 << 16) + (255 << 8) + (255),         // white
            (255 << 24) + (230 << 16) + (236 << 8) + (255),         //
            (255 << 24) + (204 << 16) + (217 << 8) + (255),         //
            (255 << 24) + (179 << 16) + (198 << 8) + (255),         // ...
            (255 << 24) + (153 << 16) + (179 << 8) + (255),         // from
            (255 << 24) + (128 << 16) + (159 << 8) + (255),         // white
            (255 << 24) + (102 << 16) + (140 << 8) + (255),         // to
            (255 << 24) + (77 << 16) + (121 << 8) + (255),          // dark
            (255 << 24) + (51 << 16) + (102 << 8) + (255),          // blue
            (255 << 24) + (26 << 16) + (83 << 8) + (255),           // ...
            (255 << 24) + (0 << 16) + (64 << 8) + (255),            //
            (255 << 24) + (0 << 16) + (57 << 8) + (230),            //
            (255 << 24) + (0 << 16) + (51 << 8) + (204),            //
            (255 << 24) + (0 << 16) + (45 << 8) + (179)             // dark blue
    };

    public IDWView(final IDW idw) {
        this.idw = Objects.requireNonNull(idw);

        this.createWidgets();
        this.setupInteractions();
        this.addWidgets();

        this.calculateImage();
    }

    private void createWidgets() {
        this.root = new BorderPane();
        this.imageView = new ImageView();

        this.tooltipValue = new Tooltip();
        this.tooltipValue.setFont(new Font(16));
    }

    private void setupInteractions() {

        // Tooltip beim Betreten des Bildes anzeigen
        this.imageView.setOnMouseEntered(
                e -> this.tooltipValue.show(this.imageView, e.getScreenX(), e.getScreenY() + TOOLTIP_OFFSET));

        // Wert von Position der Maus mittels Tooltip anzeigen
        this.imageView.setOnMouseMoved(event -> {
            this.tooltipValue.setAnchorX(event.getScreenX());
            this.tooltipValue.setAnchorY(event.getScreenY() + TOOLTIP_OFFSET);

            int x = (int) event.getX();
            int y = (int) event.getY();
            this.tooltipValue.setText(String.format("Wert: %.2f", this.allValues[x][y]));
        });

        // Tooltip verstecken, sobald Bild verlassen wird
        this.imageView.setOnMouseExited(e -> this.tooltipValue.hide());
    }

    private void addWidgets() {
        this.root.setCenter(new ScrollPane(this.imageView));
    }

    private void calculateImage() {

        this.calculateMinMaxValues();
        this.calculateImageSize();

        this.calculatedImage = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_INT_ARGB);
        this.allValues = new double[this.imageWidth][this.imageHeight];

        this.calculateAllPixelValues();
        this.calculateAllPixelColors();

        // Errechnetes Bild darstellen
        this.imageView.setImage(SwingFXUtils.toFXImage(this.calculatedImage, null));
    }

    /**
     * Sucht und berechnet die minimalen und maximalen
     * x- und y-Werte, die eingelesen wurden
     */
    private void calculateMinMaxValues() {
        double[][] valueEntries = this.idw.getValues();

        for (double[] entry : valueEntries) {
            long xCoordinate = Math.round(entry[IDW.X_INDEX]);
            long yCoordinate = Math.round(entry[IDW.Y_INDEX]);
            double value = entry[IDW.VALUE_INDEX];

            // Calculate min- and max X-Coordinate
            if (xCoordinate < this.minXCoordinate) {
                this.minXCoordinate = xCoordinate;
            }
            if (xCoordinate > this.maxXCoordinate) {
                this.maxXCoordinate = xCoordinate;
            }

            // Calculate min- and max Y-Coordinate
            if (yCoordinate < this.minYCoordinate) {
                this.minYCoordinate = yCoordinate;
            }
            if (yCoordinate > this.maxYCoordinate) {
                this.maxYCoordinate = yCoordinate;
            }

            // Calculate min- and max Value
            if (value < this.minValue) {
                this.minValue = value;
            }
            if (value > this.maxValue) {
                this.maxValue = value;
            }
        }

        System.out.println("Min x-Coor: " + this.minXCoordinate);
        System.out.println("Max x-Coor: " + this.maxXCoordinate);
        System.out.println("Min y-Coor: " + this.minYCoordinate);
        System.out.println("Max y-Coor: " + this.maxYCoordinate);
        System.out.println("Min Val: " + this.minValue);
        System.out.println("Max Val: " + this.maxValue);
    }

    private void calculateImageSize() {
        // ImageOffset soll auf beide Seiten addiert werden
        // Auf beiden Seiten soll nicht nur vom kleinsten eingelesen x-Wert bis zum größten
        // eingelesen x-Wert dargestellt werden, sondern noch zusätzlich ein Bereich von jeweils 100px
        this.imageWidth = (int) (this.maxXCoordinate - this.minXCoordinate + (IMAGE_OFFSET * 2));
        this.imageHeight = (int) (this.maxYCoordinate - this.minYCoordinate + (IMAGE_OFFSET * 2));
        System.out.println("Size: " + (this.imageWidth) + "x" + (this.imageHeight));
    }

    private void calculateAllPixelValues() {
        for (int x = 0; x < this.imageWidth; x++) {
            for (int y = 0; y < this.imageHeight; y++) {

                double valueAtPoint = this.idw.getValueOfPoint( //
                        x + this.minXCoordinate - IMAGE_OFFSET, // x-Koordinate wiederherstellen
                        y + this.minYCoordinate - IMAGE_OFFSET); // y-Koordinate wiederherstellen
                this.allValues[x][y] = valueAtPoint;
            }
        }
    }

    private void calculateAllPixelColors() {

        double schrittweite = (this.maxValue - this.minValue) / this.rgbValues.length;

        for (int x = 0; x < this.imageWidth; x++) {
            for (int y = 0; y < this.imageHeight; y++) {

                double calcValue = this.allValues[x][y];
                int indexOfColor = (int) ((calcValue - this.minValue) / schrittweite);

                // Korrigieren Werte, falls zu groß, hoechsten Wert annehmen
                if (indexOfColor >= this.rgbValues.length) {
                    indexOfColor = this.rgbValues.length - 1;
                }
                // falls zu klein, kleinsten Wert annehmen
                if (indexOfColor < 0) {
                    indexOfColor = 0;
                }

                // errechneten Blauwert (Index des Blauwertes im rgbValues-Array) in das Bild einfuegen
                this.calculatedImage.setRGB(x, y, this.rgbValues[indexOfColor]);
            }
        }
    }

    public Pane getPane() {
        return this.root;
    }

}
