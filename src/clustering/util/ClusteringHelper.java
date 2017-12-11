package clustering.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ClusteringHelper {

    /**
     * Returns the Cluster from clusters with the minimum distance to d1
     *
     * @param d1       The set of attributes you want to get the min distance to from centroids
     * @param clusters The list of clusters
     * @return The cluster with the least distance
     */
    public static Cluster argMin(AttributeSet d1, Cluster[] clusters) {
        Cluster cluster = null;
        double minDistance = Double.MAX_VALUE;

        for (Cluster cluster1 : clusters) {
            double distance = distance(d1, cluster1.centroid.values);
            if (distance < minDistance) {
                minDistance = distance;
                cluster = cluster1;
            }
        }

        return cluster;
    }

    /**
     * Calculates the distance between two lists of attributes
     *
     * @param d1 The first list of attributes
     * @param d2 The second list of attributes
     * @return The distance
     */
    public static double distance(AttributeSet d1, AttributeSet d2) {
        return Math.sqrt(IntStream.range(0, d1.size()).mapToDouble(i -> Math.pow(d1.attributes.get(i) - d2.attributes.get(i), 2)).sum());
    }

    /**
     * Checks if all the centroids are equal
     *
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

    /**
     * Calculates and returns a list of all the centroids of the clusters
     *
     * @return A list of all the centroids of the clusters
     */
    public static List<Centroid> centroids(Cluster[] clusters) {
        return Stream.of(clusters).map(i -> i.centroid.copy()).collect(Collectors.toList());
    }

    /**
     * Calculates all the values in D within distance epsilon from AttributeSet p
     *
     * @param D       The list of AttributeSets
     * @param p       The AttributeSet you want all elements within the distance from
     * @param epsilon The distance
     * @return A list of all values in D within distance epsilon from AttributeSet p
     */
    public static List<AttributeSet> valuesWithinDistance(List<AttributeSet> D, AttributeSet p, double epsilon) {
        List<AttributeSet> valuesWithinDistance = new ArrayList<>();
        for (AttributeSet set : D) {
            if (ClusteringHelper.distance(p, set) < epsilon) {
                valuesWithinDistance.add(set);
            }
        }
        return valuesWithinDistance;
    }

    /**
     * Computes a rand index double signifying how similar to clusterings are
     *
     * @param clusters  The first cluster
     * @param clusters2 The second cluster
     * @return The rand index value
     */
    public static double computeRandIndex(Map<Integer, List<AttributeSet>> clusters, Map<Integer, List<AttributeSet>> clusters2) {
        // Calculate averages, store them in a list <cluster id from first list, cluster id from second, distance>
        List<List<Double>> averageDistances = new ArrayList<>();
        for (int id : clusters.keySet()) {
            for (int id2 : clusters2.keySet()) {
                List<AttributeSet> cluster1 = clusters.get(id);
                List<AttributeSet> cluster2 = clusters2.get(id2);

                double averageDistance = 0;
                for (AttributeSet s1 : cluster1) {
                    for (AttributeSet s2 : cluster2) {
                        averageDistance += ClusteringHelper.distance(s1, s2);
                    }
                }
                averageDistance /= (cluster1.size() * cluster2.size());
                averageDistances.add(Arrays.stream(new double[]{id, id2, averageDistance}).boxed().collect(Collectors.toList()));
            }
        }

        // Get total yes and no's (matches or not)
        int yes = 0;
        int no = 0;

        // Get cluster size diff
        int numK;
        if (clusters.keySet().size() > clusters2.keySet().size()) {
            numK = clusters2.keySet().size();
        } else {
            numK = clusters.keySet().size();
        }

        // Sort the array for minimum distances (remember, distance is 3rd element ie. list index 2)
        averageDistances.sort((n, m) -> n.get(2) < m.get(2) ? -1 : 1);

        // Get all pairs that can't be similar due to size diff
        List<List<Double>> pairsNotIncluded = averageDistances.stream().skip(numK).collect(Collectors.toList());

        // Get all pairs that are probably similar according to distance sort
        averageDistances = averageDistances.stream().limit(numK).collect(Collectors.toList());

        // Only add to no if the pairs don't match any of the chosen min dist pairs
        for (List<Double> noPairs : pairsNotIncluded) {
            boolean isInFirstSet = false;
            boolean isInSecondSet = false;
            for (List<Double> p : averageDistances) {
                if (p.get(0) == noPairs.get(0)) {
                    isInFirstSet = true;
                } else if (p.get(1) == noPairs.get(1)) {
                    isInSecondSet = true;
                }
            }

            // If cluster size is different, add the remaining to no
            if (isInFirstSet && !isInSecondSet) {
                no += clusters.get(noPairs.get(0)).size();
            } else if (isInSecondSet && !isInFirstSet) {
                no += clusters2.get(noPairs.get(1)).size();
            }
        }

        // Add to yes/no for elements that are in both (yes) and elements that don't match (no)
        for (List<Double> pairs : averageDistances) {
            List<AttributeSet> cluster1 = clusters.get((int) (pairs.get(0).doubleValue()));
            List<AttributeSet> cluster2 = clusters2.get((int) (pairs.get(1).doubleValue()));

            if (cluster1.size() > cluster2.size()) {
                no += cluster1.size() - cluster2.size();
                for (int i = 0; i < cluster2.size(); i++) {
                    int y = 0;
                    for (int j = 0; j < cluster1.size(); j++) {
                        if (cluster1.get(i).equals(cluster2.get(i))) {
                            y++;
                        }
                    }
                    yes += y;
                    no += (cluster2.size() - y);
                }
            } else {
                no += cluster2.size() - cluster1.size();
                for (int i = 0; i < cluster1.size(); i++) {
                    int y = 0;
                    for (int j = 0; j < cluster2.size(); j++) {
                        if (cluster1.get(i).equals(cluster2.get(i))) {
                            y++;
                        }
                    }
                    yes += y;
                    no += (cluster1.size() - y);
                }
            }
        }

        return (double) yes / (yes + no);
    }

    /**
     * Computes a rand index double signifying how correctly a data set was clustered
     *
     * @param clusters The first cluster
     * @return The rand index value
     */
    public static double computeRandIndex(Map<Integer, List<AttributeSet>> clusters) {
        List<RandData> randList = new ArrayList<>();
        int id = 1;
        for (Map.Entry<Integer, List<AttributeSet>> pair : clusters.entrySet()) {
            for (AttributeSet set : pair.getValue()) {
                RandData data = new RandData();
                data.data = set;
                data.foundCluster = pair.getKey();
                data.id = id;
                ++id;
                randList.add(data);
            }
        }

        int truePositive = 0;
        int trueNegative = 0;

        for (RandData dataPoint : randList) {
            int inCluster = 0;
            Map<Integer, Integer> wrongClusters = new HashMap<>();
            double totalInDistance = 0;
            Map<Integer, Double> wrongDistance = new HashMap<>();
            for (RandData comparePoint : randList) {
                if (dataPoint.id != comparePoint.id) {
                    if (dataPoint.foundCluster == comparePoint.foundCluster) {
                        double distance = attributeDistance(dataPoint, comparePoint);
                        totalInDistance += distance;
                        ++inCluster;
                    } else {
                        double distance = attributeDistance(dataPoint, comparePoint);
                        if (!wrongDistance.containsKey(comparePoint.foundCluster))
                            wrongDistance.put(comparePoint.foundCluster, 0.0);
                        if (!wrongClusters.containsKey(comparePoint.foundCluster))
                            wrongClusters.put(comparePoint.foundCluster, 0);
                        wrongDistance.put(comparePoint.foundCluster, wrongDistance.get(comparePoint.foundCluster) + distance);
                        wrongClusters.put(comparePoint.foundCluster, wrongClusters.get(comparePoint.foundCluster) + 1);
                    }
                }
            }
            double averageInCluster = totalInDistance / inCluster;
            boolean foundBetterCluster = false;
            for (Integer key : wrongDistance.keySet()) {
                double averageOutCluster = wrongDistance.get(key) / wrongClusters.get(key);
                if (averageOutCluster < averageInCluster) {
                    dataPoint.trueCluster = key;
                    foundBetterCluster = true;
                }
            }
            if (!foundBetterCluster)
                dataPoint.trueCluster = dataPoint.foundCluster;
        }

        for (RandData dataPoint : randList) {
            if (dataPoint.trueCluster == dataPoint.foundCluster)
                ++truePositive;
            else
                ++trueNegative;
        }

        double randIndex = (double) (truePositive + trueNegative) / NChooseR(randList.size(), 2);
        return randIndex;
    }

    public static int NChooseR(int n, int r) {
        int nCk = 1;
        for (int i = 0; i < r; ++i) {
            nCk = nCk * (n - i) / (i + 1);
        }
        return nCk;
    }

    public static double attributeDistance(RandData first, RandData second) {
        double sum = 0;
        for (int i = 0; i < first.data.attributes.size(); ++i) {
            double difference = first.data.attributes.get(i) - second.data.attributes.get(i);
            difference *= difference;
            sum += difference;
        }
        return Math.sqrt(sum);
    }
}
