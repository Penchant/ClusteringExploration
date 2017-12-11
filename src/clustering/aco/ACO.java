package clustering.aco;

import clustering.util.AttributeSet;

import java.util.*;

public class ACO {

    private static Random random = new Random();

    /****
     * acoAlg
     * Puts the data into clusters using an ant colony algorithm.
     *
     * antCount - number of ants, iterations - # of times to run algorithm
     * length - size of neighborhood. alpha - tunable used for how closely related data points are
     * slope - tunable for how likely to drop an object into an area, vMax - maximum velocity that an ant can go
     * data- data points
     *
     * returns data in clusters
     */
    public static Map<Integer, List<AttributeSet>> run(int antCount, int iterations, double length, double alpha, double slope, double vMax, List<AttributeSet> data) {
        List<ACOData> dataList = new ArrayList<>();
        List<Ant> ants = new ArrayList<>();
        List<Integer> dataToPick = new ArrayList<>();
        int size = data.get(0).size();
        // Maps data rnadomly onto 2d plane
        for (int i = 0; i < data.size(); ++i) {
            ACOData newData = new ACOData();
            newData.data = data.get(i).attributes;
            newData.x = randomInRange(-1 * size, size);
            newData.y = randomInRange(-1 * size, size);
            newData.pickedUp = false;
            newData.id = i + 1;
            newData.cluster = -1;
            dataList.add(newData);
            dataToPick.add(i);
        }
        // Sets up ants
        for (int i = 0; i < antCount; ++i) {
            Ant ant = new Ant();
            if (dataToPick.size() > 0) {
                int pick = (int) (Math.random() * dataToPick.size());
                dataToPick.remove(new Integer(pick));

                ACOData pickedData = dataList.get(pick);
                ant.pickUp(pickedData);
            } else
                ant.loaded = false;
            ant.velocity = randomInRange(1, vMax);
            ant.maxDistance = Math.sqrt(Math.pow(size, 2) + Math.pow(size, 2));
            ant.id = i + 1;
            ants.add(ant);
        }
        // Eun algorithm
        for (int i = 0; i < iterations; ++i) {
            for (Ant ant : ants) {
                for (int k = 0; k < dataList.size(); ++k) {
                    ACOData found = dataList.get(k);
                    found.similarity = averageSimilarity(found, dataList, alpha, ant.velocity, vMax, length);
                }
                if (ant.loaded) {
                    double drop = probDrop(ant.load.similarity, slope);
                    double probability = randomInRange(0, 1);
                    if (drop > probability) {
                        ant.drop();
                        boolean picked = false;
                        while (!picked) {
                            int pick = (int) (Math.random() * dataList.size());
                            if (!dataList.get(pick).pickedUp) {
                                ant.considerPickUp(dataList.get(pick));
                                picked = true;
                            }
                        }
                    } else {
                        ant.MoveAnt();
                    }
                } else {
                    double pickUp = probPickUp(ant.load.similarity, slope);
                    double probability = randomInRange(0, 1);
                    if (pickUp > probability && !ant.load.pickedUp) {
                        ant.pickUp(ant.load);
                        ant.MoveAnt();
                    } else {
                        boolean picked = false;
                        while (!picked) {
                            int pick = (int) (Math.random() * dataList.size());
                            if (!dataList.get(pick).pickedUp) {
                                ant.considerPickUp(dataList.get(pick));
                                picked = true;
                            }
                        }
                    }
                }
            }
        }
        int cluster = 0;
        int isolated = -1;
        boolean clusterFound;
        Map<Integer, List<AttributeSet>> clusters = new HashMap<>();
        // Determine clusters
        for (int i = 0; i < dataList.size(); i++) {
            clusterFound = false;
            ACOData current = dataList.get(i);
            if (current.cluster == -1) {
                for (ACOData neighbor : dataList) {
                    if (neighbor.id != current.id && neighbor.cluster == -1 && distance(current, neighbor) < (length / 2)) {
                        if (!clusterFound) {
                            clusterFound = true;
                            ++cluster;
                            current.cluster = cluster;
                            AttributeSet set = new AttributeSet(current.data);
                            List<AttributeSet> setList = new ArrayList<>();
                            setList.add(set);
                            clusters.put(cluster, setList);
                        }
                        neighbor.cluster = current.cluster;
                        AttributeSet set = new AttributeSet(neighbor.data);
                        List<AttributeSet> setList = clusters.get (current.cluster);
                        setList.add (set);
                        clusters.put (current.cluster, setList);
                    } else if (neighbor.id != current.id && neighbor.cluster != -1 && !clusterFound && distance(current, neighbor) < (length / 2)) {
                        clusterFound = true;
                        current.cluster = neighbor.cluster;
                        AttributeSet set = new AttributeSet(current.data);
                        List<AttributeSet> setList = clusters.get(neighbor.cluster);
                        setList.add(set);
                        clusters.put(current.cluster, setList);
                    }
                }
                if (!clusterFound) {
                    --isolated;
                    current.cluster = isolated;
                    AttributeSet set = new AttributeSet(current.data);
                    List<AttributeSet> setList = new ArrayList<>();
                    setList.add(set);
                    clusters.put(isolated, setList);
                }
            }
        }
        return clusters;
    }

