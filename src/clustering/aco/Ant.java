package clustering.aco;

import java.util.Random;

/*****
 * ANT
 *
 * tracks information about each ant
 */
public class Ant {
    ACOData load;
    boolean loaded;
    Vector2D pos;
    double velocity;
    private Random random;
    double maxDistance;
    int id;

    Ant() {
        random = new Random();
    }

    /***
     * sets a point that an ant is considering picking up
     * during the next iteration
     */
    void considerPickUp(ACOData data) {
        load = data;
        pos = new Vector2D(data.x, data.y);
    }

    /****
     * Tells the ant to pick up the data point
     */
    void pickUp(ACOData data) {
        data.pickedUp = true;

        load = data;
        pos = new Vector2D(data.x, data.y);
        loaded = true;
    }

    /****
     * Tells the ant to drop the data point
     */
    void drop() {
        load.pickedUp = false;
        loaded = false;
        load.x = pos.x;
        load.y = pos.y;

        load = null;
    }

    /*****
     * Causes the ant to move in a random direction at its velocity
     *
     * If it gets outside of the range of the problem it will head towards the origin
     */
    void MoveAnt() {
        if (distanceFromOrigin() < maxDistance) {
            double theta = randomInRange(0, 2.0 * Math.PI);
            Vector2D newDirection = new Vector2D(Math.cos(theta), Math.sin(theta));
            newDirection.x *= velocity;
            newDirection.y *= velocity;
            pos.x += newDirection.x;
            pos.y += newDirection.y;
        } else {
            Vector2D newDirection = new Vector2D(-1.0 * pos.x, -1.0 * pos.y);
            double mag = newDirection.Magnitude();
            newDirection.x = newDirection.x / mag * velocity;
            newDirection.y = newDirection.y / mag * velocity;
            pos.x += newDirection.x;
            pos.y += newDirection.y;
        }
        load.x = pos.x;
        load.y = pos.y;
    }

    /****
     * determines the distance the ant is from the origin
     */
    private double distanceFromOrigin() {
        return Math.sqrt(Math.pow((0.0 - pos.x), 2.0) + Math.pow((0.0 - pos.y), 2.0));
    }

    /**
     * randomInRange
     * $param min and max - doubles that define the range of the desired
     * random number
     * $return random number in the given range
     */
    private double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

    public String toString() {
        String output = "Ant: \n";
        output += load + "\n";
        output += "Loaded: " + loaded + "\n";
        output += "Pos: " + pos.x + " " + pos.y + " Velocity: " + velocity;

        return output;
    }
}