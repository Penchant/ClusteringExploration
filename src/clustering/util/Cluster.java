package clustering.util;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    public int id;

    public Centroid centroid;

    public List<AttributeSet> members;

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