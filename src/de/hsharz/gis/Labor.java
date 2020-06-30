package de.hsharz.gis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Labor {

    public static void main(final String[] args) throws FileNotFoundException {
        double[][] values = DataReader.readValues(new File(
                "C:\\Users\\Oliver\\Daten\\Uni\\HS Harz (Wernigerode)\\4. Semester\\Geoinformationssysteme und -dienste\\Labore\\Labor IIIb,IV\\IDW-Studierende\\xyw1.txt"));

        IDW idw = new IDW(values);
        System.out.println(idw.getValueOfPoint(32748635.25, 5743384.25));

        double xMin = Arrays.stream(values).mapToDouble(v -> v[0]).min().getAsDouble();
        double xMax = Arrays.stream(values).mapToDouble(v -> v[0]).max().getAsDouble();
        double yMin = Arrays.stream(values).mapToDouble(v -> v[1]).min().getAsDouble();
        double yMax = Arrays.stream(values).mapToDouble(v -> v[1]).max().getAsDouble();

        System.out.println("Size: " + (xMax - xMin) + "x" + (yMax - yMin));

    }

}
