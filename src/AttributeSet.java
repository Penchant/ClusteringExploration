import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeSet {

    static final int unclassified = -1;
    static final int outlier = -2;

    int clusterId;
    List<Double> attributes;

    AttributeSet() {
        clusterId = unclassified;
        attributes = new ArrayList<>();
    }

    AttributeSet(List<Double> attributes) {
        clusterId = unclassified;
        this.attributes = attributes;
    }

    int size() {
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
