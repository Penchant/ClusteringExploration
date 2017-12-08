import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class DataLoader {

    // Loads the data from a csv or .data file and returns it as a List<List<Double>>
    public static List<List<Double>> loadData(String path) {
        List<List<Double>> out = new ArrayList<>();

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
                List<Double> samples = new ArrayList<>();
                // Limit to not include the class label
                Stream.of(values).limit(values.length - 2).forEach(i -> {
                    samples.add(Double.parseDouble(i));
                });
                out.add(samples);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

}
