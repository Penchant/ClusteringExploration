import java.util.List;

public class Particle {
    private double fitnessVal;
    private double[] velocity;
    private double[] location;
    private double[] fitness;
    private List<Centroid> centroids;

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
    public double[] getFitness() {
        //fitness function to be defined

        return fitness;
    }

    public void setCentroids(List<Centroid> centroids) {
        this.centroids = centroids;
    }


}
