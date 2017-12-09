import java.util.List;

public class DBScan {

    static List<Centroid> dbScan(List<AttributeSet> D, double epsilon, int minPts) {
        // currClustLbl <- 1
        int currClustLabel = 1;

        Cluster[] clusters;

        // for all p in Core do do
        for (int i = 0; i < D.size(); i++) {
            AttributeSet p = D.get(i);
            // If point is unclassified
            if (p.clusterId == -1) {
                currClustLabel += 1;
                p.clusterId = currClustLabel;
            }

            for (int j = 0; j < D.size(); j++) {
                if (i == j) continue;

                AttributeSet other = D.get(j);

                // For all in neighborhood
                if (ClusteringHelper.distance(p, other) < epsilon) {
                    // If other label is unknown
                    if (other.clusterId == -1) {
                        other.clusterId = currClustLabel;
                    }
                }
            }
        }
        return null;
    }

}
