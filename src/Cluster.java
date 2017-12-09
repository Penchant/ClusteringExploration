import java.util.ArrayList;
import java.util.List;

class Cluster {
    int id;

    Centroid centroid;

    List<AttributeSet> members;

    Cluster(int length) {
        this.id = id;
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

    public Cluster(int length, int id) {
        this.id = id;
        centroid = new Centroid();
        members = new ArrayList<>();
    }
}