import java.util.ArrayList;
import java.util.List;

public class AttributeSet {

    int clusterId;
    List<Double> attributes;

    public AttributeSet() {
        clusterId = -1;
        attributes = new ArrayList<>();
    }

    public AttributeSet(List<Double> attributes) {
        clusterId = -1;
        this.attributes = attributes;
    }

    public int size() {
        return attributes.size();
    }

}
