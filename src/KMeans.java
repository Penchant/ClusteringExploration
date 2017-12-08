import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KMeans {
    public static Cluster[] clusters;

    public static List<Centroid> kmeans(List<List<Double>> D, int k) {
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
            clusters[i].members = new ArrayList<>();
        }

        // Repeat until no change in mu_i ... mu_k
        while (!ClusteringHelper.areAllCentroidsEqual(lastCentroid, centroids())) {
            Logger.info("At least one centroid has changed - Updating");
            lastCentroid = new ArrayList<>();
            lastCentroid.addAll(centroids());

            // Clear members
            for (Cluster c : clusters) {
                c.members.clear();
            }

            Logger.info("Clustering data to centroids");
            // for all x in D do
            for (List x : D) {
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
                    averages.add(c.members.get(0).get(m));
                }

                for (int j = 1; j < c.members.size(); j++) {
                    // For the values of the members
                    for (int m = 0; m < c.members.get(j).size(); m++) {
                        averages.set(m, averages.get(m) + c.members.get(j).get(m));
                    }
                }

                for (int i = 0; i < averages.size(); i++) {
                    averages.set(i, averages.get(i) / c.members.get(0).size());
                }

                // For the values of the centroid
                c.centroid.values = averages;
            }
        }

        return centroids();
    }

    public static List<Centroid> centroids() {
        return Stream.of(clusters).map(i -> i.centroid.copy()).collect(Collectors.toList());
    }

}
