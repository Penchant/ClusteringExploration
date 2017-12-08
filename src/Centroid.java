import java.util.ArrayList;
import java.util.List;

class Centroid {
    List<Double> values;

    public Centroid(List<Double> values) {
        this.values = new ArrayList<>();
        for (double d : values) {
            this.values.add(d);
        }
    }

    public Centroid(double ... values) {
        this.values = new ArrayList<>();
        for (double d : values) {
            this.values.add(d);
        }
    }

    public int size() {
        return values.size();
    }

    public void add(double value) {
        values.add(value);
    }

    public double get(int index) {
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

    public Centroid copy() {
        Centroid out = new Centroid();
        out.values = values;
        return out;
    }
}