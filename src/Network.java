
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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

        layers.add(inputLayer = new Layer(dimension, Type.INPUT, dimension));

        this.fullSet = examples;
        setupExamples();

        if (hiddenLayers.size() !=0 && hiddenLayers.get(0) != 0) {
            for (int i : hiddenLayers) {
                layers.add(new Layer(i, Type.HIDDEN, layers.get(layers.size() - 1).nodes.size()));
            }
        }

        this.outputLayer = new Layer(numOfClusters, Type.OUTPUT, layers.get(layers.size() - 1).nodes.size());
        layers.add(this.outputLayer);

        setNodeConnections();
    }

    /**
     * Constructor for Network
     * @param hiddenLayers List containing number of nodes per layer
     * @param dimension Number of input nodes
     * @param outputDimension Number of output nodes
     */
    public Network(final List<Integer> hiddenLayers, int dimension, int outputDimension) {
        if (hiddenLayers.get(0) == 0){
            this.hiddenLayers = 0;
        } else {
            this.hiddenLayers = hiddenLayers.size();
        }
        this.dimension = dimension;

        Layer.network = this;

        layers.add(inputLayer = new Layer(dimension, Type.INPUT, dimension));

        if (hiddenLayers.get(0) != 0) {
            for (int i : hiddenLayers) {
                layers.add(new Layer(i, Type.HIDDEN, layers.get(layers.size() - 1).nodes.size()));
            }
        }

        this.outputLayer = new Layer(outputDimension, Type.OUTPUT, layers.get(layers.size() - 1).nodes.size());
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

//            File file = new File(System.currentTimeMillis() + ".csv");
//
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            PrintWriter writer = new PrintWriter(file);

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

                    //System.out.println(networkOutput);

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

                    backPropagate(target);

                    layers.parallelStream().forEach(Layer::updateNodeWeights);
                });

                //TODO check if clusters change
                if(IntStream.range(0, numOfClusters).allMatch((index)->this.clusters.get(index).equals(this.clusters.get(index)))){
                    same++;
                }
                else{
                    same = 0;
                }

                if(same == 5){
                    shouldRun = false;
                }



                double mean = output.parallelStream().mapToDouble(d -> d).sum()/ (output.size());

                double standardDeviation = output
                        .parallelStream()
                        .mapToDouble(d -> Math.pow(d - mean, 2))
                        .sum() / (output.size() - 1);
                standardDeviation = Math.sqrt(standardDeviation);

                System.out.println("Mean is " + mean + " and standard deviation is " + standardDeviation);

//                List<Double> outputs = examples
//                        .stream()
//                        .map(example -> example.outputs.get(0))
//                        .collect(Collectors.toList());
//
//                System.out.println("Average error is " + calculateAverageError(output, outputs));

                run_count++;
                // If we have done 5 runs, do a verify check to see how error is coming along
                if (run_count % 100 == 0) {
                    double total = 0;
                    // calculate error for each example in the verifySet
                    for (int i = 0; i < verifySet.size(); i++){
                        AttributeSet example = verifySet.get(i);
                        List<Double> networkOutput = forwardPropagate(example);
//                        Double exampleError = Math.abs(example.outputs.get(0) - networkOutput.get(0));
//                        total += exampleError;
                    }
                    // average error across verifySet
                    Double error = total / verifySet.size();

//                    writer.print(error + ", ");
//                    writer.flush();

                    System.out.println("Verify Error " + error);
                    verifyError.offer(error);

                    // if verifyError is full check slope
                    if (verifyError.size() == 20) {
                        double first = verifyError.getFirst();
                        double last = verifyError.getLast();
                        // if slope is positive stop experiment
                        if (last - first > 0) {
                            shouldRun = false;
                        }
                        // pop off oldest error and add new error
                        verifyError.remove();
                    }
                }
            }

            System.out.println("Run Ended");
            List<Double> errors = new ArrayList<Double>();
            List<Boolean> correctApproximations = new ArrayList<Boolean>();
            for (int i = 0; i < testSet.size (); i++) {
                AttributeSet example = testSet.get(i);
                List<Double> networkOutput = forwardPropagate(example);
                Double exampleError = 0.0d;
                //= Math.abs(example.outputs.get(0) - networkOutput.get(0));
                //TODO
                errors.add(exampleError);
                if (exampleError <= 0.001) {
                    correctApproximations.add(true);
                } else {
                    correctApproximations.add(false);
                }
            }
    }

    /**
     * TODO: write a description of forward propagation
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
     * Calculate percent correct for list of outputs for a given Chromosome
     * @return the percentage of correctly guessed classes
     */
