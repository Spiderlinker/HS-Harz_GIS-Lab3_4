package de.hsharz.gis;

import java.awt.geom.Point2D;
import java.util.Objects;

public class IDW {

    /** Stelle im Array der X-Koordinate */
    public static final int X_INDEX     = 0;
    /** Stelle im Array der X-Koordinate */
    public static final int Y_INDEX     = 1;
    /** Stelle im Array des Messwertes */
    public static final int VALUE_INDEX = 2;

    /** Messwerte */
    private double[][]      values;

    /**
     * Erzeugen der IDW-Klasse mit Messwerten
     *
     * @param values Messwerte
     */
    public IDW(final double[][] values) {
        this.values = Objects.requireNonNull(values);
    }

    /**
     * Liefert den geschätzten Messwert an dem gegebenen Punkt, angegeben mit x und y.
     *
     * @param x X-Koordinate des Punktes
     * @param y Y-Koordinate des Punktes
     * @return geschätzter Messwert am gegebenen Punkt
     */
    public double getValueOfPoint(final double x, final double y) {
        // Prüfen, ob dieser Punkt bereits in Messwerten vorhanden ist
        int indexOfPoint = this.getIndexOfPoint(x, y);
        if (indexOfPoint != -1) {
            // Punkt bereits in Messwerten vorhanden, diesen Messwert zurückliefern
            return this.values[indexOfPoint][VALUE_INDEX];
        }
        // Punkt nicht in Messwerten vorhanden -> mittels IDW-Verfahren schätzen

        // Für das IDW-Verfahren werden die Distanzen jedes bereits
        // bestimmten Punktes zu dem gesuchten Punkt ermittelt.
        // Hieraus erfolgt eine Gewichtung der einzelnen Messpunkte und
        // und aus der Gewichtung (auf Basis der Entfernung) und dem
        // Messwert wird dann der Wert am gesuchten Messpunkt geschätzt
        double[] distances = this.calculateDistancesToPoint(x, y); // Distanzen ermitteln
        return this.calculateValueOfPoint(distances); // Wert schätzen
    }

    /**
     * Es wird der Index des Punktes in den Messwerten ermittelt.
     * Falls der gegebene Punkt nicht in den Messwerten vorhanden ist,
     * so wird -1 zurückgeliefert.
     *
     * @param x x-Koordinate des Punktes
     * @param y y-Koordinate des Punktes
     * @return Index des Messwertes des gegebenen Punktes,
     *         -1 falls nicht in Messwerten vorhanden
     */
    private int getIndexOfPoint(final double x, final double y) {
        for (int i = 0; i < this.values.length; i++) {
            // Falls x und y in Messwerte-Array gleich mit dem gegebenen Punkt ist,
            // wurde Punkt in Messwerten gefunden, den Index zurückgeben
            if (this.values[i][X_INDEX] == x && this.values[i][Y_INDEX] == y) {
                return i;
            }
        }
        // Punkt nicht gefunden, -1 zurückliefern
        return -1;
    }

    /**
     * Errechnung der Distanzen jedes Messwertpunktes zu dem gegebenen Punkt.
     *
     * @param x x-Koordinate des Punktes
     * @param y y-Koordinate des Punktes
     * @return Array mit Distanzen jedes Messwertpunktes zu gegebenem Punkt
     */
    private double[] calculateDistancesToPoint(final double x, final double y) {
        // Array enthält Distanzen jedes Messwertpunktes zu gegebenem Punkt
        double[] distances = new double[this.values.length];

        for (int i = 0; i < distances.length; i++) {
            // Distanz zwischen gegebenem Punkt und i-tem Punkt aus Messwerten errechnen
            distances[i] = Point2D.distance(this.values[i][X_INDEX], this.values[i][Y_INDEX], x, y);
        }
        return distances;
    }

    /**
     * Schätzt den Wert am gegebenen Punkt ({@code x, y}) mittels der
     * gegebenen Distanzen {@code distances} der Messwerte zu diesem Punkt
     *
     * @param distances Distanzen der Messwerte zu dem gegebenen Punkt
     * @return geschätzter Wert des gegebenen Punktes
     */
    private double calculateValueOfPoint(final double[] distances) {

        // Der Zähler enthält die Summe "Messwert / Distanz des Messwertes zu gesuchtem Punkt"
        double valueOverDistanceSum = 0;
        // Der Nenner enthält die Inverse Distanz des Messwertes zu dem gesuchten Punkt
        double inverseDistanceSum = 0;

        // Den Zähler und Nenner ermitteln. Beide Male werden Summen gebildet, deshalb
        // jedesmal komplett über die Messwerte iterieren
        for (int i = 0; i < this.values.length; i++) {
            // Messwert[i] / Distanz[i] ( Messwert[i] geteilt durch Distanz von Messwert[i] zu gesuchtem Punkt
            valueOverDistanceSum += (this.values[i][VALUE_INDEX] / distances[i]);
            // 1 / Distanz[i] (Inverse Distanz)
            inverseDistanceSum += (1 / distances[i]);
        }

        // Abschließend den Zähler (Summe "Messwert / Distanz des Messwertes zu gesuchtem Punkt")
        // durch den Nenner (Summe "Inverse Distanz des Messwertes zu dem gesuchten Punkt") teilen
        return valueOverDistanceSum / inverseDistanceSum;
    }

}
