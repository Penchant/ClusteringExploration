package clustering.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeSet {

    public static final int unclassified = -1;
    public static final int outlier = -2;

    public int clusterId;
    public List<Double> attributes;

    public AttributeSet() {
        clusterId = unclassified;
        attributes = new ArrayList<>();
    }

    public AttributeSet(List<Double> attributes) {
        clusterId = unclassified;
        this.attributes = attributes;
    }

    public int size() {
        return attributes.size();
    }

    @Override
    public String toString() {
        String tab = "\t";
        return "AttributeSet: {\n" +
                tab + "ClusterID: " + clusterId + "\n" +
                tab + "Attributes: [" + attributes.stream().map(i -> "" + i).collect(Collectors.joining()) + "]\n" +
                "}";
    }

}
