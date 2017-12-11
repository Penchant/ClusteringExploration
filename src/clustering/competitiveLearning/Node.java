package clustering.competitiveLearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import clustering.util.*;

public class Node {

    public static double sigma = 0;

    public List<Double> inputs = new ArrayList<>();
    public List<Double> weights = new ArrayList<>();
    public List<Double> newWeights = new ArrayList<>();


    public final Function<Double, Double> activationFunction;

    public double output;
    public double mu = 0;
    public double delta;
    public int id;
    public List<Node> inputNodes = new ArrayList<>();

    private Type nodeType;

    public Node(Type nodeType, int inputCount, Centroid stats) {
        this.nodeType = nodeType;


        for (int i = 0; i < inputCount ; i++) {
            if (nodeType == Type.INPUT) {
                weights.add(1d);
            } else if (nodeType == Type.OUTPUT){
                double newWeight = stats.values.attributes.get(i) + stats.stdDev.attributes.get(i) * (Math.random() - .5) * 2;
                weights.add(newWeight);
            } else {
                weights.add(Math.random()/10);
            }
        }

        switch (nodeType) {
            case HIDDEN:    activationFunction = logisticActivation; break;
            case RBFHIDDEN: activationFunction = gaussianBasisFunction; break;
            case INPUT:
            case RBFINPUT:
            case OUTPUT:
            default:        activationFunction = linearActivation; break;
        }

        newWeights.addAll(weights);
    }

    public Node(Type nodeType, List<Double> weights) {
        this.nodeType = nodeType;
        this.weights = weights;

        switch (nodeType) {
            case HIDDEN:    activationFunction = logisticActivation; break;
            case RBFHIDDEN: activationFunction = gaussianBasisFunction; break;
            case INPUT:
            case OUTPUT:    activationFunction = logisticActivation; break;
            case RBFINPUT:
            default:        activationFunction = linearActivation; break;
        }

        newWeights.addAll(weights);
    }

    public double calculateOutput() {
        if(this.nodeType.equals(Type.OUTPUT)){
            Double distance = ClusteringHelper.distance(new AttributeSet(this.weights), new AttributeSet(this.inputs));
            return output = activationFunction.apply(1/distance);
        }

        return output = activationFunction.apply(
                IntStream.range(0, inputs.size())
                        .boxed()
                        .parallel()
                        .map(i -> {
                            try {
                                return new Double[]{inputs.get(i), weights.get(i)};
                            }
                            catch (Exception e){
                                return new Double[]{0d,1d};
                            }
                        })
                        .mapToDouble(calculateOutput)
                        .sum()
        );
    }

    public void updateWeights() {
        weights = new ArrayList<Double>(newWeights);
    }

    /**
     * Function to calculate the output for each [Node]
     * Takes in a value and a weight and multiplies them
     */
    private ToDoubleFunction<Double[]> calculateOutput = values -> values[0] * values[1];
    /**
     * Gaussian Basis Function (RBF Activation function)
     */
    private Function<Double, Double> gaussianBasisFunction = value -> Math.pow(Math.E, -Math.pow(value - mu, 2) / (2 * sigma * sigma));
    /**
     * Linear Activation Function
     * Returns the input
     */
    private Function<Double, Double> linearActivation = Function.identity();
    /**
     * Logistic Activation Function
     * Returns the input mapped to a sigmoidal curve
     */
    private Function<Double, Double> logisticActivation = value -> 1 / (1 + Math.exp(-1 * value));

    @Override
    public String toString() {
        return "Node Output: " + output;
    }

}