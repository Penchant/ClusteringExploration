package clustering.aco;

import java.util.List;

/****
 * ACOData
 *
 * is used to map the data onto a 2d plane and tracks whether it is picked up or not
 */
class ACOData {
    List<Double> data;
    double x;
    double y;
    boolean pickedUp;
    int id;
    double similarity;
    int cluster;

    public String toString() {
        String output = "Data: \n";
        for (Double aData : data) {
            output += aData + " ";
        }
        output += "\npos: " + x + " " + y + " picked up? " + pickedUp + " similarity " + similarity + " cluster " + cluster;
        return output;
    }
}