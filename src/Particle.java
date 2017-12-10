class Particle {
    private double[] velocity;
    private double[] location;
    private Cluster[] clusters;

    // Setters and getters for Velocity
    void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    double[] getVelocity() {
        return velocity;
    }

    // Setter and getters for Location
    void setLoc(double[] location) {
        this.location = location;
    }

    double[] getLoc() {
        return location;
    }

    void setCluster(Cluster[] clusters) {
        this.clusters = clusters;
    }

    Cluster[] getClusters() {
        return clusters;
    }

}
