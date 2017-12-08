import java.util.List;
import java.util.stream.IntStream;

public class ClusteringHelper {

    public static Cluster argMin(List<Double> d1, Cluster[] clusters) {
        Cluster cluster = null;
        double minDistance = Double.MAX_VALUE;

        for (int j = 0; j < clusters.length; j++) {
            for (int i = 0; i < d1.size(); i++) {
                double distance = distance(d1, clusters[j].centroid.values);
                if (distance < minDistance) {
                    minDistance = distance;
                    cluster = clusters[j];
                }
            }
        }

        return cluster;
    }

    public static double distance(List<Double> d1, List<Double> d2) {
        return Math.sqrt(IntStream.range(0, d1.size()).mapToDouble(i -> Math.pow(d1.get(i) - d2.get(i), 2)).sum());
    }

    public static boolean areAllCentroidsEqual(List<Centroid> oldCentroids, List<Centroid> newCentroids) {
        if (oldCentroids.size() != newCentroids.size()) return false;

        for (int i = 0; i < oldCentroids.size(); i++) {
            if (!oldCentroids.get(i).equals(newCentroids.get(i))) {
                return false;
            }
        }

        return true;
    }
}
