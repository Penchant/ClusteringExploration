package clustering.competitiveLearning;

import clustering.util.Centroid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Layer {

    public static int count = 0;
    public static Network network;

    public List<Node> nodes;
    public Type layerType;
    public int id;

    /**
     * Creates a layer of a given type with a specified number of nodes
     * @param nodeCount number of nodes initially in layer
     * @param layerType type of layer
     */
    public Layer(int nodeCount, Type layerType, int numWeights, Centroid stats) {
        this.layerType = layerType;
        this.id = count++;

        nodes = IntStream.range(0, nodeCount)
                .boxed()
                .parallel()
                .map(i -> new Node(layerType, numWeights, stats))
                .collect(Collectors.toList());
    }

    public boolean add(Node newNode) {
        return nodes.add(newNode);
    }

    public void updateNodeWeights() {
        nodes.parallelStream().forEach(Node::updateWeights);
    }

    public List<Double> calculateNodeOutputs() {
        return nodes.stream()
                .parallel()
                .map(Node::calculateOutput)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "layer ID: " + id;
    }

}