    /******
     * averageSimilarity
     * calculates the average similarity between a data point and its neighbors.
     *
     * found - current data point we are checking
     * dataList- all other data points, alpha - how similar data points are
     * antVelocity - current ants velocity, vMax - max ant velocity
     * size - size of local neighbor area
     *
     * returns the average similarity
     */
    private static double averageSimilarity(ACOData found, List<ACOData> dataList, double alpha, double antVelocity, double vMax, double size) {
        double sum = 0;
        for (ACOData neighbor : dataList) {
            if (neighbor.id != found.id && distance(found, neighbor) < (size / 2)) {
                double attDist = attributeDistance(found, neighbor);
                double quotient = 1 - (attDist / (alpha * (1 + (antVelocity - 1) / vMax)));
                sum += quotient;
            }
        }
        double total = (1 / Math.pow(size, 2)) * sum;
        return Math.max(0, total);
    }

    /***
     * distance
     * calculates the distance between two points
     *
     * first - first data point, second - second data point
     *
     * returns the distance
     */
    private static double distance(ACOData first, ACOData second) {
        return Math.sqrt(Math.pow((second.x - first.x), 2) + Math.pow((second.y - first.y), 2));
    }

    /****
     * attributeDistance
     * calculates the distance between the attributes in two data points
     *
     * first - first data point, second- second data points
     *
     * returns the distance between the attributes in two points
     */
    private static double attributeDistance(ACOData first, ACOData second) {
        double sum = 0;
        for (int i = 0; i < first.data.size(); ++i) {
            double difference = first.data.get(i) - second.data.get(i);
            difference *= difference;
            sum += difference;
        }
        return Math.sqrt(sum);
    }

    /****
     * probPickUp
     * calculates the probability of an ant picking up a data point
     *
     * similarity - the similarity of that data point to its neighbors
     * c - slope.
     *
     * returns probability of picking up
     */
    private static double probPickUp(double similarity, double c) {
        return (1 - Sigmoid(similarity, c));
    }

    /*****
     * probDrop
     * calculates the probability of an ant dropping a data point
     *
     * similarity - the similarity of that data point to its neighbors
     * c - slope.
     *
     * returns probability of dropping
     */
    private static double probDrop(double similarity, double c) {
        return (Sigmoid(similarity, c));
    }

    /*****
     * Sigmoid
     * creates a sigmoid value
     *
     * x - variable to run on
     * c - slope
     *
     * return sigmoid value
     */
    private static double Sigmoid(double x, double c) {
        double e = Math.exp(-1 * c * x);
        return (1 - e) / (1 + e);
    }

    /**
     * randomInRange
     * $param min and max - doubles that define the range of the desired
     * random number
     * $return random number in the given range
     */
    private static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }
}