import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a Centroid
 * Used for more clarity and a few additional functions over just
 * using a List<Double>
 */
class Centroid {
    List<Double> values;

    Centroid(List<Double> values) {
        this.values = new ArrayList<>();
        for (double d : values) {
            this.values.add(d);
        }
    }

    Centroid(double ... values) {
        this.values = new ArrayList<>();
        for (double d : values) {
            this.values.add(d);
        }
    }

    /**
     * Returns the size of the centroid
     * @return The size of the centroid
     */
    int size() {
        return values.size();
    }

    /**
     * Adds an element to the centroid
     * @param value The element to be added
     */
    void add(double value) {
        values.add(value);
    }

    /**
     * Returns the value in the given index
     * @param index the index of the wanted value
     * @return The value in the given index
     */
    double get(int index) {
        return values.get(index);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Centroid)) {
            return false;
        } else {
            Centroid c = (Centroid) other;
            for (int i = 0; i < size(); i++) {
                if (get(i) != c.get(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Makes and returns a copy of this centroid
     * @return The copy of the centroid
     */
    Centroid copy() {
        Centroid out = new Centroid();
        out.values = values;
        return out;
    }

    @Override
    public String toString() {
        String out = "";
        for (Double d : values) {
            out += d + ", ";
        }
        return out;
    }
}