import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class PSO {
    private List<Particle> swarm = new Vector<Particle>();
    private List<double []> pBestLoc = new Vector<double []>();
    private double[] pBest;
    private double[] fitnessList;
    private double[] gBestLoc;
    private double gBest;
    private static Cluster[] clusters;
    private static int epochs = 0;
    int ITERATION = 200;

    int NUM_OF_PARTICLES = 4;
    int K = 2;
    double C1 = 2.0;
    double C2 = 2.0;
    List<AttributeSet> data;
    int DIMENSION;

    public PSO(List<AttributeSet> data, int ITERATION, int NUM_OF_PARTICLES, int K, double C1, double C2 ) {
        this.data = data;
        this.ITERATION = ITERATION;
        this.NUM_OF_PARTICLES = NUM_OF_PARTICLES;
        this.K = K;
        this.C1 = C1;
        this.C2 = C2;
        DIMENSION = data.get(0).size();
        gBestLoc = new double [DIMENSION];
        pBest = new double[NUM_OF_PARTICLES];
        fitnessList = new double [NUM_OF_PARTICLES];
        run();
    }

    Random numGenerator = new Random();

    public void updateClusters(Particle p) {
//        Logger.info("Clustering data to centroids");
        // Clear members

        Logger.info("Clustering data to centroids");
        // for all x in D do
        for (AttributeSet x : data) {
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

            for (int m = 0; m < averages.size(); m++) {
                averages.set(m, averages.get(m) / c.members.size());
            }

            // For the values of the centroid
            c.centroid.values = new AttributeSet(averages);
        }

        p.setCluster(clusters);
    }

    //Updating fitness for each particle
    public double updateFitness(Particle p){
        double fitness = 0;
        double temp = 0;
        double temp1 = 0;

        //Summing distance from C_ij to AttributeSet_p divided by |C_ij|
        for (int i = 0; i < K; i++) {
           for (AttributeSet a: p.getClusters()[i].members ){
                temp += ClusteringHelper.distance(data.get(i), a)/p.getClusters()[i].members.size();
            }
            temp1 += temp;
            temp = 0;
        }
        fitness = temp1/DIMENSION;
        return fitness;
    }

    /**
     * Initializing Swarm
     */
    public void initializeSwarm() {
        Particle p;
        List<Centroid> centroids;
        double[] velocity = new double[DIMENSION];
        double[] position = new double[DIMENSION];

        //Init random centroids, velocities, and positions
        for (int i = 0; i < NUM_OF_PARTICLES; i++) {
            p = new Particle();
            Cluster cluster = null;
            clusters = new Cluster[K];


            Logger.info("Randomly initializing centroids");
            // Init mu_i ... mu_k randomly
            for (int k = 0; k < clusters.length; k++) {
                clusters[k] = new Cluster(K);
                List<Double> vals = new ArrayList<>();
                for (int j = 0; j < data.get(0).size(); j++) {
                    vals.add(Math.random());
                }

                clusters[k].centroid = new Centroid(vals);
            }
//            p.setCluster(clusters);


            Logger.info("Clustering data to centroids");
            // for all x in D do
            for (AttributeSet x : data) {
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

                for (int m = 0; m < averages.size(); m++) {
                    averages.set(m, averages.get(m) / c.members.size());
                }

                // For the values of the centroid
                c.centroid.values = new AttributeSet(averages);
            }

            p.setCluster(clusters);

            for (int j = 0; j < DIMENSION; j++) {
                velocity[j] = Math.random()*2;
            }
            p.setVelocity(velocity);
            for (int k = 0; k < DIMENSION; k++) {
                position[k] = Math.random()*2;
            }
            p.setLoc(position);

            swarm.add(p);

            }

    }

    public int findMinIndex(double [] list) {
        int minIndex = 0;
        double min = list[0];

        for (int i = 0; i < list.length; i++) {
            if(list[i] < min) {
                minIndex = i;
                min = list[i];
            }
        }

        return minIndex;
    }

    public void run() {
        initializeSwarm();
//        updateClusters();
        //For each particle, update fitness
        for(int i = 0; i < NUM_OF_PARTICLES; i++) {
            fitnessList[i] = updateFitness(swarm.get(i));
        }

        //Creating pBest and pBestLoc list
        for (int i = 0; i < NUM_OF_PARTICLES; i++) {
            pBest[i] = fitnessList[i];
            pBestLoc.add(swarm.get(i).getLoc());
        }

        double w;

        while(epochs < ITERATION) {
            //Update pBest
            for (int i = 0; i < NUM_OF_PARTICLES; i++) {
                if(fitnessList[i] < pBest[i]) {
                    pBest[i] = fitnessList[i];
                    pBestLoc.set(i, swarm.get(i).getLoc());
                }
            }


            //Update gBest
            int bestParticleIndex = findMinIndex(fitnessList);
            if( epochs == 0 || fitnessList[bestParticleIndex] < gBest) {
                gBest = fitnessList[bestParticleIndex];
                gBestLoc = swarm.get(bestParticleIndex).getLoc();
            }

            //For each particle, update clusters
            for(int i = 0; i < NUM_OF_PARTICLES; i++) {
                fitnessList[i] = updateFitness(swarm.get(i));
            }
            for(Particle p: swarm) {
                updateClusters(p);
            }

            //Momentum term
            w = 1-(((double) epochs) / ITERATION);

            //Updating Velocity
            for (int i = 0; i < NUM_OF_PARTICLES; i++) {
                double r1 = Math.random();
                double r2 = Math.random();

                Particle p = swarm.get(i);

                double[] newVelocity = new double[DIMENSION];
                for (int j = 0; j < DIMENSION ; j++) {
                    newVelocity[j] = (w * p.getVelocity()[j]) + (r1 * C1) * (pBestLoc.get(i)[j] - p.getLoc()[j]) +
                            (r2 * C2) * (gBestLoc[j] - p.getLoc()[j]);
                }
                p.setVelocity(newVelocity);

                //Updating Location
                double[] newLocation = new double[DIMENSION];
                for (int j = 0; j < DIMENSION; j++) {
                    newLocation[j] = p.getLoc()[j] + newVelocity[j];
                }
                p.setLoc(newLocation);
            }
            System.out.println("Epoch: " + epochs);

            for (int i = 0; i < K; i++) {
                System.out.println("Best Particle Centroids: " + swarm.get(bestParticleIndex).getClusters()[i].centroid);
            }

            epochs++;

        }



    }


}


