import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DBScan {

    /**
     * DB Scan clustering algorithm
     * @param D The data to run it on
     * @param epsilon The max distance between points
     * @param minPts The min number of points per cluster operation
     * @return A Map of each cluster id and all values that belong
     */
    static Map<Integer, List<AttributeSet>> dbScan(List<AttributeSet> D, double epsilon, int minPts) {
        // currClustLbl <- 1
        int currClustLabel = 1;

        // Loop through all elements in the set
        for (int i = 0; i < D.size(); i++) {
            AttributeSet p = D.get(i);
            // If point is unclassified
            if (p.clusterId == AttributeSet.unclassified) {
                // Find the neighbors
                List<AttributeSet> neighbors = ClusteringHelper.valuesWithinDistance(D, p, epsilon);

                // Label as outlier if the point does not have enough close neighbors
                if (neighbors.size() < minPts) {
                    p.clusterId = AttributeSet.outlier;
                } else {
                    // The conditions are satisfied, set the id to the current cluster id
                    p.clusterId = currClustLabel;
                    // Check which neighbors belong in this cluster id also
                    for (int j = 0; j < neighbors.size(); j++) {
                        AttributeSet neighbor = neighbors.get(j);
                        // Set neighbor's cluster id to current cluster id also if they are unclassified, then check their neighbors
                        if (neighbor.clusterId == AttributeSet.unclassified) {
                            neighbor.clusterId = currClustLabel;

                            List<AttributeSet> secondNeighbors = ClusteringHelper.valuesWithinDistance(D, neighbor, epsilon);

                            // Add the neighbors to this list we are iterating over to include them in our clusters if they
                            // satisfy the conditions
                            if (secondNeighbors.size() >= minPts) {
                                neighbors.addAll(secondNeighbors);
                            }
                        }

                        if (neighbor.clusterId == AttributeSet.outlier) {
                            neighbor.clusterId = currClustLabel;
                        }
                    }
                    currClustLabel++;
                }
            }
        }

        Logger.important("DB Scan finished successfully.  Calculating Centroids...");

        // Move all of our clusters to a map for easy access
        Map<Integer, List<AttributeSet>> clusters = new HashMap<>();

        for (AttributeSet s : D) {
            List<AttributeSet> c = clusters.get(s.clusterId);
            if (c == null) c = new ArrayList<>();

            c.add(s);
            clusters.put(s.clusterId, c);
        }

        // Calculate centroids (mostly for sanity check that the algorithm is working)
        for (int id : clusters.keySet()) {
            List<AttributeSet> attributes = clusters.get(id);
            List<Double> averages = new ArrayList<>();

            for (int m = 0; m < attributes.get(0).size(); m++) {
                averages.add(0d);
            }

            for (AttributeSet attribute : attributes) {
                // For the values of the members
                for (int m = 0; m < attribute.size(); m++) {
                    averages.set(m, averages.get(m) + attribute.attributes.get(m));
                }
            }

            for (int i = 0; i < averages.size(); i++) {
                averages.set(i, averages.get(i) / attributes.size());
            }

            averages.stream().forEach(i -> System.out.print(i + ", "));
            System.out.println();
        }

        return clusters;
    }

}