//    public double getPercentCorrect(){
//        int numCorrect = 0;
//        double temp;
//        double max = 0;
//
//        if (percentCorrect >= 0) {
//            return percentCorrect;
//        }
//
//        List<List<Double>> outputs = new ArrayList<>();
//        // For each example we set the input layer's node's inputs to the example value,
//        // then calculate the output for that example.
//        examples.forEach(example -> {
//            List<Double> networkOutput = forwardPropagate(example);
//            outputs.add(networkOutput);
//        });
//        //Setting greatest probability to 1, rest to zero of outputs
//        for (int i = 0; i < outputs.size(); i++) {
//            for (int j = 0; j < outputs.get(i).size(); i++) {
//                temp = outputs.get(i).get(j);
//                if(temp > max){
//                    max = temp;
//                    outputs.get(i).set(j, 1d);
//                    //TODO Review this ->
//                    if(examples.get(i).networkWinner == j){
//                        numCorrect++;
//                    }
//                }else;
//                outputs.get(i).set(j, 0d);
//            }
//        }
//        percentCorrect =  numCorrect / testSet.size();
//
//        return percentCorrect;
//    }


    /**
     * Uses outputs to update weights through backpropagation
     * @param target target values for output layer
     */
    public void backPropagate(List<Double> target) {

        Layer currentLayer = layers.get(hiddenLayers + 1);
        Layer previousLayer = layers.get(hiddenLayers);
        List<Node> outputNodes = currentLayer.nodes;

        // Updating weights on output layer
        for (int i = 0; i < outputNodes.size(); i++) {
            Node outputNode = outputNodes.get(i);
            try {
                outputNode.delta = -1 * (target.get(i) - outputNode.output) * outputNode.output * (1 - outputNode.output);
            } catch (Exception e) {
                String mes =  e.getMessage();
                e.printStackTrace();
            }

            for (int j = 0; j < outputNode.newWeights.size(); j++) {
                double weightChange = outputNode.delta * previousLayer.nodes.get(j).output;

                if (Double.isNaN(weightChange)){
                    System.err.println("weightChange is not a number");
                }
                if (outputNode.delta == 0) {
                    System.err.println("delta is zero");
                }

                outputNode.newWeights.set(j, outputNode.newWeights.get(j) - learningRate * weightChange);
            }
        }

        // Iterating over all hidden layers to calculate weight change
        for(int x = hiddenLayers; x > 0; x--) {
            outputNodes = currentLayer.nodes;
            currentLayer = layers.get(x);
            previousLayer = layers.get(x - 1);

            // Updates the weights of each node in the layer
            for (int i = 0; i < currentLayer.nodes.size(); i++) {
                final int index = i;
                Node currentNode = currentLayer.nodes.get(i);

                double weightedDeltaSum = outputNodes.parallelStream().mapToDouble(node -> node.delta * node.weights.get(index)).sum();
                currentNode.delta = weightedDeltaSum * currentNode.output * (1 - currentNode.output);

                // Updating each weight in the node
                for (int j = 0; j < currentNode.newWeights.size(); j++) {
                    double weightChange = currentNode.delta * previousLayer.nodes.get(j).output;
                    if (Double.isNaN(weightChange)){
                        System.err.println("weightChange is not a number");
                    }

                    currentNode.newWeights.set(j, currentNode.newWeights.get(j) - learningRate * weightChange);
                }
            }
        }
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

    /**
     * Calculates total error from Rosenbrock inputs and output from nodes
     * f(x) = sum(.5(expected-output)^2)
     * @param outputs from calculated node output
     * @param inputs from rosenBrock
     * @return squared error result
     */
    public double calculateTotalError(List<Double> outputs, List<Double> inputs) {
        return IntStream.range(0, outputs.size())
                .mapToDouble(i -> 0.5d * Math.pow(inputs.get(i) - outputs.get(i), 2))
                .sum();
    }

    public double calculateAverageError(List<Double> outputs, List<Double> inputs) {
        return IntStream.range(0, outputs.size())
                .mapToDouble(i -> Math.abs(inputs.get(i) - outputs.get(i)))
                .sum() / outputs.size();
    }
}