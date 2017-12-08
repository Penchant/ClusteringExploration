import java.util.List;

class Cluster {
    Centroid centroid;

    List<List<Double>> members;

    Cluster(int length) {
        centroid = new Centroid();

        // Randomly init centroids
        for (int i = 0; i < length; i++) {
            centroid.add(Math.random());
        }
    }
}