package de.hsharz.gis.ui;

import java.awt.image.BufferedImage;
import java.util.Arrays;
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

    private static final int TOOLTIP_OFFSET = 10;

    private BorderPane       root;
    private ImageView        imageView;
    Tooltip                  tooltipValue;

    private IDW              idw;
    private double[][]       allValues;

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

        double[][] values = this.idw.getValues();
        long xMin = Arrays.stream(values).mapToLong(v -> Math.round(v[0])).min().getAsLong();
        long xMax = Arrays.stream(values).mapToLong(v -> Math.round(v[0])).max().getAsLong();
        long yMin = Arrays.stream(values).mapToLong(v -> Math.round(v[1])).min().getAsLong();
        long yMax = Arrays.stream(values).mapToLong(v -> Math.round(v[1])).max().getAsLong();

        int xMid = (int) (xMax - xMin + 200);
        int yMid = (int) (yMax - yMin + 200);
        System.out.println("Size: " + (xMid) + "x" + (yMid));

        BufferedImage bufferedImage = new BufferedImage(xMid, yMid, BufferedImage.TYPE_INT_ARGB);

        this.allValues = new double[xMid][yMid];
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (int x = 0; x < xMid; x++) {
            for (int y = 0; y < yMid; y++) {

                double valueAtPoint = this.idw.getValueOfPoint(x + xMin - 100.0, y + yMin - 100.0);
                this.allValues[x][y] = valueAtPoint;

                if (valueAtPoint < minValue) {
                    minValue = valueAtPoint;
                }
                if (valueAtPoint > maxValue) {
                    maxValue = valueAtPoint;
                }
            }
        }

        //        int[] rgb = { (255 << 24) + (255 << 16) + (255 << 8) + (255), // white
        //                (255 << 24) + (230 << 16) + (236 << 8) + (255), //
        //                (255 << 24) + (179 << 16) + (198 << 8) + (255), //
        //                (255 << 24) + (128 << 16) + (159 << 8) + (255), //
        //                (255 << 24) + (77 << 16) + (121 << 8) + (255), //
        //                (255 << 24) + (26 << 16) + (83 << 8) + (255), //
        //                (255 << 24) + (0 << 16) + (57 << 8) + (230), //
        //                (255 << 24) + (0 << 16) + (45 << 8) + (179), //
        //                (255 << 24) + (0 << 16) + (32 << 8) + (128), //
        //                (255 << 24) + (0 << 16) + (19 << 8) + (77) // dark blue
        //        };
        int[] rgb = { (255 << 24) + (255 << 16) + (255 << 8) + (255), // white
                (255 << 24) + (230 << 16) + (236 << 8) + (255), //
                (255 << 24) + (204 << 16) + (217 << 8) + (255), //
                (255 << 24) + (179 << 16) + (198 << 8) + (255), //
                (255 << 24) + (153 << 16) + (179 << 8) + (255), //
                (255 << 24) + (128 << 16) + (159 << 8) + (255), //
                (255 << 24) + (102 << 16) + (140 << 8) + (255), //
                (255 << 24) + (77 << 16) + (121 << 8) + (255), //
                (255 << 24) + (51 << 16) + (102 << 8) + (255), //
                (255 << 24) + (26 << 16) + (83 << 8) + (255), //
                (255 << 24) + (0 << 16) + (64 << 8) + (255), //
                (255 << 24) + (0 << 16) + (57 << 8) + (230), //
                (255 << 24) + (0 << 16) + (51 << 8) + (204), //
                (255 << 24) + (0 << 16) + (45 << 8) + (179), //
                //                        (255 << 24) + (0 << 16) + (38 << 8) + (153), //
                //                        (255 << 24) + (0 << 16) + (32 << 8) + (128), //
                //                        (255 << 24) + (0 << 16) + (26 << 8) + (102), //
                //                        (255 << 24) + (0 << 16) + (19 << 8) + (77) // dark blue
        };

        double schrittweite = (maxValue - minValue) / rgb.length;

        double[][] displayValues = new double[xMid][yMid];
        for (int x = 0; x < xMid; x++) {
            for (int y = 0; y < yMid; y++) {

                double calcValue = this.allValues[x][y];
                int indexOfColor = (int) ((calcValue - minValue) / schrittweite);

                if (indexOfColor >= rgb.length) {
                    indexOfColor = rgb.length - 1;
                }
                if (indexOfColor < 0) {
                    indexOfColor = 0;
                }
                displayValues[x][y] = rgb[indexOfColor];

                bufferedImage.setRGB(x, y, rgb[indexOfColor]);
            }
        }

        this.imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
    }

    public Pane getPane() {
        return this.root;
    }

}
