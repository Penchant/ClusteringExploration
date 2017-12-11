package clustering.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class DataLoader {

    // Loads the data from a csv or .data file and returns it as a List<List<Double>>
    public static List<AttributeSet> loadData(String path) {
        List<AttributeSet> out = new ArrayList<>();

        try {
            File file = new File(path);

            Scanner scanner = new Scanner(file);
            // Skip first line... who needs attribute labels??
            scanner.nextLine();

            // Read line by line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] values = line.split(",");

                // Get each comma separated value and add to our list
                AttributeSet samples = new AttributeSet();
                // Limit to not include the class label
                Stream.of(values).limit(values.length - 1).forEach(i -> samples.attributes.add(Double.parseDouble(i)));
                out.add(samples);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    /**
     * Returns a set of two square clusters
     * @return A set of two square clusters
     */
    public static List<AttributeSet>  genTwoSquaresSample() {
        List<AttributeSet> values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                List<Double> n = new ArrayList<>();
                List<Double> n2 = new ArrayList<>();
                n.add(i + 0.5);
                n.add(j + 0.5);
                n2.add(i + 50.5);
                n2.add(j + 50.5);
                values.add(new AttributeSet(n));
                values.add(new AttributeSet(n2));
            }
        }

        return values;
    }

    /**
     * Returns large circular clusters for each radius inputted
     * @return N Large clusters
     */
    public static List<AttributeSet>  genCircleSample(double ... radii) {
        List<AttributeSet> values = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            final double radians = Math.toRadians(i);
            Arrays.stream(radii).forEach(r -> {
                double x = Math.cos(radians);
                double y = Math.sin(radians);

                List<Double> n = new ArrayList<>();
                n.add(x * r);
                n.add(y * r);
                values.add(new AttributeSet(n));

            });
        }

        return values;
    }

}
