package clustering.kmeans;

import clustering.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KMeans {
    private static Cluster[] clusters;
    private static int epochs = 0;

    /**
     * Runs the K Means algorithm
     * @param D The data to run it on
     * @param k The number of clusters desired
     * @return A list of the centroids of each cluster calculated
     */
    public static Map<Integer, List<AttributeSet>> run(List<AttributeSet> D, int k) {
        clusters = new Cluster[k];

        List<Centroid> lastCentroid = new ArrayList<>();

        Logger.info("Randomly initializing centroids");
        // Init mu_i ... mu_k randomly
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new Cluster(k);
            List<Double> vals = new ArrayList<>();
            for (int j = 0; j < D.get(0).size(); j++) {
                vals.add(Math.random());
            }

            clusters[i].centroid = new Centroid(vals);
        }

        // Repeat until no change in mu_i ... mu_k
        while (!ClusteringHelper.areAllCentroidsEqual(lastCentroid, ClusteringHelper.centroids(clusters))) {
            Logger.info("At least one centroid has changed - Updating");
            epochs++;
            lastCentroid = new ArrayList<>(ClusteringHelper.centroids(clusters));

            // Clear members
            for (Cluster c : clusters) {
                c.members.clear();
            }

            Logger.info("Clustering data to centroids");
            // for all x in D do
            for (AttributeSet x : D) {
                // c <- arg_min mu_j d(x_i, mu_j)
                Logger.info("Applying arg min on the data and each cluster");
                Cluster c = ClusteringHelper.argMin(x, clusters);

                // assign x_i to the cluster c
                Logger.info("Assigning to cluster");
                c.members.add(x);
            }

            Logger.info("Recalculating cluster members");
            // recalculate all mu_j based on new clusters
            // Fop each cluster
            for (Cluster c : clusters) {
                // For the members of the cluster
                List<Double> averages = new ArrayList<>();

                if (c.members.size() == 0) continue;

                for (int m = 0; m < c.members.get(0).size(); m++) {
                    averages.add(0d);
                }

                for (int j = 0; j < c.members.size(); j++) {
                    // For the values of the members
                    for (int m = 0; m < c.members.get(j).size(); m++) {
                        averages.set(m, averages.get(m) + c.members.get(j).attributes.get(m));
                    }
                }

                for (int i = 0; i < averages.size(); i++) {
                    averages.set(i, averages.get(i) / c.members.size());
                }

                // For the values of the centroid
                c.centroid.values = new AttributeSet(averages);
            }
        }

        Map<Integer, List<AttributeSet>> out = new HashMap<>();

        for (int i = 0; i < clusters.length; i++) {
            out.put(i, clusters[i].members);
        }

        Logger.important("K Means successfully finished in " + epochs + " epochs");
        return out;
    }

}
