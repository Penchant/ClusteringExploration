package clustering.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents a Centroid
 * Used for more clarity and a few additional functions over just
 * using a List<Double>
 */
public class Centroid {
    public AttributeSet values;

    public Centroid(List<Double> values) {
        this.values = new AttributeSet();
        this.values.attributes.addAll(values);
    }

    public Centroid(double ... values) {
        this.values = new AttributeSet();
        // Effectively `this.values.addAll(values)`
        this.values.attributes.addAll(Arrays.stream(values).boxed().collect(Collectors.toList()));
    }

    /**
     * Returns the size of the centroid
     * @return The size of the centroid
     */
    public int size() {
        return values.size();
    }

    /**
     * Adds an element to the centroid
     * @param value The element to be added
     */
    public void add(double value) {
        values.attributes.add(value);
    }

    /**
     * Returns the value in the given index
     * @param index the index of the wanted value
     * @return The value in the given index
     */
    public double get(int index) {
        return values.attributes.get(index);
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
    public Centroid copy() {
        Centroid out = new Centroid();
        out.values = values;
        return out;
    }

    @Override
    public String toString() {
        String out = "";
        for (Double d : values.attributes) {
            out += d + ", ";
        }
        return out;
    }
}