package clustering.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that represents a Centroid
 * Used for more clarity and a few additional functions over just
 * using a List<Double>
 */
public class Centroid {
    public AttributeSet values;
    public AttributeSet stdDev;

    public Centroid(List<Double> values) {
        this.values = new AttributeSet();
        this.values.attributes.addAll(values);
    }

    public Centroid(ArrayList<AttributeSet> attributeSets){
        AttributeSet average = new AttributeSet();
        stdDev = new AttributeSet();
        if(attributeSets.size() > 0) {
            IntStream.range(0, attributeSets.get(0).attributes.size()).forEach(index -> average.attributes.add(0d));
            IntStream.range(0, attributeSets.get(0).attributes.size()).forEach(index -> stdDev.attributes.add(0d));


            IntStream.range(0, attributeSets.get(0).attributes.size()).forEach(attributeIndex -> {
                        double mean = IntStream.range(0, attributeSets.size()).mapToDouble(index -> attributeSets.get(index).attributes.get(attributeIndex)).sum();
                        average.attributes.set(attributeIndex, mean / attributeSets.size());

                        double standardDev = Math.sqrt(IntStream.range(0, attributeSets.size())
                                .mapToDouble(index -> Math.pow(attributeSets.get(index).attributes.get(attributeIndex) - average.attributes.get(attributeIndex), 2))
                                .sum() / (attributeSets.size() - 1));
                        stdDev.attributes.set(attributeIndex, standardDev);
                    }
            );
        }
        this.values = average;
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