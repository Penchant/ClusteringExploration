package clustering.pso;

import clustering.util.Cluster;

public class Particle {
    private double[] velocity;
    private double[] location;
    private Cluster[] clusters;

    // Setters and getters for Velocity
    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double[] getVelocity() {
        return velocity;
    }

    // Setter and getters for Location
    public void setLoc(double[] location) {
        this.location = location;
    }

    public double[] getLoc() {
        return location;
    }

    public void setCluster(Cluster[] clusters) {
        this.clusters = clusters;
    }

    public Cluster[] getClusters() {
        return clusters;
    }

}
