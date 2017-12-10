import java.util.List;

public class Particle {
    private double fitnessVal;
    private double[] velocity;
    private double[] location;
    private double fitness;
    private List<Centroid> centroids;
    private Cluster[] clusters;

    public Particle(){

    }

    public Particle(double fitnessVal, double[] velocity, double[] location, List<Centroid> centroids) {
        this.fitnessVal = fitnessVal;
        this.velocity = velocity;
        this.location = location;
        this.centroids = centroids;
    }

    //Setters and getters for Velocity
    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double[] getVelocity() {
        return velocity;
    }

    //Setter and getters for Location
    public void setLoc(double[] location) {
        this.location = location;
    }

    public double[] getLoc() {
        return location;
    }
    //Update fitness values
    public void setFitness(double fitness) {
        //fitness function to be defined
        this.fitness = fitness;
    }

    public List<Centroid> getCentroids() {
        return centroids;
    }

    public void setCluster(Cluster[] clusters) {
        this.clusters = clusters;
    }

    public Cluster[] getClusters() {
        return clusters;
    }


}
