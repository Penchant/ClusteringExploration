import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AttributeSet {

    int clusterId;
    int networkWinner;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!AttributeSet.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final AttributeSet other = (AttributeSet) obj;

        return IntStream.range(0, attributes.size())
                .allMatch((index) -> this.attributes.get(index).equals(other.attributes.get(index)));
    }

}
