import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Logger.level = Logger.LoggingLevel.IMPORTANT;
        List<Centroid> centroids = KMeans.kmeans(genSamples(), 4);
        // List<Centroid> centroids = KMeans.kmeans(DataLoader.loadData("a1_raw.csv"), 8);
        centroids.stream().forEach(i -> {
            System.out.println(i);
        });
    }

    static List<AttributeSet>  genSamples() {
        List<AttributeSet> values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                List n = new ArrayList<>();
                List n2 = new ArrayList<>();
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
}
