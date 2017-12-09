import java.util.List;
import java.util.Random;
import java.util.Vector;

public class PSO implements PSOTunableParameters {
    private List<Particle> swarm = new Vector<Particle>();
    private List<Double> pBestLoc = new Vector<Double>();
    private double[] pBest = new double[NUM_OF_PARTICLES];
    private double[] fitness = new double [NUM_OF_PARTICLES];
    private double[] gBestLoc = new double [DIMENSION];
    private double gBest;
    private static Cluster[] clusters;
    private static int epochs = 0;



    List<List<Double>> data = DataLoader.loadData("C:\\Users\\gross\\Documents\\GitHub\\ClusteringExploration\\a1_raw.csv");

    Random numGenerator = new Random();

    public void updateClusters() {
        Logger.info("Clustering data to centroids");
        // for all x in D do
        for (List x : data) {
            // c <- arg_min mu_j d(x_i, mu_j)
            Logger.info("Applying arg min on the data and each cluster");
            Cluster c = ClusteringHelper.argMin(x, clusters);

            // assign x_i to the cluster c
            Logger.info("Assigning to cluster");
            c.members.add(x);
        }

            }

    public void updateFitness(){
        for (int i = 0; i < DIMENSION; i++) {
            for (List<Double> d: data){

            }

        }
    }

    /**
     * Initializing Swarm
     */
    public void initializeSwarm() {
        Particle p;
        List<Centroid> c;
        double[] velocity = new double[DIMENSION];
        double[] position = new double[DIMENSION];

        //Init random centroids, velocities, and positions
        for (int i = 0; i < NUM_OF_PARTICLES; i++) {
            p = new Particle();
            c = ClusteringHelper.randomCentroids(data, clusters, K);
            p.setCentroids(c);
            for (int j = 0; j < DIMENSION; j++) {
                velocity[i] = Math.random()*2;
            }
            p.setVelocity(velocity);
            for (int k = 0; k < DIMENSION; k++) {
                position[i] = Math.random()*2;
            }
            p.setLoc(position);

            swarm.add(p);

            }

    }

    public void run() {
        initializeSwarm();

        //For each particle, update clusters
        for(Particle p: swarm) {
            updateClusters();
        }

    }


}

interface PSOTunableParameters {
    int ITERATION = 200;
    int DIMENSION = 18;
    int NUM_OF_PARTICLES = 20;
    int K = 5;
    double C1 = 2.0;
    double C2 = 2.0;
}

