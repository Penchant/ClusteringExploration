package clustering.competitiveLearning;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import clustering.util.*;
import static java.util.Comparator.comparingDouble;

public class Network implements Runnable {

    public double percentCorrect = -1;
    public List<Layer> layers = new ArrayList<>();
    public int epoch = 0;
    public List<Cluster> clusters = new ArrayList<>();

    private Layer inputLayer;
    private Layer outputLayer;
    private List<AttributeSet> examples;
    private List<AttributeSet> fullSet;
    private List<AttributeSet> verifySet;
    private List<AttributeSet> testSet;

    private List<Cluster> oldClusters;
    private Centroid stats;
    private int hiddenLayers;
    private int numOfClusters;
    private int dimension;
    private int same = 0;

    public static double learningRate = .0001d;

    /**
     * Blank constructor for Chromosome.ToNetwork()
     */
    public Network() {}

    public Network(final List<Integer> hiddenLayers, int numOfClusters, List<AttributeSet> examples) {
        if (hiddenLayers.size() == 0 || hiddenLayers.get(0) == 0) {
            this.hiddenLayers = 0;
        } else {
            this.hiddenLayers = hiddenLayers.size();
        }
        this.dimension = examples.get(0).attributes.size();
        learningRate = learningRate / examples.size();

        Layer.network = this;
        this.numOfClusters = numOfClusters;
        IntStream.range(0, numOfClusters).forEach((index) -> this.clusters.add(new Cluster()));

        this.stats = new Centroid((ArrayList<AttributeSet>) examples);

        layers.add(inputLayer = new Layer(dimension, Type.INPUT, dimension, this.stats));

        this.fullSet = examples;
        setupExamples();

        if (hiddenLayers.size() !=0 && hiddenLayers.get(0) != 0) {
            for (int i : hiddenLayers) {
                layers.add(new Layer(i, Type.HIDDEN, layers.get(layers.size() - 1).nodes.size(), this.stats));
            }
        }

        this.outputLayer = new Layer(numOfClusters, Type.OUTPUT, layers.get(layers.size() - 1).nodes.size(), this.stats);
        layers.add(this.outputLayer);

        setNodeConnections();
    }

    public void setupExamples () {
        examples = new ArrayList<AttributeSet> ();
        verifySet = new ArrayList<AttributeSet> ();
        testSet = new ArrayList<AttributeSet> ();

        // Test set will be 10% of the total example size
        int testSize = fullSet.size() / 10;
        // Verify set will be 5% of the total example size
        int verifySize = fullSet.size() / 20;
        // setup the test examples
        for (int i = 0; i < testSize; i++) {
            int index = ThreadLocalRandom.current().nextInt(0, fullSet.size() - 1);
            testSet.add(fullSet.get(index));
            fullSet.remove(index);
        }
        // setup the verify examples
        for (int i = 0; i < verifySize; i++) {
            int index = ThreadLocalRandom.current().nextInt(0, fullSet.size() - 1);
            verifySet.add(fullSet.get(index));
            fullSet.remove(index);
        }
        // Once test and verify values are pulled out, set examples to remainder.
        examples = fullSet;
    }

    @Override
    public void run() {

            int run_count = 0;
            LinkedList<Double> verifyError = new LinkedList<Double>();
            boolean shouldRun = true;
            while (shouldRun) {
                List<Double> output = new ArrayList<Double>();

                epoch++;

                boolean never = false;

                while (never) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                this.oldClusters = this.clusters;
                this.clusters = new ArrayList<>();
                IntStream.range(0, numOfClusters).forEach((index) -> this.clusters.add(new Cluster()));

                // For each example we set the input layer's node's inputs to the example value,
                // then calculate the output for that example.
                examples.forEach(example -> {
                    List<Double> networkOutput = forwardPropagate(example);
                    output.add(networkOutput.get(0));

                    if (Double.isNaN(networkOutput.get(0))) {
                        System.err.println("NaN");
                        System.exit(1);
                    }

                    int networkWinner = IntStream.range(0, networkOutput.size()).boxed()
                            .max(comparingDouble(networkOutput::get))
                            .get();

                    this.clusters.get(networkWinner).members.add(example);
                    List<Double> target = new ArrayList<>();
                    IntStream.range(0, numOfClusters).forEach((i) -> target.add(i == networkWinner ? 1d : 0d)
                    );

                    weightUpdate(networkWinner, example);

                    layers.parallelStream().forEach(Layer::updateNodeWeights);
                });

                if (IntStream.range(0, numOfClusters).allMatch((index) -> this.clusters.get(index).equals(this.oldClusters.get(index)))) {
                    same++;
                } else {
                    same = 0;
                }

                if (same == 5) {
                    shouldRun = false;
                }
            }

            this.epoch -= 4;

            Logger.important("Competitive Learning successfully finished in " + this.epoch + " epochs");
    }

    /**
     *  Forward propagates input through network
     * Used for batch updates, where all examples will have their outputs calculated
     *
     * @return A [List] containing the output for each example in the examples list.
     */
    public List forwardPropagate(AttributeSet example) {
        Layer input = layers.get(0);

        // For each node in the input layer, set the input to the node
        IntStream.range(0, example.attributes.size()).parallel().forEach(index -> {
            input.nodes.get(index).inputs.clear();
            input.nodes.get(index).inputs.add(example.attributes.get(index));
        });

        // Calculate the output for each layer and pass it into the next layer
        for (int j = 0; j < layers.size() - 1; j++) {
            Layer currentLayer = layers.get(j);
            List<Double> outputs = currentLayer.calculateNodeOutputs();
            // If we are not at the output layer, we are going to set the
            // Next layers inputs to the current layers outputs.
            Layer nextLayer = layers.get(j + 1);
            // Grab each node in the layer
            nextLayer.nodes.parallelStream().forEach(node -> {
                // Set each node's inputs to the outputs
                node.inputs.clear();
                node.inputs.addAll(outputs);
            });
        }

        // We have hit the output and need to save it - Assume output has only one node.
        return layers.get(layers.size() - 1).calculateNodeOutputs();
    }

    /**
     * Update weights
     * @param winner Winning node
     * @param example Example to update off of
     */
    public void weightUpdate(int winner, AttributeSet example){
        List<Integer> indices = IntStream.range(0, layers.get(0).nodes.size()).boxed().collect(Collectors.toList());

        IntStream.range(0, example.attributes.size()).forEach( index -> {
                    Double currentWeight = outputLayer.nodes.get(winner).weights.get(index);
                    outputLayer.nodes.get(winner).weights.set(index, currentWeight + learningRate*(example.attributes.get(index) - currentWeight));
                }
        );
    }

    /**
     * Sets the inputNodes on all nodes
     */
    public void setNodeConnections() {
        IntStream.range(1, layers.size()).parallel().forEach(
                index -> {
                    Layer currentLayer = layers.get(index);
                    currentLayer.nodes.stream().forEach(
                            node ->  {
                                node.inputNodes.clear();
                                node.inputNodes.addAll(layers.get(index - 1).nodes);
                            }
                    );
                }
        );
    }

}