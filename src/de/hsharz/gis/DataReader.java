package de.hsharz.gis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Diese Klasse ist eine Hilfsklasse zum Laden der Messwertdaten.
 * Sie bietet eine Methode {@link #readValues(File)} an,
 * welche die angegebene Datei einliest und die darin enthaltenen
 * Messwerte gespeichert in einem 2d-Array zurückliefert.
 *
 * @author Oliver Lindemann
 * u33873@hs-harz.de
 * Matrikel-Nr.: 26264
 */
public class DataReader {

    private DataReader() {
        // Utility class
    }

    /**
     * Diese Methode liefert die in der angegebenen Datei {@code file} enthaltenen Messwerte in einem 2d-Array.
     * Die Datei muss dabei nach folgendem Schema aufgebaut sein:
     *      <Anzahl Messwerte [Int]>
     *      <Bezeichnung X-Koordinate [String]>    <Bezeichnung Y-Koordinate [String]>    <Bezeichnung Messwert [String]>
     *      <Datensatz1_X-Koordinate [Int/Double]> <Datensatz1_Y-Koordinate [Int/Double]> <Datensatz1_Messwert [Int/Double]>
     *      <Datensatz1_X-Koordinate [Int/Double]> <Datensatz2_Y-Koordinate [Int/Double]> <Datensatz2_Messwert [Int/Double]>
     *                  ...                                     ...                                 ...
     *      <Datensatzn_X-Koordinate [Int/Double]> <Datensatzn_Y-Koordinate [Int/Double]> <Datensatzn_Messwert [Int/Double]>
     *
     * Falls das vorgegebene Dateiformat nicht eingehalten wird,
     * kann es zu fehlerhaftem Verhalten bzw. zur Erzeugung eines Fehlers kommen.
     *
     * Der Rückgabetyp ist ein 2d-Array. Dieses ist nach folgender Struktur aufgebaut:
     * double[DatensatzX][{X-Koordinate, Y-Koordinate, Messwert3}]
     *
     * @param file Datei mit Messwerten, die eingelesen werden soll
     * @return Eingelesene Messwerte
     * @throws FileNotFoundException Fehler bei nicht gefundener Datei
     */
    public static double[][] readValues(final File file) throws FileNotFoundException {
        double[][] values = null;

        // try-with-resources zum automatischen Schließen des Scanners
        try (Scanner scanner = new Scanner(file)) {
            // In der ersten Zeile steht die Anzahl der Datensätze
            // Array mit dieser Größe initialisieren
            values = new double[scanner.nextInt()][3];

            // Ausgabe der Messwertbezeichnungen in zweiter Zeile
            System.out.println("Reading " + values.length + " values with: " //
                    + scanner.next() + ", " //
                    + scanner.next() + ", " //
                    + scanner.next());

            // Einlesen der Messwerte
            for (int i = 0; i < values.length; i++) {
                // X-, Y-Koordinate und Messwert lesen und an die i-te Stelle des values-Arrays schreiben
                values[i] = new double[] { scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble() };
            }
        }

        // Rückgabe der eingelesenen Messwerte
        return values;
    }

}
