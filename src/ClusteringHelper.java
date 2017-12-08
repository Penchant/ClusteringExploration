import java.util.List;
import java.util.stream.IntStream;

public class ClusteringHelper {

    /**
     * Returns the Cluster from clusters with the minimum distance to d1
     * @param d1 The set of attributes you want to get the min distance to from centroids
     * @param clusters The list of clusters
     * @return The cluster with the least distance
     */
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

    /**
     * Calculates the distance between two lists of attributes
     * @param d1 The first list of attributes
     * @param d2 The second list of attributes
     * @return The distance
     */
    public static double distance(List<Double> d1, List<Double> d2) {
        return Math.sqrt(IntStream.range(0, d1.size()).mapToDouble(i -> Math.pow(d1.get(i) - d2.get(i), 2)).sum());
    }

    /**
     * Checks if all the centroids are equal
     * @param oldCentroids THe list of old centroids
     * @param newCentroids The list of new centroids
     * @return Boolean of if all centroids are equal or not
     */
    public static boolean areAllCentroidsEqual(List<Centroid> oldCentroids, List<Centroid> newCentroids) {
        if (oldCentroids.size() != newCentroids.size()) return false;

        // Check if there are any centroids that are not equal
        for (int i = 0; i < oldCentroids.size(); i++) {
            // Use overloaded equals function for comparison
            if (!oldCentroids.get(i).equals(newCentroids.get(i))) {
                // Return false if they are not equal
                return false;
            }
        }

        // All centroids must match, return true
        return true;
    }
}
