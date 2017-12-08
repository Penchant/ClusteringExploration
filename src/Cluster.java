import java.util.ArrayList;
import java.util.List;

class Cluster {
    Centroid centroid;

    List<List<Double>> members;

    public Cluster(int length) {
        centroid = new Centroid();
        members = new ArrayList<>();

        // Randomly init centroids
        for (int i = 0; i < length; i++) {
            centroid.add(Math.random());
        }
    }

    public Cluster() {
        centroid = new Centroid();
        members = new ArrayList<>();
    }
}